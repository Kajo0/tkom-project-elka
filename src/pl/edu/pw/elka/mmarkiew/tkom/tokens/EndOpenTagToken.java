package pl.edu.pw.elka.mmarkiew.tkom.tokens;

/**
 * Class represents EndOpenTagToken : </tag
 * 
 * @author Mikolaj Markiewicz
 * 
 */
public class EndOpenTagToken extends Token {

	/**
	 * C-tor
	 * 
	 * @param value
	 */
	public EndOpenTagToken(String value) {
		super(value);
	}

	public String toString() {
		return "</" + value;
	}

}