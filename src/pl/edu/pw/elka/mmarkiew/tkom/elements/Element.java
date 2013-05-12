package pl.edu.pw.elka.mmarkiew.tkom.elements;

public abstract class Element {

	protected String tag;
	protected boolean empty;
	protected Element parent = null;
	protected int level = 0;

	public Element() {
	}

	public Element(String tag, Element parent) {
		this.parent = parent;
		this.tag = tag;

		if (parent == null)
			this.level = 0;
		else
			this.level = parent.getLevel() + 1;
	}

	public abstract Element clone();

	public void setTag(String tag) {
		this.tag = tag;
	}

	public String getTag() {
		return tag;
	}

	public void setEmpty(boolean isEmpty) {
		this.empty = isEmpty;
	}

	public boolean isEmpty() {
		return empty;
	}

	public void setParent(Element parent) {
		this.parent = parent;
		setLevel((parent == null ? 0 : parent.getLevel() + 1));
	}

	public Element getParent() {
		return parent;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int l) {
		this.level = l;
	}

}
