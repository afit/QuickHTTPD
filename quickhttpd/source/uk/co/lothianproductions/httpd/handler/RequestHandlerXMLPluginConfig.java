
package uk.co.lothianproductions.httpd.handler;

import uk.co.lothianproductions.httpd.config.ConfigException;
import uk.co.lothianproductions.httpd.config.PluginBroker;
import uk.co.lothianproductions.httpd.config.XMLPluginConfig;

import java.lang.reflect.InvocationTargetException;
import java.net.Socket;

import org.w3c.dom.Node;

/**
 * @author	Aidan Fitzpatrick
 * @version $Revision: 1.4 $, $Date: 2003/12/15 20:43:03 $
 */
public class RequestHandlerXMLPluginConfig extends XMLPluginConfig {

	public RequestHandlerXMLPluginConfig( final Node node, final PluginBroker broker ) {
		parseDOM( node );
		mPluginBroker = broker;
	}
	
	public RequestHandler spawn( final Socket server ) throws ConfigException {
		try {
			return (RequestHandler) Class.forName( mClassPath ).getConstructor( new Class[] { XMLPluginConfig.class, Socket.class } ).newInstance( new Object[] { this, server } );
		} catch (ClassNotFoundException e) {
			throw new ConfigException( "Failed to classload the specified request handler: \"" +  mClassPath + "\".", e );
		} catch (NoSuchMethodException e) {
			throw new ConfigException( "Failed to classload the specified request handler: \"" +  mClassPath + "\".", e );
		} catch (InvocationTargetException e) {
			throw new ConfigException( "Failed to classload the specified request handler: \"" +  mClassPath + "\".", e );
		} catch (IllegalAccessException e) {
			throw new ConfigException( "Failed to access the specified request handler: \"" +  mClassPath + "\".", e );
		} catch (InstantiationException e) {
			throw new ConfigException( "Failed to instantiate the specified request handler: \"" +  mClassPath + "\".", e );
		}
	}
}
