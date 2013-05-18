package pl.edu.pw.elka.mmarkiew.tkom.elements;

import pl.edu.pw.elka.mmarkiew.tkom.Utilities;

/**
 * Class represents Text element
 * 
 * @author Mikolaj Markiewicz
 * 
 */
public class TextElement extends Element {

	/**
	 * Text content
	 */
	protected String content;

	{
		empty = true;
	}

	/**
	 * C-tor
	 * 
	 * @param content
	 *            Text content
	 * 
	 * @param parent
	 *            Parent element
	 */
	public TextElement(String content, Element parent) {
		super("#text", parent);
		this.content = content;
	}

	/**
	 * Always set text element as empty element
	 */
	public void setEmpty(boolean isEmpty) {
		// Always empty
	}

	/**
	 * Set text content
	 * 
	 * @param content
	 *            Text content
	 */
	public void setContent(String content) {
		this.content = content;
	}

	/**
	 * Get text content
	 * 
	 * @return Text content
	 */
	public String getContent() {
		return content;
	}

	/**
	 * Return string representation of element
	 * 
	 * @return Text with proper indent
	 */
	public String toString() {
		return Utilities.genTabs(level) + content + "\n";
	}

	/**
	 * Clone element and null parent to avoid reference dependency
	 */
	public TextElement clone() {
		return new TextElement(content, null);
	}

}
