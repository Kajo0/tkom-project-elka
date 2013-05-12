package pl.edu.pw.elka.mmarkiew.tkom;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.TreeMap;

import javax.swing.JOptionPane;

import pl.edu.pw.elka.mmarkiew.tkom.elements.Element;
import pl.edu.pw.elka.mmarkiew.tkom.elements.TagElement;
import pl.edu.pw.elka.mmarkiew.tkom.elements.TextElement;
import pl.edu.pw.elka.mmarkiew.tkom.elements.TreeElement;

public class Linker {

	private TreeElement first;
	private TreeElement second;
	private TreeElement result;
	private TagElement actualResult;

	public Linker(TreeElement first, TreeElement second) {
		this.first = first;
		this.second = second;
		this.result = new TreeElement();
	}

	public void generateResult() {
		conflictDoctype1();

		goThroughElement(first.getElements().iterator(), second.getElements()
				.iterator());
	}

	private void goThroughElement(Iterator<Element> fir, Iterator<Element> sec) {
		while (fir.hasNext() || sec.hasNext()) {
			if (!fir.hasNext()) {
				conflictInnerAmount11(sec.next());
			} else if (!sec.hasNext()) {
				conflictInnerAmount11(fir.next());
			} else {
				compareThem(fir.next(), sec.next());
			}
		}
	}

	// Doctype via 1.
	private void conflictDoctype1() {
		result.setDoctype(second.getDoctype());
	}

	// Merge attributes via 2.-5.
	private void conflictAttributes2_5(TagElement fe, TagElement se,
			TagElement resElement) {

		Map<String, String> res = new TreeMap<String, String>(
				fe.getAttributes());

		for (Map.Entry<String, String> entry : se.getAttributes().entrySet())
			res.put(entry.getKey(), entry.getValue());

		resElement.setAttributes(res);
	}

	// Inner via 6.
	private void conflictGoThroughInner6(TagElement fe, TagElement se) {
		TagElement resElement = new TagElement(fe.getTag(), actualResult);

		conflictAttributes2_5(fe, se, resElement);

		newActualResult(resElement);

		goThroughElement(fe.getElements().iterator(), se.getElements()
				.iterator());

		parentActualResult();
	}

	// Conflict via 7.
	private void conflictText7(TextElement fe, TextElement se) {
		TextElement resElement = fe.clone();

		String f = fe.toString();
		String s = se.toString();

		if (!f.equals(s)) {// TODO
			String[] a = new String[] { f, s, f + s, s + f };
			int c = JOptionPane.showOptionDialog(null, "ktore?", "text", 0, 0,
					null, a, null);

			resElement.setContent(a[c]);
		} else {
			// Same texts
		}

		addElementToResult(resElement);
	}

	// Helper to compare tag with non tag element
	private void conflictTagTextHelper(TagElement tag, TextElement text) {
		if (tag.hasOnlyText()) {
			// First has only text element inside
			if (((TagElement) tag).getTextElementContent().equals(
					((TextElement) text).getContent())) {
				// Texts are equal
				conflictText9(tag);
			} else {
				// Texts are different
				conflictText10(tag, text);
			}
		} else {
			// Tag contains not only text
			conflictText10(tag, text);
		}
	}

	// Via 9.
	private void conflictText9(TagElement tag) {
		addElementToResult(tag.clone());
	}

	// Via 10.
	private void conflictText10(TagElement tag, TextElement text) {
		addElementToResult(text.clone());
		addElementToResult(tag.clone());
	}

	// Just add via 11.
	private void conflictInnerAmount11(Element e) {
		addElementToResult(e.clone());
	}

	// Conflict, via 12.
	private void conflictTagNames12(TagElement fe, TagElement se) {
		String[] a = new String[] { "<tag/><tag1/>", "<tag1>", "<tag2>",
				"Wnetrze" };
		int c = JOptionPane.showOptionDialog(null, "ktore?", "text", 0, 0,
				null, a, null);

		TagElement resElement;
		switch (c) {
		case 0:
			addElementToResult(fe.clone());
			addElementToResult(se.clone());
			break;
		case 1:
			addElementToResult(fe.clone());
			break;
		case 2:
			addElementToResult(se.clone());
			break;
		case 3:
			resElement = se.clone();
			resElement.clearElements();

			newActualResult(resElement);

			compareThem(fe, se.getElements().getFirst());

			// Add rest via 11
			if (se.getElements().size() > 1) {
				LinkedList<Element> elements = se.getElements();

				elements.pollFirst();
				for (Element e : elements)
					addElementToResult(e);
			}

			parentActualResult();

			break;
		}
	}

	private void compareThem(Element fe, Element se) {
		if (fe.getTag().equals(se.getTag())) {
			// Both has same tag
			if (fe instanceof TagElement) {
				// Both fe and se are TagElement
				conflictGoThroughInner6((TagElement) fe, (TagElement) se);
			} else {
				// Both are CommentElement or TextElement
				conflictText7((TextElement) fe, (TextElement) se);
			}
		} else {
			if (se instanceof TagElement && !(fe instanceof TagElement)) {
				// Different tags
				// Second is TagElement, first isn't
				conflictTagTextHelper((TagElement) se, (TextElement) fe);
			} else if (fe instanceof TagElement && !(se instanceof TagElement)) {
				// Different tags
				// First is TagElement, second isn't
				conflictTagTextHelper((TagElement) fe, (TextElement) se);
			} else {
				// Both are TagElement
				conflictTagNames12((TagElement) fe, (TagElement) se);
			}
		}
	}

	private void addElementToResult(Element tag) {
		tag.setParent(actualResult);

		if (actualResult == null) {
			result.addElement(tag);
			actualResult = (TagElement) tag;
		} else {
			actualResult.addElement(tag);
		}
	}

	private void newActualResult(TagElement tag) {
		addElementToResult(tag);
		actualResult = tag;
	}

	private void parentActualResult() {
		actualResult = (TagElement) actualResult.getParent();
	}

	public TreeElement getResult() {
		return result;
	}

}
