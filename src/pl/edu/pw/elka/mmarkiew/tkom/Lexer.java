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

public class Lexer {

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
	 * @param text Text to analyze
	 */
	public Lexer(String text) {
		this.text = text;
		this.pos = 0;
		this.tokens = new LinkedList<>();
		this.stack = new Stack<>();
	}

	/**
	 * Extract tokens from given text
	 * 
	 * @return List of extracted tokens
	 */
	public  LinkedList<Token> extractTokens() {
		findDoctype();

		// While there is character to analyze
		while (pos < text.length()) {
			try {
				findElement();
			} catch (ArrayIndexOutOfBoundsException e) {
				// EOF
				break;
			}
		}
		
		return tokens;
	}

	/**
	 * Move pointer to the next character
	 * 
	 * @throws ArrayIndexOutOfBoundsException when end of characters in text
	 * 
	 * @return Actual character before next move
	 */
	private char popChar() {
		if (pos + 1 >= text.length())
			throw new ArrayIndexOutOfBoundsException("Char after EOS");

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

	// private boolean isWhitespace(char c) {
	// return whitespaceCharacters.contains(c);
	// }

	// private boolean isAlphanum(char c) {
	// return alphanumCharacters.contains(c);
	// }

	// /**
	// * Go through text to bound which is defined as whitespace char
	// *
	// * @return Gathered word
	// */
	// private String gatherBound() {
	// StringBuilder str = new StringBuilder();
	//
	// while (!isWhitespace(getChar()))
	// str.append(popChar());
	//
	// return str.toString();
	// }

	/**
	 * Gather word from actual position to any character on given list
	 * 
	 * @param bounds List of boundary characters
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
	 * @param bounds List of boundary characters
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
	 * @throws RuntimeException if bad doctype word, or no doctype found
	 */
	private void findDoctype() {
		ommitWhitespaces();

		if (popChar() == '<' && popChar() == '!') {
			String doc = gatherBoundWhite(new char[] { '>' });

			if (!doc.equalsIgnoreCase("doctype"))
				throw new RuntimeException("Bad doctype tag");

			ommitWhitespaces();

			String type = gatherBound(new char[] { '>' });
			popChar();

			tokens.add(new DoctypeToken(type));
		} else {
			throw new RuntimeException("No doctype found!");
		}
	}

	/**
	 * Find next element starting from < character<br />
	 * If analyzing element, run it recursive
	 * 
	 * @throws RuntimeException if no close tag found
	 */
	private void findElement() {
		String word;

		ommitWhitespaces();
		// If not found < - last tag already read, exception is thrown
		word = gatherBound(new char[] { '<' }).trim();

		if (!word.isEmpty())
			tokens.add(new TextToken(word));

		// We are on < character
		popChar();

		if (getChar() == '/') {
			// </ end tag
			popChar();
			expectCloseTag();
		} else if (getChar() == '!') {
			// Comment tag <!--
			popChar();
			expectComment();
		} else {
			analyzeElement();

			if (getChar() == '>')
				tokens.add(new CloseTagToken(""));
			else if (popChar() == '/' && getChar() == '>')
				tokens.add(new CloseEmptyTagToken(""));
			else
				throw new RuntimeException("Expected close tag.");

			popChar();

			findElement();
		}
	}

	/**
	 * Analyze tag element in order to find attributes
	 */
	private void analyzeElement() {
		ommitWhitespaces();

		String word = gatherBoundWhite(new char[] { '/', '>' });

		checkProperTag(word);
		if (!TagQuantity.valueOf(word).isSingle())
			putOnStack(word);

		tokens.add(new StartOpenTagToken(word));

		ommitWhitespaces();

		while (getChar() != '/' && getChar() != '>') {
			ommitWhitespaces();

			word = gatherBoundWhite(new char[] { '=', '>', '/' });
			ommitWhitespaces();

			tokens.add(new AttributeKeyToken(word));
			tokens.add(new AttributeEqualsToken(""));

			if (getChar() != '=') {
				// No value attribute, proper form => key="key"
				tokens.add(new AttributeValueToken(word, '\"'));
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
	 * Gather attribute inside double quotes
	 * 
	 * @throws RuntimeException If no end quote found
	 */
	private void attributeDoubleQuoteExpected() {
		String word = gatherBound(new char[] { '"' });//, '/', '>' });
		
		while (text.charAt(pos - 1) == '\\') {
			word += "\"";
			popChar();
			word += gatherBound(new char[] { '"' });//, '/', '>' });
		}

		if (getChar() != '"')
			throw new RuntimeException("Expected double quote character.");

		popChar();
		tokens.add(new AttributeValueToken(word, '\"'));
	}

	/**
	 * Gather attribute inside single quotes
	 * 
	 * @throws RuntimeException If no end quote found
	 */
	private void attributeSingleQuoteExpected() {
		String word = gatherBound(new char[] { '\'' });//, '/', '>' });

		while (text.charAt(pos - 1) == '\\') {
			word += "\'";
			popChar();
			word += gatherBound(new char[] { '\'' });//, '/', '>' });
		}

		if (getChar() != '\'')
			throw new RuntimeException("Expected single quote character.");

		popChar();
		tokens.add(new AttributeValueToken(word, '\''));
	}

	/**
	 * Gather attribute after equal sign without quotes
	 * 
	 * @throws RuntimeException If no value found
	 */
	private void attributeSingleWordExpected() {
		String word = gatherBoundWhite(new char[] { '>', '/' });

		if (word.length() == 0)
			throw new RuntimeException("Expected attribute value");

		tokens.add(new AttributeValueToken(word, '\"'));
	}

	/**
	 * Find close tag sign : >
	 */
	private void expectCloseTag() {
		String word = gatherBound(new char[] { '>' }).trim();
		popChar();

		checkProperTag(word);
		popFromStack(word);

		tokens.add(new EndOpenTagToken(word));
		tokens.add(new CloseTagToken(""));
	}

	/**
	 * Gather comment part
	 * 
	 * @throws RuntimeException if no comment tag found
	 */
	private void expectComment() {
		StringBuilder wrd = new StringBuilder();

		if (popChar() != '-' || popChar() != '-')
			throw new RuntimeException("Expected comment tag.");

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
			throw new RuntimeException("Expected comment end tag.");
		}

		tokens.add(new CommentToken(wrd.toString()));
	}

	/**
	 * Check whether is proper tag
	 * 
	 * @throws Enum Exception if no enumertation set 
	 * 
	 * @param tag Tag to check
	 */
	public void checkProperTag(String tag) {
		TagQuantity.valueOf(tag);
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
	 * @param tag Tag name
	 */
	private void putOnStack(String tag) {
		stack.push(tag);
	}

	/**
	 * Pop double tag from stack to check proper tag order
	 * 
	 * @throws RuntimeException if bad stack order
	 * 
	 * @param tag Tag name
	 */
	private void popFromStack(String tag) {
		if (!stack.pop().equals(tag))
			throw new RuntimeException("Bad stack order");
	}

	// private String text;
	// private int pos;
	// private LinkedList<Token> tokens;
	// private Stack<String> stack;
	//
	// public Lexer(String text) {
	// this.text = text;
	// this.pos = 0;
	// this.tokens = new LinkedList<>();
	// this.stack = new Stack<>();
	// }
	//
	// public void extractTokens() throws Exception {
	//
	// findDoctype();
	// while (pos < text.length()) {
	//
	// try {
	// findElement();
	// } catch (ArrayIndexOutOfBoundsException e) {
	// // EOF
	// break;
	// }
	// }
	// }
	//
	// private char nextChar() {
	// if (pos + 1 >= text.length())
	// throw new ArrayIndexOutOfBoundsException("Char after EOS");
	//
	// return text.charAt(pos++);
	// }
	//
	// private void ommitWhitespaces() {
	// while (Character.isWhitespace(nextChar()))
	// ;
	//
	// --pos;
	// }
	//
	// private String gatherWordFromCurrentPosToBound() {
	// StringBuilder str = new StringBuilder();
	//
	// char c;
	//
	// while (!Character.isWhitespace((c = nextChar())) && c != '>'
	// && c != '<' && c != '/')
	// str.append(c);
	//
	// return str.toString();
	// }
	//
	// private String gatherWordFromCurrentPosToBound(char bound) {
	// StringBuilder str = new StringBuilder();
	//
	// char c;
	//
	// while ((c = nextChar()) != bound || text.charAt(pos - 2) == '\\')
	// str.append(c);
	//
	// return str.toString();
	// }
	//
	// private void findDoctype() throws Exception {
	// ommitWhitespaces();
	//
	// if (nextChar() == '<' && nextChar() == '!') {
	// gatherWordFromCurrentPosToBound();
	// String doctype = gatherWordFromCurrentPosToBound('>');
	// tokens.add(new DoctypeToken(doctype));
	// } else {
	// throw new Exception("ERROR");
	// }
	//
	// }
	//
	// private void findElement() throws Exception {
	// String word;
	//
	// ommitWhitespaces();
	// word = gatherWordFromCurrentPosToBound('<');
	//
	// if (!word.isEmpty())
	// tokens.add(new TextToken(word));
	//
	// char c = nextChar();
	// // </ end tag
	// if (c == '/') {
	// word = gatherWordFromCurrentPosToBound('>');
	// word.trim();
	//
	// popFromStack(word);
	// tokens.add(new EndOpenTagToken(word));
	// tokens.add(new CloseTagToken(""));
	// }
	// // Comment tag <!--
	// else if (c == '!') {
	// StringBuilder wrd = new StringBuilder();
	// pos += 2;
	// do {
	// wrd.append(gatherWordFromCurrentPosToBound('>'));
	// wrd.append('>');
	// } while (text.charAt(pos - 2) != '-' && text.charAt(pos - 1) != '-');
	//
	// wrd.delete(wrd.length() - 3, wrd.length());
	// tokens.add(new CommentToken(wrd.toString()));
	// } else {
	// --pos;
	// analyzeElement();
	// }
	// }
	//
	// private void analyzeElement() {
	// String word = gatherWordFromCurrentPosToBound();
	// --pos;
	//
	// if (!TagQuantity.valueOf(word).isSingle())
	// putOnStack(word);
	// tokens.add(new StartOpenTagToken(word));
	//
	// char c;
	// while ((c = nextChar()) != '/' && c != '>') {
	// ommitWhitespaces();
	// c = nextChar();
	// --pos;
	//
	// if (c == '/' || c == '>')
	// break;
	//
	// word = gatherWordFromCurrentPosToBound('=');
	// tokens.add(new AttributeKeyToken(word));
	//
	// tokens.add(new AttributeEqualsToken(""));
	//
	// c = nextChar();
	// if (c == '"') {
	// word = gatherWordFromCurrentPosToBound('"');
	// } else if (c == '\'') {
	// word = gatherWordFromCurrentPosToBound('\'');
	// } else {
	// --pos;
	// word = gatherWordFromCurrentPosToBound();
	// --pos;
	// }
	//
	// tokens.add(new AttributeValueToken(word));
	// }
	//
	// if (c == '/') {
	// tokens.add(new CloseEmptyTagToken(""));
	// pos += 2;
	// } else
	// tokens.add(new CloseTagToken(""));
	// }
	//
	// public LinkedList<Token> getTokens() {
	// return tokens;
	// }
	//
	// private void putOnStack(String tag) {
	// stack.push(tag);
	// }
	//
	// private void popFromStack(String tag) throws Exception {
	// if (!stack.pop().equals(tag))
	// throw new Exception("Bad stack order");
	// }

}
