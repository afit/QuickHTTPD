
package uk.co.lothianproductions.httpd.document.impl;

import uk.co.lothianproductions.httpd.config.XMLPluginConfig;
import uk.co.lothianproductions.httpd.document.Document;
import uk.co.lothianproductions.httpd.document.DocumentNotFoundException;
import uk.co.lothianproductions.httpd.document.DocumentRenderException;
import uk.co.lothianproductions.httpd.document.DocumentSource;

import java.io.InputStream;

/**
 * Simple DocumentSource for returning classloadable resources
 * as documents.
 * 
 * @author	Aidan Fitzpatrick
 * @version $Revision: 1.4 $, $Date: 2004/01/02 18:58:45 $
 */
public class ResourceDocumentSource implements DocumentSource {

	public ResourceDocumentSource( final XMLPluginConfig config ) { }
	
	public Document getDocument( final char[] identifier ) throws DocumentNotFoundException {
		
		final InputStream mStream = this.getClass().getClassLoader().getResourceAsStream( new String( identifier, 1, identifier.length - 1 ) ); 
		
		if( mStream == null )
			throw new DocumentNotFoundException( "Document not found." );
			
		return new Document() {
			public InputStream render( final String[][] parameters ) throws DocumentRenderException {
				return mStream;
			}
			
			public long length() {
				return -1;
			}
		};
		
	}
	
}
