package htmlparser.core;

import htmlparser.HtmlStream;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import static htmlparser.utils.Constants.FORWARD_SLASH;

public class DomBuilder implements EventParser {
    private Tag root = new Document();
    private Tag current = root;

    public void startNode(final String name, final Map<String, AttributeValue> attrs) {
        final Tag tmp = new Tag(this.current, name, attrs);
        this.current.appendChild(tmp);
        this.current = tmp;
    }
    public void startIEComment(final String name, final String content) {
        final IEComment tmp = new IEComment(this.current, name, content);
        this.current.appendChild(tmp);
        this.current = tmp;
    }

    public void endNode(final String name) {
        if (this.current instanceof Document) {
            final Document document = (Document) this.current;
            final Tag newTag = new Tag(this.current, FORWARD_SLASH+ name, new HashMap<>());
            newTag.isAutoClosing = true;
            document.endTags.add(newTag);
            return;
        }
        this.current.closingName = name;
        this.current = this.current.parent;
    }
    public void endIEComment(final String name) {
        if (this.current instanceof Document) {
            final Document document = (Document) this.current;
            final Tag newTag = new Tag(this.current, FORWARD_SLASH+ name, new HashMap<>());
            newTag.isAutoClosing = true;
            document.endTags.add(newTag);
            return;
        }
        this.current.closingName = name;
        this.current = this.current.parent;
    }

    public void endSelfClosing() {
        this.current.isSelfClosing = true;
        this.current = this.current.parent;
    }
    public void endAutoClosing() {
        this.current.isAutoClosing = true;
        this.current = this.current.parent;
    }

    public void someText(final String original, final String decoded) {
        if (original == null || original.isEmpty()) return;

        this.current.children.add(new HtmlTextElement(this.current, original, decoded));
    }


    public Tag getRoot() {
        return this.root;
    }

    public static Tag toHtmlDocument(final InputStreamReader in) throws IOException {
        final DomBuilder p = new DomBuilder();
        HtmlStream.toTagStream(in, p);
        return p.getRoot();
    }
    public static Tag toHtmlFragment(final InputStreamReader in) throws IOException {
        final DomBuilder p = new DomBuilder();
        HtmlStream.toTagStream(in, p);
        return p.getRoot();
    }

}
