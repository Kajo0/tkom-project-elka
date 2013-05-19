package pl.edu.pw.elka.mmarkiew.tkom.elements;

import java.util.LinkedList;
import java.util.Map;
import java.util.TreeMap;

import pl.edu.pw.elka.mmarkiew.tkom.Utilities;

/**
 * Class represents Tag element
 * 
 * @author Mikolaj Markiewicz
 * 
 */
public class TagElement extends Element {

	/**
	 * Map of attributes with key=value
	 */
	private Map<String, String> attributes = new TreeMap<String, String>();

	/**
	 * List of element children
	 */
	private LinkedList<Element> elements = new LinkedList<>();

	/**
	 * C-tor
	 * 
	 * @param tag
	 *            Tag name
	 * 
	 * @param parent
	 *            Element parent
	 */
	public TagElement(String tag, Element parent) {
		super(tag, parent);
	}

	/**
	 * Add new attribute
	 * 
	 * @param key
	 *            Key name
	 * 
	 * @param value
	 *            Value of attribute
	 */
	public void addAttribute(String key, String value) {
		attributes.put(key, value);
	}

	/**
	 * Clear elements from tag element
	 */
	public void clearElements() {
		elements.clear();
	}

	/**
	 * Check whether tag has any element inside
	 * 
	 * @return True if count of elements > 0, false otherwise
	 */
	public boolean hasElements() {
		return elements.size() > 0;
	}

	/**
	 * Get children list
	 * 
	 * @return Elements list
	 */
	public LinkedList<Element> getElements() {
		return elements;
	}

	/**
	 * Add new element into list
	 * 
	 * @param elem
	 *            New element
	 */
	public void addElement(Element elem) {
		if (elem instanceof TagElement)
			if (TagQuantity.valueOf(elem.getTag().toLowerCase()).isSingle())
				elem.setEmpty(true);

		elem.level = this.level + 1;
		elements.add(elem);
	}

	/**
	 * Set new attributes map
	 * 
	 * @param attribs
	 *            New map
	 */
	public void setAttributes(Map<String, String> attribs) {
		this.attributes = attribs;
	}

	/**
	 * Get attributes map of tag element
	 * 
	 * @return Map of attributes
	 */
	public Map<String, String> getAttributes() {
		return attributes;
	}

	/**
	 * Check whether element has only one child, and it's instance of
	 * TextElement
	 * 
	 * @return True if has only one child and it's TextElement, false otherwise
	 */
	public boolean hasOnlyText() {
		if (elements.size() == 1 && elements.getFirst() instanceof TextElement)
			return true;

		return false;
	}

	/**
	 * Get Text content of text element
	 * 
	 * @return Content of first child text element
	 */
	public String getTextElementContent() {
		if (!hasOnlyText())
			throw new ArrayIndexOutOfBoundsException();

		return ((TextElement) elements.getFirst()).getContent();
	}

	/**
	 * Set new level of element, which is applied into his children
	 */
	public void setLevel(int l) {
		this.level = l;

		for (Element e : elements)
			e.setLevel(l + 1);
	}

	/**
	 * Get string representation of tag element with whole children
	 */
	public String toString() {
		StringBuilder str = new StringBuilder();

		str.append(Utilities.genTabs(level));
		str.append("<");
		str.append(tag);

		for (Map.Entry<String, String> entry : attributes.entrySet()) {
			str.append(" ");
			str.append(entry.getKey());
			str.append("=");
			str.append(entry.getValue());
		}

		if (empty)
			str.append(" />\n");
		else {
			str.append(">\n");

			for (Element elem : elements)
				str.append(elem.toString());

			str.append(Utilities.genTabs(level));
			str.append("</" + tag + ">\n");
		}

		return str.toString();
	}

	/**
	 * Clone element, null parent to avoid reference dependency and clone each
	 * of children
	 */
	public TagElement clone() {
		TagElement t = new TagElement(tag, null);
		t.setEmpty(empty);

		for (Map.Entry<String, String> entry : attributes.entrySet())
			t.addAttribute(entry.getKey(), entry.getValue());

		for (Element e : elements) {
			Element cloned = e.clone();
			cloned.setParent(t);

			t.addElement(cloned);
		}

		return t;
	}

	// /**
	// * @deprecated
	// */
	// private Iterator<Element> iter = null;

	// /**
	// * @deprecated
	// */
	// private Element actualElement = null;

	// /**
	// * @deprecated
	// */
	// public Element nextElement() {
	// // Nic w sobie nie ma
	// if (elements.isEmpty())
	// return null;
	//
	// // Pierwszy z elementow
	// if (actualElement == null) {
	// iter = elements.iterator();
	// actualElement = iter.next();
	// return actualElement;
	// }
	//
	// // Byl tag to w niego wchodzimy
	// if (actualElement instanceof TagElement) {
	// Element next = ((TagElement) actualElement).nextElement();
	//
	// // Jak jest jakis element w rozwazanym
	// if (next != null)
	// return next;
	// }
	//
	// // Przeszlo przez nie TagElement lub srodek TagElementu przeszlo
	// // Wez nastepny jak jest lub null
	// if (iter.hasNext()) {
	// actualElement = iter.next();
	// return actualElement;
	// } else {
	// return null;
	// }
	// }

}
