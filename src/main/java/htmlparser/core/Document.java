package htmlparser.core;

import java.util.ArrayList;
import java.util.List;

public class Document extends Tag {

    public final List<Tag> endTags = new ArrayList<>();

    public Document() {
        super(null, "", null);
    }
}
