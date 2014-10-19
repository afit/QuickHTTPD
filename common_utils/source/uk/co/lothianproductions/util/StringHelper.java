
package uk.co.lothianproductions.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.IOException;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Assorted String helper methods.
 * 
 * @author	Aidan Fitzpatrick
 * @version $Revision: 1.7 $, $Date: 2003/12/11 17:12:58 $
 */
public final class StringHelper {
	
	/**
	 * The character used in listings of stories by letter where the letter
	 * prefix is numeric.
	 * 
	 * @see cleanAlphabet
	 */
	public final static char NON_ALPHABET_CHARACTER = '1';
	
	/**
	 * Two char[]s with diametric and non diametric characters for conversion.
	 */
	public static final char[][] DIAMETRIC_RELATIONSHIP = {
		"ÀÁÂÃÄÅÆÇÈÉÊËÌÎÏÐÑÒÓÔÕÖØÙÚÛÜÝàáâãäåæçèéêëìíîïðñòóôõöøùúûüýÿÐ".toCharArray(),
		"AAAAAAACEEEEIIIDNOOOOOOUUUUYaaaaaaaceeeeiiiionoooooouuuuyyD".toCharArray()
	};
	
	/**
	 * Two char[]s with smartquote and smartquote substitute characters for
	 * String cleaning.
	 */
	public static final char[][] SMARTQUOTE_RELATIONSHIP = {
		{'\u0091','\u0092','\u0093','\u0094'},
		{'\'','\'','"','"'}
	};
		
	/**
	 * This is just a convenience constant to avoid instantiating a String for
	 * comparisons and such
	 */
	public static final String EMPTY_STRING = "";
	
	/**
	 * A char array of characters that are valid in hex notation.
	 */
	public final static char[] HEX_CHARACTERS = "0123456789abcdef".toCharArray();
	
	/**
	 * The UTF-8 Java charset String constant.
	 */
	public final static String UTF8			= "UTF-8";
	
	/**
	 * The ISO 8859 CP 1 Java charset String constant.
	 */
	public final static String ISO88591		= "ISO-8859-1";
	
	/**
	 * Helper method to turn an array into a useful String.
	 * 
	 * @return A String such as "[a, b, c]", calling toString() on all the
	 *         arrayed objects.
	 */
	public static String toString( final Object[] array ) {
		StringBuffer buffer = new StringBuffer("[ ");
		
		for( int i = 0; i < array.length; i++ ) {
			if ( i > 0 )
				buffer.append( ", " );
			
			buffer.append( array[i] );
		}
		
		return buffer.append( "]" ).toString();
	}
	
	/**
	 * Helper method to turn a List into a useful String.
	 * 
	 * @return A String such as "[a, b, c]", calling toString() on all the
	 *         contained objects, where "," is the passed separator.
	 */
	public static String toString( final List list, final String separator ) {
		StringBuffer buffer = new StringBuffer();
		
		for( int i = 0; i < list.size(); i++ ) {
			if ( i > 0 )
				buffer.append( separator );
			
			buffer.append( list.get(i) );
		}
		
		return buffer.toString();
	}
	
	/**
	 * Efficiently converts an InputStream to a String.
	 */
	public static String toString( final InputStream stream ) throws IOException {
		BufferedReader in = new BufferedReader( new InputStreamReader( stream ) );
		
		StringBuffer b = new StringBuffer();
		
		String line = in.readLine();

		// FIXME not efficient enough.
		while( line != null ) {
			b.append( line );
			line = in.readLine();
		}
			
		return b.toString();
	}

	/**
	 * Efficiently converts a Reader to a String.
	 */
	public static String toString( final Reader reader ) throws IOException {
		StringWriter writer = new StringWriter();
		
		// FIXME not efficient enough.
		while ( true ) {
			int ch = reader.read();
			
			if ( ch < 0 )
				break;
			else
				writer.write( ch );
		}
		
		return writer.toString();
	}
	
	/**
	 * Cleans smartquotes out of a String by replacing them with ordinary
	 * quotes (single and double, as appropriate).
	 * 
	 * @param string
	 *            the String to clean up
	 */
	public static String cleanSmartQuotes( final String string ) {
		String process = noNull( string );
	
		// FIXME not efficient enough.
		for( int i = 0; i < SMARTQUOTE_RELATIONSHIP[0].length; i++ )
			process = process.replace(
				SMARTQUOTE_RELATIONSHIP[0][i],
				SMARTQUOTE_RELATIONSHIP[1][i] 
			);
		
		return process;
	}

	/**
	 * Substitutes a null variable with an empty String.
	 */
	public static String noNull( final Object string ) {
		if( string == null )
			return EMPTY_STRING;
		
		// Don't call toString twice.
		String ser = string.toString();
		
		return ser == null ? EMPTY_STRING : ser;
	}
	
