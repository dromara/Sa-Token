package cn.dev33.satoken.test.json;

import cn.dev33.satoken.SaManager;
import cn.dev33.satoken.exception.SaJsonConvertException;
import cn.dev33.satoken.json.*;
import cn.dev33.satoken.plugin.SaTokenPluginForJackson3;
import cn.dev33.satoken.plugin.SaTokenPluginForJackson3Plain;
import cn.dev33.satoken.session.SaSessionForJackson3PlainCustomized;
import cn.dev33.satoken.test.model.SysUser;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/** Plain JSON boundaries, coexistence and compatibility contracts. */
public class SaJsonTemplateForJackson3PlainBoundaryTest extends SaJsonTemplateTestCommon {
    private final SaJsonTemplate template = new SaJsonTemplateForJackson3Plain();

    @Test
    void nestedCollectionsRemainUntypedUntilTargetClassIsSpecified() {
        Map<String, Object> source = new LinkedHashMap<>();
        source.put("users", Arrays.asList(new SysUser(1, "测试🙂", 18)));
        source.put("nested", Collections.singletonMap("empty", null));
        String json = template.objectToJson(source);
        assertFalse(json.contains("@class"));
        Map<?, ?> restored = template.jsonToObject(json, Map.class);
        Object user = ((List<?>) restored.get("users")).get(0);
        assertTrue(user instanceof Map);
        SysUser typed = template.jsonToObject(template.objectToJson(user), SysUser.class);
        assertEquals("测试🙂", typed.getName());
        assertTrue(((Map<?, ?>) restored.get("nested")).containsKey("empty"));
        assertNull(((Map<?, ?>) restored.get("nested")).get("empty"));
    }

    @Test
    void explicitTypesPreserveDatesAndDecimalPrecision() {
        ValueBean source = new ValueBean();
        source.date = new Date(1234567890000L);
        source.time = LocalDateTime.of(2026, 10, 24, 12, 30, 45);
        source.amount = new BigDecimal("12345678901234567890.12345678901234567890");
        source.number = Long.MAX_VALUE;
        String json = template.objectToJson(source);
        assertFalse(json.contains("@class"));
        ValueBean restored = template.jsonToObject(json, ValueBean.class);
        assertEquals(source.date, restored.date);
        assertEquals(source.time, restored.time);
        assertEquals(source.amount, restored.amount);
        assertEquals(source.number, restored.number);
        assertEquals(Long.MAX_VALUE, template.jsonToMap("{\"n\":9223372036854775807}").get("n"));
    }

    @Test
    void typedSessionValuesUseExplicitModelConversion() {
        SaSessionForJackson3PlainCustomized session = new SaSessionForJackson3PlainCustomized("values");
        SysUser user = new SysUser(1, "bean", 20);
        session.set("bean", user);
        assertEquals(user.toString(), session.getModel("bean", SysUser.class).toString());
        assertThrows(NullPointerException.class, () -> session.set("empty", null));
        assertNull(session.getModel("empty", SysUser.class));
        session.set("bad", "{broken");
        SaJsonConvertException error = assertThrows(SaJsonConvertException.class,
                () -> session.getModel("bad", SysUser.class));
        assertNotNull(error.getCause());
    }

    @Test
    void sessionRoundTripPreservesDecimalPrecisionBeforeGetModel() {
        ValueBean source = new ValueBean();
        source.amount = new BigDecimal("12345678901234567890.12345678901234567890");
        source.number = Long.MAX_VALUE;
        SaSessionForJackson3PlainCustomized session = new SaSessionForJackson3PlainCustomized("decimal");
        session.set("values", source);
        SaSessionForJackson3PlainCustomized restored = template.jsonToObject(
                template.objectToJson(session), SaSessionForJackson3PlainCustomized.class);
        ValueBean model = restored.getModel("values", ValueBean.class);
        assertEquals(source.amount, model.amount);
        assertEquals(source.number, model.number);
    }

