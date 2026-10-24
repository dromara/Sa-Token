package cn.dev33.satoken.json;

import cn.dev33.satoken.exception.SaJsonConvertException;
import cn.dev33.satoken.util.SaFoxUtil;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.SerializationFeature;
import tools.jackson.databind.json.JsonMapper;

import java.util.Map;

/** Jackson 3 plain JSON converter (without polymorphic type metadata). */
public class SaJsonTemplateForJackson3Plain implements SaJsonTemplate {

    public final JsonMapper objectMapper;

    public SaJsonTemplateForJackson3Plain() {
        this.objectMapper = JsonMapper.builder()
                .disable(SerializationFeature.FAIL_ON_EMPTY_BEANS)
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                // Session values are first restored as Maps; avoid rounding decimals through Double.
                .enable(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS)
                .build();
    }

    @Override
    public String objectToJson(Object obj) {
        if (SaFoxUtil.isEmpty(obj)) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (JacksonException e) {
            throw new SaJsonConvertException(e);
        }
    }

    @Override
    public <T> T jsonToObject(String jsonStr, Class<T> type) {
        if (SaFoxUtil.isEmpty(jsonStr)) {
            return null;
        }
        try {
            return objectMapper.readValue(jsonStr, type);
        } catch (JacksonException e) {
            throw new SaJsonConvertException(e);
        }
    }

    @Override
    public Map<String, Object> jsonToMap(String jsonStr) {
        if (SaFoxUtil.isEmpty(jsonStr)) {
            return null;
        }
        try {
            return objectMapper.readValue(jsonStr, Map.class);
        } catch (JacksonException e) {
            throw new SaJsonConvertException(e);
        }
    }
}
