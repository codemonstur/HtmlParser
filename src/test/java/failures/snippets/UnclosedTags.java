package failures.snippets;

import java.io.IOException;

import static htmlparser.HtmlParser.newHtmlParser;
import static org.junit.Assert.assertEquals;
import static util.IO.resourceAsString;

public class UnclosedTags {

    public static void main(final String... args) throws IOException {
        final var parser = newHtmlParser().shouldPrettyPrint(false).build();
        final var code = resourceAsString("/unclosed_tags.html");

        final var result = parser.toHtml(parser.fromHtml(code));
        assertEquals(code, result);
    }

}
