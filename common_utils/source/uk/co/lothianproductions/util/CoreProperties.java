
package uk.co.lothianproductions.util;

import java.io.InputStream;
import java.io.IOException;

import java.util.Properties;
import java.util.Enumeration;

/**
 * Extension of properties with helper calls.
 * 
 * @author	Aidan Fitzpatrick
 * @version $Revision: 1.3 $, $Date: 2003/12/16 14:30:01 $
 */
public class CoreProperties extends Properties {

	public CoreProperties() { }

	public CoreProperties(final Properties preface) {
		super(preface);
	}

	public CoreProperties(final InputStream stream) throws IOException {
		load(stream);
	}

	public void setDefaults(final Properties preface) {
		defaults = preface;
	}

	/**
	 * Returns a subset of the CoreProperties with all keyed values beginning
	 * with prefix.
	 * 
	 * @param prefix
	 *            The delimiting key filter to apply to the properties.
	 */
	public CoreProperties getSubproperties(final String prefix) {
		
		// We should provide a sorted property-names call.
		Enumeration keys				= propertyNames();
		CoreProperties subproperties	= new CoreProperties();

		while( keys.hasMoreElements() ) {
			String key					= (String) keys.nextElement();
			
			if( key.startsWith(prefix) ) {
				String subkey			= key.substring( prefix.length() );
				
				if(subkey.length() > 0)
					subproperties.put( subkey, getProperty(key) );
			}
		}
		
		return subproperties;
	}

	/**
	 * Wrapper around Properties.getProperty to lend some consistency to
	 * getInteger.
	 * 
	 * @param key
	 *            The key to retrieve the String from.
	 */
	public String getString(final String key) {
		return getProperty(key);
	}

	/**
	 * Returns an int from the properties. Will return default value if the
	 * value returned from properties is empty or cannot be converted to an
	 * int.
	 * 
	 * @param key
	 *            The key to retrieve the int from.
	 * @param default_value
	 *            The default value to return, if the properties value cannot
	 *            be converted to an int.
	 */
	public int getInteger(final String key, final int default_value) {
		String value = getProperty( key );

		if( value == null || value.length() == 0 )
			return default_value;
		
		try {
			return Integer.parseInt( value );
		} catch (NumberFormatException e) {
			return default_value;
		}
	}

	/**
	 * Returns an int from the properties, with -1 as the default value.
	 * 
	 * @param key
	 *            The key to retrieve the int from.
	 */
	public int getInteger(final String key) {
		return getInteger( key, -1 );
	}
}