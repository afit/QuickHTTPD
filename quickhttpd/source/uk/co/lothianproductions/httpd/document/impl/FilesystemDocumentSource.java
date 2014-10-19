
package uk.co.lothianproductions.httpd.document.impl;

import uk.co.lothianproductions.httpd.config.ConfigException;
import uk.co.lothianproductions.httpd.config.XMLPluginConfig;
import uk.co.lothianproductions.httpd.document.Document;
import uk.co.lothianproductions.httpd.document.DocumentNotFoundException;
import uk.co.lothianproductions.httpd.document.DocumentRenderException;
import uk.co.lothianproductions.httpd.document.DocumentRetrievalException;
import uk.co.lothianproductions.httpd.document.DocumentSource;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/**
 * The FilesystemDocumentSource provides a simple mechanism
 * for retrieving files from a filesystem as Documents.
 * 
 * @author	Aidan Fitzpatrick
 * @version $Revision: 1.4 $, $Date: 2004/01/02 18:58:45 $
 */
public class FilesystemDocumentSource implements DocumentSource {

	protected XMLPluginConfig mXMLPluginConfig;
	
	public FilesystemDocumentSource( final XMLPluginConfig config ) {
		mXMLPluginConfig = config;
	}
	
	public Document getDocument( final char[] identifier ) throws ConfigException, DocumentNotFoundException, DocumentRetrievalException {
	
		try {
			final FileInputStream stream = new FileInputStream( mXMLPluginConfig.getProperty( "documentRoot" ) + new String( identifier ) );			
			final long length = stream.getChannel().size();
		
			if( stream == null )
				throw new DocumentNotFoundException( "Document not found." );
			
			return new Document() {
				public InputStream render( final String[][] parameters ) throws DocumentRenderException {
					return stream;
				}
				
				public long length() {
					return length;
				}
			};
		} catch (FileNotFoundException e) {
			throw new DocumentNotFoundException( "Document not found." );
		} catch (IOException e) {
			throw new DocumentRetrievalException( "Failed to measure size of document.", e );
		}
		
	}
	
}