    @Test
    void forgedClassHintsAreDataAndNeverInstantiateHintedClass() {
        String json = "{\"@class\":\"java.lang.ProcessBuilder\",\"name\":\"safe\",\"id\":1}";
        Object value = template.jsonToObject(json);
        assertTrue(value instanceof Map);
        assertEquals("java.lang.ProcessBuilder", ((Map<?, ?>) value).get("@class"));
        assertEquals("safe", template.jsonToObject(json, SysUser.class).getName());
        assertTrue(template.objectToJson(value).contains("@class"),
                "Plain does not remove a caller-provided business field");
    }

    @Test
    void invalidInputsPreserveConversionCause() {
        for (String json : Arrays.asList("{", "[1,2]", "{\"age\":\"invalid\"}")) {
            SaJsonConvertException error = assertThrows(SaJsonConvertException.class,
                    () -> template.jsonToObject(json, SysUser.class));
            assertNotNull(error.getCause());
        }
        assertThrows(SaJsonConvertException.class, () -> template.jsonToMap("[1]"));
    }

    @Test
    void plainWinsOverWeakDefaultJackson3InEitherInstallationOrder() {
        SaManager.setSaJsonTemplate(new SaJsonTemplateDefaultImpl());
        new SaTokenPluginForJackson3().install();
        assertInstanceOf(SaJsonTemplateForJackson3.class, SaManager.getSaJsonTemplate());
        new SaTokenPluginForJackson3Plain().install();
        new SaTokenPluginForJackson3().install();
        assertInstanceOf(SaJsonTemplateForJackson3Plain.class, SaManager.getSaJsonTemplate());
    }

    @Test
    void explicitPlainInstallationReplacesPreviousCustomTemplateLikeFastjson2() {
        SaManager.setSaJsonTemplate(new SaJsonTemplateDefaultImpl() {});
        new SaTokenPluginForJackson3Plain().install();
        assertInstanceOf(SaJsonTemplateForJackson3Plain.class, SaManager.getSaJsonTemplate());
        SaJsonTemplate custom = new SaJsonTemplateDefaultImpl() {};
        SaManager.setSaJsonTemplate(custom);
        new SaTokenPluginForJackson3().install();
        assertSame(custom, SaManager.getSaJsonTemplate());
    }

    @Test
    void fastjson2AndPlainAgreeOnBasicModelsButNullFieldOutputDiffers() {
        SaJsonTemplate fastjson = new SaJsonTemplateForFastjson2();
        SysUser user = new SysUser(1, "comparison", 18);
        assertEquals(fastjson.jsonToObject(fastjson.objectToJson(user), SysUser.class).toString(),
                template.jsonToObject(template.objectToJson(user), SysUser.class).toString());
        assertNull(fastjson.objectToJson(null));
        assertNull(template.objectToJson(null));
        assertTrue(template.objectToJson(user).contains("\"role\":null"));
        assertFalse(fastjson.objectToJson(user).contains("\"role\""));
    }

    @Test
    void typedAndPlainPayloadsAreNotSymmetricallyInterchangeable() {
        resetJsonStrategy();
        SaJsonTemplate typed = new SaJsonTemplateForJackson3();
        SaSessionForJackson3PlainCustomized session = new SaSessionForJackson3PlainCustomized("compat");
        String typedJson = typed.objectToJson(session);
        assertTrue(typedJson.contains("@class"));
        assertThrows(SaJsonConvertException.class,
                () -> template.jsonToObject(typedJson, SaSessionForJackson3PlainCustomized.class));
        assertThrows(SaJsonConvertException.class,
                () -> typed.jsonToObject(template.objectToJson(session), SaSessionForJackson3PlainCustomized.class));
    }

    public static class ValueBean {
        public Date date;
        public LocalDateTime time;
        public BigDecimal amount;
        public long number;
    }
}