	/**
	 * Trims any leading instances of the passed String from the subject
	 * String.
	 */
	public static String trimLeading( final String string, final String remove ) {
		String process = string;
		
		while( process.startsWith( remove ) )
			process = process.substring( remove.length() );
		
		return process;
	}
	
	/**
	 * Cleans a String by reducing its contents to characters adhering to a
	 * reduced, single-case alphabet. Valid characters in the returned String
	 * will be between 'A' to 'Z' and NON_ALPHABET_CHARACTER. Invalid characters
	 * will be replaced by NON_ALPHABET_CHARACTER.
	 * 
	 * @param s
	 *            The String to clean.
	 * @return A String rebuild in a reduced alphabet.
	 */
	public static String cleanAlphabet(final String string ) {
		String process = noNull( string ).toUpperCase();
		StringBuffer clean = new StringBuffer( process.length() );
		
		for( int i = 0 ; i < process.length() ; i++ ) {
			char c = process.charAt(i);
			
			if( c < 'A' || c > 'Z' )
				clean.append( NON_ALPHABET_CHARACTER );
			else
				clean.append( c );
		}
		
		return clean.toString();
	}
	
	/**
	 * Removes all non-alphanumeric characters from the given String.
	 */
	public static String cleanAlphaNumeric( final String string ){
		String process = noNull( string );
		StringBuffer buffer = new StringBuffer( process.length() );

		for( int i = 0; i < process.length(); i++ ) {
			char c = process.charAt(i);
			
			if(		( c >= 'a' && c <= 'z' )
				||	( c >= 'A' && c <= 'Z' )
				||	( c >= '0' && c <= '9' )
			)
				buffer.append(c);
		}
		
		return buffer.toString();
	}
	
	/**
	 * Cleans a String by swapping diametric (i.e. accented) characters out.
	 * 
	 * @param s
	 *            The String to clean.
	 * @return A String with diametric characters removed.
	 */
	public static String cleanDiametrics( final String string ) {
		String process = noNull( string );
		
		// FIXME not efficient enough.
		for( int i = 0 ; i < DIAMETRIC_RELATIONSHIP[0].length; i++ )
			process = process.replace(
				DIAMETRIC_RELATIONSHIP[0][i],
				DIAMETRIC_RELATIONSHIP[1][i]
			);
		
		return process;
	}
	
	/**
	 * Entitises the passed input String so that it is safe for
	 * use within HTML, XHTML or XML.
	 */
	public static String entitise( final String string ) {
		StringBuffer buffer = new StringBuffer( string.length() );
				
		for( int i = 0; i < string.length(); i++ ) {
			char c = string.charAt( i );
			
			// Heavily entitise character outside normal range.
			if( c < (char) 32  || c > (char) 126 )
				buffer.append( "&#" ).append( "" + (int) c ).append( ';' );
			else if( c == '<' )
				buffer.append( "&lt;" );
			else if( c == '>' )
				buffer.append( "&gt;" );
			else if( c == '%' )
				buffer.append( "&#25;" );
			else if( c == '&' )
				buffer.append( "&amp;" );
			else if( c == '\"' )
				buffer.append( "&quot;" );
			else if( c == '\n' )
				buffer.append( "<br/>" );
			else
				buffer.append( c );
		}
		
		return buffer.toString();
	}
	
	/**
	 * Converts an array of ints into sets of comma-delimited value strings,
	 * each with a maximum number of numbers in. Will return only one group if
	 * max is zero. Where values = [1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11] and max = 4,
	 * the return value will equal ["1,2,3,4", "5,6,7,8", "9,10,11"].
	 * 
	 * The character mask will only be used when its value is other than '0'.
	 */
	public static String[] toChunkedStringArrays( final int[] values, final int max, final char mask ) {
		if( values.length == 0 )
			return new String[] {};
		
		String[] groups = new String[ max != 0 ? values.length / max : 1 ];
		StringBuffer ints = new StringBuffer();
		
		for( int i = 0; i < values.length; i++ ) {
			if( mask != '0' )
				ints.append( mask );
			else
				ints.append( values[i] );
			
			if( i + 1 != values.length && (max == 0 || (i + 1) % max != 0) )
				ints.append(',');
			else {
				groups[ max != 0 ? (i / max) - 1 : 0 ] = ints.toString();
				ints = new StringBuffer();
			}
		}
		
		return groups;
	}

