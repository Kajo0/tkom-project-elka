package pl.edu.pw.elka.mmarkiew.tkom.tokens;

public class DoctypeToken extends Token {

	public DoctypeToken(String value) {
		super(value);
	}

	public String toString() {
		return "<!doctype " + value + ">";
	}

}