
package uk.co.lothianproductions.util.testcases;

import uk.co.lothianproductions.util.CachedDate;

import junit.framework.TestCase;

/**
 * @author	 Aidan Fitzpatrick
 * @version $Revision: 1.2 $, $Date: 2003/12/11 14:26:33 $
 */
public class CachedDateTestCase extends TestCase {

	public CachedDateTestCase( final String name ) {
		super( name );
	}
	
	public void testSmallDateChars() {
		// Simple run-time invocation test that method works.
		CachedDate.getInstance().getSmallDateChars();
	}
	
	public void testGetDate() { 
		// Iterate a thousand or so times, not a
		// particularly thorough check but it gives
		// good ROI!
		for( int i = 0; i < 1000; i++ ) {
			long time = System.currentTimeMillis();
			long from = CachedDate.getInstance().getDate().getTime();
			
			// Check that returned time is within 2x CHECK_FREQUENCY of
			// real time.
			assertTrue(	from >= ( time - CachedDate.CHECK_FREQUENCY )
					&&	from <= ( time + CachedDate.CHECK_FREQUENCY ) );
		}					
	}
	
}
