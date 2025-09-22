package ch.so.agi.mcp;

import org.junit.jupiter.api.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

@Tag("e2e")
public class StdioE2eTest {

    @Test
    void endToEndInitializeListCall() throws Exception {
        System.out.println("Hallo Welt.");
//        Process p = new ProcessBuilder("java", "-jar", "build/libs/mcp-interlis.jar")
//                .redirectErrorStream(true)
//                .start();
//
//        OutputStream out = p.getOutputStream();
//        InputStream in = p.getInputStream();
//
//        send(out, """
//                {"jsonrpc":"2.0","id":1,"method":"initialize","params":{}}
//                """);
//        String r1 = read(in);
//        assertThat(r1).contains("\"serverName\"");
//
//        send(out, """
//                {"jsonrpc":"2.0","id":2,"method":"tools/list","params":{}}
//                """);
//        String r2 = read(in);
//        assertThat(r2).contains("\"createSnippet\"");
//
//        send(out, """
//                  {"jsonrpc":"2.0","id":3,"method":"tools/call","params":{
//                    "name":"createSnippet",
//                    "arguments":{"name":"E2E","version":"2025-09-16","uri":"https://example.org/e2e"}
//                  }}
//                """);
//        String r3 = read(in);
//        assertThat(r3).contains("MODEL E2E (de) AT \"https://example.org/e2e\" VERSION \"2025-09-16\"");
//
//        p.destroy();
//        p.waitFor(5, TimeUnit.SECONDS);
    }

    private static void send(OutputStream out, String json) throws IOException {
        byte[] body = json.getBytes(StandardCharsets.UTF_8);
        String header = "Content-Length: " + body.length + "\r\n\r\n";
        out.write(header.getBytes(StandardCharsets.UTF_8));
        out.write(body);
        out.flush();
    }

    private static String read(InputStream in) throws IOException {
        // read headers
        var reader = new PushbackInputStream(in);
        StringBuilder headers = new StringBuilder();
        int state = 0;
        while (true) {
            int b = reader.read();
            if (b == -1)
                throw new EOFException();
            headers.append((char) b);
            state = (state == 0 && b == '\r') ? 1
                    : (state == 1 && b == '\n') ? 2 : (state == 2 && b == '\r') ? 3 : (state == 3 && b == '\n') ? 4 : 0;
            if (state == 4)
                break;
        }
        int contentLength = -1;
        for (String line : headers.toString().split("\r\n")) {
            if (line.toLowerCase().startsWith("content-length:")) {
                contentLength = Integer.parseInt(line.split(":", 2)[1].trim());
            }
        }
        if (contentLength < 0)
            throw new IOException("Missing Content-Length");
        byte[] body = in.readNBytes(contentLength);
        return new String(body, StandardCharsets.UTF_8);
    }
}
