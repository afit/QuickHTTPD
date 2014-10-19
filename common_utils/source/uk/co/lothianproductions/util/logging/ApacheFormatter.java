
package uk.co.lothianproductions.util.logging;

import uk.co.lothianproductions.util.CachedDate;

import java.io.IOException;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Filter;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

/**
 * Log formatter for producing Apache HTTPd "style" access log
 * and error files. These files can then be parsed with Apache
 * log-parsing tools.
 * 
 * @author	Aidan Fitzpatrick
 * @version	$Revision: 1.1 $, $Date: 2003/12/11 14:26:33 $
 */
public class ApacheFormatter extends Formatter {

	public String format( final LogRecord record ) {
		StringBuffer buffer = new StringBuffer( 24 );
		
		buffer.append( '[' );
		buffer.append( CachedDate.getInstance().getSmallDateChars() );
		buffer.append( ']' );
		buffer.append( ' ' );
		buffer.append( record.getMessage() );
		buffer.append( '\n' );
		
		return buffer.toString();
	}

	/**
	 * Configures the passed logger to use an ApacheFormatter when
	 * writing its log. This involves use of an access_log and an
	 * error_log file.
	 *
	 * The logger will not log recursively once configured.
	 *
	 * If the console parameter is passed as true, output from both
	 * the access and error logs will be passed back to the console.
	 */
	public static void configureLogger( final Logger logger, final boolean console ) throws SecurityException, IOException {
		
		// Wire up Apache-style formatter.
		Handler access = new FileHandler( "%t/" + logger.getName() + "-access%g_log", true );
		Handler error = new FileHandler( "%t/" + logger.getName() + "-error%g_log", true );
		Handler out = new ConsoleHandler();
		
		Formatter formatter = new ApacheFormatter();
		
		access.setFormatter( formatter );
		error.setFormatter( formatter );
		out.setFormatter( formatter );
		
		// Only log info or nicer messages.
		access.setFilter( new Filter() {
			public boolean isLoggable( final LogRecord record ) {
				return record.getLevel().intValue() >= Level.INFO.intValue();
			}
		} );
		
		// Only log error messages.
		error.setFilter( new Filter() {
			public boolean isLoggable( final LogRecord record ) {
				return record.getLevel().intValue() < Level.INFO.intValue();
			}
		} );
		
		logger.addHandler( access );
		logger.addHandler( error );
		
		if( console ) {
			// Add view to console and Apache logs.
			logger.addHandler( out );
			logger.addHandler( new ConsoleHandler() );
		}
		
		// Prevent logger from logging recursively.
		logger.setUseParentHandlers( false );
	}
	
}
