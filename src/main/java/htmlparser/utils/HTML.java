package htmlparser.utils;

import htmlparser.core.AttributeValue;

import java.io.IOException;
import java.io.Writer;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import static htmlparser.utils.Constants.*;
import static htmlparser.utils.Functions.isNullOrEmpty;

public enum HTML {;

    public static final Set<String> AUTO_CLOSING_TAGS = Set.of("!DOCTYPE", "area", "base", "br",
        "col", "command", "embed", "hr", "img", "input", "keygen", "link", "meta", "param",
        "source", "track", "wbr");

    public static String escapeHtml(final String str, final boolean encodeUTF8) {
        if (isNullOrEmpty(str)) return str;

        final StringBuilder encoded = new StringBuilder();
        for (final char c : str.toCharArray()) {
            switch (c) {
                case CHAR_LESS_THAN:
                    encoded.append(ENCODED_LESS_THAN); break;
                case CHAR_DOUBLE_QUOTE:
                    encoded.append(ENCODED_DOUBLE_QUOTE); break;
                case CHAR_GREATER_THAN:
                    encoded.append(ENCODED_GREATER_THAN); break;
                case CHAR_SINGLE_QUOTE:
                    encoded.append(ENCODED_SINGLE_QUOTE); break;
                case CHAR_AMPERSAND:
                    encoded.append(ENCODED_AMPERSAND); break;
                default:
                    encoded.append( (encodeUTF8 && c > 0x7e) ? AMPERSAND+HASH+((int)c)+SEMICOLON : c);
                    break;
            }
        }

        return encoded.toString();
    }

    public static String unescapeHtml(final String text) {
        StringBuilder result = new StringBuilder(text.length());
        int i = 0;
        int n = text.length();
        while (i < n) {
            char charAt = text.charAt(i);
            if (charAt != CHAR_AMPERSAND) {
                result.append(charAt);
                i++;
            } else {
                if (text.startsWith(ENCODED_AMPERSAND, i)) {
                    result.append(CHAR_AMPERSAND);
                    i += 5;
                } else if (text.startsWith(ENCODED_SINGLE_QUOTE, i)) {
                    result.append(CHAR_SINGLE_QUOTE);
                    i += 6;
                } else if (text.startsWith(ENCODED_DOUBLE_QUOTE, i)) {
                    result.append(CHAR_DOUBLE_QUOTE);
                    i += 6;
                } else if (text.startsWith(ENCODED_LESS_THAN, i)) {
                    result.append(CHAR_LESS_THAN);
                    i += 4;
                } else if (text.startsWith(ENCODED_GREATER_THAN, i)) {
                    result.append(CHAR_GREATER_THAN);
                    i += 4;
                } else if (text.startsWith(ENCODED_UTF8, i)) {
                    final int index = text.indexOf(';', i);
                    char value = text.charAt(i+2) == 'x'
                               ? hexToChar(text.substring(i+3, index))
                               : decToChar(text.substring(i+2, index));
                    result.append(value);
                    i = index+1;
                }
                else {
                    // If we get here the website failed to properly encode the & symbol
                    result.append("&");
                    i++;
                }
            }
        }
        return result.toString();
    }

    private static char decToChar(final String substring) {
        return (char) Integer.parseInt(substring);
    }
    private static char hexToChar(final String hex) {
        return (char) Integer.parseInt(hex, 16);
    }

    public static String attributesToHtml(final Map<String, AttributeValue> map) {
        if (map == null || map.isEmpty()) return EMPTY;

        final StringBuilder builder = new StringBuilder();
        for (final Entry<String, AttributeValue> entry : map.entrySet()) {
            final AttributeValue value = entry.getValue();
            if (value == null) addAttribute(builder, entry.getKey());
            else addAttribute(builder, entry.getKey(), value.getQuote(), value.original);
        }
        return builder.toString();
    }

    public static void addAttribute(final StringBuilder builder, final String name) {
        builder.append(SPACE).append(name);
    }
    public static void addAttribute(final StringBuilder builder, final String name, final String quote, final String value) {
        builder.append(SPACE).append(name).append(EQUALS).append(quote).append(value).append(quote);
    }

    public static void writeTag(final Writer writer, final String name, final String text) throws IOException {
        if (isNullOrEmpty(text)) {
            writeSelfClosingTag(writer, name);
        } else {
            writeOpeningAndClosingTag(writer, name, text);
        }
    }
    public static void writeTag(final Writer writer, final String name, final String attributes, final String text) throws IOException {
        if (isNullOrEmpty(text)) {
            writeSelfClosingTag(writer, name, attributes);
        } else {
            writeOpeningTag(writer, name, attributes);
            writer.append(text);
            writeClosingTag(writer, name);
        }
    }
    public static void writeOpeningAndClosingTag(final Writer writer, final String name, final String text) throws IOException {
        writeOpeningTag(writer, name);
        writer.append(text);
        writeClosingTag(writer, name);
    }
    public static void writeOpeningTag(final Writer writer, final String name) throws IOException {
        writer.append(LESS_THAN).append(name).append(GREATER_THAN);
    }
    public static void writeOpeningTag(final Writer writer, final String name, final String attributes) throws IOException {
        writer.append(LESS_THAN).append(name).append(attributes).append(GREATER_THAN);
    }
    public static void writeClosingTag(final Writer writer, final String name) throws IOException {
        writer.append(LESS_THAN).append(FORWARD_SLASH).append(name).append(GREATER_THAN);
    }
    public static void writeSelfClosingTag(final Writer writer, final String name) throws IOException {
        writer.append(LESS_THAN).append(name).append(FORWARD_SLASH).append(GREATER_THAN);
    }
    public static void writeSelfClosingTag(final Writer writer, final String name, final String attributes) throws IOException {
        writer.append(LESS_THAN).append(name).append(attributes).append(SPACE).append(FORWARD_SLASH).append(GREATER_THAN);
    }

}
