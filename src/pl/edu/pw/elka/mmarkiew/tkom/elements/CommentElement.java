package pl.edu.pw.elka.mmarkiew.tkom.elements;

import pl.edu.pw.elka.mmarkiew.tkom.Utilities;

public class CommentElement extends TextElement {

	public CommentElement(String content, Element parent) {
		super(content, parent);
		this.tag = "#comment";
	}

	public CommentElement clone() {
		return new CommentElement(content, null);
	}
	
	public String getContent() {
		return "<!--" + content + "-->";
	}

	public String toString() {
		return Utilities.genTabs(level) + "<!--" + content + "-->" + "\n";
	}

}
