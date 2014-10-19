
package uk.co.lothianproductions.httpd.document.impl;

import uk.co.lothianproductions.httpd.config.XMLPluginConfig;
import uk.co.lothianproductions.httpd.document.Document;
import uk.co.lothianproductions.httpd.document.DocumentSource;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

/**
 * This DocumentSource returns 0-byte empty Documents. Its
 * primary use is for testing.
 * 
 * @author	Aidan Fitzpatrick
 * @version $Revision: 1.4 $, $Date: 2004/01/02 18:58:45 $
 */
public class EmptyDocumentSource implements DocumentSource {

	public EmptyDocumentSource( final XMLPluginConfig config ) { }
	
	public Document getDocument( final char[] identifier ) {
		return new Document() {
			public InputStream render( final String[][] parameters ) {
				return new ByteArrayInputStream( new byte[] { } );
			}
			
			public long length() {
				return 0;
			}
		};
		
	}
	
}
