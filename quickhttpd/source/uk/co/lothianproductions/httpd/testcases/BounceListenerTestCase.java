
package uk.co.lothianproductions.httpd.testcases;

import uk.co.lothianproductions.httpd.config.ConfigException;
import uk.co.lothianproductions.httpd.config.PluginBroker;
import uk.co.lothianproductions.httpd.listener.RequestListener;
import uk.co.lothianproductions.httpd.listener.RequestListenerController;

import java.io.IOException;
import java.io.StringReader;

import org.xml.sax.InputSource;

import junit.framework.TestCase;

/**
 * @author	Aidan Fitzpatrick
 * @version	$Revision: 1.1 $, $Date: 2003/12/10 13:47:22 $
 */
public class BounceListenerTestCase extends TestCase {

	public BounceListenerTestCase( final String name ) {
		super( name );
	}

	public void testBounceListener() {
		RequestListener listener = null;
		
		try {
			PluginBroker broker = new PluginBroker( new InputSource( new StringReader(
				ResourceDocumentSourceTestCase.TEST_XML
			) ) );
			listener = broker.getRequestListeners()[0].spawn();
			RequestListenerController.getInstance().start( listener );
		} catch (ConfigException e) {
			e.printStackTrace();
			fail( "The test failed due to a configuration exception." );
		}			

		try {
			listener.stop();
		} catch (IOException e) {
			e.printStackTrace();
			fail( "The listener threw an IO exception whilst stopping." );
		}
	}
	
}