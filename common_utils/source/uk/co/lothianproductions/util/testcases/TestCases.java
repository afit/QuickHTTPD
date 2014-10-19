
package uk.co.lothianproductions.util.testcases;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * @author 	Aidan Fitzpatrick
 * @version $Revision: 1.2 $, $Date: 2003/12/11 14:26:33 $
 */
public class TestCases {

	public static Test suite() {
		TestSuite suite = new TestSuite();
		
		suite.addTestSuite( CachedDateTestCase.class );
		suite.addTestSuite( CleverExceptionTestCase.class );
		suite.addTestSuite( CorePropertiesTestCase.class );
		suite.addTestSuite( HTTPHelperTestCase.class );
		suite.addTestSuite( StringHelperTestCase.class );
		
		return suite;
	}
}