
package uk.co.lothianproductions.httpd.testcases;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * @author	Aidan Fitzpatrick
 * @version $Revision: 1.4 $, $Date: 2004/01/02 19:02:15 $
 */
public class TestCases {

	public static Test suite() {
		TestSuite suite = new TestSuite();
		
		suite.addTestSuite( BounceListenerTestCase.class );
		suite.addTestSuite( ResourceDocumentSourceTestCase.class );
		suite.addTestSuite( HTTPDocumentSourceTestCase.class );
		
		return suite;
	}
}