	/**
	 * Convert an array of bytes into a hexadecimal string.
	 * 
	 * @param bytes
	 *            The array of bytes.
	 * @return The encoded string.
	 */
	public static String byteToHex( final byte[] bytes ) {
		StringBuffer buffer = new StringBuffer( bytes.length * 2 );
		
		for( int i = 0; i < bytes.length; i++ )
			buffer.append(
				HEX_CHARACTERS[ ( ( bytes[i] & 0xf0 ) >> 4 ) ]
			).append(
				HEX_CHARACTERS[ ( bytes[i] & 0x0f ) ]
			);
		
		return buffer.toString();
	}
	
	/**
	 * Converts a single char hex value to its corresponding int.
	 */
	public static int hexToInt( final char hex ) {
		// intToHex "%" + hex.charAt( c / 16 ) + hex.charAt( c % 16 )
		if( hex >= '0' && hex <= '9' )
			return hex - 48;
		
		char dehex = hex;
		
		// If it's the wrong case...
		if( dehex >= 'a' && dehex <= 'z' )
			return dehex - 16 - 'G';
		
		return 16 + (dehex - 'G');
	}
	
	/**
	 * Converts a List of Integers into a comma-delimited String for binding to
	 * SQL. With a mask of '?' its suitable for SQL bindings.
	 * 
	 * This method automatically uses '?' as a mask.
	 * 
	 * @deprecated Call the full method.
	 */
	public static String toString( final List integers ) {
		return toString( integers, '?' );
	}
	
	/**
	 * Converts a List of Integers into a comma-delimited String for binding to
	 * SQL. With a mask of '?' its suitable for SQL bindings.
	 */
	public static String toString( final List integers, final char mask ) {
		if( integers == null || integers.isEmpty() )
			return "";
		
		StringBuffer buffer = new StringBuffer( integers.size() * 2 );
		
		for( int i = 0; i < integers.size(); i++ )
			if( mask != '0' )
				buffer.append( mask );
			else
				buffer.append( integers.get(i) );
		
			buffer.append(',');
		
		return buffer.substring( 0, buffer.length() - 1 );
	}
	
	/**
	 * Accepts a String and wraps tokens within it with the given wrappers, by
	 * the given delimiter inserting the string &lt;insert&gt; after the
	 * &lt;insert_after&gt;th block. Note that the delimiting Strings are
	 * preserved. <br/><br/>
	 * 
	 * ie. "&lt;P&gt;A&lt;/P&gt;\n&lt;P&gt;B&lt;/P&gt;\n" =
	 * wrapDelimitedString( "A\nB\n", "&lt;P&gt;", "&lt;/P&gt;", "\n" );
	 * 
	 * @param string
	 *            The String to process.
	 * @param wrap_begin
	 *            The String to append to the beginning of each token.
	 * @param wrap_end
	 *            The String to append to the end of each token.
	 * @param delimiter
	 *            The String which delimits each token.
	 * @param insertAt
	 *            The number of blocks after which the insert text is inserted
	 * @param insert
	 *            A String to insert after block number insert_after
	 */
	public static String wrapDelimitedString( final String string, final String start, 
											  final String end, final String delimiter,
											  final int insertAt, final String insert ) {
		if( string == null || start == null || end == null || delimiter == null )
			return "";
		
		StringTokenizer tokenizer = new StringTokenizer( string, delimiter );
		StringBuffer buffer = new StringBuffer( string.length() + (start.length() + end.length()) * 2 );
		int para = 0;
		
		// See comment above
		if(tokenizer.countTokens() < 2 )
			return string;
		
		while( tokenizer.hasMoreTokens() ) {
			buffer.append( start ).append( tokenizer.nextToken() ).append( end ).append( delimiter );
			
			if( insert != null && para++ == insertAt )
				buffer.append( insert );
		}
		
		return buffer.toString();
	}
	
	/**
	 * Turn a String with delimiters into a List.
	 * 
	 * @param string
	 *            The String to process
	 * @param delimiter
	 *            The String with delimiters with which to split the String by.
	 */
	public static List toList(final String string, final String delimiter) {
		if( string == null || delimiter == null )
			return new ArrayList();
		
		StringTokenizer tokenizer = new StringTokenizer( string, delimiter );
		List result = new ArrayList( tokenizer.countTokens() );
		
		while( tokenizer.hasMoreTokens() )
			result.add( tokenizer.nextToken() );
		
		return result;
	}

	/**
	 * Clever converts a String to a URL, making a reasonable guess to coerce
	 * it into an email address first, falling back to an HTTP URI.
	 */
	public static URL toURL(final String url) throws MalformedURLException {
		if( url == null || url.length() == 0 )
			return null;

		URL new_url = null;

		// Try first:
		try {
			new_url = new URL( url );
		} catch (MalformedURLException e) {
	
			if( url.indexOf('@') == -1 )
				// Probably not an email address.
				new_url = new URL( "http://" + url );
			else
				new_url = new URL( "mailto:" + url );

		}

		return new_url;
	}	
}