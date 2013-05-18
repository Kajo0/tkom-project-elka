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
	 * C-tor
	 * 
	 * @param value
	 */
	public Token(String value) {
		this.value = value;
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

}