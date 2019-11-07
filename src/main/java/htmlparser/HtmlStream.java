package htmlparser;

import htmlparser.core.AttributeValue;
import htmlparser.core.EventParser;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.LinkedHashMap;

import static htmlparser.utils.Constants.*;
import static htmlparser.utils.Functions.trim;
import static htmlparser.utils.HTML.AUTO_CLOSING_TAGS;
import static htmlparser.utils.HTML.unescapeHtml;
import static htmlparser.utils.HtmlParse.*;

public interface HtmlStream {

    static void toTagStream(final InputStreamReader in, final EventParser parser) throws IOException {
        String str;
        while ((str = readLine(in, HTML_TAG_START)) != null) {
            if (!str.isEmpty()) parser.someText(str, unescapeHtml(str));

            str = trim(readLine(in, HTML_TAG_END));
            if (str.isEmpty()) {
                parser.someText(str, str);
                continue;
            }

            if (str.charAt(0) == CHAR_FORWARD_SLASH) {
                final String name = getNameOfTag(str.substring(1));
                if (AUTO_CLOSING_TAGS.contains(name)) {
                    parser.startNode(FORWARD_SLASH+name, new HashMap<>());
                    parser.endAutoClosing();
                } else {
                    parser.endNode(name);
                }
            }
            else {
                final String name = getNameOfTag(str);
                if (str.length() == name.length()) {
                    if (name.equals("![endif]--")) {
                        parser.endIEComment(name);
                        continue;
                    }

                    parser.startNode(str, new HashMap<>());
                    if (name.equals("script")) {
                        String jscode = readScriptTag(in);
                        parser.someText(jscode, unescapeHtml(jscode));
                        parser.endNode(name);
                    } else if (AUTO_CLOSING_TAGS.contains(name)) {
                        parser.endAutoClosing();
                    }
                    continue;
                }

                final int beginAttr = name.length();
                final int end = str.length();

                if (name.equals("!--[if")) {
                    parser.startIEComment(name, str.substring(beginAttr, end));
                    continue;
                }

                if (str.endsWith(FORWARD_SLASH)) {
                    parser.startNode(name, htmlToAttributes(str.substring(beginAttr, end-1)));
                    parser.endSelfClosing();
                } else if (AUTO_CLOSING_TAGS.contains(name)) {
                    parser.startNode(name, htmlToAttributes(str.substring(beginAttr, end)));
                    parser.endAutoClosing();
                } else {
                    parser.startNode(name, htmlToAttributes(str.substring(beginAttr+1, end)));
                    if (name.equals("script")) {
                        final String jscode = readScriptTag(in);
                        parser.someText(jscode, unescapeHtml(jscode));
                        parser.endNode(name);
                    }
                }
            }
        }
    }

    class ClosingScriptTagFinder {

        boolean sendData = false;
        int windowOffset = 0;
        int dataOffset = 0;
        final static char[] CLOSING_SCRIPT_TAG = "</script>".toCharArray();
        char lastChar = 0;

        boolean foundClosingTag(final int c) {
            if (CLOSING_SCRIPT_TAG[windowOffset] == c) {
                if (windowOffset+1 == CLOSING_SCRIPT_TAG.length) return true;
                windowOffset++;
                return false;
            }
            lastChar = (char) c;
            sendData = true;
            return false;
        }

        boolean hasData() {
            return sendData;
        }
        char data() {
            if (dataOffset < windowOffset) {
                return CLOSING_SCRIPT_TAG[dataOffset++];
            }
            windowOffset = 0;
            dataOffset = 0;
            sendData = false;
            return lastChar;
        }
    }

    private static String readScriptTag(final InputStreamReader in) throws IOException {
        final StringBuilder builder = new StringBuilder();
        final ClosingScriptTagFinder window = new ClosingScriptTagFinder();

        int data;
        while ((data = in.read()) != -1) {
            if (window.foundClosingTag(data)) break;
            while (window.hasData()) {
                builder.append(window.data());
            }
        }
        if (data == -1) {
            while (window.hasData()) {
                builder.append(window.data());
            }
        }

        return builder.toString();
    }

    static HashMap<String, AttributeValue> htmlToAttributes(String input) {
        final HashMap<String, AttributeValue> attributes = new LinkedHashMap<>();

        while (!input.isEmpty()) {
            int startName = indexOfNonWhitespaceChar(input, 0);
            if (startName == -1) break;

            if (input.charAt(startName) == '"') {
                int index = input.indexOf("\"", startName+1);
                if (index == -1) {
                    attributes.put(input.substring(startName), null);
                    break;
                }
                attributes.put(input.substring(startName, index+1), null);
                input = input.substring(index+1);
                continue;

            }
            int endName = indexOfEndOfName(input,startName+1);
            if (endName == -1) {
                attributes.put(input.substring(startName), null);
                break;
            }
            int startOfValue = indexOfNonWhitespaceChar(input, endName);
            if (startOfValue == -1) {
                attributes.put(input.substring(startName, endName), null);
                break;
            }

            if (input.charAt(startOfValue) != '=') {
                attributes.put(input.substring(startName, endName), null);
                input = input.substring(endName);
                continue;
            }

            final String name = input.substring(startName, endName);
            input = input.substring(endName+1);

            int startValue = indexOfNonWhitespaceChar(input, 0);
            if (startValue == -1) break;

            int endValue; final String value; boolean isDoubleQuoted = false; boolean isSingleQuoted = false;
            if (input.charAt(startValue) == CHAR_DOUBLE_QUOTE) {
                isDoubleQuoted = true;
                startValue++;
                endValue = input.indexOf(CHAR_DOUBLE_QUOTE, startValue);
                if (endValue == -1) endValue = input.length()-1;
                value = input.substring(startValue, endValue);
            } else if (input.charAt(startValue) == CHAR_SINGLE_QUOTE) {
                isSingleQuoted = true;
                startValue++;
                endValue = input.indexOf(CHAR_SINGLE_QUOTE, startValue);
                if (endValue == -1) endValue = input.length()-1;
                value = input.substring(startValue, endValue);
            } else {
                endValue = indexOfWhitespaceChar(input, startValue+1);
                if (endValue == -1) endValue = input.length()-1;
                value = input.substring(startValue, endValue+1);
            }

            input = input.substring(endValue+1);

            attributes.put(name, new AttributeValue(value, unescapeHtml(value), isDoubleQuoted, isSingleQuoted));
        }

        return attributes;
    }

}
