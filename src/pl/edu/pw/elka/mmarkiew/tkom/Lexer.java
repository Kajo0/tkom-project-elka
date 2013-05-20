package pl.edu.pw.elka.mmarkiew.tkom;

import java.util.Arrays;
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

/**
 * Class responsible for tokenize given text
 * 
 * @author Mikolaj Markiewicz
 * 
 */
public class Lexer {

	/**
	 * Number of actual line
	 */
	private int lineNumber;

	/**
	 * Position of actual character in line
	 */
	private int linePosition;

	/**
	 * When illegal tag occurred, to show was it
	 */
	private String illegalTag;

	/**
	 * Whitespace characters
	 */
	public static final LinkedList<Character> whitespaceCharacters = new LinkedList<>(
			Arrays.asList(' ', '\t', '\n', '\r'));

	// public static final LinkedList<Character> alphanumCharacters = new
	// LinkedList<>(
	// Arrays.asList('A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J',
	// 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V',
	// 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h',
	// 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't',
	// 'u', 'v', 'w', 'x', 'y', 'z', '0', '1', '2', '3', '4', '5',
	// '6', '7', '8', '9', '-', '_'));

	/**
	 * Text to analyze
	 */
	private String text;

	/**
	 * Actual position in given text
	 */
	private int pos;

	/**
	 * List of extracted tokens
	 */
	private LinkedList<Token> tokens;

	/**
	 * Stack to check proper order of tags
	 */
	private Stack<String> stack;

	/**
	 * C-tor
	 * 
	 * @param text
	 *            Text to analyze
	 */
	public Lexer(String text) {
		this.text = text + "          "; // Out of bound avoider
		this.pos = 0;
		this.tokens = new LinkedList<>();
		this.stack = new Stack<>();
		this.lineNumber = 1;
		this.linePosition = 0;
		this.illegalTag = "";
	}

	/**
	 * Extract tokens from given text
	 * 
	 * @return List of extracted tokens
	 */
	public LinkedList<Token> extractTokens() {
		findDoctype();

		// While there is character to analyze
		while (pos < text.length()) {
			try {
				findElement();
			} catch (ArrayIndexOutOfBoundsException e) {
				// EOF
				break;
			} catch (IllegalArgumentException e) {
				throw new RuntimeException("No html tag defined: " + "<"
						+ this.illegalTag + ">" + positionDebugString());
			}
		}

		return tokens;
	}

	/**
	 * Move pointer to the next character
	 * 
	 * @throws ArrayIndexOutOfBoundsException
	 *             when end of characters in text
	 * 
	 * @return Actual character before next move
	 */
	private char popChar() {
		if (pos + 1 >= text.length())
			throw new ArrayIndexOutOfBoundsException("Char after EOS");

		if (text.charAt(pos) == '\n') {
			++lineNumber;
			linePosition = 0;
		} else {
			++linePosition;
		}

		return text.charAt(pos++);
	}

	/**
	 * Get actual character
	 * 
	 * @return Actual character
	 */
	private char getChar() {
		return text.charAt(pos);
	}

	/**
	 * Omit whitespace characters<br />
	 * Pointer stop on the first non whitespace character
	 */
	private void ommitWhitespaces() {
		while (Character.isWhitespace(getChar()))
			popChar();
	}

	/**
	 * Gather word from actual position to any character on given list
	 * 
	 * @param bounds
	 *            List of boundary characters
	 * 
	 * @return Gathered char sequence
	 */
	private String gatherBound(char[] bounds) {
		LinkedList<Character> bound = new LinkedList<>();
		for (char c : bounds)
			bound.add(c);

		StringBuilder str = new StringBuilder();

		while (!bound.contains(getChar()))
			str.append(popChar());

		return str.toString();
	}

	/**
	 * Gather to given char boundary list plus whitespaces
	 * 
	 * @param bounds
	 *            List of boundary characters
	 * 
	 * @return Gathered char sequence
	 */
	private String gatherBoundWhite(char[] bounds) {
		LinkedList<Character> bound = new LinkedList<>(whitespaceCharacters);
		for (char c : bounds)
			bound.add(c);

		char bc[] = new char[bound.size()];
		for (int i = 0; i < bound.size(); ++i)
			bc[i] = bound.get(i);

		return gatherBound(bc);
	}

