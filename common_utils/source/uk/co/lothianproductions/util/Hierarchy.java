
package uk.co.lothianproductions.util;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;
import java.util.Set;
import java.util.List;
import java.util.Iterator;

/**
 * Helper for collating hierarchical objects.
 * 
 * The two hierarchy objects are unfinished.
 * 
 * @author	Aidan Fitzpatrick
 * @version $Revision: 1.4 $, $Date: 2003/12/11 17:18:06 $
 */
public abstract class Hierarchy {
	
	protected final Map map = new HashMap();
	protected final Map depth = new HashMap();
	protected int max = 0;

	public Hierarchy( final List elements ) {
		renderHierarchy( elements );
	}

	public List getParallelElements( final int level ) {
		// FIXME rather inefficient?
		List list = new ArrayList();
		
		Iterator i = depth.keySet().iterator();
		Integer desired = new Integer( level );
		
		while( i.hasNext() ) {
			Object o = i.next();
			
			if( depth.get( o ).equals( desired ) )
				list.add( o );
		}
		
		return list;
	}
	
	private void renderHierarchy( final List elements ) { 
	
		Iterator i = elements.iterator();
		
		while( i.hasNext() ) {
			int down = 0;
			
			Object h = i.next();
			Object j = h;
			
			while( getParent( j ) != null ) {
				j = getParent( j );
				down++;
			}
			
			if ( max < down )
				max = down;
			
			depth.put( h, new Integer( down ) );
		}
	
		// Sort the elements into a series of parent-based maps.
		// This is very slow, not least as getParent will hit the
		// DB on it's first invocation. There's not much we can do
		// about this.
		i = elements.iterator();

		// Now, in order to find the children for a given element, a map lookup
		// keyed by that map will return its children. Similarly, an element's peers
		// can be returned by using the element's parent as key.
		while( i.hasNext() ) {
			Object h = i.next();

			Object key = getParent( h );
						
			if( map.containsKey( key ) )
				((Set) map.get( key ) ).add( h );
			else {
				Set set = new TreeSet( getComparator() );
				set.add( h );
				map.put( key , set );
			}
		}
	}
	
	public int getDepth() {
		return max;
	}
	
	public List getChildren( final Object element ) {
		return new ArrayList( (Set) map.get( element ) );
	}
	
	public Comparator getComparator() {
		// Provides ordering for both keys and peers.
		return new Comparator() {
			public int compare(Object o1, Object o2) {
				
				// Order by no of children.
				Integer i = (Integer) depth.get( o1 );
				Integer j = (Integer) depth.get( o2 );
				
				if( i == null )
					i = new Integer( 0 );
					
				if( j == null )
					j = new Integer( 0 );
				
				int comparison = i.compareTo( j );
				
				if( comparison != 0 )
					return comparison;
				
				// Order by name.	
				return 0;
			}
		};
	}
	
	public abstract Object getParent( final Object o );
}