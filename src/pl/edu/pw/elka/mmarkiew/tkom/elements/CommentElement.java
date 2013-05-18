package pl.edu.pw.elka.mmarkiew.tkom.elements;

import pl.edu.pw.elka.mmarkiew.tkom.Utilities;

/**
 * Class represents Comment element
 * 
 * @author Mikolaj Markiewicz
 * 
 */
public class CommentElement extends TextElement {

	/**
	 * C-tor
	 * 
	 * @param content
	 *            Content of comment without <!-- & -->
	 * 
	 * @param parent
	 *            Parent of element
	 */
	public CommentElement(String content, Element parent) {
		super(content, parent);
		this.tag = "#comment";
	}

	/**
	 * Clone element and null parent to avoid reference dependency
	 */
	public CommentElement clone() {
		return new CommentElement(content, null);
	}

	/**
	 * Get content of element wrapped into html comment <!-- & -->
	 */
	public String getContent() {
		return "<!--" + content + "-->";
	}

	/**
	 * Get string representation with proper indent
	 */
	public String toString() {
		return Utilities.genTabs(level) + "<!--" + content + "-->" + "\n";
	}

}
