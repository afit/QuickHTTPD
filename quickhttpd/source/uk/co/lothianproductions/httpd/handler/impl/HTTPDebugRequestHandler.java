
package uk.co.lothianproductions.httpd.handler.impl;

import uk.co.lothianproductions.httpd.config.ConfigException;
import uk.co.lothianproductions.httpd.config.XMLPluginConfig;
import uk.co.lothianproductions.httpd.document.Document;
import uk.co.lothianproductions.httpd.document.DocumentNotFoundException;
import uk.co.lothianproductions.httpd.document.DocumentRenderException;
import uk.co.lothianproductions.httpd.document.DocumentRetrievalException;
import uk.co.lothianproductions.httpd.document.DocumentTimeoutException;

import java.net.Socket;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Simple request handler for debugging. Dumps out request header values.
 * 
 * @author	Aidan Fitzpatrick
 * @version $Revision: 1.4 $, $Date: 2004/01/02 19:00:48 $
 */
public class HTTPDebugRequestHandler extends HTTPRequestHandler {
	
	// Hide default constructor.
	protected HTTPDebugRequestHandler() {}

	// FIXME why not factor as a documentsource?
	
	public HTTPDebugRequestHandler( final XMLPluginConfig config, final Socket server ) throws SecurityException, IOException {
		super( config, server );
	}
	
	public Document handleProcessing(final char[] method, final char[] uri, final char[][] header,
									 final char[] message) throws ConfigException, DocumentRenderException, DocumentNotFoundException, DocumentTimeoutException, DocumentRetrievalException {
		return new Document() {
					
			public InputStream render( final String[][] parameters ) {
				StringBuffer b = new StringBuffer();
				
				b.append( "<html><pre>" );
				
				for( int i = 0; i < header.length; i++ ) {
					for( int j = 0; j < header[i].length; j++ )
						b.append( header[i][j] );
					b.append( "\n" );
				}
				
				b.append( "</pre></html>" );
				
				return new ByteArrayInputStream( b.toString().getBytes() );
			}
			
			public long length() {
				return -1;
			}
		};
	}
}