package pl.edu.pw.elka.mmarkiew.tkom.tokens;

/**
 * Class represents AttributeKeyToken : key
 * 
 * @author Mikolaj Markiewicz
 * 
 */
public class AttributeKeyToken extends Token {

	/**
	 * C-tor
	 * 
	 * @param value
	 */
	public AttributeKeyToken(String value) {
		super(value);
	}

	public String toString() {
		return " " + value;
	}

}