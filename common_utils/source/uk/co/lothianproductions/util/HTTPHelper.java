
package uk.co.lothianproductions.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * A collection of static methods for common HTTP functions.
 * 
 * @author	Aidan Fitzpatrick
 * @version $Revision: 1.8 $, $Date: 2003/12/11 17:12:58 $
 */
public final class HTTPHelper {

	/**
	 * Expands a compressed array of bytes using a GZIPInputStream.
	 * @see compress
	 */
	public static byte[] expandBytes( byte[] b ) throws IOException {	
		InputStream in = new GZIPInputStream( new ByteArrayInputStream( b ) );
		ByteArrayOutputStream out = new ByteArrayOutputStream( b.length * 2 );

		int CHUNK_SIZE = b.length, bytes_read;
		byte[] chunk = new byte[ CHUNK_SIZE ];
		
		while( (bytes_read = in.read(chunk, 0, CHUNK_SIZE)) != -1 ) {
			// Optimize buffering...
			if( bytes_read == chunk.length )
				out.write( chunk );
			else
				// Slower route -- approaching EOS.
				for(int i = 0 ; i < bytes_read; i++)
					out.write( chunk[i] );
		}
		
		return out.toByteArray();
	}

	/**
	 * Compresses an array of bytes using a GZIPOutputStream.
	 * @see expand
	 */
	public static byte[] compressBytes( byte[] b ) throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream( b.length / 4 );
		OutputStream gzip = new GZIPOutputStream( out );
		
		gzip.write( b );
		gzip.close();
		
		return out.toByteArray();
	}
	
	/**
	 * Takes a char[] representing the HTTP request line and returns char[][]
	 * representing the HTTP request split up into two distinct sections: index
	 * 0 being the HTTP method and index 1 being the path.
	 * 
	 * @param request
	 * @return
	 */
	public static char[][] httpRequestSplit( char[] request ) {
		
		// Find the GET and POST path.
		int first = -1, last = -1;
		
		for( int i = 0; i < request.length; i++ )
			if( request[i] == ' ' && first == -1 )
				first = i + 1;
			else if ( request[i] == ' ' ) {
				last = i;
				break;
			}
			
		// If protocol was missed off extend the URI length.
		if( last == -1 )
			last = request.length;

		char[][] parts = new char[2][];
		
		// Create the method.
		parts[0] = new char[ first - 1 ];
		// Create the path.
		parts[1] = new char[ last - first  ];
		
		System.arraycopy( request, 0, parts[0], 0, first - 1 );
		System.arraycopy( request, first , parts[1], 0, last - first );	
		
		return parts;
	}

	/**
	 * Dehexes a char[], useful for rapidly converting back a URL.
	 * 
	 * @see httpEncode
	 * @param message
	 *            The message to dehex.
	 * @return The dehexed message.
	 */
	public static char[] httpDecode( final char[] message ) {
		char ESCAPE = 0;
		char[] clean_message = { };
		int bad = 0; 
			
		for( int k = 0; k < message.length - 2; k++ )
			if( message[k] == '%' ) {
				message[k] = (char) ((StringHelper.hexToInt(message[k + 1]) * 16) + StringHelper.hexToInt(message[k + 2]));  
					
				// Mark next two for cleaning.
				message[k + 1] = ESCAPE;
				message[k + 2] = ESCAPE;
				bad += 2;
				k += 2;
			}
					
		// Fix bad chars.
		if( bad > 0 ) {
			clean_message = new char[ message.length - bad ];
				
			int found = 0;
				
			for( int k = 0; k < message.length; k++ )
				if( message[k] != ESCAPE )
					clean_message[ found++ ] = message[k];

			return clean_message;
		} else
			return message;
	}
	
	/**
	 * Analyses a section of URL space for the URL mappers and returns a
	 * two-dimensioned primitive array of information.
	 */
	public static String [][] analyseURLSpace( final String url ) {
		// Note maximum number of slashes to be found in the URL space
		// is limited at six here.
		int [] indexes = new int[ 5 ];
		String [] sections = new String[ 5 ];
		int i = 0;

		// Create a primitive String array of sections of the URL-space
		// delimited by slashes.
		while( ( indexes[++i] = url.indexOf( '/', indexes[i-1] + 1 ) ) != -1 )
			sections[i] = url.substring( indexes[i-1] + 1, indexes[i] );

		sections[i] = url.substring( indexes[i-1] + 1, url.length() );

		String [][] keys = new String [i][1];

		for( int j = 1; j < i + 1; j++ ) {
			int dash = sections[j].indexOf('-');

			if( dash == -1 )
				keys[j-1] = new String[] { sections[j], null };
			else
				keys[j-1] = new String[] { sections[j].substring( 0, dash ), sections[j].substring( dash + 1 ) };
		}

		return keys;
	}
	
	/**
	 * Encodes a string to hex format.
	 * 
	 * @see httpDecode
	 */
	public static char[] httpEncode( final char[] message ) {
		StringBuffer out = new StringBuffer( message.length );
			
		for( int i = 0; i < message.length; i++ )			
			if(		( message[i] >= 'a' && message[i] <= 'z' ) 
				||	( message[i] >= 'A' && message[i] <= 'Z' )
				||	( message[i] >= '0' && message[i] <= '9' )
			)
				out.append( message[i] );
			else
				out.append( new char[] { '%', StringHelper.HEX_CHARACTERS[ message[i] / 16 ], StringHelper.HEX_CHARACTERS[ message[i] % 16 ] } );
		
		return out.toString().toCharArray();
	}
}