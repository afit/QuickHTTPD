
package uk.co.lothianproductions.util;

import java.io.File;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * The FileChangeWatcher monitors files for modifications to their
 * modification date. When a file's modification date is updated,
 * any associated FileChangeListeners are notified immediately.
 * 
 * @author	Aidan Fitzpatrick
 * @version $Revision: 1.2 $, $Date: 2003/12/11 14:26:33 $
 */
public class FileChangeWatcher {

	/**
	 *	This is the default time in milliseconds for the interval
	 *	between file check polls. Value is 5,000ms.
	 */
	public static final int DEFAULT_INTERVAL			=	5000;
	
	protected File mFile;
	protected int mFileStamp;
	protected List mListeners = new ArrayList();

	/**
	 * Creates a new FCW on the given file using the default
	 * check interval.
	 */
	public FileChangeWatcher( final File file ) {
		this( file, DEFAULT_INTERVAL );
	}
	
	/**
	 * Creates a new FCW on the given file using the given check
	 * interval in milliseconds.
	 */
	public FileChangeWatcher( final File file, final int interval ) {
		
		mFile = file;
		
		Thread mWatcher = new Thread(
				new Runnable() {
					public void run() {
						do {
							try {
								Thread.sleep( DEFAULT_INTERVAL );
							} catch (InterruptedException e) {
								// Sleep is interrupted.
							}
							
							int stamp;
							
							if( mFile != null &&
								mFileStamp != 0 &&
								(stamp = mFileStamp) < mFile.lastModified() ) {
								
								mFileStamp = stamp;
								
								Iterator i = mListeners.iterator();
								
								while( i.hasNext() )
									((FileChangeListener) i.next()).notifyListener();
							}
						} while( true );
					}
				}
		);
		
		// Set the watcher as a daemon so it doesn't
		// prevent the JVM from ending naturally.
		mWatcher.setDaemon(true);
		
		mWatcher.start();
	}
	
	/**
	 * Removes a listener from being notified of any changes.
	 */
	public void removeListener( final FileChangeListener listener ) {
		mListeners.remove( listener );
	}

	/**
	 * Associates a listener to be notified of any changes.
	 */
	public void addListener( final FileChangeListener listener ) {
		mListeners.add( listener );
	}

}
