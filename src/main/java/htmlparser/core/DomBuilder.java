package htmlparser.core;

import htmlparser.HtmlStream;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;

public class DomBuilder implements EventParser {
    private Tag root;
    private Tag current;

    public void startNode(final String name, final Map<String, String> attrs) {
        final Tag tmp = new Tag(this.current, name, attrs);

        if (this.current != null) this.current.appendChild(tmp);
        else this.root = tmp;

        this.current = tmp;
    }
    public void endNode() {
        this.current = this.current.parent;
    }
    public void someText(final String txt) {
        if (txt == null || txt.isEmpty()) return;

        this.current.children.add(new HtmlTextElement(this.current, txt.trim()));
    }
    public Tag getRoot() {
        return this.root;
    }

    public static Tag toHtmlDom(final InputStreamReader in) throws IOException {
        final DomBuilder p = new DomBuilder();
        HtmlStream.toTagStream(in, p);
        return p.getRoot();
    }
}
