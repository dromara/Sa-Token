package cn.dev33.satoken.session;

import cn.dev33.satoken.util.SaFoxUtil;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.SerializationFeature;
import tools.jackson.databind.json.JsonMapper;

import java.util.Map;

/** SaSession variant that converts plain Map values with Jackson 3. */
public class SaSessionForJackson3PlainCustomized extends SaSession {
    private static final long serialVersionUID = 1L;
    private static final JsonMapper MAPPER = JsonMapper.builder()
            .disable(SerializationFeature.FAIL_ON_EMPTY_BEANS)
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
            .enable(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS)
            .build();

    public SaSessionForJackson3PlainCustomized() { super(); }
    public SaSessionForJackson3PlainCustomized(String id) { super(id); }

    @Override
    public <T> T getModel(String key, Class<T> cs) {
        Object value = get(key);
        if (SaFoxUtil.isBasicType(cs)) {
            return SaFoxUtil.getValueByType(value, cs);
        }
        if (valueIsNull(value)) {
            return null;
        }
        try {
            if (value instanceof Map) {
                return MAPPER.convertValue(value, cs);
            }
            if (value instanceof String) {
                return MAPPER.readValue((String) value, cs);
            }
            return MAPPER.convertValue(value, cs);
        } catch (JacksonException | IllegalArgumentException e) {
            throw new cn.dev33.satoken.exception.SaJsonConvertException(e);
        }
    }
}
