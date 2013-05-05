package pl.edu.pw.elka.mmarkiew.tkom;

import java.util.LinkedList;
import java.util.Stack;

import pl.edu.pw.elka.mmarkiew.tkom.elements.TagQuantity;
import pl.edu.pw.elka.mmarkiew.tkom.tokens.AttributeEqualsToken;
import pl.edu.pw.elka.mmarkiew.tkom.tokens.AttributeKeyToken;
import pl.edu.pw.elka.mmarkiew.tkom.tokens.AttributeValueToken;
import pl.edu.pw.elka.mmarkiew.tkom.tokens.CloseEmptyTagToken;
import pl.edu.pw.elka.mmarkiew.tkom.tokens.CloseTagToken;
import pl.edu.pw.elka.mmarkiew.tkom.tokens.CommentToken;
import pl.edu.pw.elka.mmarkiew.tkom.tokens.DoctypeToken;
import pl.edu.pw.elka.mmarkiew.tkom.tokens.EndOpenTagToken;
import pl.edu.pw.elka.mmarkiew.tkom.tokens.StartOpenTagToken;
import pl.edu.pw.elka.mmarkiew.tkom.tokens.TextToken;
import pl.edu.pw.elka.mmarkiew.tkom.tokens.Token;

public class Lexer {

	private String text;
	private int pos;
	private LinkedList<Token> tokens;
	private Stack<String> stack;

	public Lexer(String text) {
		this.text = text;
		this.pos = 0;
		this.tokens = new LinkedList<>();
		this.stack = new Stack<>();
	}

	public void extractTokens() throws Exception {

		findDoctype();
		while (pos < text.length()) {

			try {
				findElement();
			} catch (ArrayIndexOutOfBoundsException e) {
				// EOF
				break;
			}
		}
	}

	private char nextChar() {
		if (pos + 1 >= text.length())
			throw new ArrayIndexOutOfBoundsException("Char after EOS");

		return text.charAt(pos++);
	}

	private void ommitWhitespaces() {
		while (Character.isWhitespace(nextChar()))
			;

		--pos;
	}

	private String gatherWordFromCurrentPosToBound() {
		StringBuilder str = new StringBuilder();

		char c;

		while (!Character.isWhitespace((c = nextChar())) && c != '>'
				&& c != '<' && c != '/')
			str.append(c);

		return str.toString();
	}

	private String gatherWordFromCurrentPosToBound(char bound) {
		StringBuilder str = new StringBuilder();

		char c;

		while ((c = nextChar()) != bound || text.charAt(pos - 2) == '\\')
			str.append(c);

		return str.toString();
	}

	private void findDoctype() throws Exception {
		ommitWhitespaces();

		if (nextChar() == '<' && nextChar() == '!') {
			gatherWordFromCurrentPosToBound();
			String doctype = gatherWordFromCurrentPosToBound('>');
			tokens.add(new DoctypeToken(doctype));
		} else {
			throw new Exception("ERROR");
		}

	}

	private void findElement() throws Exception {
		String word;

		ommitWhitespaces();
		word = gatherWordFromCurrentPosToBound('<');

		if (!word.isEmpty())
			tokens.add(new TextToken(word));

		char c = nextChar();
		// </ end tag
		if (c == '/') {
			word = gatherWordFromCurrentPosToBound('>');
			word.trim();

			popFromStack(word);
			tokens.add(new EndOpenTagToken(word));
			tokens.add(new CloseTagToken(""));
		}
		// Comment tag <!--
		else if (c == '!') {
			StringBuilder wrd = new StringBuilder();
			pos += 2;
			do {
				wrd.append(gatherWordFromCurrentPosToBound('>'));
				wrd.append('>');
			} while (text.charAt(pos - 2) != '-' && text.charAt(pos - 1) != '-');

			wrd.delete(wrd.length() - 3, wrd.length());
			tokens.add(new CommentToken(wrd.toString()));
		} else {
			--pos;
			analyzeElement();
		}
	}

	private void analyzeElement() {
		String word = gatherWordFromCurrentPosToBound();
		--pos;

		if (!TagQuantity.valueOf(word).isSingle())
			putOnStack(word);
		tokens.add(new StartOpenTagToken(word));

		char c;
		while ((c = nextChar()) != '/' && c != '>') {
			ommitWhitespaces();
			c = nextChar();
			--pos;

			if (c == '/' || c == '>')
				break;

			word = gatherWordFromCurrentPosToBound('=');
			tokens.add(new AttributeKeyToken(word));

			tokens.add(new AttributeEqualsToken(""));

			c = nextChar();
			if (c == '"') {
				word = gatherWordFromCurrentPosToBound('"');
			} else if (c == '\'') {
				word = gatherWordFromCurrentPosToBound('\'');
			} else {
				--pos;
				word = gatherWordFromCurrentPosToBound();
				--pos;
			}

			tokens.add(new AttributeValueToken(word));
		}

		if (c == '/') {
			tokens.add(new CloseEmptyTagToken(""));
			pos += 2;
		} else
			tokens.add(new CloseTagToken(""));
	}

	public LinkedList<Token> getTokens() {
		return tokens;
	}

	private void putOnStack(String tag) {
		stack.push(tag);
	}

	private void popFromStack(String tag) throws Exception {
		if (!stack.pop().equals(tag))
			throw new Exception("Bad stack order");
	}

}
