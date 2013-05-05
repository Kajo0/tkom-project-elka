package pl.edu.pw.elka.mmarkiew.tkom.elements;

import pl.edu.pw.elka.mmarkiew.tkom.Utilities;

public class TextElement extends Element {

	protected String content;

	{
		empty = true;
	}

	public TextElement(String content, Element parent) {
		super("#text", parent);
		this.content = content;
	}

	public void setEmpty(boolean isEmpty) {
		// Always empty
	}
	
	public void setContent(String content) {
		this.content = content;
	}
	
	public String getContent() {
		return content;
	}

	public String toString() {
		return Utilities.genTabs(level) + content + "\n";
	}

	public TextElement clone() {
		return new TextElement(content, null);
	}

}
