package htmlparser.core;

public final class HtmlTextElement extends Tag {
    public String original;
    public String decoded;

    public HtmlTextElement(final Tag parent, final String original, final String decoded) {
        super(parent, null, null, null);
        this.original = original;
        this.decoded = decoded;
    }
}

