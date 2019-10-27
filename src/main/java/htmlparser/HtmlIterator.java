package htmlparser;

import htmlparser.core.DomBuilder;
import htmlparser.core.Tag;
import htmlparser.error.InvalidHtml;
import htmlparser.utils.Interfaces.CheckedIterator;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

public interface HtmlIterator {

    default CheckedIterator<String> iterateHtml(final InputStreamReader in) {
        return new CheckedIterator<String>() {
            public boolean hasNext() throws IOException {
                final Character next = readFirstNonWhiteChar(in);
                if (next == null) return false;
                if (next == '<') return true;
                throw new InvalidHtml();
            }

            public String next() throws IOException {
                return readUntilCurrentTagIsClosed(in);
            }
        };
    }

    default CheckedIterator<Tag> iterateDom(final InputStreamReader in, final Charset charset) {
        return new CheckedIterator<Tag>() {
            public boolean hasNext() throws Exception {
                final Character next = readFirstNonWhiteChar(in);
                if (next == null) return false;
                if (next == '<') return true;
                throw new InvalidHtml();
            }

            public Tag next() throws Exception {
                final String html = readUntilCurrentTagIsClosed(in);
                return DomBuilder.toHtmlDocument(new InputStreamReader(new ByteArrayInputStream(html.getBytes(charset)), charset));
            }
        };
    }

    static Character readFirstNonWhiteChar(final InputStreamReader in) throws IOException {
        int r;
        while ((r = in.read()) != -1) {
            final char c = (char) r;
            if (c == ' ' || c == '\t' || c == '\n' || c == '\r') continue;
            return c;
        }
        return null;
    }

    static String readUntilCurrentTagIsClosed(final InputStreamReader in) throws IOException {
        final StringBuilder builder = new StringBuilder();
        builder.append('<');

        boolean previousWasForwardSlash = false;
        boolean previousWasSmallerThan = true;

        int numberOfTagsOpened = 1;

        int r;
        while ((r = in.read()) != -1) {
            final char c = (char) r;
            builder.append(c);
            if (c == '>' && previousWasForwardSlash) numberOfTagsOpened--;
            if (c == '/' && previousWasSmallerThan) numberOfTagsOpened -= 2;
            if (c == '<') numberOfTagsOpened++;

            if (numberOfTagsOpened < 0) throw new InvalidHtml();
            if (numberOfTagsOpened == 0) {
                if (c != '>') {
                    while ((r = in.read()) != -1) {
                        final char ch = (char) r;
                        builder.append(ch);
                        if (ch == '>') break;
                    }
                }
                break;
            }

            previousWasForwardSlash = c == '/';
            previousWasSmallerThan = c == '<';
        }
        return builder.toString();
    }

}
