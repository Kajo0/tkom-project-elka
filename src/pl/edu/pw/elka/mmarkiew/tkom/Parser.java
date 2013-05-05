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

public class Parser {

	private TreeElement tree;
	private TagElement actualElement;
	private LinkedList<Token> tokens;
	private Iterator<Token> tokenIterator;

	public Parser(LinkedList<Token> tokens) {
		this.tokens = tokens;
		tokenIterator = tokens.iterator();
		tree = new TreeElement();
	}

	private Token nextToken() {
		if (!tokenIterator.hasNext())
			throw new ArrayIndexOutOfBoundsException();

		return tokenIterator.next();
	}

	public void parseTokens() throws Exception {
		try {
			readDoctype();

			while (tokenIterator != tokens.listIterator())
				readElements();
		} catch (ArrayIndexOutOfBoundsException e) {
			// end of tokens
		}
	}

	private void readDoctype() throws Exception {
		if (!tokenIterator.hasNext())
			throw new Exception("No doctype");

		Token t = nextToken();

		if (!(t instanceof DoctypeToken))
			throw new Exception("No doctype");

		tree.setDoctype(t.getValue());
	}

	private void readElements() throws Exception {
		Token t = nextToken();
		TagElement lastElem = actualElement;

		if (t instanceof StartOpenTagToken) {
			actualElement = new TagElement(t.getValue(), actualElement);

			if (lastElem == null)
				tree.addElement(actualElement);
			else
				lastElem.addElement(actualElement);

			innerTagExpected();
		} else if (t instanceof EndOpenTagToken) {
			actualElement = (TagElement) actualElement.getParent();
		} else if (t instanceof TextToken) {
			lastElem.addElement(new TextElement(t.getValue(), lastElem));
		} else if (t instanceof CommentToken) {
			lastElem.addElement(new CommentElement(t.getValue(), lastElem));
		}
	}

	private void innerTagExpected() throws Exception {
		Token t = nextToken();

		if (t instanceof CloseEmptyTagToken) {
			actualElement.setEmpty(true);
			actualElement = (TagElement) actualElement.getParent();
			return;
		} else if (t instanceof CloseTagToken) {
			if (TagQuantity.valueOf(actualElement.getTag()).isSingle()) {
				actualElement.setEmpty(true);
				actualElement = (TagElement) actualElement.getParent();
			}

			readElements();
		} else if (t instanceof AttributeKeyToken) {
			String key = t.getValue();
			String value;

			t = nextToken();
			if (!(t instanceof AttributeEqualsToken))
				throw new Exception("No equal token");

			t = nextToken();
			if (!(t instanceof AttributeValueToken))
				throw new Exception("No attribute value");

			value = t.getValue();

			((TagElement) actualElement).addAttribute(key, value);

			innerTagExpected();
		} else {
			throw new Exception("Bad token expected.");
		}
	}

	public TreeElement getTree() {
		return tree;
	}

}
