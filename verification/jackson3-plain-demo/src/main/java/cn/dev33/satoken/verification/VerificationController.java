package cn.dev33.satoken.verification;

import cn.dev33.satoken.SaManager;
import cn.dev33.satoken.session.SaSession;
import cn.dev33.satoken.strategy.SaStrategy;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/verify")
public class VerificationController {

    private static final String SESSION_ID = "jackson3-plain-real-session";

    private final RedisSessionStore sessionStore;

    public VerificationController(RedisSessionStore sessionStore) {
        this.sessionStore = sessionStore;
    }

    @PostMapping("/write")
    public Map<String, Object> write() {
        SaSession session = SaStrategy.instance.createSession.apply(SESSION_ID);
        session.set("profile", new Profile(10001L, "张三", "ADMIN"));
        session.set("preferences", Map.of("theme", "dark", "notifications", true));
        sessionStore.write(SESSION_ID, session, 600);

        String storedJson = sessionStore.readRaw(SESSION_ID);
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("sessionId", SESSION_ID);
        response.put("template", SaManager.getSaJsonTemplate().getClass().getSimpleName());
        response.put("sessionType", session.getClass().getSimpleName());
        response.put("storedJson", storedJson);
        response.put("containsClassMarker", storedJson != null && storedJson.contains("@class"));
        response.put("nullSerialization", SaManager.getSaJsonTemplate().objectToJson(null));
        return response;
    }

    @GetMapping("/read")
    public Map<String, Object> read() {
        SaSession restored = sessionStore.read(SESSION_ID);
        Profile profile = restored.getModel("profile", Profile.class);
        Map<?, ?> preferences = restored.getModel("preferences", Map.class);

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("restoredSessionType", restored.getClass().getSimpleName());
        response.put("profile", profile);
        response.put("preferences", preferences);
        response.put("profileRestoredAsRequestedType", profile != null && profile.id() == 10001L);
        response.put("mapRestored", preferences != null && Boolean.TRUE.equals(preferences.get("notifications")));
        response.put("missingModel", restored.getModel("missing", Profile.class));
        return response;
    }

    public record Profile(long id, String name, String role) {
    }

}
