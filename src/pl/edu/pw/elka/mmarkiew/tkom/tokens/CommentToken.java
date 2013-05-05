package pl.edu.pw.elka.mmarkiew.tkom.tokens;

public class CommentToken extends Token {

	public CommentToken(String value) {
		super(value);
	}

	public String toString() {
		return "<!--" + value + "-->";
	}

}