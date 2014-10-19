
package uk.co.lothianproductions.httpd.document;

import uk.co.lothianproductions.util.CleverException;

/**
 * @author Aidan Fitzpatrick
 * @version $Revision: 1.1 $, $Date: 2003/12/15 20:45:35 $
 */
public final class DocumentTimeoutException extends CleverException {

	public DocumentTimeoutException( final String message ) {
		super( message );
	}
}