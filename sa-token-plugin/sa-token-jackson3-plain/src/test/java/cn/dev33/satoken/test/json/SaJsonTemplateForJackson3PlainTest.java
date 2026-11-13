package cn.dev33.satoken.test.json;

import cn.dev33.satoken.SaManager;
import cn.dev33.satoken.json.SaJsonTemplate;
import cn.dev33.satoken.json.SaJsonTemplateForJackson3Plain;
import cn.dev33.satoken.plugin.SaTokenPluginForJackson3Plain;
import cn.dev33.satoken.session.SaSessionForJackson3PlainCustomized;
import cn.dev33.satoken.test.model.SysUser;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

/** Jackson3 plain JSON plugin tests; scenarios mirror Fastjson2 tests. */
public class SaJsonTemplateForJackson3PlainTest extends SaJsonTemplateTestCommon {

    @Test
    void convertsValues() {
        SaJsonTemplate template = new SaJsonTemplateForJackson3Plain();
        assertTemplate(SaJsonTemplateForJackson3Plain.class, template,
                "{\"age\":18,\"id\":10001,\"name\":\"张三\",\"role\":null}", false, false);
        Assertions.assertFalse(template.objectToJson(new SysUser(1, "a", 2)).contains("@class"));
    }

    @Test
    void convertsEmptyStringsToNull() {
        assertEmptyStringConversions(new SaJsonTemplateForJackson3Plain());
    }

    @Test
    void installsJsonTemplateAndSessionStrategy() {
        assertPluginInstall(new SaTokenPluginForJackson3Plain(),
                SaJsonTemplateForJackson3Plain.class,
                SaSessionForJackson3PlainCustomized.class);
    }

    @Test
    void getsModelsFromBasicNullMapAndJsonString() {
        SaSessionForJackson3PlainCustomized session =
                new SaSessionForJackson3PlainCustomized("jackson3-plain");
        session.set("count", "12");
        LinkedHashMap<String, Object> userMap = new LinkedHashMap<>();
        userMap.put("id", 10001L);
        userMap.put("name", "张三");
        userMap.put("age", 18);
        session.set("userObject", userMap);
        session.set("userJson", "{\"id\":10002,\"name\":\"李四\",\"age\":20}");
        Assertions.assertEquals(Integer.valueOf(12), session.getModel("count", Integer.class));
        Assertions.assertNull(session.getModel("missing", SysUser.class));
        Assertions.assertEquals("张三", session.getModel("userObject", SysUser.class).getName());
        Assertions.assertEquals("李四", session.getModel("userJson", SysUser.class).getName());
    }

    @Test
    void createsSessionWithNoArgConstructor() {
        Assertions.assertNotNull(new SaSessionForJackson3PlainCustomized());
    }

    @Test
    void wrapsInvalidJsonExceptions() {
        SaJsonTemplate template = new SaJsonTemplateForJackson3Plain();
        Assertions.assertThrows(RuntimeException.class, () -> template.jsonToObject("{", Object.class));
        Assertions.assertThrows(RuntimeException.class, () -> template.jsonToMap("{"));
    }

    @Test
    void pluginInstallsPlainTemplate() {
        new SaTokenPluginForJackson3Plain().install();
        Assertions.assertEquals(SaJsonTemplateForJackson3Plain.class,
                SaManager.getSaJsonTemplate().getClass());
    }

    @Test
    void discoversPluginThroughSpi() {
        cn.dev33.satoken.plugin.SaTokenPluginHolder holder =
                new cn.dev33.satoken.plugin.SaTokenPluginHolder();
        holder.loaderPlugins();
        Assertions.assertTrue(holder.isInstalledPlugin(SaTokenPluginForJackson3Plain.class));
        // Multiple explicit JSON plugins can be discovered on this test classpath.
        // Discovery is independent of their unspecified relative installation order.
        Assertions.assertNotNull(holder.getPlugin(SaTokenPluginForJackson3Plain.class));
    }

