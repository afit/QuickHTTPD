
package uk.co.lothianproductions.util.testcases;

import uk.co.lothianproductions.util.CleverException;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;

import junit.framework.TestCase;

/**
 * @author	Aidan Fitzpatrick
 * @version	$Revision: 1.1 $, $Date: 2003/12/11 14:26:33 $
 */
public class CleverExceptionTestCase extends TestCase {
	
	public CleverExceptionTestCase( final String name ) {
		super( name );
	}
	
	public void testCleverException() {
		
		Exception e = new Exception( "Test cause." );
		
		Exception ce = new CleverException( "Test CE.", e ) {};
		
		// Test cause retrieval
		assertEquals( e, ce.getCause() );
		
		// Ensure that returned message includes strace.
		assertTrue( ce.getMessage().length() > "Test CE.".length() );
		
		String message = ce.getClass().getName() + ": " + ce.getMessage();
		ByteArrayOutputStream out = new ByteArrayOutputStream();	
		PrintStream stream = new PrintStream( out );
		
		ce.printStackTrace( stream );
		stream.flush();
		 
		// Ensure something was written to the stream (as much as we can).
		assertEquals(
			message,
			out.toString().substring( 0, message.length() )
		);
			
		out = new ByteArrayOutputStream();
		PrintWriter writer = new PrintWriter( out );
		
		ce.printStackTrace( writer );
		writer.flush();

		// Ensure something was written to the writer (as much as we can).
		assertEquals(
			message,
			out.toString().substring( 0, message.length() ) 
		);
		
		out = new ByteArrayOutputStream();
		stream = new PrintStream( out );
		
		PrintStream err = System.err;
		System.setErr( stream );
		
		ce.printStackTrace();
		
		System.setErr( err );
		
		// Ensure something was written to err (as much as we can).
		assertEquals( 
			message,
			out.toString().substring( 0, message.length() )
		);
		
	}

}
