
package uk.co.lothianproductions.util;

import java.util.EventListener;

/**
 * Provides listener functionality for use with the FileChangeWatcher.
 * 
 * @author	Aidan Fitzpatrick
 * @version $Revision: 1.2 $, $Date: 2003/12/11 14:26:33 $
 */
public abstract class FileChangeListener implements EventListener {

	/**
	 * Notifies a monitor of a file change.
	 */
	public abstract void notifyListener();
	
}