	/**
	 * Find doctype part on the beginning of document
	 * 
	 * @throws RuntimeException
	 *             if bad doctype word, or no doctype found
	 */
	private void findDoctype() {
		ommitWhitespaces();

		if (popChar() == '<' && popChar() == '!') {
			String doc = gatherBoundWhite(new char[] { '>' });

			if (!doc.equalsIgnoreCase("doctype"))
				throw new RuntimeException("Bad doctype tag"
						+ positionDebugString());

			ommitWhitespaces();

			String type = gatherBound(new char[] { '>' });
			popChar();

			tokens.add(new DoctypeToken(type).setPosition(lineNumber, linePosition, pos));
		} else {
			throw new RuntimeException("No doctype found!"
					+ positionDebugString());
		}
	}

	/**
	 * Find next element starting from < character<br />
	 * If analyzing element, run it recursive
	 * 
	 * @throws RuntimeException
	 *             if no close tag found
	 */
	private void findElement() {
		String word;

		ommitWhitespaces();
		// If not found < - last tag already read, exception is thrown
		word = gatherBound(new char[] { '<' }).trim();

		if (!word.isEmpty()) {
			if (tokens.getLast() instanceof DoctypeToken)
				throw new RuntimeException("Expected Tag Element."
						+ positionDebugString());

			tokens.add(new TextToken(word).setPosition(lineNumber, linePosition, pos));
		}

		// We are on < character
		popChar();

		if (getChar() == '/') {
			// </ end tag
			popChar();
			expectCloseTag();
		} else if (getChar() == '!') {
			// Comment tag <!--
			if (tokens.getLast() instanceof DoctypeToken)
				throw new RuntimeException("Expected Tag Element."
						+ positionDebugString());

			popChar();
			expectComment();
		} else {
			analyzeElement();

			if (getChar() == '>')
				tokens.add(new CloseTagToken("").setPosition(lineNumber, linePosition, pos));
			else if (popChar() == '/' && getChar() == '>')
				tokens.add(new CloseEmptyTagToken("").setPosition(lineNumber, linePosition, pos));
			else
				throw new RuntimeException("Expected close tag."
						+ positionDebugString());

			popChar();

			// Rule exception
			if (stack.peek().equals("script")) {
				handleScriptTag();
			} else {
				findElement();
			}
		}
	}

	/**
	 * Analyze tag element in order to find attributes
	 */
	private void analyzeElement() {
		ommitWhitespaces();

		String word = gatherBoundWhite(new char[] { '/', '>' });

		checkProperTag(word);
		if (!TagQuantity.valueOf(word.toLowerCase()).isSingle())
			putOnStack(word);

		tokens.add(new StartOpenTagToken(word).setPosition(lineNumber, linePosition, pos));

		ommitWhitespaces();

		while (getChar() != '/' && getChar() != '>') {
			ommitWhitespaces();

			String wo = gatherBoundWhite(new char[] { '=', '>', '/' });
			ommitWhitespaces();

			tokens.add(new AttributeKeyToken(wo).setPosition(lineNumber, linePosition, pos));
			tokens.add(new AttributeEqualsToken("").setPosition(lineNumber, linePosition, pos));

			if (getChar() != '=') {
				// No value attribute, proper form => key="key"
				tokens.add(new AttributeValueToken(wo, '\"').setPosition(lineNumber, linePosition, pos));
			} else {
				popChar();
				ommitWhitespaces();

				if (getChar() == '"') {
					// Inside double quotes
					popChar();
					attributeDoubleQuoteExpected();

				} else if (getChar() == '\'') {
					// Inside single quotes
					popChar();
					attributeSingleQuoteExpected();
				} else {
					// Without quote, single word expected
					attributeSingleWordExpected();
				}
			}

			ommitWhitespaces();
		}
	}

	/**
	 * Handle script tag occurrence<br />
	 * Get entire inner text as comment wrapped Text, which doesn't broke
	 * anything
	 */
	private void handleScriptTag() {
		String word = gatherBound(new char[] { '<' });
		popChar();

		while (getChar() != '/' || text.charAt(pos + 1) != 's'
				|| text.charAt(pos + 2) != 'c' || text.charAt(pos + 3) != 'r'
				|| text.charAt(pos + 4) != 'i' || text.charAt(pos + 5) != 'p'
				|| text.charAt(pos + 6) != 't') {
			word += '<' + gatherBound(new char[] { '<' });
			popChar();
		}

		popChar();

		if (word.trim().length() != 0)
			tokens.add(new CommentToken("//<!--\r\n" + word + "\r\n//-->").setPosition(lineNumber, linePosition, pos));

		expectCloseTag();
	}

	/**
	 * Gather attribute inside double quotes
	 * 
	 * @throws RuntimeException
	 *             If no end quote found
	 */
	private void attributeDoubleQuoteExpected() {
		String word = null;

		try {
			word = gatherBound(new char[] { '"' });// , '/', '>' });

			while (text.charAt(pos - 1) == '\\') {
				word += "\"";
				popChar();
				word += gatherBound(new char[] { '"' });// , '/', '>' });
			}
		} catch (IndexOutOfBoundsException e) {
		}

		if (getChar() != '"')
			throw new RuntimeException("Expected double quote character."
					+ positionDebugString());

		popChar();
		tokens.add(new AttributeValueToken(word, '\"').setPosition(lineNumber, linePosition, pos));
	}

