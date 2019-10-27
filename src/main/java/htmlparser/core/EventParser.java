package htmlparser.core;

import java.util.Map;

public interface EventParser {

    void startNode(final String name, final Map<String, AttributeValue> attrs);
    void endSelfClosing();
    void endAutoClosing();
    void endNode(String name);
    void someText(final String original, final String decoded);
    void startIEComment(String name, String content);
    void endIEComment(String name);

}

