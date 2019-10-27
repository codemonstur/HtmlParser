package htmlparser.core;

import htmlparser.utils.Constants;

public final class AttributeValue {

    public String original;
    public String decoded;
    public boolean isDoubleQuoted;
    public boolean isSingleQuoted;

    public AttributeValue(final String original, final String decoded, final boolean isDoubleQuoted, final boolean isSingleQuoted) {
        this.original = original;
        this.decoded = decoded;
        this.isDoubleQuoted = isDoubleQuoted;
        this.isSingleQuoted = isSingleQuoted;
    }

    public String getQuote() {
        if (isSingleQuoted) return Constants.SINGLE_QUOTE;
        if (isDoubleQuoted) return Constants.DOUBLE_QUOTE;
        return "";
    }
}
