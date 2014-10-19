
package uk.co.lothianproductions.util;

/**
 * VersionedObjects support the getVersionInformation method which returns
 * version information on that object. Version information is commonly returned
 * in the format of a CVS revision and date keyword substitution.
 * 
 * @author	Aidan Fitzpatrick
 * @version $Revision: 1.2 $, $Date: 2003/12/11 14:26:33 $
 */
public interface VersionedObject {

	/**
	 * Returns version information on the object. Version information is
	 * commonly returned in the format of a CVS revision and date keyword
	 * substitution.
	 * 
	 * @return Version information on the object.
	 */
	public String getVersionInformation();

}