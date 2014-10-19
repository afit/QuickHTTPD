
package uk.co.lothianproductions.util;

import java.io.IOException;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.FileNotFoundException;
import java.io.File;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Provides dynamic access to application and extended properties. The property
 * file is found using a bootstrap file named
 * &quot;application.core_properties&quot; that is built into the Jar file that
 * this object is contained in. It will look for the property key
 * &quot;application.extended_properties&quot; as a file on the filesystem, and
 * attempt to load that file as the primary properties file. <br><br>
 * 
 * In order to configure extensions to the property file system, users must
 * declare the extension by creating an entry in the global properties file as:
 * <br><br>
 * 
 * <code>application.properties.extension.KEY_FOR_EXTENSION = EXTENSION_FILE</code>
 * <br><br>
 * 
 * To retrieve a property from the global properties, no argument is required
 * on the getProperties method. In order to retrieve a property from a
 * properties extension file, the key value for the extension should be passed
 * as a String. <br><br>
 * 
 * All properties files will be read and cached upon instantiation (first
 * request) of the PropertiesHelper. All property caches will be flushed when
 * the flushProperties method is invoked, or when the global properties file
 * has been seen to be updated. <br><br>
 * 
 * The simplest method to cause the PropertiesHelper to invalidate its caches
 * and reread all properties on a UNIX or Linux machine is to use the
 * &quot;touch&quot; tool on the global properties file. <br><br>
 * 
 * A summary of the properties file types follows. <br><br>
 * 
 * <li><b>Core properties</b>: This is the single-value properties file
 * built into this object's Jar file to bootstrap to the global properties
 * file.</li>
 * 
 * <li><b>Global properties</b>: This is the main properties file that all
 * application values and extensions are referenced from.</li>
 * 
 * <li><b>Extended properties</b>: These are extra properties files. Good
 * examples of these are the ad-system properties and locale-based properties.
 * </li><br><br>
 * 
 * @author	Aidan Fitzpatrick
 * @version $Revision: 1.11 $, $Date: 2003/12/11 17:24:40 $
 */
public final class PropertiesHelper {

	/**
	 * This is the number of tries the helper will take before failing when it
	 * is unable to find the core properties. <br><br>
	 * 
	 * Application servers such as Tomcat, which do not atomically reload
	 * contexts, benefit from this. <br><br>
	 * 
	 * It is safe to try again after.
	 */
	public static final int CORE_SEARCH_TRIES			=	5;

	/**
	 * This represents the delay between tries that the helper will take when
	 * trying to read the core properties.
	 */
	public static final int CORE_SEARCH_DELAY			=	100;

	private static CoreProperties	mProperties;
	
	private static Map mPropertiesExtensions			=	new HashMap();

	private static Object			mLock				=	new Object();
	private static Object			mExtensionsLock		=	new Object();
	
	private static List				mListeners			=	new ArrayList();
	
	private static Logger			mLogger				=	Logger.getLogger( "uk.co.lothianproductions.util.PropertiesHelper" );

	// Protects from instantiation...
	private PropertiesHelper() { }

	/**
	 * Returns the global properties.
	 * 
	 * @return The global properties.
	 */
	public static CoreProperties getProperties() {
		synchronized (mLock) {
			if( mProperties == null ) {
				// Find core properties.
				try {
					InputStream input_stream = null;
					
					int tries = 0;
					
					while( ++tries <= CORE_SEARCH_TRIES && input_stream == null ) {

						if( tries > 0 )
							Thread.sleep( CORE_SEARCH_DELAY );
						
						input_stream = new PropertiesHelper().getClass().getClassLoader().getResourceAsStream("application.core_properties");
					}
					
					if( input_stream == null )
						// Nothing to do but fail.
						throw new IllegalStateException(	"The PropertiesHelper cannot open core properties file \"application.core_properties\". " +
															"Perhaps the relevant ClassLoader is being shut down?" );
					
					mProperties = new CoreProperties( input_stream );
				} catch (IOException e) {
					e.printStackTrace();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				
				// Read in core properties
				String real_config_file = getProperties().getProperty("application.extended_properties");
				
				// Read in global properties
				if( real_config_file != null ) {
					try {
						BufferedInputStream stream = new BufferedInputStream( new FileInputStream(real_config_file) );
						
						if( stream != null ) {
							mProperties = new CoreProperties(stream);
							
							new FileChangeWatcher( new File(real_config_file) ).addListener( new FileChangeListener() {
								public void notifyListener() {
									
									// Flush and re-read primary and extended properties.
									// This is relatively cheap.
									PropertiesHelper.flushProperties();
									PropertiesHelper.flushPropertiesExtensions();
									PropertiesHelper.getProperties();

									mLogger.log( Level.INFO, "Primary properties have changed, notifying listeners." );

									Iterator i = mListeners.iterator();
									
									while( i.hasNext() )
										try {
										((PropertiesListener) i.next()).notifyListener();
										} catch (Exception e) {
											mLogger.log( Level.SEVERE, "A listener threw an exception after being notified of a properties change.", e );
										}
									
								}
							} );
								
						}
					} catch (FileNotFoundException e) {
						mLogger.log( Level.SEVERE, "Unable to find global properties file \"" + real_config_file + "\" specified in the core properties file.", e );
					} catch (IOException e) {
						mLogger.log( Level.SEVERE, "Unable to read global properties file \"" + real_config_file + "\" specified in the core properties file.", e );
					}
				} else
					mLogger.log( Level.INFO, "No global properties file specified in the core properties. Please read the documentation." );
			}
		}
		
		return mProperties;
	}

	/**
	 * Returns the requested properties extension.
	 * 
	 * @param extension_key
	 *            An optional key to reference properties extensions with. If
	 *            the key isn't passed, the properties return will be the core
	 *            application properties.
	 * @return The requested properties extension.
	 */
	public static CoreProperties getProperties(final String extension_key) {
		synchronized (mExtensionsLock) {
			if( mPropertiesExtensions.isEmpty() ) {
				// Read in properties extensions
				Iterator i = getProperties().getSubproperties("application.properties.extension.").keySet().iterator();

				while( i.hasNext() ) {
					String name = (String) i.next();

					try {
						mPropertiesExtensions.put( name, new CoreProperties( 
							new BufferedInputStream( new FileInputStream(
								getProperties().getProperty("application.properties.extension." + name)
							) )
						) );
					} catch (IOException e) {
						mLogger.log( Level.SEVERE, "Unable to read properties extension \"" + name + "\".", e );
					}
				}
			}
		}

		return (CoreProperties) mPropertiesExtensions.get( extension_key );
	}

	/**
	 * Associates a listener to be notified of any changes.
	 */
	public static void addListener( final PropertiesListener listener ) {
		mListeners.add( listener );
	}
	
	/**
	 * Removes a listener from being notified of any changes.
	 */
	public static void removeListener( final PropertiesListener listener ) {
		mListeners.remove( listener );
	}
	
	/**
	 * Flushes cache of global properties.
	 */
	private static void flushProperties() {
		synchronized (mLock) {
			mProperties = null;
		}
	}
	
	/**
	 * Flushes cache of properties extensions.
	 */
	private static void flushPropertiesExtensions() {
		synchronized (mExtensionsLock) {
			mPropertiesExtensions.clear();
		}
	}
}