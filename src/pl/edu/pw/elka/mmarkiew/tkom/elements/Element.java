package pl.edu.pw.elka.mmarkiew.tkom.elements;

/**
 * Base class represents html element
 * 
 * @author Mikolaj Markiewicz
 * 
 */
public abstract class Element {

	/**
	 * Name of tag
	 */
	protected String tag;

	/**
	 * Whether element is empty - has no end tag
	 */
	protected boolean empty;

	/**
	 * Parent of element
	 */
	protected Element parent = null;

	/**
	 * Element level - defines indent
	 */
	protected int level = 0;

	/**
	 * Number of line where occurred
	 */
	protected int lineNumber;

	/**
	 * Position in line where occurred
	 */
	protected int linePosition;

	/**
	 * Position in file where occured
	 */
	protected int globalPosition;

	/**
	 * C-tor
	 */
	public Element() {
	}

	/**
	 * C-tor
	 * 
	 * @param tag
	 *            Tag name
	 * @param parent
	 *            Parent of element
	 */
	public Element(String tag, Element parent) {
		this.parent = parent;
		this.tag = tag;

		if (parent == null)
			this.level = 0;
		else
			this.level = parent.getLevel() + 1;
	}

	/**
	 * Clone function
	 */
	public abstract Element clone();

	/**
	 * Set tag name of element
	 * 
	 * @param tag
	 *            New name of tag
	 */
	public void setTag(String tag) {
		this.tag = tag;
	}

	/**
	 * Get name of element tag
	 * 
	 * @return Tag name
	 */
	public String getTag() {
		return tag;
	}

	/**
	 * Set whether element is empty or not
	 * 
	 * @param isEmpty
	 *            True if want to set empty, false otherwise
	 */
	public void setEmpty(boolean isEmpty) {
		this.empty = isEmpty;
	}

	/**
	 * Chech whether element is empty
	 * 
	 * @return True if empty, false otherwise
	 */
	public boolean isEmpty() {
		return empty;
	}

	/**
	 * Set new parent to element, parent is null, set 0 level
	 * 
	 * @param parent
	 *            New parent
	 */
	public void setParent(Element parent) {
		this.parent = parent;
		setLevel((parent == null ? 0 : parent.getLevel() + 1));
	}

	/**
	 * Get elements parent
	 * 
	 * @return Parent of element
	 */
	public Element getParent() {
		return parent;
	}

	/**
	 * Get element level
	 * 
	 * @return Get actual element level
	 */
	public int getLevel() {
		return level;
	}

	/**
	 * Set element level
	 * 
	 * @param l
	 *            New level
	 */
	public void setLevel(int l) {
		this.level = l;
	}

	/**
	 * Get line number where token occurred
	 * 
	 * @return Line number
	 */
	public int getLineNumber() {
		return lineNumber;
	}

	/**
	 * Get position in line where token occurred
	 * 
	 * @return Position in line
	 */
	public int getLinePosition() {
		return linePosition;
	}

	/**
	 * Get position in file where occurred
	 * 
	 * @return Global file position
	 */
	public int getGlobalPosition() {
		return globalPosition;
	}

	/**
	 * Set positions
	 * 
	 * @param positions
	 *            Array with positions
	 * 
	 * @return Self element instance
	 */
	public Element setPositions(int[] positions) {
		this.lineNumber = positions[0];
		this.linePosition = positions[1];
		this.globalPosition = positions[2];

		return this;
	}

}
