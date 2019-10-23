package htmlparser;

import htmlparser.core.HtmlTextElement;
import htmlparser.core.Tag;
import htmlparser.utils.Interfaces.ParserConfiguration;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

import static htmlparser.utils.Constants.INDENT;
import static htmlparser.utils.Constants.NEW_LINE;
import static htmlparser.utils.HTML.*;

public interface HtmlWriter extends ParserConfiguration {

    default String toHtml(final Tag node) {
        final StringWriter output = new StringWriter();

        try { toHtml(node, output); }
        catch (IOException e) { /* can't happen */ }

        return output.toString();
    }
    default void toHtml(final Tag node, final Writer writer) throws IOException {
        toHtml(node, writer, "");
    }
    default void toHtml(final Tag node, final Writer writer, final String indent) throws IOException {
        final String text = node.getText();
        if (text == null && node.children.isEmpty()) {
            writeIndent(writer, indent);
            writeSelfClosingTag(writer, node.name, attributesToHtml(node.attributes, shouldEncodeUTF8()));
            writeNewLine(writer);
        } else if (!node.hasNonTextChildren() && node.attributes.isEmpty()) {
            writeIndent(writer, indent);
            writeOpeningAndClosingTag(writer, node.name, text);
            writeNewLine(writer);
        } else {
            writeIndent(writer, indent);
            writeOpeningTag(writer, node.name, attributesToHtml(node.attributes, shouldEncodeUTF8()));
            writeNewLine(writer);
            for (final Tag child : node.children) {
                if (child instanceof HtmlTextElement) continue;

                toHtml(child, writer, INDENT+indent);
            }
            if (text != null) {
                writeIndent(writer, indent);
                writer.append(escapeHtml(text, shouldEncodeUTF8()));
            }
            writeIndent(writer, indent);
            writeClosingTag(writer, node.name);
            writeNewLine(writer);
        }
    }

    default void writeIndent(final Writer writer, final String indent) throws IOException {
        if (shouldPrettyPrint()) writer.append(indent);
    }
    default void writeNewLine(final Writer writer) throws IOException {
        if (shouldPrettyPrint()) writer.append(NEW_LINE);
    }

}
