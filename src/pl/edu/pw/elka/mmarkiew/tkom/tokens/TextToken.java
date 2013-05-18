package pl.edu.pw.elka.mmarkiew.tkom.tokens;

/**
 * Class represents TextToken : #text
 * 
 * @author Mikolaj Markiewicz
 * 
 */
public class TextToken extends Token {

	/**
	 * C-tor
	 * 
	 * @param value
	 */
	public TextToken(String value) {
		super(value.trim());
	}

}