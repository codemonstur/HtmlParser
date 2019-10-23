package htmlparser;

import htmlparser.core.Tag;
import htmlparser.utils.Interfaces.CheckedIterator;

import java.io.*;
import java.nio.charset.Charset;

import static htmlparser.core.DomBuilder.toHtmlDom;
import static java.nio.charset.StandardCharsets.UTF_8;

public final class HtmlParser {
    private final HtmlCompress compress;
    private final HtmlWriter writer;
    private final HtmlIterator stream;
    private final Charset charset;

    public HtmlParser() {
        this(false, true, UTF_8);
    }

    private HtmlParser(final boolean shouldEncodeUTF8, final boolean shouldPrettyPrint, final Charset charset) {
        this.charset = charset;
        this.compress = new HtmlCompress() {};
        this.writer = new HtmlWriter() {
            public boolean shouldEncodeUTF8() {
                return shouldEncodeUTF8;
            }
            public boolean shouldPrettyPrint() {
                return shouldPrettyPrint;
            }
        };
        this.stream = new HtmlIterator() {};
    }

    public String compressHtml(final String html) {
        return compress.compressXml(html);
    }

    public Tag fromHtml(final String input) {
        try {
            return fromHtml(new ByteArrayInputStream(input.getBytes(charset)));
        } catch (IOException e) {
            // Not possible
            return null;
        }
    }
    public Tag fromHtml(final InputStream stream) throws IOException {
        return toHtmlDom(new InputStreamReader(stream, charset));
    }
    public String toHtml(final Tag node) {
        return writer.toHtml(node);
    }
    public void toHtml(final Tag node, final Writer out) throws IOException {
        writer.toHtml(node, out);
    }
    public CheckedIterator<String> iterateHtml(final InputStream in) {
        return stream.iterateHtml(new InputStreamReader(in, charset));
    }
    public CheckedIterator<Tag> iterateDom(final InputStream in) {
        return stream.iterateDom(new InputStreamReader(in, charset), charset);
    }

    public static Builder newHtmlParser() {
        return new Builder();
    }

    public static class Builder {
        private boolean shouldEncodeUTF8 = false;
        private boolean shouldPrettyPrint = true;
        private Charset charset = UTF_8;

        public Builder shouldPrettyPrint() {
            this.shouldPrettyPrint = true;
            return this;
        }
        public Builder shouldPrettyPrint(final boolean shouldPrettyPrint) {
            this.shouldPrettyPrint = shouldPrettyPrint;
            return this;
        }
        public Builder shouldEncodeUTF8() {
            this.shouldEncodeUTF8 = true;
            return this;
        }
        public Builder shouldEncodeUTF8(final boolean shouldEncodeUTF8) {
            this.shouldEncodeUTF8 = shouldEncodeUTF8;
            return this;
        }
        public Builder charset(final Charset charset) {
            this.charset = charset;
            return this;
        }

        public HtmlParser build() {
            return new HtmlParser(shouldEncodeUTF8, shouldPrettyPrint, charset);
        }
    }
}
