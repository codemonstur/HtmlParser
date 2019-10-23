package htmlparser.error;

import java.io.IOException;

public final class InvalidHtml extends IOException {
    public InvalidHtml() {}
    public InvalidHtml(final String message) {
        super(message);
    }
}
