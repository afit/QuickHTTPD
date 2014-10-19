
package uk.co.lothianproductions.util;

/**
 * Hierarchical objects can be built together into a tree as they follow a
 * pattern, each owning a parent hierarchical object. <br><br>
 * 
 * @see Hierarchy
 * 
 * The two hierarchy objects are unfinished.
 * 
 * @author	Aidan Fitzpatrick
 * @version $Revision: 1.3 $, $Date: 2003/12/11 17:18:06 $
 */
public interface Hierarchical {

	/**
	 * Returns the hierarchical object's parent, or null if it lacks one.
	 * 
	 * @return Hierarchical The hierarchical's parent.
	 */
	public Hierarchical getParent();
}