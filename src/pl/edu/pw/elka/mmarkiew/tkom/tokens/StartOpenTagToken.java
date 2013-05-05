package pl.edu.pw.elka.mmarkiew.tkom.tokens;

public class StartOpenTagToken extends Token {

	public StartOpenTagToken(String value) {
		super(value);
	}

	public String toString() {
		return "<" + value;
	}

}