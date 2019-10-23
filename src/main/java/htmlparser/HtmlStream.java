package htmlparser;

import htmlparser.core.EventParser;
import htmlparser.error.InvalidHtml;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;

import static htmlparser.utils.Constants.*;
import static htmlparser.utils.Functions.trim;
import static htmlparser.utils.HTML.unescapeHtml;
import static htmlparser.utils.HtmlParse.*;

public interface HtmlStream {

    static void toTagStream(final InputStreamReader in, final EventParser parser) throws IOException {
        String str;
        while ((str = readLine(in, HTML_TAG_START)) != null) {
            if (!str.isEmpty()) parser.someText(unescapeHtml(str.trim()));

            str = trim(readLine(in, HTML_TAG_END));
            if (str.isEmpty()) throw new InvalidHtml("Unclosed tag");
            if (str.charAt(0) == HTML_PROLOG) continue;

            if (str.charAt(0) == HTML_SELF_CLOSING) parser.endNode();
            else {
                final String name = getNameOfTag(str);
                if (str.length() == name.length()) {
                    parser.startNode(str, new HashMap<>());
                    continue;
                }

                final int beginAttr = name.length();
                final int end = str.length();
                if (str.endsWith(FORWARD_SLASH)) {
                    parser.startNode(name, htmlToAttributes(str.substring(beginAttr, end-1)));
                    parser.endNode();
                } else {
                    parser.startNode(name, htmlToAttributes(str.substring(beginAttr+1, end)));
                }
            }
        }
    }

    static HashMap<String, String> htmlToAttributes(String input) {
        final HashMap<String, String> attributes = new HashMap<>();

        while (!input.isEmpty()) {
            int startName = indexOfNonWhitespaceChar(input, 0);
            if (startName == -1) break;
            int equals = input.indexOf(CHAR_EQUALS, startName+1);
            if (equals == -1) break;

            final String name = input.substring(startName, equals).trim();
            input = input.substring(equals+1);

            int startValue = indexOfNonWhitespaceChar(input, 0);
            if (startValue == -1) break;

            int endValue; final String value;
            if (input.charAt(startValue) == CHAR_DOUBLE_QUOTE) {
                startValue++;
                endValue = input.indexOf(CHAR_DOUBLE_QUOTE, startValue);
                if (endValue == -1) endValue = input.length()-1;
                value = input.substring(startValue, endValue).trim();
            } else {
                endValue = indexOfWhitespaceChar(input, startValue+1);
                if (endValue == -1) endValue = input.length()-1;
                value = input.substring(startValue, endValue+1).trim();
            }

            input = input.substring(endValue+1);

            attributes.put(name, unescapeHtml(value));
        }

        return attributes;
    }

}
