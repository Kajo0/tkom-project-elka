package pl.edu.pw.elka.mmarkiew.tkom.tokens;

/**
 * Base class represents Token
 * 
 * @author Mikolaj Markiewicz
 * 
 */
public abstract class Token {

	/**
	 * Value of token if necessary to hold it
	 */
	protected String value;

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
	 * 
	 * @param value
	 *            Value of token
	 */
	public Token(String value) {
		this.value = value;
	}

	/**
	 * Set token position
	 * 
	 * @param lineNum
	 *            Line number
	 * 
	 * @param linePos
	 *            Position in line
	 * 
	 * @param pos
	 *            Global position
	 * 
	 * @return Self token instance
	 */
	public Token setPosition(int lineNum, int linePos, int pos) {
		this.lineNumber = lineNum;
		this.linePosition = linePos;
		this.globalPosition = pos;

		return this;
	}

	/**
	 * Get token value
	 * 
	 * @return Token value
	 */
	public String getValue() {
		return value;
	}

	public String toString() {
		return value;
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
	 * Get array of position numbers
	 * 
	 * @return Array of positions on line, in line, in file
	 */
	public int[] getPositions() {
		return new int[] { lineNumber, linePosition, globalPosition };
	}

}