package htmlparser.core;

import java.util.*;

import static htmlparser.utils.HTML.escapeHtml;

public class Tag {

    public Tag parent;
    public String name;
    public String closingName;
    public Map<String, AttributeValue> attributes;
    public List<Tag> children;
    public boolean isSelfClosing;
    public boolean isAutoClosing;

    public Tag(final Tag parent, final String name,
               final Map<String, AttributeValue> attributes) {
        this(parent, name, attributes, new LinkedList<>());
    }
    public Tag(final Tag parent, final String name, final Map<String, AttributeValue> attributes,
               final List<Tag> children) {
        this.parent = parent;
        this.name = name;
        this.attributes = attributes;
        this.children = children;
    }

    public void appendChild(final Tag child) {
        this.children.add(child);
    }

    public Tag findChildForName(final String name, final Tag _default) {
        for (final Tag child : children) {
            if (name.equals(child.name))
                return child;
        }
        return _default;
    }

    public static Tag findChildForName(final Tag element, final String name, final Tag _default) {
        if (element == null) return _default;
        for (final Tag child : element.children) {
            if (name.equals(child.name))
                return child;
        }
        return _default;
    }

    public int numChildrenWithName(final String name) {
        int num = 0;
        for (final Tag child : children) {
            if (name.equals(child.name)) num++;
        }
        return num;
    }

    public static Tag element(final String name) {
        return new Tag(null, name, new HashMap<>(), new ArrayList<>());
    }
    public Tag child(final Tag child) {
        this.children.add(child);
        child.parent = this;
        return this;
    }
    public Tag attribute(final String name, final AttributeValue value) {
        attributes.put(name, value);
        return this;
    }
    public Tag text(final String text) {
        this.children.add(new HtmlTextElement(this, text, escapeHtml(text, true)));
        return this;
    }

}

