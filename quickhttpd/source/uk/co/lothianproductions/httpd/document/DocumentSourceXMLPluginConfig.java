
package uk.co.lothianproductions.httpd.document;

import uk.co.lothianproductions.httpd.config.ConfigException;
import uk.co.lothianproductions.httpd.config.PluginBroker;
import uk.co.lothianproductions.httpd.config.XMLPluginConfig;

import java.lang.reflect.InvocationTargetException;

import org.w3c.dom.Node;

/**
 * @author	Aidan Fitzpatrick
 * @version $Revision: 1.4 $, $Date: 2003/12/15 20:43:03 $
 */
public class DocumentSourceXMLPluginConfig extends XMLPluginConfig {

	public DocumentSourceXMLPluginConfig( final Node node, final PluginBroker broker ) {
		parseDOM( node );
		mPluginBroker = broker;
	}
	
	public DocumentSource spawn() throws ConfigException {
		try {
			return (DocumentSource) Class.forName( mClassPath ).getConstructor( new Class[] { XMLPluginConfig.class } ).newInstance( new Object[] { this } ); 		
		} catch (ClassNotFoundException e) {
			throw new ConfigException( "Failed to classload the specified document source: \"" +  mClassPath + "\".", e );
		} catch (NoSuchMethodException e) {
			throw new ConfigException( "Failed to classload the specified document source: \"" +  mClassPath + "\".", e );
		} catch (InvocationTargetException e) {
			throw new ConfigException( "Failed to classload the specified document source: \"" +  mClassPath + "\".", e );
		} catch (IllegalAccessException e) {
			throw new ConfigException( "Failed to access the specified document source: \"" +  mClassPath + "\".", e );
		} catch (InstantiationException e) {
			throw new ConfigException( "Failed to instantiate the specified document source: \"" +  mClassPath + "\".", e );
		}
	}
	
}
