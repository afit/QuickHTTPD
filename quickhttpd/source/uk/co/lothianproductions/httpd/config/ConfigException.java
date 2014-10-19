
package uk.co.lothianproductions.httpd.config;

import uk.co.lothianproductions.util.CleverException;

/**
 * @author	Aidan Fitzpatrick
 * @version $Revision: 1.2 $, $Date: 2003/11/29 14:48:13 $
 */
public class ConfigException extends CleverException {

	public ConfigException( final String message ) {
		super( message );
	}

	public ConfigException( final String message, final Throwable e ) {
		super( message, e );
	}
}
