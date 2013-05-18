package pl.edu.pw.elka.mmarkiew.tkom.tokens;

public class AttributeValueToken extends Token {

	private char quote;
	
	public AttributeValueToken(String value, char quote) {
		super(value);
		
		this.quote = quote;
	}

	public String toString() {
		return this.quote + value + this.quote;
	}

}