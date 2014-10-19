
package uk.co.lothianproductions.util.testcases;

import uk.co.lothianproductions.util.StringHelper;

import java.net.MalformedURLException;
import java.net.URL;

import junit.framework.TestCase;

/**
 * @author	 Aidan Fitzpatrick
 * @version $Revision: 1.3 $, $Date: 2003/12/11 17:12:58 $
 */
public class StringHelperTestCase extends TestCase {
	
	public StringHelperTestCase( final String name ) {
		super( name );
	}
	
	public void testNoNull() {
		String unset = null;
		assertEquals( "", StringHelper.noNull( unset ) );
		
		assertEquals( "not null", StringHelper.noNull( "not null" ) );
	}
	
	public void testToURL() {
		try {
			assertEquals( new URL( "http://www.website.com" ), StringHelper.toURL( "www.website.com" ) );
			assertEquals( new URL( "mailto:owner@website.com" ), StringHelper.toURL( "owner@website.com" ) );
			
			assertEquals( new URL( "http://localhost" ), StringHelper.toURL( "localhost" ) );
			assertEquals( new URL( "http://localhost:8080" ), StringHelper.toURL( "localhost:8080" ) );
		} catch (MalformedURLException e) {
			fail( "The testcase encountered a misformed URL and failed." );
		}
	}
	
	public void testHexToInt() {
		char[] question = {
					'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F', 'a', 'b', 'c', 'd', 'e', 'f'
		};
		
		int[] answer = {
					0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 10, 11, 12, 13, 14, 15
		};
		
		for( int i = 0; i < question.length; i++ )
			assertEquals( answer[i], StringHelper.hexToInt( question[i] ) );
	}

}
