package util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

import static java.nio.charset.StandardCharsets.UTF_8;

public enum IO {;

    public static String resourceAsString(final String resource) throws IOException {
        try (final InputStream in = IO.class.getResourceAsStream(resource)) {
            if (in == null) throw new IOException("Resource " + resource + " does not exist.");
            return streamAsString(in, UTF_8);
        }
    }

    public static String streamAsString(final InputStream in, final Charset charset) throws IOException {
        try (final ByteArrayOutputStream result = new ByteArrayOutputStream()) {
            final byte[] buffer = new byte[1024]; int read;
            while ((read = in.read(buffer)) != -1) {
                result.write(buffer, 0, read);
            }
            return result.toString(charset.name());
        }
    }

}
