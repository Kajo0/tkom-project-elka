package pl.edu.pw.elka.mmarkiew.tkom.tokens;

/**
 * Class represents AttributeValueToken : "value" | 'value' | value
 * 
 * @author Mikolaj Markiewicz
 * 
 */
public class AttributeValueToken extends Token {

	/**
	 * Quote char used to wrap value
	 */
	private char quote;

	/**
	 * C-tor
	 * 
	 * @param value
	 * @param quote
	 */
	public AttributeValueToken(String value, char quote) {
		super(value);

		this.quote = quote;
	}

	/**
	 * Get quote used to wrap value
	 * 
	 * @return Used quote
	 */
	public char getQuote() {
		return this.quote;
	}

	public String toString() {
		return this.quote + value + this.quote;
	}

}