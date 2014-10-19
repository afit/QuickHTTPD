
package uk.co.lothianproductions.httpd.handler.impl;

import uk.co.lothianproductions.httpd.config.ConfigException;
import uk.co.lothianproductions.httpd.config.XMLPluginConfig;
import uk.co.lothianproductions.httpd.document.Document;
import uk.co.lothianproductions.httpd.document.DocumentNotFoundException;
import uk.co.lothianproductions.httpd.document.DocumentRenderException;
import uk.co.lothianproductions.httpd.document.DocumentRetrievalException;
import uk.co.lothianproductions.httpd.document.DocumentTimeoutException;

import uk.co.lothianproductions.util.CachedDate;
import uk.co.lothianproductions.util.HTTPHelper;
import uk.co.lothianproductions.util.StringHelper;
import uk.co.lothianproductions.util.logging.ApacheFormatter;

import java.net.Socket;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.Properties;
import java.util.logging.Level;
import java.util.zip.GZIPOutputStream;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

/**
 * Partial implementation of the RequestHandler for HTTP.
 * 
 * @author	Aidan Fitzpatrick
 * @version $Revision: 1.7 $, $Date: 2004/01/02 19:00:37 $
 */
public class HTTPRequestHandler extends SocketRequestHandler {
	
	public static final char[] RESPONSE_OKAY = 			"HTTP/1.1 200 OK\n".toCharArray();
	public static final char[] RESPONSE_NOT_FOUND =		"HTTP/1.1 404 Not Found\n".toCharArray();
	public static final char[] RESPONSE_ERROR =			"HTTP/1.1 500 Internal Server Error\n".toCharArray();
	public static final char[] RESPONSE_CANT_SERVICE = 	"HTTP/1.1 501 Not Implemented\n".toCharArray();

	public static final int READ_MORE_DATA_INTERVAL =	10;
		
	protected static final DateFormat formatter = new SimpleDateFormat( "EEE, d MMM yyyy HH:mm:ss z" );
	protected static boolean mInitialised = false;
	
	protected static synchronized void initialiseLogger() throws SecurityException, IOException {
		if( mInitialised )
			return;
		
		ApacheFormatter.configureLogger( mLogger, true );
		
		mInitialised = true;
	}
	
	// Hide default constructor.
	protected HTTPRequestHandler() {}
	
	public HTTPRequestHandler( final XMLPluginConfig config, final Socket server ) throws SecurityException, IOException {
		mXMLPluginConfig = config;
		mServer = server;
		initialiseLogger();
	}
	
