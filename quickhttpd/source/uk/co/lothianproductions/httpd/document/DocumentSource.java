
package uk.co.lothianproductions.httpd.document;

import uk.co.lothianproductions.httpd.config.ConfigException;

/**
 * The DocumentSource interface describes how the document lookup mechanism
 * should be implemented.
 * 
 * @author	Aidan Fitzpatrick
 * @version $Revision: 1.4 $, $Date: 2003/12/15 20:45:35 $
 */
public interface DocumentSource {

	// FIXME need to abort and timeout documentsource requests.
	
	public Document getDocument( final char[] identifier ) throws ConfigException, 
		DocumentNotFoundException, DocumentTimeoutException, DocumentRetrievalException;

}