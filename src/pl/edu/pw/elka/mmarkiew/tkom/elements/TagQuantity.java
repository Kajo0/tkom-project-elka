package pl.edu.pw.elka.mmarkiew.tkom.elements;

/**
 * Enumeration defines allowed tags and their quantity - whether has to end them
 * in separated tag
 * 
 * @author Mikolaj Markiewicz
 * 
 */
public enum TagQuantity {

	a(false), abbr(false), acronym(false), address(false), applet(false), article(
			false), aside(false), audio(false), b(false), bdi(false), bdo(false), big(
			false), blockquote(false), body(false), button(false), canvas(false), caption(
			false), center(false), cite(false), code(false), colgroup(false), command(
			false), datalist(false), dd(false), del(false), details(false), dfn(
			false), dialog(false), dir(false), div(false), dl(false), dt(false), em(
			false), fieldset(false), figcaption(false), figure(false), font(
			false), footer(false), form(false), frame(false), frameset(false), head(
			false), header(false), hgroup(false), h1(false), h2(false), h3(
			false), h4(false), h5(false), h6(false), html(false), i(false), iframe(
			false), ins(false), kbd(false), label(false), legend(false), li(
			false), map(false), mark(false), menu(false), meter(false), nav(
			false), noframes(false), noscript(false), object(false), ol(false), optgroup(
			false), option(false), output(false), p(false), pre(false), progress(
			false), q(false), rp(false), rt(false), ruby(false), s(false), samp(
			false), script(false), section(false), select(false), small(false), span(
			false), strike(false), strong(false), style(false), sub(false), summary(
			false), sup(false), table(false), tbody(false), td(false), textarea(
			false), tfoot(false), th(false), thead(false), time(false), title(
			false), tr(false), tt(false), u(false), ul(false), var(false), video(
			false), area(true), base(true), basefont(true), br(true), col(true), embed(
			true), hr(true), img(true), input(true), keygen(true), link(true), meta(
			true), param(true), source(true), track(true), wbr(true);

	/**
	 * Whether is single tag or not
	 */
	private boolean single;

	/**
	 * C-tor
	 * 
	 * @param single
	 *            Single tag or not
	 */
	TagQuantity(boolean single) {
		this.single = single;
	}

	/**
	 * Whether is single tag or not
	 * 
	 * @return True if single, false otherwise
	 */
	public boolean isSingle() {
		return single;
	}

}
