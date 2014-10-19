
package uk.co.lothianproductions.util.testcases;

import uk.co.lothianproductions.util.CoreProperties;

import java.util.Properties;

import junit.framework.TestCase;

/**
 * @author 	Aidan Fitzpatrick
 * @version $Revision: 1.2 $, $Date: 2003/12/11 14:26:33 $
 */
public class CorePropertiesTestCase extends TestCase {

	public CoreProperties mProperties;

	public CorePropertiesTestCase( final String name ) {
		super( name );
	}
	
	public void setUp() {
		// Set up the test.
		mProperties = new CoreProperties( new Properties() );
		
		mProperties.setProperty( "person.forename", "john" );
		mProperties.setProperty( "person.surname", "doe" );
		mProperties.setProperty( "company.name", "megacorp." );
		mProperties.setProperty( "company.age", "15 years" );
		mProperties.setProperty( "company.staff", "20" );
	}
	
	public void testGetSubProperties() {
		CoreProperties subs = mProperties.getSubproperties( "person." );
		
		// Ensure the correct number of properties have been returned.
		assertEquals( 2, subs.size() );
		
		// Ensure the keys have been correctly split.
		assertEquals( "john", subs.getProperty("forename") );
		assertEquals( "doe", subs.getProperty("surname") );
		assertEquals( null, subs.getProperty(".forename") );
		assertEquals( null, subs.getProperty("name") );
		
		// Ensure these haven't been set or corrupted.
		assertEquals( null, subs.getProperty("person.forename") );
		assertEquals( null, subs.getProperty("person.surname") );
		assertEquals( null, subs.getProperty("company.name") );
		
		// Try to generate a run-time exception.
		mProperties.getSubproperties( "oops" );
		mProperties.getSubproperties( "person.forename." );
	}
	
	public void testGetInteger() {
		// Ensure to check default values.
		assertEquals( -1, mProperties.getInteger( "company.age" ) );
		assertEquals( 10, mProperties.getInteger( "company.age", 10 ) );
		assertEquals( 20, mProperties.getInteger( "company.staff", 10 ) );
	}
	
	public void testGetString() {
		// Test present and absent keys.
		assertEquals( mProperties.getProperty("person.forename"), mProperties.getString("person.forename") );
		assertEquals( mProperties.getProperty("person.age"), mProperties.getString("person.age") );
	}
	
	public void testSetDefaults() {
		Properties defaults = new Properties();
		
		defaults.setProperty( "company.name", "unknown" );
		defaults.setProperty( "company.location", "unknown" );
		
		mProperties.setDefaults( defaults );

		// Ensure default values don't overwrite others.
		assertEquals( "megacorp.", mProperties.getProperty( "company.name" )  );
		assertEquals( "unknown", mProperties.getProperty( "company.location" ) );
	}
	
}