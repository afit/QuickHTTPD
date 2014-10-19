
package uk.co.lothianproductions.util;

import java.util.EventListener;

/**
 * @author	Aidan Fitzpatrick
 * @version $Revision: 1.4 $, $Date: 2003/12/11 14:26:33 $
 */
public abstract class PropertiesListener implements EventListener {

	/**
	 * Notifies the properties service of a change in properties.
	 */
	public abstract void notifyListener() throws Exception;

}