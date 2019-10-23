package htmlparser;

import htmlparser.error.InvalidHtml;

import java.io.*;

import static htmlparser.utils.Constants.*;
import static htmlparser.utils.Functions.trim;
import static htmlparser.utils.HtmlParse.*;
import static java.nio.charset.StandardCharsets.UTF_8;

public interface HtmlCompress {

    default String compressXml(final String input) {
        try ( final ByteArrayOutputStream out = new ByteArrayOutputStream()
            ; final InputStreamReader reader = new InputStreamReader(new ByteArrayInputStream(input.getBytes(UTF_8)), UTF_8)
            ; final OutputStreamWriter writer = new OutputStreamWriter(out, UTF_8)
            ) {
            compressXML(reader, writer);
            return new String(out.toByteArray(), UTF_8);
        } catch (IOException e) {
            // can't happen.
            return null;
        }
    }

    static void compressXML(final InputStreamReader in, final OutputStreamWriter out) throws IOException {
        String str;
        while ((str = readLine(in, HTML_TAG_START)) != null) {
            // Probably a text node, remove the whitespace and write
            if (!str.isEmpty()) out.write(str.trim());

            str = trim(readLine(in, HTML_TAG_END));
            if (str.isEmpty()) throw new InvalidHtml("Unclosed tag");
            // We don't care about comments
            if (str.charAt(0) == HTML_PROLOG) continue;

            out.write(HTML_TAG_START);
            // It might be a closing tag which is easy to deal with
            if (str.charAt(0) == CHAR_FORWARD_SLASH) {
                out.write(CHAR_FORWARD_SLASH);
                out.write(getNameOfTag(str.substring(1)).trim());
                out.write(HTML_TAG_END);
                continue;
            }

            // Its an opening tag so we need the attributes and the whole thing
            final String name = getNameOfTag(str);
            if (str.length() == name.length()) {
                out.write(name);
                out.write(HTML_TAG_END);
                continue;
            }

            final int beginAttr = name.length();
            final int end = str.length();
            out.write(name);
            // It could be a self closing tag
            if (str.endsWith(FORWARD_SLASH)) {
                parseAttributes(str.substring(beginAttr+1, end-1), out);
                out.write(FORWARD_SLASH);
            } else {
                parseAttributes(str.substring(beginAttr+1, end), out);
            }
            out.write(HTML_TAG_END);
        }
        out.flush();
    }

    static void parseAttributes(String input, final OutputStreamWriter out) throws IOException {

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

            out.write(CHAR_SPACE);
            out.write(name);
            out.write(CHAR_EQUALS);
            out.write(CHAR_DOUBLE_QUOTE);
            out.write(value);
            out.write(CHAR_DOUBLE_QUOTE);
        }
    }

}
