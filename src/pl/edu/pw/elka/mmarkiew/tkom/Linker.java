package pl.edu.pw.elka.mmarkiew.tkom;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Pattern;

import javax.swing.JOptionPane;

import pl.edu.pw.elka.mmarkiew.tkom.elements.CommentElement;
import pl.edu.pw.elka.mmarkiew.tkom.elements.Element;
import pl.edu.pw.elka.mmarkiew.tkom.elements.TagElement;
import pl.edu.pw.elka.mmarkiew.tkom.elements.TextElement;
import pl.edu.pw.elka.mmarkiew.tkom.elements.TreeElement;

/**
 * Class represents main program logic, it's sth like code generator
 * 
 * @author Mikolaj Markiewicz
 * 
 */
public class Linker {

	/**
	 * Examined conflict counter
	 */
	private Map<Integer, Integer> conflictMap;

	/**
	 * Examied conflict trace
	 */
	private String conflictTrace;

	/**
	 * First tree used in merge
	 */
	private TreeElement first;

	/**
	 * Second tree used in merge
	 */
	private TreeElement second;

	/**
	 * Result merge tree
	 */
	private TreeElement result;

	/**
	 * Actual element - used as reference to add elements to it and move around
	 * result tree
	 */
	private TagElement actualResult;

	/**
	 * C-tor
	 * 
	 * @param first
	 *            First tree to merge it
	 * 
	 * @param second
	 *            Second tree to merge with
	 */
	public Linker(TreeElement first, TreeElement second) {
		this.first = first;
		this.second = second;
		this.result = new TreeElement();

		this.conflictTrace = "";
		this.conflictMap = new TreeMap<>();
		for (int i = 1; i < 13; ++i)
			if (i == 8)
				;// deprecated conflict
			else if (i == 3 || i == 4 || i == 5)
				;// merge attributes conflict
			else
				this.conflictMap.put(i, 0);
	}

	/**
	 * Start merging trees
	 * 
	 * @return Merged tree
	 */
	public TreeElement generateResult() {
		// Choose doctype
		conflictDoctype1();

		// Go through root elements of each tree
		goThroughElement(first.getElements().iterator(), second.getElements()
				.iterator());

		return result;
	}

	/**
	 * Go through elements of each element
	 * 
	 * @param fir
	 *            First element children iterator
	 * @param sec
	 *            Second element children iterator
	 */
	private void goThroughElement(Iterator<Element> fir, Iterator<Element> sec) {
		while (fir.hasNext() || sec.hasNext()) {
			// While there is any element

			if (!fir.hasNext()) {
				// If there are elements only in second tree, add them to result
				conflictInnerAmount11(sec.next());
			} else if (!sec.hasNext()) {
				// If there are elements only in first tree, add them to result
				conflictInnerAmount11(fir.next());
			} else {
				// If there are elements in both trees, compare them
				compareThem(fir.next(), sec.next());
			}
		}
	}

	/**
	 * Increment occurrence of conflict in counter map
	 * 
	 * @param i
	 *            Conflict number
	 */
	private void incrementConflictCounter(int i) {
		conflictMap.put(i, conflictMap.get(i) + 1);

		conflictTrace += i + " -> ";
	}

	/**
	 * Get conflict counter map
	 * 
	 * @return Map with counters
	 */
	public Map<Integer, Integer> getConflictCounterMap() {
		return conflictMap;
	}

	/**
	 * Get conflict trace of used conflicts
	 * 
	 * @return Trace string
	 */
	public String getConflictTrace() {
		return conflictTrace;
	}

	/**
	 * Resolve conflict via no. 1 in conflict table
	 */
	private void conflictDoctype1() {
		// Doctype via 1.
		result.setDoctype(second.getDoctype());

		incrementConflictCounter(1);
	}

	/**
	 * Resolve conflict via no. 2 to 5 in conflict table
	 * 
	 * @param fe
	 *            First element
	 * 
	 * @param se
	 *            Second element
	 * 
	 * @param resElement
	 *            Result element to put result inside
	 */
	private void conflictAttributes2_5(TagElement fe, TagElement se,
			TagElement resElement) {
		// Merge attributes via 2.-5.
		Map<String, String> res = new TreeMap<String, String>(
				fe.getAttributes());

		for (Map.Entry<String, String> entry : se.getAttributes().entrySet())
			res.put(entry.getKey(), entry.getValue());

		resElement.setAttributes(res);

		incrementConflictCounter(2);
	}