	/**
	 * Gather attribute inside single quotes
	 * 
	 * @throws RuntimeException
	 *             If no end quote found
	 */
	private void attributeSingleQuoteExpected() {
		String word = null;

		try {
			word = gatherBound(new char[] { '\'' });// , '/', '>' });

			while (text.charAt(pos - 1) == '\\') {
				word += "\'";
				popChar();
				word += gatherBound(new char[] { '\'' });// , '/', '>' });
			}
		} catch (IndexOutOfBoundsException e) {
		}

		if (getChar() != '\'')
			throw new RuntimeException("Expected single quote character."
					+ positionDebugString());

		popChar();
		tokens.add(new AttributeValueToken(word, '\'').setPosition(lineNumber, linePosition, pos));
	}

	/**
	 * Gather attribute after equal sign without quotes
	 * 
	 * @throws RuntimeException
	 *             If no value found
	 */
	private void attributeSingleWordExpected() {
		String word = gatherBoundWhite(new char[] { '>' });// , '/' });

		if (word.length() == 0)
			throw new RuntimeException("Expected attribute value"
					+ positionDebugString());

		tokens.add(new AttributeValueToken(word, '\"').setPosition(lineNumber, linePosition, pos));
	}

	/**
	 * Find close tag sign : >
	 */
	private void expectCloseTag() {
		String word = gatherBound(new char[] { '>' }).trim();
		popChar();

		checkProperTag(word);
		popFromStack(word);

		tokens.add(new EndOpenTagToken(word).setPosition(lineNumber, linePosition, pos));
		tokens.add(new CloseTagToken("").setPosition(lineNumber, linePosition, pos));
	}

	/**
	 * Gather comment part
	 * 
	 * @throws RuntimeException
	 *             if no comment tag found
	 */
	private void expectComment() {
		StringBuilder wrd = new StringBuilder();

		if (popChar() != '-' || popChar() != '-')
			throw new RuntimeException("Expected comment tag."
					+ positionDebugString());

		try {
			while (true) {
				String s = gatherBound(new char[] { '>' });
				wrd.append(s);

				popChar();

				if (s.length() >= 2 && s.charAt(s.length() - 1) == '-'
						&& s.charAt(s.length() - 2) == '-') {
					wrd.delete(wrd.length() - 2, wrd.length());
					break;
				} else {
					wrd.append('>');
				}
			}
		} catch (ArrayIndexOutOfBoundsException e) {
			throw new RuntimeException("Expected comment end tag."
					+ positionDebugString());
		}

		tokens.add(new CommentToken(wrd.toString()).setPosition(lineNumber, linePosition, pos));
	}

	/**
	 * Check whether is proper tag
	 * 
	 * @throws IllegalArgumentException
	 *             if no enumertation set
	 * 
	 * @param tag
	 *            Tag to check
	 */
	public void checkProperTag(String tag) {
		try {
			TagQuantity.valueOf(tag.toLowerCase());
		} catch (IllegalArgumentException e) {
			this.illegalTag = tag;
			throw e;
		}
	}

	/**
	 * Get list of extracted tokens
	 * 
	 * @return List of tokens
	 */
	public LinkedList<Token> getTokens() {
		return tokens;
	}

	/**
	 * Put double tag on stack to check proper tags order
	 * 
	 * @param tag
	 *            Tag name
	 */
	private void putOnStack(String tag) {
		stack.push(tag);
	}

	/**
	 * Pop double tag from stack to check proper tag order
	 * 
	 * @throws RuntimeException
	 *             if bad stack order
	 * 
	 * @param tag
	 *            Tag name
	 */
	private void popFromStack(String tag) {
		if (!stack.pop().equals(tag))
			throw new RuntimeException("Bad stack order, " + tag + " expected."
					+ positionDebugString());
	}

	/**
	 * Get position offset with +- 10 characters around current position
	 * 
	 * @return String information
	 */
	private String positionDebugString() {
		return "\nLine: "
				+ lineNumber
				+ " Column: "
				+ linePosition
				+ ". "
				+ "\nOn global position: "
				+ pos
				+ " (+-10 wrapped into __ characters):\n\n__"
				+ text.substring((pos - 10 < 0 ? 0 : pos - 10),
						(pos + 10 > text.length() ? text.length() : pos + 10))
				+ "__";
	}
}
