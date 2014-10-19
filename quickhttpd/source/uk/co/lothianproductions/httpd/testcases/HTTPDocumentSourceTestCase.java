
package uk.co.lothianproductions.httpd.testcases;

import uk.co.lothianproductions.httpd.config.ConfigException;
import uk.co.lothianproductions.httpd.config.PluginBroker;
import uk.co.lothianproductions.httpd.handler.RequestHandler;
import uk.co.lothianproductions.httpd.listener.RequestListener;
import uk.co.lothianproductions.httpd.listener.RequestListenerController;
import uk.co.lothianproductions.util.StringHelper;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.net.Socket;
import java.net.UnknownHostException;

import org.xml.sax.InputSource;

import junit.framework.TestCase;

/**
 * Tests the HTTPDocumentSource by retrieving a single page from Google.
 * 
 * @author	Aidan Fitzpatrick
 * @version $Revision: 1.1 $, $Date: 2004/01/02 18:49:19 $
 */
public class HTTPDocumentSourceTestCase extends TestCase {

	protected final static String TEST_XML =
	"<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
	"<server>" +
	"	<requestListeners>" +
	"		<requestListener classPath=\"uk.co.lothianproductions.httpd.listener.impl.ServerSocketRequestListener\">" +
	"			<property name=\"port\" value=\"8080\" />" +
	"			<property name=\"ceiling\" value=\"0\" />	" +
	"		</requestListener>" +
	"	</requestListeners>" +
	"	<requestHandlers>" +
	"		<requestHandler classPath=\"uk.co.lothianproductions.httpd.handler.impl.SocketRequestHandler\" />" +
	"	</requestHandlers>" +
	"	<documentSources>" +
	"		<documentSource classPath=\"uk.co.lothianproductions.httpd.document.impl.HTTPDocumentSource\">" +
	"			<property name=\"gzip\" value=\"true\" />" +
	"			<property name=\"hostname\" value=\"http://www.google.com\" />" +
	"		</documentSource>" +
	"	</documentSources>" +
	"</server>";
	
	protected RequestListener mListener;
	
	public HTTPDocumentSourceTestCase( final String name ) {
		super( name );
	}

	public void setUp() {
		try {
			PluginBroker broker = new PluginBroker( new InputSource( new StringReader( TEST_XML ) ) );
			mListener = broker.getRequestListeners()[0].spawn();
			RequestListenerController.getInstance().start( mListener );
		} catch (ConfigException e) {
			e.printStackTrace();
			fail( "The test failed due to a configuration exception." );
		}			
	}
	
	public void tearDown() {
		try {
			mListener.stop();
		} catch (IOException e) {
			e.printStackTrace();
			fail( "The listener threw an IO exception whilst stopping." );
		}
	}

	public void testGetConfigurationDocument() {
		
		try {
			Socket socket = new Socket( "localhost", 8080 );
			
			OutputStream out = socket.getOutputStream();
			InputStream in = socket.getInputStream();
			
			out.write( "/".getBytes() );
			out.flush();
			
			String response = StringHelper.toString( in );
			
			socket.close();
			
			assertTrue(
				response.length() > 0
			);
		} catch (UnknownHostException e) {
			e.printStackTrace();
			fail( "The configuration document failed to return." );
		} catch (IOException e) {
			e.printStackTrace();
			fail( "The configuration document failed to return." );
		}
	}
	
	public void testGetNonExistantDocument() {
		
		try {
			Socket socket = new Socket( "localhost", 8080 );
			
			OutputStream out = socket.getOutputStream();
			InputStream in = socket.getInputStream();
			
			out.write( "/404".getBytes() );
			out.flush();
			
			String response = StringHelper.toString( in );
			
			socket.close();
			
			assertEquals(
					new String( RequestHandler.RESPONSE_NOT_FOUND ),
					response
			);
		} catch (UnknownHostException e) {
			e.printStackTrace();
			fail( "The configuration document failed to return." );
		} catch (IOException e) {
			e.printStackTrace();
			fail( "The configuration document failed to return." );
		}
	}
}