package htmlparser.core;

import java.util.*;

public class Tag {

    public Tag parent;
    public String name;
    public Map<String, String> attributes;
    public List<Tag> children;

    public Tag(final Tag parent, final String name,
               final Map<String, String> attributes) {
        this(parent, name, attributes, new LinkedList<>());
    }
    public Tag(final Tag parent, final String name, final Map<String, String> attributes,
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

    public String getText() {
        final StringBuilder builder = new StringBuilder();
        for (final Tag child : children) {
            if (child instanceof HtmlTextElement)
                builder.append(((HtmlTextElement)child).text);
        }
        return builder.length() == 0 ? null : builder.toString();
    }

    public boolean hasNonTextChildren() {
        if (children.isEmpty()) return false;
        for (final Tag e : children) {
            if (e instanceof HtmlTextElement) continue;
            return true;
        }
        return false;
    }

    public static Tag element(final String name) {
        return new Tag(null, name, new HashMap<>(), new ArrayList<>());
    }
    public Tag child(final Tag child) {
        this.children.add(child);
        child.parent = this;
        return this;
    }
    public Tag attribute(final String name, final String value) {
        attributes.put(name, value);
        return this;
    }
    public Tag text(final String text) {
        this.children.add(new HtmlTextElement(this, text));
        return this;
    }

}

