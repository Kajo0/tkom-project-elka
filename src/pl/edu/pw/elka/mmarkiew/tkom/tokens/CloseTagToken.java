package pl.edu.pw.elka.mmarkiew.tkom.tokens;

/**
 * Class represents CloseTagToken : >
 * 
 * @author Mikolaj Markiewicz
 * 
 */
public class CloseTagToken extends Token {

	/**
	 * C-tor
	 * 
	 * @param value
	 */
	public CloseTagToken(String value) {
		super(value);
	}

	public String toString() {
		return ">";
	}

}