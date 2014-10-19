
package uk.co.lothianproductions.httpd.document;

import uk.co.lothianproductions.util.CleverException;

/**
 * @author	Aidan Fitzpatrick
 * @version	$Revision: 1.1 $, $Date: 2003/12/10 13:47:22 $
 */
public class DocumentNotFoundException extends CleverException {
	
	public DocumentNotFoundException( final String message ) {
		super( message );
	}

	public DocumentNotFoundException( final String message, final Throwable e ) {
		super( message, e );
	}
}
