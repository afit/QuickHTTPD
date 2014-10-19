
package uk.co.lothianproductions.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.util.Date;

/**
 * Cache object for rapid sequential date construction. Building date objects
 * is expensive as it involves JNI, object construction and GC. Calling this
 * lessens the load, but will provide a date that could be out by
 * CHECK_FREQUENCY.
 * 
 * @author Aidan Fitzpatrick
 * @version $Revision: 1.2 $, $Date: 2003/12/11 14:26:33 $
 */
public final class CachedDate {

	/**
	 * Constant value representing the check frequency of the underlying date
	 * object. This should be around 1,000ms, to ensure that a date can never
	 * be out by more than a second or so.
	 */
	public final static long CHECK_FREQUENCY = 1000;

	private static DateFormat formatter = new SimpleDateFormat( "dd/MM/yyyy HH:mm:ss z" );
	
	private static CachedDate mInstance;
	
	private long mLastInstantiation;
	private Date mNow;

	// Protect default constructor.
	private CachedDate() {
		update();
	}

	/**
	 * Returns the Singleton instance of the CachedDate.
	 * 
	 * @return CachedDate The Singleton instance of the CachedDate.
	 */
	public static synchronized CachedDate getInstance() {
		return (
			mInstance == null ?
			mInstance = new CachedDate() :
			mInstance
		);
	}
	
	/**
	 * Returns the date, accurate to CHECK_FREQUENCY.
	 * 
	 * @return Date The date, accurate to CHECK_FREQUENCY.
	 */
	public Date getDate() {
		if( (System.currentTimeMillis() - mLastInstantiation) > CHECK_FREQUENCY )
			update();

		return mNow;
	}

	/**
	 * Returns the date as a char array, accurate to CHECK_FREQUENCY. Formatted
	 * in the following format "dd/MM/yyyy HH:mm:ss z".
	 * 
	 * @return byte[] The date as a char array, accurate to CHECK_FREQUENCY.
	 */	
	public char[] getSmallDateChars() {
		return formatter.format( getDate() ).toCharArray();
	}

	/**
	 * Updates the date.
	 */
	private void update() {
		mLastInstantiation = System.currentTimeMillis();
		mNow = new Date();
	}
}