	/**
	 * Resolve conflict via no. 6 in conflict table
	 * 
	 * @param fe
	 *            First element
	 * 
	 * @param se
	 *            Second element
	 */
	private void conflictGoThroughInner6(TagElement fe, TagElement se) {
		// Inner via 6.
		TagElement resElement = new TagElement(fe.getTag(), actualResult);

		conflictAttributes2_5(fe, se, resElement);

		newActualResult(resElement);

		goThroughElement(fe.getElements().iterator(), se.getElements()
				.iterator());

		parentActualResult();

		incrementConflictCounter(6);
	}

	/**
	 * Resolve conflict via no. 7 in conflict table
	 * 
	 * @param fe
	 *            First element
	 * 
	 * @param se
	 *            Second element
	 */
	private void conflictText7(TextElement fe, TextElement se) {
		// Conflict via 7.
		TextElement resElement = new TextElement(fe.getContent(), actualResult);

		String f = fe.toString().replace('\n', ' ').trim();
		String s = se.toString().replace('\n', ' ').trim();

		if (!f.equals(s)) {
			String[] a = new String[] { "1", "2", "1 + 2", "2 + 1" };

			String message = "<html><body>";
			message += "Place of last considered tag character:<br />";
			message += "&lt;1&gt; in line: " + fe.getLineNumber() + " on col: "
					+ fe.getLinePosition() + "<br />";
			message += "&lt;2&gt; in line: " + se.getLineNumber() + " on col: "
					+ se.getLinePosition() + "<br />";

			if (fe instanceof CommentElement) {
				message += "<strong>First one is comment so it's hidden</strong>. ";
				message += "It's this:<br />&lt;!--"
						+ f.replaceAll("^(<!--)|(-->)$", "") + "--&gt;";
			} else if (se instanceof CommentElement) {
				message += "<strong>First one is comment so it's hidden</strong>";
				message += "It's this:<br />&lt;!--"
						+ s.replaceAll("^(<!--)|(-->)$", "") + "--&gt;";
			}

			message += "<ul>" + "<li>" + f + "</li>" + "<li>" + s + "</li>"
					+ "<li>" + f + s + "</li>" + "<li>" + s + f + "</li>"
					+ "</ul>" + "</body></html>";

			int c = JOptionPane.showOptionDialog(null, message,
					"Conflict no.7 - Text comparison", 0, 0, null, a, null);

			switch (c) {
			case 1:
				resElement.setContent(s);
				break;
			case 2:
				resElement.setContent(f + s);
				break;
			case 3:
				resElement.setContent(s + f);
				break;
			case 0:
			default:
				resElement.setContent(f);
			}
		} else {
			// Same texts
		}

		addElementToResult(resElement);

		incrementConflictCounter(7);
	}

