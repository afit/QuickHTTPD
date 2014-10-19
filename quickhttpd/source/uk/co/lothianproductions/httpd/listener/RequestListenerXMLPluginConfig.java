
package uk.co.lothianproductions.httpd.listener;

import uk.co.lothianproductions.httpd.config.ConfigException;
import uk.co.lothianproductions.httpd.config.PluginBroker;
import uk.co.lothianproductions.httpd.config.XMLPluginConfig;

import java.lang.reflect.InvocationTargetException;

import org.w3c.dom.Node;

/**
 * @author	Aidan Fitzpatrick
 * @version $Revision: 1.4 $, $Date: 2003/12/15 20:43:03 $
 */
public class RequestListenerXMLPluginConfig extends XMLPluginConfig {

	public RequestListenerXMLPluginConfig( final Node node, final PluginBroker broker ) {
		parseDOM( node );
		mPluginBroker = broker;
	}

	public RequestListener spawn() throws ConfigException {
		try {
			return (RequestListener) Class.forName( mClassPath ).getConstructor( new Class[] { XMLPluginConfig.class } ).newInstance( new Object[] { this } ); 		
		} catch (ClassNotFoundException e) {
			throw new ConfigException( "Failed to classload the specified request listener: \"" +  mClassPath + "\".", e );
		} catch (NoSuchMethodException e) {
			throw new ConfigException( "Failed to classload the specified request listener: \"" +  mClassPath + "\".", e );
		} catch (InvocationTargetException e) {
			throw new ConfigException( "Failed to classload the specified request listener: \"" +  mClassPath + "\".", e );
		} catch (IllegalAccessException e) {
			throw new ConfigException( "Failed to access the specified request listener: \"" +  mClassPath + "\".", e );
		} catch (InstantiationException e) {
			throw new ConfigException( "Failed to instantiate the specified request listener: \"" +  mClassPath + "\".", e );
		}
	}
}