	public void handleRequest() {	
		InputStream in;
		
		try {
			in = mServer.getInputStream();
		} catch (IOException e) {
			mLogger.log( Level.SEVERE, "Failed to retrieve input stream from client.", e );
			closeRequest();
			return;
		}
			
		// First, find the request line and headers.
		// The request line is terminated by a newline.
		// The two sections are teminated by two newlines.
		StringBuffer buffer = new StringBuffer( 512 );
		char[][] header = null;
		int header_length = 0;
		
		while( header == null ) {
			
			int i = 0;
			
			try {
				while( ( i = in.available() ) == 0 ) {
					try {
						Thread.sleep( READ_MORE_DATA_INTERVAL );
					} catch (InterruptedException e) {
						// Do nothing.
					}
				}
			} catch (IOException e) {
				mLogger.log( Level.SEVERE, "Failed to evaluate size of available byte-stream.", e );
				closeRequest();
				return;
			}
			
			byte[] chunk = new byte[ i ];
						
			try {
				// Failed to read correctly.
				if( in.read( chunk ) < chunk.length )
					mLogger.log( Level.WARNING, "Failed to read as many bytes as were indicated available in request." );
			} catch (IOException e) {
				mLogger.log( Level.SEVERE, "Failed to read request data.", e );
				closeRequest();
				return;
			}
			
			// We can only append a char or char[]
			// to the buffer but we have a byte[].
			for( int j = 0; j < chunk.length; j++ )
				buffer.append( (char) chunk[j] );	
			
			// Magic number one indicates that more than a CRLF is present.
			if( chunk.length > 1 ) {
				int headers = 0;
				
				// Iterate header twice, first to count the header entries.
				// Only check new part of buffer (might this cause a problem
				// if the incoming stream is flushed midway between CR & LF? 
				for( int j = buffer.length() - chunk.length; j < buffer.length(); j++ )
					if( buffer.charAt( j ) == '\n' && buffer.charAt( j - 1 ) == '\r' )
						headers++;
						
				// Subtract one from headers index, as last index is a newline on its own.
				header = new char[ headers ][];
				headers = 0;
				int marker = 0;

				// Second time to reader header entries.					
				for( int j = buffer.length() - chunk.length; j < buffer.length(); j++ ) {			
					if( buffer.charAt( j ) == '\n' && buffer.charAt( j - 1 ) == '\r' ) {
						// Don't bother with last 'empty' header.
						if( headers == header.length )
							break;
						
						// We've found the end of request header, let's create
						// the header array.
						// Subtract an extra 2 to remove CR/LF.
						header[ headers ] = new char[ j - marker - 1 ];
						
						// Don't -1 as we need to include CRLF character counts
						// when checksumming
						header_length += j - marker + 1;
										
						// Copy header data to header array -- including final
						// two newlines.
						buffer.getChars( marker, j - 1, header[ headers ], 0 );
						
						// Update marker for last start position.
						marker = j + 1;

						// Increment headers counter.
						headers++;
					}
				}
			}			
		}

		// Need to interpret header here before the message
		// body can be read.
		final char[] content_length_header = {
			'C', 'o', 'n', 't', 'e', 'n', 't', '-',
			'L', 'e', 'n', 'g', 't', 'h', ':', ' '
		};
		
		// Start after first line.
		int i = 1;
		
		for( ; i < header.length; i++ ) {
			boolean find = true;
		
			// Only check "big enough" values...
			if( header[i].length > content_length_header.length )
				for( int j = 0; j < content_length_header.length; j++ ) {
					if( header[i][j] != content_length_header[j] ) {
						find = false;
						break;
					}
				}
			else
				find = false;
			
			if( find == true )
				break;
		}
				
		char[] message = {};
		
		// Find if a message has been posted.
		if( i < header.length ) {
		
			// Iterate through content-length value.
			int cl_length = 0;

			while(	content_length_header.length + cl_length < header[i].length - 1 &&
					header[i][ content_length_header.length + cl_length ] >= '0' &&
					header[i][ content_length_header.length + ++cl_length ] <= '9' ) { }
	
			int content_length = Integer.parseInt( new String( header[i], content_length_header.length, cl_length ) );
					
			while( buffer.length() < header_length + content_length + 1 ) {
				int j = 0;
					
				try {
					while( ( j = in.available() ) == 0 ) {
						try {
							Thread.sleep( 10 );
						} catch (InterruptedException e) {
							// Do nothing.
						}
					}
				} catch (IOException e) {
					mLogger.log( Level.SEVERE, "Failed to evaluate size of available byte-stream.", e );
					closeRequest();
					return;
				}
					
				byte[] chunk = new byte[ j ];
					
				// Failed to read correctly.
				try {
					if( in.read( chunk ) < chunk.length )
						mLogger.log( Level.WARNING, "Failed to read as many bytes as were indicated available in request." );
				} catch (IOException e) {
					mLogger.log( Level.SEVERE, "Failed to read request data.", e );
					closeRequest();
					return;
				}
				
				// We can only append a char or char[]
				// to the buffer but we have a byte[].
				for( int k = 0; k < chunk.length; k++ )
					buffer.append( (char) chunk[k] );	
			}	
			
			message = new char[ buffer.length() - header_length - 2 ];
			// Add 2 to header_length to skip the terminating newlines.
			buffer.getChars( header_length + 2, buffer.length(), message, 0 );
			message = HTTPHelper.httpDecode( message );
		}

		if( header.length == 0 ) {
			mLogger.log( Level.WARNING, "The request was empty. Bug in user-agent?" );
			closeRequest();
			return;
		}
		
		char[][] clean = HTTPHelper.httpRequestSplit( header[0] );
		
		try {
			handleResponse( clean[0], HTTPHelper.httpDecode( clean[1] ), header, message );
		} catch (IOException e) {
			mLogger.log( Level.SEVERE, "Failed to read request data.", e );
		} catch (ConfigException e) {
			mLogger.log( Level.SEVERE, "Server was misconfigured and couldn't reply to request.", e );
		} finally {
			closeRequest();
		}
	}
	
