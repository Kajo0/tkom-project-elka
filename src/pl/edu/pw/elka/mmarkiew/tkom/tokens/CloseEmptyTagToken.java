package pl.edu.pw.elka.mmarkiew.tkom.tokens;

public class CloseEmptyTagToken extends Token {

	public CloseEmptyTagToken(String value) {
		super(value);
	}

	public String toString() {
		return " />";
	}

}