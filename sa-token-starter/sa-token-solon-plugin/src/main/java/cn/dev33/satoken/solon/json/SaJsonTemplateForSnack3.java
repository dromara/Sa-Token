package cn.dev33.satoken.solon.json;

import cn.dev33.satoken.json.SaJsonTemplate;
import org.noear.snack.ONode;

import java.util.Map;

/**
 * @author noear
 * @since 2.0
 */
public class SaJsonTemplateForSnack3 implements SaJsonTemplate {
    @Override
    public String toJsonString(Object o) {
        return ONode.stringify(o);
    }

    @Override
    public Map<String, Object> parseJsonToMap(String s) {
        return ONode.deserialize(s, Map.class);
    }
}