    @Test
    void restoresSessionAndModelsWithoutRedis() {
        SaJsonTemplate template = new SaJsonTemplateForJackson3Plain();
        SaSessionForJackson3PlainCustomized session = new SaSessionForJackson3PlainCustomized("round-trip");
        session.set("user", new SysUser(10001, "张三", 18));
        session.set("count", "12");
        String json = template.objectToJson(session);
        Assertions.assertFalse(json.contains("@class"));
        SaSessionForJackson3PlainCustomized restored =
                template.jsonToObject(json, SaSessionForJackson3PlainCustomized.class);
        Assertions.assertEquals("round-trip", restored.getId());
        Assertions.assertTrue(restored.get("user") instanceof java.util.Map);
        Assertions.assertEquals("张三", restored.getModel("user", SysUser.class).getName());
        Assertions.assertEquals(Integer.valueOf(12), restored.getModel("count", Integer.class));
        Assertions.assertNull(restored.getModel("missing", SysUser.class));
    }

    @Test
    @org.junit.jupiter.api.condition.EnabledIfSystemProperty(named = "ospp.redis.integration", matches = "true")
    void persistsSessionThroughDockerRedisWithoutTypeMetadata() throws Exception {
        SaJsonTemplate template = new SaJsonTemplateForJackson3Plain();
        SaSessionForJackson3PlainCustomized session = new SaSessionForJackson3PlainCustomized("redis-session");
        LinkedHashMap<String, Object> user = new LinkedHashMap<>();
        user.put("id", 10001L);
        user.put("name", "张三");
        user.put("age", 18);
        session.set("user", user);
        String json = template.objectToJson(session);
        Assertions.assertFalse(json.contains("@class"));

        String key = "sa-token:jackson3-plain:test:" + java.util.UUID.randomUUID();
        try {
            Assertions.assertEquals("+OK", redisCommand("SET", key, json, "EX", "60"));
            String restoredJson = redisCommand("GET", key);
            SaSessionForJackson3PlainCustomized restored =
                    template.jsonToObject(restoredJson, SaSessionForJackson3PlainCustomized.class);
            Assertions.assertEquals("张三", restored.getModel("user", SysUser.class).getName());
        } finally {
            redisCommand("DEL", key);
        }
    }

    private static String redisCommand(String command, String... args) throws Exception {
        try (Socket socket = new Socket()) {
            socket.connect(new java.net.InetSocketAddress(
                    System.getProperty("ospp.redis.host", "127.0.0.1"),
                    Integer.getInteger("ospp.redis.port", 6379)), 3000);
            socket.setSoTimeout(3000);
            java.io.OutputStream out = socket.getOutputStream();
            StringBuilder request = new StringBuilder("*").append(args.length + 1).append("\r\n")
                    .append("$").append(command.length()).append("\r\n").append(command).append("\r\n");
            for (String arg : args) {
                byte[] bytes = arg.getBytes(StandardCharsets.UTF_8);
                request.append("$").append(bytes.length).append("\r\n").append(arg).append("\r\n");
            }
            out.write(request.toString().getBytes(StandardCharsets.UTF_8));
            out.flush();
            java.io.InputStream in = socket.getInputStream();
            String header = readLine(in);
            if (header.startsWith("-")) {
                throw new java.io.IOException("Redis command failed: " + header);
            }
            if (header.startsWith("$")) {
                int length = Integer.parseInt(header.substring(1));
                if (length == -1) {
                    return null;
                }
                byte[] payload = in.readNBytes(length);
                byte[] terminator = in.readNBytes(2);
                if (payload.length != length || terminator.length != 2
                        || terminator[0] != '\r' || terminator[1] != '\n') {
                    throw new java.io.EOFException("Incomplete Redis response");
                }
                return new String(payload, StandardCharsets.UTF_8);
            }
            return header;
        }
    }

    private static String readLine(java.io.InputStream in) throws Exception {
        java.io.ByteArrayOutputStream buf = new java.io.ByteArrayOutputStream();
        int previous = -1;
        int current;
        while ((current = in.read()) != -1) {
            if (previous == '\r' && current == '\n') {
                byte[] bytes = buf.toByteArray();
                return new String(bytes, 0, bytes.length - 1, StandardCharsets.UTF_8);
            }
            buf.write(current);
            previous = current;
        }
        throw new java.io.EOFException("Incomplete Redis response header");
    }
}
