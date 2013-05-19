package pl.edu.pw.elka.mmarkiew.tkom.elements;

import java.util.LinkedList;

/**
 * Class represents simple html DOM tree
 * 
 * @author Mikolaj Markiewicz
 * 
 */
public class TreeElement {

	/**
	 * Tree document doctype
	 */
	private String doctype;

	/**
	 * List of children, there has to be one root element
	 */
	private LinkedList<Element> elements; // Always one should be there

	/**
	 * C-tor
	 */
	public TreeElement() {
		elements = new LinkedList<>();
	}

	/**
	 * Set tree doctype
	 * 
	 * @param doctype
	 *            New doctype
	 */
	public void setDoctype(String doctype) {
		this.doctype = doctype;
	}

	/**
	 * Add element on the top of tree
	 * 
	 * @param elem
	 *            Element to add
	 */
	public void addElement(Element elem) {
		elements.add(elem);
	}

	/**
	 * Get elements of the top element tree
	 * 
	 * @return Top tree elements
	 */
	public LinkedList<Element> getElements() {
		return elements;
	}

	/**
	 * Get string representation
	 */
	public String toString() {
		StringBuilder str = new StringBuilder();

		str.append("<!doctype ");
		str.append(doctype);
		str.append(">\n");

		for (Element elem : elements)
			str.append(elem.toString());

		return str.toString();
	}

	/**
	 * Get document doctype
	 * 
	 * @return Document doctype
	 */
	public String getDoctype() {
		return doctype;
	}

}
