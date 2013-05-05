package pl.edu.pw.elka.mmarkiew.tkom.tokens;

public class AttributeValueToken extends Token {

	public AttributeValueToken(String value) {
		super(value);
	}

	public String toString() {
		return "\"" + value + "\"";
	}

}