	public void handleResponse(final char[] method, final char[] uri, final char[][] header, 
							   final char[] message) throws IOException, ConfigException {

		OutputStreamWriter osw = new OutputStreamWriter( mServer.getOutputStream(), StringHelper.ISO88591 );
		Writer out = new BufferedWriter( osw );
		
		Document document = null;
		InputStream response = null;
						
		// Ensure the server can support the HTTP method.
		if( 	! ( method.length == 3 && method[0] == 'G' && method[1] == 'E' && method[2] == 'T' )
			&&  ! ( method.length == 4 && method[0] == 'P' && method[1] == 'O' && method[2] == 'S' && method[3] == 'T' ) )
			
			out.write( RESPONSE_CANT_SERVICE );
		
		// Render document, handle runtime "errors" in resources.
		else
			try {
				// Render document, handle runtime "errors" in resources.
				document = handleProcessing( method, uri, header, message ); 
				response = document.render( null );
				
				mLogger.log( Level.INFO, "200 " + new String( uri ) );
				
				out.write( RESPONSE_OKAY );
				
			// FIXME clarify differences between errors in log
			} catch (DocumentRenderException e) {
				out.write( RESPONSE_ERROR );
				
				mLogger.log( Level.INFO, "500 " + new String( uri ), e );

			} catch (DocumentRetrievalException e) {
				out.write( RESPONSE_ERROR );
				
				mLogger.log( Level.INFO, "500 " + new String( uri ), e );
				
			} catch (DocumentTimeoutException e) {
				out.write( RESPONSE_ERROR );
				
				mLogger.log( Level.INFO, "500 " + new String( uri ), e );
				
			} catch (DocumentNotFoundException e) {
				out.write( RESPONSE_NOT_FOUND );
				
				mLogger.log( Level.INFO, "404 " + new String( uri ), e );
			}		
							
		boolean supports_gzip = false;
		
		// Need to interpret header here before the message
		// body can be read.
		final char[] accept_encoding_header = {
			'A', 'c', 'c', 'e', 'p', 't', '-',
			'E', 'n', 'c', 'o', 'd', 'i', 'n', 'g', ':', ' '
		};
		
		// Start after first line.
		int i = 1;
		
		for( ; i < header.length; i++ ) {
			boolean find = true;
			
			// Only check "big enough" values...
			if( header[i].length > accept_encoding_header.length )
				for( int j = 0; j < accept_encoding_header.length; j++ ) {
					if( header[i][j] != accept_encoding_header[j] ) {
						find = false;
						break;
					}
				}
			else
				find = false;
			
			// Is there an instance of "gzip" in the accepts-encoding header?
			if( find == true ) {		
				for( int k = accept_encoding_header.length; k < header[i].length - 2; k++ )
					if(		header[i][k] == 'g' &&
						header[i][k + 1] == 'z' &&
						header[i][k + 2] == 'i' &&
						header[i][k + 3] == 'p' &&
						Boolean.getBoolean( mXMLPluginConfig.getProperty("gzip") )
					)
						supports_gzip = true;
				break;
			}
		}
		
		out.write( "Date: " );
		out.write( formatter.format( CachedDate.getInstance().getDate() ) );
		out.write( "\nServer: QuickHTTPD/$Version$ (Java 2)\nPragma: no-cache\n" );
		
		String type = null;
		String extension = null;
		
		Properties types = mXMLPluginConfig.getSubproperties( "type-" );
			
		// Iterate to reduce synchronisation issues.
		Iterator j = types.keySet().iterator();
			
		// Find file extension to check for.
		String suri = new String( uri );
		int index = suri.lastIndexOf( '.' );
			
		if( index > 0 && uri.length - 1 > index ) 
			extension = suri.substring( index + 1 );

		// If don't bother to figure out type for null extensions
		// let it default below.
		if( extension == null )
			type = "text/html";
			
			while( type == null && j.hasNext() ) {
				Object k = j.next();
				
				// FIXME more robust usage of indexOf needed
				// FIXME only check on filename, not folder name.
				if( types.get( k ).toString().indexOf( extension ) > -1 )
					// FIXME might be cheaper to trim this every time
					// rather than relying on the CoreProperties.
					// Cache XMLPC?
					type = k.toString();
			}
			
		// If you can't figure out the type, default it...
		if( type == null )
			type = mXMLPluginConfig.getProperty( "default-type" );
		
		out.write( "Content-Type: " );
		out.write( type );
		out.write( "; charset=" );
		out.write( osw.getEncoding() );
		out.write( "\nConnection: close" );
		
		if( supports_gzip ) {
			// Don't know length
			out.write( "\nContent-Encoding: gzip\n\n" );
			
			// Flush now to let us write direct.
			out.flush();
			
			out = new BufferedWriter( new OutputStreamWriter( new GZIPOutputStream( mServer.getOutputStream() ), StringHelper.ISO88591 ) );
		}

		// Write the length if we know it, which we can't do
		// when gzipping the response.
		if( ! supports_gzip && document.length() > -1 )
			out.write( "\nContent-Length: " + document.length() + "\n\n" );
		else
			out.write( "\n\n" );

		// FIXME try-catch the whole lot for socketexceptions to get user aborts?
		
		if( response != null ) {
			StringHelper.writeInputStream( out, response, StringHelper.ISO88591, INTERNAL_BUFFER_SIZE );
			response.close();
		}
		
		// Flush, rather than close the output stream.
		// We'll explicitly close it later.
		out.flush();
	}

}