
package uk.co.lothianproductions.util;

import java.io.PrintStream;
import java.io.PrintWriter;

/**
 * An abstract subclass of Exception which provides for clearer
 * rendering of Exception traces as well as easier attachment of
 * cause Exceptions.
 * 
 * @author	Aidan Fitzpatrick
 * @version $Revision: 1.5 $, $Date: 2003/12/11 14:26:33 $
 */
public abstract class CleverException extends Exception {

	/**
	 * Creates a new CleverException without a 'cause' exception with message
	 * as the String description.
	 * 
	 * @param message
	 *            The String description of the CleverException.
	 */
	public CleverException( final String message ) {
		super( message );
	}
	
	/**
	 * Creates a critical nested exception.
	 * 
	 * @param throwable
	 *            An attached cause Throwable.
	 */
	public CleverException( final Throwable throwable ) {
		super( throwable );
	}

	/**
	 * Creates a CleverException with message as the description and extra as
	 * the cause.
	 * 
	 * @param message
	 *            The String description of the CleverException.
	 * @param throwable
	 *            An attached cause Throwable.
	 */
	public CleverException( final String message, final Throwable throwable ) {
		super( message, throwable );
	}
	
	/**
	 * Prints the Exception's stack-trace to the given stream.
	 */
	public void printStackTrace( final PrintStream stream ) {
		super.printStackTrace( stream );
		
		if( super.getCause() != null ) {
			stream.println( "Caused by:" );
			super.getCause().printStackTrace( stream );
		} else
			stream.println( "No further child causes." );
	}
	
	/**
	 * Prints the Exception's stack-trace to the given writer.
	 */
	public void printStackTrace( final PrintWriter writer ) {
		super.printStackTrace( writer );
		
		if( super.getCause() != null ) {
			writer.println( "Caused by:" );
			super.getCause().printStackTrace( writer );
		} else
			writer.println( "No further child causes." );
	}
	
	/**
	 * Prints the Exception's stack-trace to standard error stream.
	 */
	public void printStackTrace() {
		super.printStackTrace();
		
		if( super.getCause() != null ) {
			System.err.println( "Caused by:" );
			super.getCause().printStackTrace();
		} else
			System.err.println( "No further child causes." );
	}

	public String getMessage() {
		StringBuffer buffer = new StringBuffer();
		buffer.append( super.getMessage() );
		
		Throwable t = this;
		
		buffer.append( "\nReturning chained strace for above exception:\n" );
				
		StackTraceElement[] trace = t.getStackTrace();
		
		for( int i = 0; i < trace.length; i++ )
			buffer.append( "\t" + trace[i].toString() + "\n" );
			
		t = t.getCause();
			
		if( t != null)
			buffer.append( t.getMessage() );
		
		return buffer.toString();
	}
}