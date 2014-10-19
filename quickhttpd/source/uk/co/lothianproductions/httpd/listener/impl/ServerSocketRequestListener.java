
package uk.co.lothianproductions.httpd.listener.impl;

import uk.co.lothianproductions.httpd.config.ConfigException;
import uk.co.lothianproductions.httpd.config.XMLPluginConfig;
import uk.co.lothianproductions.httpd.handler.RequestHandler;
import uk.co.lothianproductions.httpd.listener.RequestListener;

import java.io.IOException;

import java.net.ServerSocket;
import java.net.Socket;

/**
 * @author	Aidan Fitzpatrick
 * @version $Revision: 1.1 $, $Date: 2003/12/10 13:47:22 $
 */
public final class ServerSocketRequestListener implements RequestListener {
		
	protected ServerSocket mListener;
	protected XMLPluginConfig mXMLPluginConfig;
	
	public ServerSocketRequestListener( final XMLPluginConfig config ) {
		mXMLPluginConfig = config;
	}
	
	public void start() throws IOException, ConfigException {

		try {
			mListener = new ServerSocket( Integer.parseInt( mXMLPluginConfig.getProperty( "port" ) ) );
		} catch (IOException e) {
			throw e;
		}

		Socket server;

		do {
			try {
				server = mListener.accept();
			} catch (IOException e) {
				throw e;
			}

			// FIXME Limit number of handler threads?
			if( Integer.parseInt( mXMLPluginConfig.getProperty( "ceiling" ) ) > 0 )
				throw new UnsupportedOperationException( "The ServerSocketRequestListener cannot work with a non-zero ceiling value." ); 
				
			RequestHandler handler = mXMLPluginConfig.getPluginBroker().getRequestHandlers()[0].spawn( server );
						
			Thread t = new Thread( handler );
			t.start();
		} while( true );
	}

	public void stop() throws IOException {
		if( mListener != null )
			mListener.close();
	}

	public String getVersionInformation() {
		return "$Revision: 1.1 $, $Date: 2003/12/10 13:47:22 $";
	}
}