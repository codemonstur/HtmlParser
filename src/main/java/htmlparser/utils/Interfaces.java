package htmlparser.utils;

import htmlparser.HtmlParser;

public enum Interfaces {;

    public interface AccessHtmlParser {
        HtmlParser getHtmlParser();
    }
    public interface ParserConfiguration {
        boolean shouldEncodeUTF8();
        boolean shouldPrettyPrint();
    }
    public interface CheckedIterator<T> {
        boolean hasNext() throws Exception;
        T next() throws Exception;
    }

}
