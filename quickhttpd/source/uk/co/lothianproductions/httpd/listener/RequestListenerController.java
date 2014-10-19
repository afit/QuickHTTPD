
package uk.co.lothianproductions.httpd.listener;

import uk.co.lothianproductions.httpd.config.ConfigException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author	Aidan Fitzpatrick
 * @version $Revision: 1.2 $, $Date: 2003/11/29 14:49:54 $
 */
public class RequestListenerController {

	public static RequestListenerController mInstance;
	
	public static synchronized RequestListenerController getInstance() {
		if( mInstance == null )
			mInstance = new RequestListenerController();
		
		return mInstance;
	}
	
	protected List mRequestListeners = new ArrayList();
	protected Map mRequestListenerFailures = new HashMap();
	
	private RequestListenerController() { }
	
	public void start( final RequestListener listener ) {
			
		Thread t = new Thread(
			new Runnable() {
				public void run() {
					try {
						listener.start();
					} catch (ConfigException e) {
						RequestListenerController.getInstance().stop( listener, e );
					} catch (IOException e) {
						RequestListenerController.getInstance().stop( listener, e );
					}
				}
			}
		);
		
		t.start();
		mRequestListeners.add( listener );
		
	}
	
	public boolean isRunning( final RequestListener listener ) throws ConfigException, IOException {
		if( mRequestListeners.contains( listener ) )
			return true;
		
		if( mRequestListenerFailures.get( listener ) == null )
			return false;
		
		if( mRequestListenerFailures.get( listener ) instanceof ConfigException )
			throw (ConfigException) mRequestListenerFailures.get( listener );
		else
			throw (IOException) mRequestListenerFailures.get( listener );
	}
	
	private void stop( final RequestListener listener, final Exception e ) {
		mRequestListenerFailures.put( listener, e );
		mRequestListeners.remove( listener );
	}
	
}
