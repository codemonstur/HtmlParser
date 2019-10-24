package scripts;

import htmlparser.HtmlParser;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static java.nio.file.StandardOpenOption.CREATE;

public class Gauntlet {

    public interface PageParser {
        void parse(String domain, String source);
    }

    public static class PrintlnCapturingPrintWriter extends PrintStream {
        private final PrintStream stdout = System.out;
        private final List<String> lines = new ArrayList<>();
        public PrintlnCapturingPrintWriter() {
            super(System.out);
        }

        public void println(final String line) {
            stdout.println(line);
            lines.add(line);
        }

        public List<String> getLines() {
            return lines;
        }
    }

    public static void main(final String... args) throws IOException {
        final HtmlParser parser = new HtmlParser();

        final PrintlnCapturingPrintWriter writer = new PrintlnCapturingPrintWriter();
        System.setOut(writer);

        final List<String> domains = toLines("/domains.txt");
        int i = 0;
        for (final String domain : domains) {
            System.out.print("" + i++ + "/" + domains.size() + " ");
            downloadHtmlPage(domain, (domain1, source) -> {
                try {
                    compareResult(domain1, source, parser.toHtml(parser.fromHtml(source)));
                } catch (Exception e) {
                    System.out.println("[FAILED] Parser failed for " + domain + " with " + e.getClass().getSimpleName() + ", message: " + e.getMessage());
                }
            });
        }

        Files.writeString(Paths.get("results.txt"), String.join("\n", writer.getLines()), CREATE);
    }

    private static List<String> toLines(final String resource) throws IOException {
        final List<String> lines = new ArrayList<>();

        try (final BufferedReader in = new BufferedReader(new InputStreamReader(Gauntlet.class.getResourceAsStream(resource)))) {
            String line;
            while ((line = in.readLine()) != null) {
                lines.add(line);
            }
        }

        return lines;
    }

    private static OkHttpClient HTTP = new OkHttpClient();
    private static void downloadHtmlPage(final String domain, final PageParser parser) {
        final Request request = new Request.Builder().url("http://"+domain).get().build();

        try (final Response response = HTTP.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("not a 200 OK: " + response.code());
            final String header = response.header("Content-Type");
            if (header == null || !header.startsWith("text/html")) throw new IOException("unknown content-type: " + header);
            final ResponseBody body = response.body();
            if (body == null) throw new IOException("no body");

            parser.parse(domain, body.string());
        } catch (Exception e) {
            System.out.println("[ERROR] Can't download: " + domain + ", " + e.getMessage());
        }
    }

    private static void compareResult(final String domain, final String source, final String output) {
        if (source.equals(output))
            System.out.println("[SUCCESS] Domain "+ domain + " parsed sucessfully.");
        else {
            System.out.println("[FAILED] Domain "+ domain + " failed.");
        }
    }
}
