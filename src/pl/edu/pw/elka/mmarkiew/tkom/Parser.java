package pl.edu.pw.elka.mmarkiew.tkom;

import java.util.Iterator;
import java.util.LinkedList;

import pl.edu.pw.elka.mmarkiew.tkom.elements.CommentElement;
import pl.edu.pw.elka.mmarkiew.tkom.elements.TagElement;
import pl.edu.pw.elka.mmarkiew.tkom.elements.TagQuantity;
import pl.edu.pw.elka.mmarkiew.tkom.elements.TextElement;
import pl.edu.pw.elka.mmarkiew.tkom.elements.TreeElement;
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
 * Class in charge of parse given token list
 * 
 * @author Mikolaj Markiewicz
 * 
 */
public class Parser {

	/**
	 * Result tree object
	 */
	private TreeElement tree;

	/**
	 * Actual element to add elements to it during parsing
	 */
	private TagElement actualElement;

	/**
	 * List of tokens to parse them
	 */
	private LinkedList<Token> tokens;

	/**
	 * Token list iterator
	 */
	private Iterator<Token> tokenIterator;

	/**
	 * C-tor<br />
	 * Prepare fields
	 * 
	 * @param tokens
	 *            List of tokens to parse them
	 */
	public Parser(LinkedList<Token> tokens) {
		this.tokens = tokens;
		tokenIterator = this.tokens.iterator();
		tree = new TreeElement();
		actualElement = null;
	}

	/**
	 * Get next token<br />
	 * Moves iterator onto next element and return that
	 * 
	 * @return Next token from list
	 */
	private Token nextToken() {
		if (!tokenIterator.hasNext())
			throw new ArrayIndexOutOfBoundsException();

		return tokenIterator.next();
	}

	/**
	 * Parse tokens
	 * 
	 * @return Parsed tree from tokens
	 */
	public TreeElement parseTokens() {
		try {
			// Read doctype
			readDoctype();

			// Read rest of tokens
			while (tokenIterator.hasNext())
				readElement();

		} catch (ArrayIndexOutOfBoundsException e) {
			// end of tokens
		}

		return tree;
	}

	/**
	 * Read doctype token and set it into result tree
	 * 
	 * @throws RuntimeException
	 *             if no DoctypeToken found
	 */
	private void readDoctype() {
		if (!tokenIterator.hasNext())
			throw new RuntimeException("No doctype");

		Token t = nextToken();

		// <!doctype ...>
		if (!(t instanceof DoctypeToken))
			throw new RuntimeException("No doctype");

		tree.setDoctype(t.getValue());
	}

	/**
	 * Read element and possibly inner evaluation invoke it recursively<br />
	 * Possible recursive evaluation, so has to done after none if-else part
	 * matches
	 */
	private void readElement() {
		Token t = nextToken();
		TagElement lastElem = actualElement;

		if (t instanceof StartOpenTagToken) {
			// Token <tag
			actualElement = (TagElement) new TagElement(t.getValue(),
					actualElement).setPositions(t.getPositions());

			if (lastElem == null)
				tree.addElement(actualElement);
			else
				lastElem.addElement(actualElement);

			innerTagExpected();
		} else if (t instanceof EndOpenTagToken) {
			// Token </tag
			actualElement = (TagElement) actualElement.getParent();
		} else if (t instanceof TextToken) {
			// Token #text
			lastElem.addElement(new TextElement(t.getValue(), lastElem)
					.setPositions(t.getPositions()));
		} else if (t instanceof CommentToken) {
			// Token #comment
			lastElem.addElement(new CommentElement(t.getValue(), lastElem)
					.setPositions(t.getPositions()));
		} else {
			// End of recursion, go on top level
		}
	}

	/**
	 * Read inner of tag<br />
	 * Read attributes or end of empty single tag<br />
	 * Recursive read element after close of tag
	 * 
	 * @throws RuntimeException
	 *             if bad tag occurred
	 */
	private void innerTagExpected() {
		Token t = nextToken();

		if (t instanceof CloseEmptyTagToken) {
			// Token />
			actualElement.setEmpty(true);
			actualElement = (TagElement) actualElement.getParent();
			return;
		} else if (t instanceof CloseTagToken) {
			// Token >
			if (TagQuantity.valueOf(actualElement.getTag().toLowerCase())
					.isSingle()) {
				actualElement.setEmpty(true);
				actualElement = (TagElement) actualElement.getParent();
			}

			// Read inner part of tag element
			readElement();
		} else if (t instanceof AttributeKeyToken) {
			// Token key
			String key = t.getValue();
			String value;

			t = nextToken();
			// Token =
			if (!(t instanceof AttributeEqualsToken))
				throw new RuntimeException("No equal token");

			t = nextToken();
			// Token "value" | 'value' | value
			if (!(t instanceof AttributeValueToken))
				throw new RuntimeException("No attribute value");

			value = t.getValue();
			value = ((AttributeValueToken) t).getQuote() + t.getValue()
					+ ((AttributeValueToken) t).getQuote();

			((TagElement) actualElement).addAttribute(key, value);

			// Read possible next attribute
			innerTagExpected();
		} else {
			throw new RuntimeException("Bad token occurred.");
		}
	}

	/**
	 * Get result tree
	 * 
	 * @return Parsed tree
	 */
	public TreeElement getTree() {
		return tree;
	}

}
