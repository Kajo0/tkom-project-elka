package pl.edu.pw.elka.mmarkiew.tkom.tokens;

/**
 * Class represents AttributeEqualsToken : =
 * 
 * @author Mikolaj Markiewicz
 * 
 */
public class AttributeEqualsToken extends Token {

	/**
	 * C-tor
	 * 
	 * @param value
	 */
	public AttributeEqualsToken(String value) {
		super(value);
	}

	public String toString() {
		return "=";
	}

}