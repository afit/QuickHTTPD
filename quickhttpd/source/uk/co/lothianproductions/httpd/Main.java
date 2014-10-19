
package uk.co.lothianproductions.httpd;

import uk.co.lothianproductions.httpd.config.PluginBroker;
import uk.co.lothianproductions.httpd.listener.RequestListener;

/**
 * @author	Aidan Fitzpatrick
 * @version $Revision: 1.2 $, $Date: 2003/11/29 14:48:13 $
 */
public final class Main {
	
	public static void main(final String[] args) throws Exception {
		RequestListener listener = PluginBroker.getInstance().getRequestListeners()[0].spawn();	
		
		listener.start();
	}
}
