package failures.snippets;

import htmlparser.core.Tag;

import java.io.IOException;

import static htmlparser.HtmlParser.newHtmlParser;
import static org.junit.Assert.assertEquals;
import static util.IO.resourceAsString;

public class TooManyClosingTags {

    public static void main(final String... args) throws IOException {
        final var parser = newHtmlParser().shouldPrettyPrint(false).build();
        final var code = resourceAsString("/snippets/too_many_closing_tags.html");

        final Tag tag = parser.fromHtml(code);
        final var result = parser.toHtml(tag);
        assertEquals(code, result);
    }

}
