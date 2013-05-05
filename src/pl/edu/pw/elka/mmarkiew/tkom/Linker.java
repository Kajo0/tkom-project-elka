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
//	private Element actualFirst;
//	private Element actualSecond;
	private TagElement actualResult;

	public Linker(TreeElement first, TreeElement second) {
		this.first = first;
		this.second = second;
		this.result = new TreeElement();
	}

	public void generateResult() {
		// Doctype via 1.
		result.setDoctype(second.getDoctype());

		goThroughElement(first.getElements().iterator(), second.getElements()
				.iterator());
	}

	private void goThroughElement(Iterator<Element> fir, Iterator<Element> sec) {
		// int i = 0;
		while (fir.hasNext() || sec.hasNext()) {
			// System.out.println("\t" + i++);

			Element resElement = null;

			if (!fir.hasNext()) {
				// Just add via 11.
				resElement = sec.next().clone();
			} else if (!sec.hasNext()) {
				// Just add via 11.
				resElement = fir.next().clone();
			} else {
				Element fe = fir.next();
				Element se = sec.next();

				resElement = compareThem(fe, se);
			}

			addElementToResult(resElement);
		}

		parentActualResult();
	}

	private Element compareThem(Element fe, Element se) {
		Element resElement = null;

		if (fe.getTag().equals(se.getTag())) {
			if (fe instanceof TagElement) {
				// Both fe and se are TagElement

				resElement = new TagElement(fe.getTag(), null);

				// Merge attributes via 2.-5.
				((TagElement) resElement).setAttributes(mergeAttributes(
						((TagElement) fe).getAttributes(),
						((TagElement) se).getAttributes()));

				resElement.setParent(actualResult);
				newActualResult((TagElement) resElement);

				// Inner via 6.
				goThroughElement(((TagElement) fe).getElements().iterator(),
						((TagElement) se).getElements().iterator());
			} else {
				// Both are CommentElement or TextElement
				String f = fe.toString();
				String s = se.toString();

				resElement = fe.clone();

				// Conflic via 7.
				if (!f.equals(s)) {
					String[] a = new String[] { f, s, f + s, s + f };
					 int c = JOptionPane.showOptionDialog(null,
					 "ktore?", "text", 0, 0, null, a, null);
					((TextElement) resElement).setContent(a[c]);
				} else {
					// Same texts
				}

				resElement.setParent(actualResult);
			}
		} else {
			// Rozne tagi
			if (se instanceof TagElement && !(fe instanceof TagElement)) {
				if (((TagElement) se).hasOnlyText()) {
					if (((TagElement) se).getTextElementContent().equals(
							((TextElement) fe).getContent())) {
						// Via 9.
						resElement = se.clone();
						resElement.setParent(actualResult);
					} else {
						// Via 10.
						resElement = fe.clone();
						resElement.setParent(actualResult);
						addElementToResult(resElement);

						resElement = se.clone();
						resElement.setParent(actualResult);
					}
				} else {
					// Via 10.
					resElement = fe.clone();
					resElement.setParent(actualResult);
					addElementToResult(resElement);

					resElement = se.clone();
					resElement.setParent(actualResult);
				}
			} else if (fe instanceof TagElement && !(se instanceof TagElement)) {
				if (((TagElement) fe).hasOnlyText()) {
					if (((TagElement) fe).getTextElementContent().equals(
							((TextElement) se).getContent())) {
						// Via 9.
						resElement = fe.clone();
						resElement.setParent(actualResult);
					} else {
						// Via 10.
						resElement = se.clone();
						resElement.setParent(actualResult);
						addElementToResult(resElement);

						resElement = fe.clone();
						resElement.setParent(actualResult);
					}
				} else {
					// Via 10.
					resElement = se.clone();
					resElement.setParent(actualResult);
					addElementToResult(resElement);

					resElement = fe.clone();
					resElement.setParent(actualResult);
				}
			} else {
				// Oba TagElement, via 12
				String[] a = new String[] { "<tag/><tag1/>", "<tag1>",
						"<tag2>", "Wnetrze" };
				int c = JOptionPane.showOptionDialog(null, "ktore?", "text", 0,
						0, null, a, null);

				switch (c) {
				case 0:
					resElement = fe.clone();
					resElement.setParent(actualResult);
					addElementToResult(resElement);

					resElement = se.clone();
					resElement.setParent(actualResult);
					break;
				case 1:
					resElement = fe.clone();
					resElement.setParent(actualResult);
					break;
				case 2:
					resElement = se.clone();
					resElement.setParent(actualResult);
					break;
				case 3:
					resElement = se.clone();
					((TagElement) resElement).clearElements();
					resElement.setParent(actualResult);

					newActualResult((TagElement) resElement);
					((TagElement) resElement).addElement(compareThem(fe, ((TagElement)se).getElements().getFirst()));

					
					// Add rest via 11
					if (((TagElement)se).getElements().size() > 1) {
						LinkedList<Element> elements = ((TagElement)se).getElements();
//						((TagElement)se).clearElements();

//						for (int i = 1; i < ((TagElement)se).getElements().size(); ++i) {
////							Element e = ((TagElement)se).getElements().get(i).clone();
////							e.setParent(actualResult);
////							((TagElement) resElement).addElement(e);
//							
//							((TagElement)se).addElement(((TagElement)se).getElements().get(i));
//
//						}

						elements.pollFirst();
						for (Element e : elements) {
							e.setParent(actualResult);
							addElementToResult(e);
						}
					}
					parentActualResult();

					break;
				}
			}
		}

		return resElement;
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
		actualResult = tag;
	}

	private void parentActualResult() {
		actualResult = (TagElement) actualResult.getParent();
	}

	private Map<String, String> mergeAttributes(
			Map<String, String> firstAttributes,
			Map<String, String> secondAttributes) {

		Map<String, String> res = new TreeMap<String, String>(firstAttributes);

		for (Map.Entry<String, String> entry : secondAttributes.entrySet())
			res.put(entry.getKey(), entry.getValue());

		return res;
	}

	public TreeElement getResult() {
		return result;
	}

}
