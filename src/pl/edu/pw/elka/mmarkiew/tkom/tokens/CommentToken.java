package pl.edu.pw.elka.mmarkiew.tkom.tokens;

/**
 * Class represents CommentToken : #comment
 * 
 * @author Mikolaj Markiewicz
 * 
 */
public class CommentToken extends Token {

	/**
	 * C-tor
	 * 
	 * @param value
	 */
	public CommentToken(String value) {
		super(value);
	}

	public String toString() {
		return "<!--" + value + "-->";
	}

}