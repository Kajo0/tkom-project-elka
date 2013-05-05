package pl.edu.pw.elka.mmarkiew.tkom.elements;

import java.util.LinkedList;

public class TreeElement {

	private String doctype;
	private LinkedList<Element> elements; // Always one should be there
	/**
	 * @deprecated
	 */
	private Element actualElement = null;

	public TreeElement() {
		elements = new LinkedList<>();
	}

	public void setDoctype(String doctype) {
		this.doctype = doctype;
	}

	public void addElement(Element elem) {
		elements.add(elem);
	}
	
	public LinkedList<Element> getElements() {
		return elements;
	}

	/**
	 * @deprecated
	 */
	public Element nextElement() {
		if (actualElement == null) {
			actualElement = elements.getFirst();
			return actualElement;
		}

		actualElement = ((TagElement) elements.getFirst()).nextElement();
		return actualElement;
	}

	/**
	 * @deprecated
	 */
	public Element getActualElement() {
		return actualElement;
	}

	public String toString() {
		StringBuilder str = new StringBuilder();

		str.append("<!doctype ");
		str.append(doctype);
		str.append(">\n");

		for (Element elem : elements)
			str.append(elem.toString());

		return str.toString();
	}

	public String getDoctype() {
		return doctype;
	}

}
