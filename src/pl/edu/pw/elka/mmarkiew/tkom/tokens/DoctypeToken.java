package pl.edu.pw.elka.mmarkiew.tkom.tokens;

/**
 * Class represents DoctypeToken : <!doctype
 * 
 * @author Mikolaj Markiewicz
 * 
 */
public class DoctypeToken extends Token {

	/**
	 * C-tor
	 * 
	 * @param value
	 */
	public DoctypeToken(String value) {
		super(value);
	}

	public String toString() {
		return "<!doctype " + value + ">";
	}

}