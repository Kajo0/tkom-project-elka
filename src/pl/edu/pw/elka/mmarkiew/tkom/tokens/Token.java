package pl.edu.pw.elka.mmarkiew.tkom.tokens;

public abstract class Token {

	protected String value;

	public Token(String value) {
		this.value = value;
	}
	
	public String getValue() {
		return value;
	}

	public String toString() {
		return value;
	}

}