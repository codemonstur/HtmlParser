package htmlparser.core;

public final class HtmlTextElement extends Tag {
    public final String text;
    public HtmlTextElement(final Tag parent, final String text) {
        super(parent, null, null, null);
        this.text = text;
    }
}

