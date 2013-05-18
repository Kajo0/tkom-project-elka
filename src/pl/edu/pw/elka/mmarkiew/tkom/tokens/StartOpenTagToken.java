package pl.edu.pw.elka.mmarkiew.tkom.tokens;

/**
 * Class represents StartOpenTagToken : <tag
 * 
 * @author Mikolaj Markiewicz
 * 
 */
public class StartOpenTagToken extends Token {

	/**
	 * C-tor
	 * 
	 * @param value
	 */
	public StartOpenTagToken(String value) {
		super(value);
	}

	public String toString() {
		return "<" + value;
	}

}