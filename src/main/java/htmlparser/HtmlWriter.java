package htmlparser;

import htmlparser.core.IEComment;
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
        if (node instanceof HtmlTextElement) {
            writeIndent(writer, indent);
            String text = ((HtmlTextElement) node).original;
            if (shouldPrettyPrint()) text = text.trim();
            writer.append(text);
            writeNewLine(writer);
            return;
        }
        if (node instanceof IEComment) {
            writeIndent(writer, indent);
            String content = ((IEComment) node).content;
            writeOpeningTag(writer, node.name+content);
            writeNewLine(writer);
            for (final Tag child : node.children) {
                toHtml(child, writer, INDENT+indent);
            }
            writeOpeningTag(writer, node.closingName);
            return;
        }

        if (node.isSelfClosing) {
            writeIndent(writer, indent);
            writeSelfClosingTag(writer, node.name, attributesToHtml(node.attributes));
            writeNewLine(writer);
            return;
        }
        if (node.isAutoClosing) {
            writeIndent(writer, indent);
            writeOpeningTag(writer, node.name, attributesToHtml(node.attributes));
            writeNewLine(writer);
            return;
        }
        writeIndent(writer, indent);
        writeOpeningTag(writer, node.name, attributesToHtml(node.attributes));
        writeNewLine(writer);
        for (final Tag child : node.children) {
            toHtml(child, writer, INDENT+indent);
        }
        if (node.closingName != null)
            writeClosingTag(writer, node.closingName);
        writeNewLine(writer);
    }

    default void writeIndent(final Writer writer, final String indent) throws IOException {
        if (shouldPrettyPrint()) writer.append(indent);
    }
    default void writeNewLine(final Writer writer) throws IOException {
        if (shouldPrettyPrint()) writer.append(NEW_LINE);
    }

}
