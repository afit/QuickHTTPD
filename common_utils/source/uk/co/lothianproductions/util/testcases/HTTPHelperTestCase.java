
package uk.co.lothianproductions.util.testcases;

import uk.co.lothianproductions.util.HTTPHelper;
import uk.co.lothianproductions.util.StringHelper;

import java.io.IOException;

import junit.framework.TestCase;

/**
 * @author 	Aidan Fitzpatrick
 * @version $Revision: 1.7 $, $Date: 2003/12/11 17:18:06 $
 */
public class HTTPHelperTestCase extends TestCase {

	public HTTPHelperTestCase( final String name ) {
		super( name );
	}

	public void testHttpDecode() {
		char[][] question = {
			"abcdef".toCharArray(),
			"".toCharArray(),
			"the%20cat%20and%20the%20dog".toCharArray(),
			"%21%22%a3%24%25%5e%26%2a%28%29%5f%2b%2d%3d%7b%7d%5b%5d%3a%40%7e%3b%27%23%3c%3e%3f%2c%2e%2f%5c%7c%60%ac".toCharArray()
		};
		
		char[][] answer = {
			"abcdef".toCharArray(),
			"".toCharArray(),
			"the cat and the dog".toCharArray(),
			"!\"£$%^&*()_+-={}[]:@~;\'#<>?,./\\|`¬".toCharArray()
		};

		// Test unidirectional conversion
		for( int i = 0; i < question.length; i++ )
			assertEquals( new String( answer[i] ), new String( HTTPHelper.httpDecode( question[i] ) ) );
	}
	
	public void testHttpEncode() {
		char[][] question = {
			"abcdef".toCharArray(),
			"".toCharArray(),
			"the cat and the dog".toCharArray(),
			"!\"£$%^&*()_+-={}[]:@~;\'#<>?,./\\|`¬".toCharArray()
		};

		char[][] answer = {
			"abcdef".toCharArray(),
			"".toCharArray(),
			"the%20cat%20and%20the%20dog".toCharArray(),
			"%21%22%a3%24%25%5e%26%2a%28%29%5f%2b%2d%3d%7b%7d%5b%5d%3a%40%7e%3b%27%23%3c%3e%3f%2c%2e%2f%5c%7c%60%ac".toCharArray()
		};
		
		// Test unidirectional conversion
		for( int i = 0; i < question.length; i++ )
			assertEquals( new String( answer[i] ), new String( HTTPHelper.httpEncode( question[i] ) ) );
	}
	
	public void testAnalyseURLSpace() {	
		String[] question = {				
			// Simple aggr. URL
			"/1/2.html",
			// Simple dobj. URL
			"/1/2/3.html",
					
			// Aggr. URL with livery 
			"/1/livery/2.html",
			// Aggr. URL with livery and aggr. argument
			"/1/livery/2-X.html",
					
			// Dobj. URL with livery 
			"/1/livery/2/3.html",
			// Dobj. URL with livery and dobj. argument
			"/1/livery/2/3-X.html",
			// Dobj. URL with livery, aggr. argument and dobj. argument
			"/1/livery/2-X/3-Y.html"
		};

		String[][][] answer = {
			// Simple aggr. URL
			{ {"1", null}, {"2.html", null} },
			// Simple dobj. URL
			{ {"1", null}, {"2", null}, {"3.html", null} },
					
			// Aggr. URL with livery 
			{ {"1", null}, {"livery", null}, {"2.html", null} },
			// Aggr. URL with livery and aggr. argument
			{ {"1", null}, {"livery", null}, {"2", "X.html"} },
					
			// Dobj. URL with livery 
			{ {"1", null}, {"livery", null}, {"2", null}, {"3.html", null} },
			// Dobj. URL with livery and dobj. argument
			{ {"1", null}, {"livery", null}, {"2", null}, {"3", "X.html"} },
			// Dobj. URL with livery, aggr. argument and dobj. argument
			{ {"1", null}, {"livery", null}, {"2", "X"}, {"3", "Y.html"} }
		};
		
		for( int i = 0; i < question.length; i++ ) {
			String[][] result = HTTPHelper.analyseURLSpace( question[i] );
			
			assertEquals( answer[i].length, result.length );
			
			for( int j = 0; j < answer[i].length; j++ )
				for( int k = 0; k < answer[i][j].length; k++ )
					assertEquals( answer[i][j][k], result[j][k] );
		}
	}
	
	public void testHttpRequestSplit() {
		char[][] question = {
			"GET /index.html HTTP/1.0".toCharArray(),
			"POST /submit.cgi HTTP/1.1".toCharArray(),
			"HEAD / HTTP/1.0".toCharArray(),
			"GET /tests/junit/junit.cgi?test=true HTTP/1.1".toCharArray(),
			"GET /".toCharArray()
			
		};

		char[][][] answer = {
			{ "GET".toCharArray(), "/index.html".toCharArray() },
			{ "POST".toCharArray(), "/submit.cgi".toCharArray() },
			{ "HEAD".toCharArray(), "/".toCharArray() },
			{ "GET".toCharArray(), "/tests/junit/junit.cgi?test=true".toCharArray() },
			{ "GET".toCharArray(), "/".toCharArray() }
		};
		
		for( int i = 0; i < question.length; i++ ) {
			assertEquals( new String( answer[i][0] ), new String( HTTPHelper.httpRequestSplit( question[i] )[0] ) );
			assertEquals( new String( answer[i][1] ), new String( HTTPHelper.httpRequestSplit( question[i] )[1] ) );
		}			
	}
	
	public void testCompressAndExpandBytes() {
		byte[][] question = {
			"".getBytes(),
			"The cat sat on the mat.".getBytes(),
			new String( StringHelper.DIAMETRIC_RELATIONSHIP[0] ).getBytes(),
			new String( StringHelper.DIAMETRIC_RELATIONSHIP[1] ).getBytes()
		};
		
		for( int i = 0; i < question.length; i++ ) {
			try {
				assertEquals( new String( question[i] ), new String( HTTPHelper.expandBytes( HTTPHelper.compressBytes( question[i] ) ) ) );
			} catch (IOException e) {
				e.printStackTrace();
				fail( "The compressBytes or expandBytes method failed." );
			}
		}
	}
	
}