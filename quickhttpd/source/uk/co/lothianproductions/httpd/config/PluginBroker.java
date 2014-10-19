
package uk.co.lothianproductions.httpd.config;

import uk.co.lothianproductions.httpd.document.DocumentSourceXMLPluginConfig;
import uk.co.lothianproductions.httpd.handler.RequestHandlerXMLPluginConfig;
import uk.co.lothianproductions.httpd.listener.RequestListenerXMLPluginConfig;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.apache.xerces.parsers.DOMParser;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * @author	Aidan Fitzpatrick
 * @version $Revision: 1.4 $, $Date: 2003/12/15 20:43:03 $
 */
public final class PluginBroker  {

	/**
	 * The default configuration file's name.
	 */
	public final static String DEFAULT_CONFIG			= "config.xml";
	
	public final static String REQUEST_LISTENERS_TAG	= "requestListeners";
	public final static String REQUEST_HANDLERS_TAG		= "requestHandlers";
	public final static String DOCUMENT_SOURCES_TAG		= "documentSources";
	
	public final static String REQUEST_LISTENER_TAG		= "requestListener";
	public final static String REQUEST_HANDLER_TAG		= "requestHandler";
	public final static String DOCUMENT_SOURCE_TAG		= "documentSource";

	public static PluginBroker mInstance;
	
	public static synchronized PluginBroker getInstance() throws ConfigException {
		if( mInstance == null )
			mInstance = new PluginBroker(
				new InputSource( new PluginBroker().getClass().getClassLoader().getResourceAsStream( DEFAULT_CONFIG ) )
			);
		
		return mInstance;
	}
	
	protected RequestListenerXMLPluginConfig[] mRequestListeners;
	protected RequestHandlerXMLPluginConfig[] mRequestHandlers;
	protected DocumentSourceXMLPluginConfig[] mDocumentSources;
	
	protected PluginBroker() { }
	
	public PluginBroker( final InputSource data ) throws ConfigException {
		
		DOMParser parser = new DOMParser();

		try {
			parser.parse( data );
		} catch (IOException e) {
			throw new ConfigException( "The parser encountered an IO exception whilst parsing DOM data.", e );
		} catch (SAXException e) {
			throw new ConfigException( "The parser encountered a SAX exception whilst parsing DOM data.", e );
		}

		// Get the parsed document
		Document document = parser.getDocument();

		// ...and the root element, which happens to be "server".
		Element root = document.getDocumentElement();
		
		// Extract server components.
		mRequestListeners = (RequestListenerXMLPluginConfig[]) parseDOM(
			root, REQUEST_LISTENERS_TAG, REQUEST_LISTENER_TAG, RequestListenerXMLPluginConfig.class
		).toArray( new RequestListenerXMLPluginConfig[] { } );
		
		mRequestHandlers = (RequestHandlerXMLPluginConfig[]) parseDOM(
			root, REQUEST_HANDLERS_TAG, REQUEST_HANDLER_TAG, RequestHandlerXMLPluginConfig.class
		).toArray( new RequestHandlerXMLPluginConfig[] { } );
		
		mDocumentSources = (DocumentSourceXMLPluginConfig[]) parseDOM(
			root, DOCUMENT_SOURCES_TAG, DOCUMENT_SOURCE_TAG, DocumentSourceXMLPluginConfig.class
		).toArray( new DocumentSourceXMLPluginConfig[] { } );
	}
	
	public List parseDOM( final Element root, final String collectionTag, final String individualTag, final Class xmlPluginConfig ) throws ConfigException {
		
		// Get root's elements.
		NodeList listeners = root.getElementsByTagName( collectionTag ).item(0).getChildNodes();
		
		if( listeners.getLength() < 1 )
			throw new ConfigException( "You must specify at least one " + individualTag + " in your configuration." );
		
		List listenerConfig = new ArrayList();
		
		int i = -1;
		Node node;
		
		while( ++i < listeners.getLength() )
			if( (node = listeners.item(i)) != null && individualTag.equals( node.getNodeName() ) ) {
				try {
					listenerConfig.add( xmlPluginConfig.getConstructor( new Class[] { Node.class, PluginBroker.class } ).newInstance( new Object[] { node, this } ) );
				} catch (SecurityException e) {
					throw new ConfigException( "Failed to construct new XMLPluginConfig.", e );
				} catch (NoSuchMethodException e) {
					throw new ConfigException( "Failed to construct new XMLPluginConfig.", e );
				} catch (IllegalArgumentException e) {
					throw new ConfigException( "Failed to construct new XMLPluginConfig.", e );
				} catch (InstantiationException e) {
					throw new ConfigException( "Failed to construct new XMLPluginConfig.", e );
				} catch (IllegalAccessException e) {
					throw new ConfigException( "Failed to construct new XMLPluginConfig.", e );
				} catch (InvocationTargetException e) {
					throw new ConfigException( "Failed to construct new XMLPluginConfig.", e );
				}
			}

		i = -1;
					
		return listenerConfig;
	}
	
	public RequestListenerXMLPluginConfig[] getRequestListeners() {
		return mRequestListeners; 
	}

	public RequestHandlerXMLPluginConfig[] getRequestHandlers() {
		return mRequestHandlers;
	}
	
	public DocumentSourceXMLPluginConfig[] getDocumentSources() {
		return mDocumentSources;
	}	
}