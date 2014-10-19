
package uk.co.lothianproductions.httpd.handler.impl;

import uk.co.lothianproductions.httpd.config.ConfigException;
import uk.co.lothianproductions.httpd.config.XMLPluginConfig;
import uk.co.lothianproductions.httpd.document.Document;
import uk.co.lothianproductions.httpd.document.DocumentNotFoundException;
import uk.co.lothianproductions.httpd.document.DocumentRenderException;
import uk.co.lothianproductions.httpd.document.DocumentRetrievalException;
import uk.co.lothianproductions.httpd.document.DocumentTimeoutException;
import uk.co.lothianproductions.httpd.handler.RequestHandler;
import uk.co.lothianproductions.util.StringHelper;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.Socket;
import java.util.logging.Level;

/**
 * @author	Aidan Fitzpatrick
 * @version	$Revision: 1.5 $, $Date: 2004/01/02 19:00:37 $
 */
public class SocketRequestHandler implements RequestHandler {

	public static final int INTERNAL_BUFFER_SIZE = 1024 * 5;
	
	protected Socket mServer;
	protected XMLPluginConfig mXMLPluginConfig;
	
	// Hide default constructor.
	protected SocketRequestHandler() {}
	
	public SocketRequestHandler( final XMLPluginConfig config, final Socket server ) {
		mXMLPluginConfig = config;
		mServer = server;
	}
	
	public void run() {
		handleRequest();
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
		
		// Read first chunk of data only.
		StringBuffer buffer = new StringBuffer( 32 );
			
		int i = 0;
		
		try {
			while( ( i = in.available() ) == 0 ) {
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
		
		try {
			handleResponse( null, buffer.toString().toCharArray(), null, null );
		} catch (IOException e) {
			mLogger.log( Level.SEVERE, "Failed to read request data.", e );
		} catch (ConfigException e) {
			mLogger.log( Level.SEVERE, "Server was misconfigured and couldn't reply to request.", e );
		} finally {
			closeRequest();
		}
	}
	
	public Document handleProcessing(final char[] method, final char[] uri, final char[][] header,
									final char[] message) throws ConfigException, DocumentRenderException, DocumentNotFoundException, DocumentTimeoutException, DocumentRetrievalException {
		
		// On the CP, but only done once per request. Needs to be done somewhere, so...
		return mXMLPluginConfig.getPluginBroker().getDocumentSources()[0].spawn().getDocument( uri );
	}
	
	public void handleResponse(final char[] method, final char[] uri, final char[][] header, 
							   final char[] message) throws IOException, ConfigException {

		Writer out = new BufferedWriter( new OutputStreamWriter( mServer.getOutputStream() ) );
		InputStream response = null;
		
		try {
			// Render document, handle runtime "errors" in resources.
			response = handleProcessing( method, uri, header, message ).render( null );
		} catch (DocumentRenderException e) {
			out.write( RESPONSE_ERROR );
		} catch (DocumentTimeoutException e) {
			out.write( RESPONSE_ERROR );
		} catch (DocumentRetrievalException e) {
			out.write( RESPONSE_ERROR );
		} catch (DocumentNotFoundException e) {
			out.write( RESPONSE_NOT_FOUND );
		}
		
		if( response != null ) {
			StringHelper.writeInputStream( out, response, StringHelper.ISO88591, INTERNAL_BUFFER_SIZE );
			response.close();
		}
		
		// Flush, rather than close the output stream.
		// We'll explicitly close it later.
		out.flush();
	}
	

	
	public void closeRequest() {
		try {
			mServer.close();
		} catch (IOException e) {
			mLogger.log( Level.SEVERE, "Failed to close socket to client.", e );
		}
	}
}