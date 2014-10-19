
package uk.co.lothianproductions.httpd.document.impl;

import uk.co.lothianproductions.httpd.config.ConfigException;
import uk.co.lothianproductions.httpd.config.XMLPluginConfig;
import uk.co.lothianproductions.httpd.document.Document;
import uk.co.lothianproductions.httpd.document.DocumentNotFoundException;
import uk.co.lothianproductions.httpd.document.DocumentSource;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

/**
 * Simple DocumentSource for returning property values as documents.
 * 
 * @author	Aidan Fitzpatrick
 * @version	$Revision: 1.5 $, $Date: 2004/01/02 18:58:45 $
 */
public class PropertiesDocumentSource implements DocumentSource {

	protected XMLPluginConfig mXMLPluginConfig;
	
	public PropertiesDocumentSource( final XMLPluginConfig config ) {
		mXMLPluginConfig = config;
	}
	
	public Document getDocument( final char[] identifier ) throws DocumentNotFoundException {
		
		try {
			final String document = mXMLPluginConfig.getProperty( new String( identifier ) );

			return new Document() {
				public InputStream render( final String[][] parameters ) {
					// Stream should convert back to bytes in same charset
					// as properties are read.
					return new ByteArrayInputStream( document.getBytes() );
				}
				
				public long length() {
					return document.length();
				}
			};
			
		} catch (ConfigException e) {
			throw new DocumentNotFoundException( "Document not found.", e );
		}	
		
	}
	
}
