package htmlparser.core;

public class IEComment extends Tag {

    public final String content;
    public IEComment(final Tag parent, final String name, final String content) {
        super(parent, name, null);
        this.content = content;
    }

}
