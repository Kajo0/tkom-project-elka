package pl.edu.pw.elka.mmarkiew.tkom.tokens;

/**
 * Class represents CloseEmptyTagToken : />
 * 
 * @author Mikolaj Markiewicz
 * 
 */
public class CloseEmptyTagToken extends Token {

	/**
	 * C-tor
	 * 
	 * @param value
	 */
	public CloseEmptyTagToken(String value) {
		super(value);
	}

	public String toString() {
		return " />";
	}

}