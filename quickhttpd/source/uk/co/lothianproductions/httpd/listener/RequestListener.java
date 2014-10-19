
package uk.co.lothianproductions.httpd.listener;

import uk.co.lothianproductions.httpd.config.ConfigException;
import uk.co.lothianproductions.util.VersionedObject;

import java.io.IOException;

/**
 * Defines the acceptable interface for a RequestListener.
 * <br><br>
 * 
 * A RequestListener listens for content requests on a before handing them on
 * to a RequestHandler.
 * 
 * @author	Aidan Fitzpatrick
 * @version $Revision: 1.4 $, $Date: 2003/11/29 14:48:40 $
 */
public interface RequestListener extends VersionedObject {
	
	public void start() throws IOException, ConfigException;
	
	public void stop() throws IOException;
}