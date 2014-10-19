
package uk.co.lothianproductions.httpd.document;

import uk.co.lothianproductions.httpd.config.ConfigException;

import java.io.InputStream;

/**
 * Represents a Document as served by a RequestHandler. Documents can be
 * obtained from the DocumentSource.
 * 
 * @author	Aidan Fitzpatrick
 * @version $Revision: 1.9 $, $Date: 2004/01/02 19:01:33 $
 */
public abstract class Document {

	// FIXME clarify render parameters
	
	/**
	 * Renders the Document to an InputStream using the passed parameters.
	 */
	public abstract InputStream render( final String[][] parameters ) throws ConfigException, DocumentRenderException;
	
	/**
	 * Returns the rendered Document's length.
	 * 
	 * Given that the Document length can vary depending on the arguments
	 * given to the Document as it's rendered the length method should only
	 * be called after the Document has been rendered.
	 * 
	 * Returns -1 if the Document's length has not or could not be measured. 
	 */
	public abstract long length();

}