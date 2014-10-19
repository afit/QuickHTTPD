
package uk.co.lothianproductions.httpd.config;

import uk.co.lothianproductions.util.CoreProperties;

import java.util.Properties;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @author	Aidan Fitzpatrick
 * @version $Revision: 1.4 $, $Date: 2003/12/15 20:43:03 $
 */
public abstract class XMLPluginConfig {

	public final static String CLASSPATH_TAG		= "classPath";
	public final static String PROPERTY_TAG			= "property";
	public final static String PROPERTY_NAME_TAG	= "name";
	public final static String PROPERTY_VALUE_TAG	= "value";
	
	protected String mClassPath;
	protected CoreProperties mProperties = new CoreProperties();
	protected PluginBroker mPluginBroker;
	
	public String getProperty( final String name ) throws ConfigException {
		if( mProperties.get( name ) == null )
			throw new ConfigException( "Property " + name + " is unset for " + toString() );

		return (String) mProperties.get( name );
	}

	public Properties getSubproperties( final String name ) throws ConfigException {
		return mProperties.getSubproperties( name );
	}
	
	public void parseDOM( final Node node ) {
		mClassPath = node.getAttributes().getNamedItem( CLASSPATH_TAG ).getNodeValue();
		
		NodeList properties = node.getChildNodes();
		
		int j = -1;
		Node property;

		while( ++j < properties.getLength() )			
			if( (property = properties.item(j)) != null && PROPERTY_TAG.equals( property.getNodeName() ) )
				mProperties.put(
					property.getAttributes().getNamedItem( PROPERTY_NAME_TAG ).getNodeValue(),
					property.getAttributes().getNamedItem( PROPERTY_VALUE_TAG ).getNodeValue()
				);
	}
	
	public PluginBroker getPluginBroker() {
		return mPluginBroker;
	}
	
	public String toString() {
		return mClassPath;
	}
}
