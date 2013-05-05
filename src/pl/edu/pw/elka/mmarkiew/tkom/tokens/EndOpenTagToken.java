package pl.edu.pw.elka.mmarkiew.tkom.tokens;

public class EndOpenTagToken extends Token {

	public EndOpenTagToken(String value) {
		super(value);
	}


	public String toString() {
		return "</" + value;
	}

}