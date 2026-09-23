package cn.dev33.satoken.verification;

import cn.dev33.satoken.SaManager;
import cn.dev33.satoken.session.SaSession;
import cn.dev33.satoken.strategy.SaStrategy;
import org.springframework.stereotype.Component;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

/**
 * Minimal RESP2 store used only by this verification app. The local Redis server predates RESP3,
 * while the application itself needs to validate the JSON and SaSession round trip.
 */
@Component
public class RedisSessionStore {

    public void write(String key, SaSession session, long timeoutSeconds) {
        String json = SaManager.getSaJsonTemplate().objectToJson(session);
        expectOk(command("SET", key, json));
        expectOk(command("EXPIRE", key, Long.toString(timeoutSeconds)));
    }

    public String readRaw(String key) {
        return command("GET", key);
    }

    public SaSession read(String key) {
        String json = readRaw(key);
        return json == null ? null : SaManager.getSaJsonTemplate().jsonToObject(json, SaStrategy.instance.sessionClassType);
    }

    private String command(String... parts) {
        try (Socket socket = new Socket("127.0.0.1", 6379);
             BufferedOutputStream output = new BufferedOutputStream(socket.getOutputStream());
             BufferedInputStream input = new BufferedInputStream(socket.getInputStream())) {
            send(output, "AUTH", System.getenv("REDIS_PASSWORD"));
            expectOk(read(input));
            send(output, "SELECT", "15");
            expectOk(read(input));
            send(output, parts);
            return read(input);
        } catch (IOException e) {
            throw new IllegalStateException("Redis verification store failed", e);
        }
    }

    private void send(BufferedOutputStream output, String... parts) throws IOException {
        output.write(("*" + parts.length + "\r\n").getBytes(StandardCharsets.UTF_8));
        for (String part : parts) {
            byte[] bytes = part.getBytes(StandardCharsets.UTF_8);
            output.write(("$" + bytes.length + "\r\n").getBytes(StandardCharsets.UTF_8));
            output.write(bytes);
            output.write("\r\n".getBytes(StandardCharsets.UTF_8));
        }
        output.flush();
    }

    private String read(BufferedInputStream input) throws IOException {
        int marker = input.read();
        String lengthOrMessage = readLine(input);
        if (marker == '$') {
            int length = Integer.parseInt(lengthOrMessage);
            if (length < 0) {
                return null;
            }
            byte[] bytes = input.readNBytes(length);
            input.read();
            input.read();
            return new String(bytes, StandardCharsets.UTF_8);
        }
        if (marker == '-') {
            throw new IllegalStateException("Redis returned an error: " + lengthOrMessage);
        }
        return marker == '+' ? "OK" : lengthOrMessage;
    }

    private String readLine(BufferedInputStream input) throws IOException {
        StringBuilder builder = new StringBuilder();
        int value;
        while ((value = input.read()) != -1 && value != '\r') {
            builder.append((char) value);
        }
        input.read();
        return builder.toString();
    }

    private void expectOk(String response) {
        if (!"OK".equals(response) && !"1".equals(response)) {
            throw new IllegalStateException("Unexpected Redis response: " + response);
        }
    }
}