	/**
	 * Helper to compare tag within non tag element<br />
	 * Used to add first and another in proper order
	 * 
	 * @param tag
	 *            First element
	 * 
	 * @param text
	 *            Second element
	 */
	private void conflictTagTextHelper(TagElement tag, TextElement text) {
		if (tag.hasOnlyText()) {
			// First has only text element inside
			if (((TagElement) tag).getTextElementContent().trim()
					.equals(((TextElement) text).getContent().trim())) {
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

	/**
	 * Resolve conflict via no. 9 in conflict table
	 * 
	 * @param tag
	 *            Element to add
	 */
	private void conflictText9(TagElement tag) {
		// Via 9.
		addElementToResult(tag.clone());

		incrementConflictCounter(9);
	}

	/**
	 * Resolve conflict via no. 10 in conflict table
	 * 
	 * @param tag
	 *            Element to add
	 * 
	 * @param text
	 *            Element to element
	 */
	private void conflictText10(TagElement tag, TextElement text) {
		// Via 10.
		addElementToResult(tag.clone());
		addElementToResult(text.clone());

		incrementConflictCounter(10);
	}

	/**
	 * Resolve conflict via no. 11 in conflict table
	 * 
	 * @param e
	 *            Element to add
	 */
	private void conflictInnerAmount11(Element e) {
		// Just add via 11.
		addElementToResult(e.clone());

		incrementConflictCounter(11);
	}

	/**
	 * Resolve conflict via no. 12 in conflict table
	 * 
	 * @param fe
	 *            First element
	 * 
	 * @param se
	 *            Second element
	 */
	private void conflictTagNames12(TagElement fe, TagElement se) {
		// Conflict, via 12.
		String[] a = new String[] { "<1> <2>", "<1>", "<2>",
				"<1> InnerOf2 ... </1>" };

		String f = fe.toString().replace('\n', ' ');
		String s = se.toString().replace('\n', ' ');

		String message = "<html><body>";
		message += "Place of last considered tag character:<br />";
		message += "&lt;1&gt; in line: " + fe.getLineNumber() + " on col: "
				+ fe.getLinePosition() + "<br />";
		message += "&lt;2&gt; in line: " + se.getLineNumber() + " on col: "
				+ se.getLinePosition() + "<br />";

		if (fe.hasScriptElement())
			message += "<strong>First contains script element (invisible)</strong>.<br />";
		if (se.hasScriptElement())
			message += "<strong>Second contains script element (invisible)</strong>.<br />";
		if (fe.hasStyleElement())
			message += "<strong>First contains style element (invisible)</strong>.<br />";
		if (se.hasStyleElement())
			message += "<strong>Second contains style element (invisible)</strong>.<br />";

		message += "<ul>"
				+ "<li>"
				+ f
				+ s
				+ "</li>"
				+ "<li>"
				+ f
				+ "</li>"
				+ "<li>"
				+ s
				+ "</li>"
				+ "<li>"
				+ "&lt;2&gt; <br /> Comparison between &lt;1&gt; and &lt;2.1&gt; <br /> &lt;/2&gt;"
				+ "</li>" + "</ul>" + "</body></html>";

		/*
		 * Script, avoid JDialog exceptions
		 */
		message = Pattern
				.compile(
						"(<script.*?</script\\s*>)|(<style.*?</style\\s*>)|(<meta.*?/>)|(<title.*?</title>)",
						Pattern.DOTALL).matcher(message).replaceAll("");

		int c = JOptionPane.showOptionDialog(null, message,
				"Conflict no.12 - Different tags comparison.", 0, 0, null, a,
				null);

		switch (c) {
		case 1:
			addElementToResult(fe.clone());
			break;
		case 2:
			addElementToResult(se.clone());
			break;
		case 3:
			TagElement resElement = se.clone();
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
		case 0:
		default:
			addElementToResult(fe.clone());
			addElementToResult(se.clone());
		}

		incrementConflictCounter(12);
	}

	/**
	 * Compare two elements
	 * 
	 * @param fe
	 *            First element
	 * 
	 * @param se
	 *            Second element
	 */
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
			} else if (!(fe instanceof TagElement)
					&& !(se instanceof TagElement)) {
				// One of them is TextElement, another is CommentElement
				conflictText7((TextElement) fe, (TextElement) se);
			} else {
				// Both are TagElement
				conflictTagNames12((TagElement) fe, (TagElement) se);
			}
		}
	}

	/**
	 * Add given element to actual result element<br />
	 * Bind his parent also
	 * 
	 * @param tag
	 *            Element to add
	 */
	private void addElementToResult(Element tag) {
		tag.setParent(actualResult);

		if (actualResult == null) {
			result.addElement(tag);
			actualResult = (TagElement) tag;
		} else {
			actualResult.addElement(tag);
		}
	}

	/**
	 * Change actual result to given one
	 * 
	 * @param tag
	 *            New actual result element
	 */
	private void newActualResult(TagElement tag) {
		addElementToResult(tag);
		actualResult = tag;
	}

	/**
	 * Go on the top level of actual result<br />
	 * Change actual result to it's parent to do that
	 */
	private void parentActualResult() {
		actualResult = (TagElement) actualResult.getParent();
	}

	/**
	 * Get generated result
	 * 
	 * @return Generated result
	 */
	public TreeElement getResult() {
		return result;
	}

}
