
package uk.co.lothianproductions.httpd.document.impl;

import uk.co.lothianproductions.httpd.config.ConfigException;
import uk.co.lothianproductions.httpd.config.XMLPluginConfig;
import uk.co.lothianproductions.httpd.document.Document;
import uk.co.lothianproductions.httpd.document.DocumentNotFoundException;
import uk.co.lothianproductions.httpd.document.DocumentRetrievalException;
import uk.co.lothianproductions.httpd.document.DocumentSource;
import uk.co.lothianproductions.httpd.document.DocumentTimeoutException;
import uk.co.lothianproductions.util.HTTPHelper;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.zip.GZIPInputStream;

/**
 * @author	Aidan Fitzpatrick
 * @version	$Revision: 1.5 $, $Date: 2004/01/02 18:58:45 $
 */
public class HTTPDocumentSource implements DocumentSource {

	/**
	 * The user-agent identifier to use when requesting pages.
	 */
	public final static String USER_AGENT			=	"Lothian Productions' Precacher";
	
	protected XMLPluginConfig mXMLPluginConfig;
	
	public HTTPDocumentSource( final XMLPluginConfig config ) {
		mXMLPluginConfig = config;
	}
	
	public Document getDocument( final char[] identifier ) throws DocumentTimeoutException, DocumentRetrievalException, DocumentNotFoundException, ConfigException {
		
		URL page;
		String harveststring = new String( HTTPHelper.httpEncode( identifier ) );
		
		try {
			if( harveststring.indexOf(':') > -1 )
				page = new URL( harveststring );
			else
				page = new URL( mXMLPluginConfig.getProperty("hostname") + harveststring );
		} catch (MalformedURLException e) {
			throw new DocumentRetrievalException( "Failed to interpret misformed URL: \"" + harveststring + "\".", e );
		}

		InputStream in;
		URLConnection connection;
		HttpURLConnection.setFollowRedirects( false );
		
		try {
			connection = page.openConnection();
			
			connection.setRequestProperty( "User-Agent", USER_AGENT );
			
			// Inform server that we can cope with gzip-encoded content.
			if( Boolean.getBoolean( mXMLPluginConfig.getProperty("gzip") ) )
				connection.setRequestProperty( "Accept-Encoding", "gzip" );
			
			in = connection.getInputStream();
		} catch (FileNotFoundException e) {
			// Sun's 1.4 JVM will throw an FNFE on a 404, although this is not enforced
			// by the JVM implementation contract.
			throw new DocumentNotFoundException( "Document did not exist.", e );
		} catch (ConnectException e) {
			throw new DocumentRetrievalException( "Failed to open stream to URL: \"" + page.toString() + "\" as the connection was refused.", e );
		} catch (IOException e) {
			throw new DocumentRetrievalException( "Failed to open stream to URL: \"" + page.toString() + "\". Perhaps the server returned a 500.", e );
		}

		// FIXME Forward on response codes, somehow 
		// FIXME hardcoded characterset reference
		// System.out.println( connection.getResponseCode() + "" );
		
		// FIXME content length invalid if we decode gzip on the fly
		final int length = connection.getContentLength();
		
		String encoding = connection.getContentEncoding();
		
		if( encoding != null && encoding.equals( "gzip" ) )
			try {
				in = new GZIPInputStream( in );
			} catch (IOException e) {
				// Handles IOException, as thrown by attempt to read gzipped input.
				throw new DocumentRetrievalException( "Failed to decompress data from: \"" + page.toString() + "\". The GZIPped response appeared invalid.", e );
			}
		
		final InputStream mStream = in;
		
		return new Document() {		
			public InputStream render( final String[][] parameters ) {
				return mStream;
			}
			
			public long length() {
				return length;
			}
		};
		
	}
}
