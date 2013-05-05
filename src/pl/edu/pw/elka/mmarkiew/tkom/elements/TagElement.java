package pl.edu.pw.elka.mmarkiew.tkom.elements;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.TreeMap;

import pl.edu.pw.elka.mmarkiew.tkom.Utilities;

public class TagElement extends Element {

	private Map<String, String> attributes = new TreeMap<String, String>();
	private LinkedList<Element> elements = new LinkedList<>();
	/**
	 * @deprecated
	 */
	private Element actualElement = null;
	private Iterator<Element> iter = null;

	public TagElement(String tag, Element parent) {
		super(tag, parent);
	}

	public void addAttribute(String key, String value) {
		attributes.put(key, value);
	}

	public void clearElements() {
		elements.clear();
	}

	public boolean hasElements() {
		return elements.size() > 0;
	}

	public boolean hasOnlyText() {
		if (elements.size() == 1 && elements.getFirst() instanceof TextElement)
			return true;

		return false;
	}

	public String getTextElementContent() {
		if (!hasOnlyText())
			throw new ArrayIndexOutOfBoundsException();

		return ((TextElement) elements.getFirst()).getContent();
	}

	public LinkedList<Element> getElements() {
		return elements;
	}

	public void addElement(Element elem) {
		if (elem instanceof TagElement)
			if (TagQuantity.valueOf(elem.getTag()).isSingle())
				elem.setEmpty(true);

		elem.level = this.level + 1;
		elements.add(elem);
	}

	/**
	 * @return
	 * 
	 * @deprecated
	 */
	public Element nextElement() {
		// Nic w sobie nie ma
		if (elements.isEmpty())
			return null;

		// Pierwszy z elementow
		if (actualElement == null) {
			iter = elements.iterator();
			actualElement = iter.next();
			return actualElement;
		}

		// Byl tag to w niego wchodzimy
		if (actualElement instanceof TagElement) {
			Element next = ((TagElement) actualElement).nextElement();

			// Jak jest jakis element w rozwazanym
			if (next != null)
				return next;
		}

		// Przeszlo przez nie TagElement lub srodek TagElementu przeszlo
		// Wez nastepny jak jest lub null
		if (iter.hasNext()) {
			actualElement = iter.next();
			return actualElement;
		} else {
			return null;
		}
	}

	public void setAttributes(Map<String, String> attribs) {
		this.attributes = attribs;
	}

	public Map<String, String> getAttributes() {
		return attributes;
	}

	public void setLevel(int l) {
		this.level = l;

		for (Element e : elements)
			e.setLevel(l + 1);
	}

	public String toString() {
		StringBuilder str = new StringBuilder();

		str.append(Utilities.genTabs(level));
		str.append("<");
		str.append(tag);

		for (Map.Entry<String, String> entry : attributes.entrySet()) {
			str.append(" ");
			str.append(entry.getKey());
			str.append("=");
			str.append("\"" + entry.getValue().replace("\"", "\\\"") + "\"");
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

	@Override
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

}
