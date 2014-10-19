
package uk.co.lothianproductions.httpd.handler;

import uk.co.lothianproductions.httpd.config.ConfigException;
import uk.co.lothianproductions.httpd.document.Document;
import uk.co.lothianproductions.httpd.document.DocumentNotFoundException;
import uk.co.lothianproductions.httpd.document.DocumentRenderException;
import uk.co.lothianproductions.httpd.document.DocumentRetrievalException;
import uk.co.lothianproductions.httpd.document.DocumentTimeoutException;

import java.io.IOException;
import java.util.logging.Logger;

/**
 * Defines the acceptable interface for a RequestHandler.
 * <br><br>
 * 
 * A RequestHandler handles and interprets requests that are passed to it by a
 * RequestListener.
 * 
 * @author	Aidan Fitzpatrick
 * @version $Revision: 1.7 $, $Date: 2004/01/02 14:23:26 $
 */
public interface RequestHandler extends Runnable {

	public final static char[] RESPONSE_OKAY			= "Okay.".toCharArray();
	public final static char[] RESPONSE_NOT_FOUND		= "Not found.".toCharArray();
	public final static char[] RESPONSE_ERROR			= "Error processing.".toCharArray();
	public final static char[] RESPONSE_CANT_SERVICE	= "Can't service request.".toCharArray();
	
	public final static Logger mLogger = Logger.getLogger("uk.co.lothianproductions.httpd.handler.RequestHandler");
		
	public void handleRequest();
	
	public Document handleProcessing(final char[] method, final char[] uri, final char[][] header,
									 final char[] message) throws ConfigException, DocumentNotFoundException, DocumentRenderException, DocumentRetrievalException, DocumentTimeoutException;
		
	public void handleResponse(final char[] method, final char[] uri, final char[][] header, 
							   final char[] message) throws IOException, ConfigException;
}