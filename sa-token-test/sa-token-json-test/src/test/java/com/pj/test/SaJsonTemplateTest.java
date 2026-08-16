package com.pj.test;

import cn.dev33.satoken.SaManager;
import cn.dev33.satoken.exception.NotImplException;
import cn.dev33.satoken.exception.SaJsonConvertException;
import cn.dev33.satoken.exception.SaTokenException;
import cn.dev33.satoken.json.*;
import cn.dev33.satoken.session.SaSession;
import cn.dev33.satoken.strategy.SaJsonStrategy;
import com.pj.test.model.SysUser;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Sa-Token json 序列化模块测试
 * 
 * @author click33 
 *
 */
public class SaJsonTemplateTest {

	// 开始 
	@BeforeAll
    public static void beforeClass() {
    	System.out.println("\n\n------------------------ SaJsonTemplateTest star ...");
    }

	// 结束 
    @AfterAll
    public static void afterClass() {
    	System.out.println("\n\n------------------------ SaJsonTemplateTest end ... \n");
    }

    // 测试：DefaultImpl
    @Test
    public void testDefaultImpl() {
        SaManager.setSaJsonTemplate(new SaJsonTemplateDefaultImpl());
        Assertions.assertEquals(SaManager.getSaJsonTemplate().getClass(), SaJsonTemplateDefaultImpl.class);

        // test   Object -> Json
        SysUser user = new SysUser(10001, "张三", 18);
        Assertions.assertThrows(NotImplException.class, () -> SaManager.getSaJsonTemplate().objectToJson(user) );
        Assertions.assertThrows(NotImplException.class, () -> SaManager.getSaJsonTemplate().jsonToObject("xxx", SysUser.class) );
        Assertions.assertThrows(NotImplException.class, () -> SaManager.getSaJsonTemplate().jsonToObject("xxx") );
        Assertions.assertThrows(NotImplException.class, () -> SaManager.getSaJsonTemplate().jsonToMap("xxx") );
    }

    // 测试：Jackson
    @Test
    public void testJackson() {
        SaJsonStrategy.instance.resetState();
        SaManager.setSaJsonTemplate(new SaJsonTemplateForJackson());
        Assertions.assertEquals(SaManager.getSaJsonTemplate().getClass(), SaJsonTemplateForJackson.class);
        Assertions.assertTrue(SaJsonStrategy.instance.isInit());

        // test   Object -> Json
        SysUser user = new SysUser(10001, "张三", 18);
        String objectJson = SaManager.getSaJsonTemplate().objectToJson(user);
        Assertions.assertEquals("{\"@class\":\"com.pj.test.model.SysUser\",\"id\":10001,\"name\":\"张三\",\"age\":18,\"role\":null}", objectJson);

        // test   Json -> Object
        SysUser user2 = SaManager.getSaJsonTemplate().jsonToObject(objectJson, SysUser.class);
        Assertions.assertEquals(user2.toString(), user.toString());

        SysUser user3 = (SysUser)SaManager.getSaJsonTemplate().jsonToObject(objectJson);
        Assertions.assertEquals(user3.toString(), user.toString());

        // more
        testNull();
        testMap();
    }

    // 测试：Fastjson
    @Test
    public void testFastjson() {
        SaManager.setSaJsonTemplate(new SaJsonTemplateForFastjson());
        Assertions.assertEquals(SaManager.getSaJsonTemplate().getClass(), SaJsonTemplateForFastjson.class);

        // test   Object -> Json
        SysUser user = new SysUser(10001, "张三", 18);
        String objectJson = SaManager.getSaJsonTemplate().objectToJson(user);
        Assertions.assertEquals("{\"age\":18,\"id\":10001,\"name\":\"张三\"}", objectJson);

        // test   Json -> Object
        SysUser user2 = SaManager.getSaJsonTemplate().jsonToObject(objectJson, SysUser.class);
        Assertions.assertEquals(user2.toString(), user.toString());

        // more
        testNull();
        testMap();
    }

    // 测试：Fastjson2
    @Test
    public void testFastjson2() {
        SaManager.setSaJsonTemplate(new SaJsonTemplateForFastjson2());
        Assertions.assertEquals(SaManager.getSaJsonTemplate().getClass(), SaJsonTemplateForFastjson2.class);

        // test   Object -> Json
        SysUser user = new SysUser(10001, "张三", 18);
        String objectJson = SaManager.getSaJsonTemplate().objectToJson(user);
        Assertions.assertEquals("{\"age\":18,\"id\":10001,\"name\":\"张三\"}", objectJson);

        // test   Json -> Object
        SysUser user2 = SaManager.getSaJsonTemplate().jsonToObject(objectJson, SysUser.class);
        Assertions.assertEquals(user2.toString(), user.toString());

        // more
        testNull();
        testMap();
    }

    // 测试：Snack3
    @Test
    public void testSnack3() {
        SaManager.setSaJsonTemplate(new SaJsonTemplateForSnack3());
        Assertions.assertEquals(SaManager.getSaJsonTemplate().getClass(), SaJsonTemplateForSnack3.class);

        // test   Object -> Json
        SysUser user = new SysUser(10001, "张三", 18);
        String objectJson = SaManager.getSaJsonTemplate().objectToJson(user);
        Assertions.assertEquals("{\"id\":10001,\"name\":\"张三\",\"age\":18}", objectJson);

        // test   Json -> Object
        SysUser user2 = SaManager.getSaJsonTemplate().jsonToObject(objectJson, SysUser.class);
        Assertions.assertEquals(user2.toString(), user.toString());

        // more
        testNull();
        testMap();
    }

    // 测试：Snack4
    @Test
    public void testSnack4() {
        SaJsonStrategy.instance.resetState();
        SaManager.setSaJsonTemplate(new SaJsonTemplateForSnack4());
        Assertions.assertEquals(SaManager.getSaJsonTemplate().getClass(), SaJsonTemplateForSnack4.class);
        Assertions.assertTrue(SaJsonStrategy.instance.isInit());

        // test   Object -> Json
        SysUser user = new SysUser(10001, "张三", 18);
        String objectJson = SaManager.getSaJsonTemplate().objectToJson(user);
        Assertions.assertEquals("{\"id\":10001,\"name\":\"张三\",\"age\":18}", objectJson);

        // test   Json -> Object
        SysUser user2 = SaManager.getSaJsonTemplate().jsonToObject(objectJson, SysUser.class);
        Assertions.assertEquals(user2.toString(), user.toString());

        // more
        testNull();
        testMap();
    }

    // 测试：Snack4 允许 Session 中常见的 JDK 值类型（Date、金额、java.time 等）
    @Test
    public void testSnack4AllowCommonTypesInSession() {
        SaJsonStrategy.instance.resetState();
        SaManager.setSaJsonTemplate(new SaJsonTemplateForSnack4());
        Date time = new Date(1234567890000L);
        BigDecimal amount = new BigDecimal("99.50");
        LocalDateTime loginTime = LocalDateTime.of(2026, 8, 15, 12, 30, 0);
        SaSession session = new SaSession("test-session");
        session.set("time", time);
        session.set("amount", amount);
        session.set("loginTime", loginTime);
        String json = SaManager.getSaJsonTemplate().objectToJson(session);
        SaSession session2 = SaManager.getSaJsonTemplate().jsonToObject(json, SaSession.class);
        Object timeValue = session2.get("time");
        if (timeValue instanceof Date) {
            Assertions.assertEquals(time, timeValue);
        } else {
            Assertions.assertEquals(time.getTime(), ((Number) timeValue).longValue());
        }
        Assertions.assertEquals(0, amount.compareTo(new BigDecimal(session2.get("amount").toString())));
        Object loginTimeValue = session2.get("loginTime");
        if (loginTimeValue instanceof LocalDateTime) {
            Assertions.assertEquals(loginTime, loginTimeValue);
        } else {
            Assertions.assertNotNull(loginTimeValue);
        }
    }

    // 测试：Snack4 白名单拦截未授权 @type
    @Test
    public void testSnack4BlockUnknownAllowType() {
        SaJsonStrategy.instance.resetState();
        SaManager.setSaJsonTemplate(new SaJsonTemplateForSnack4());
        String evilJson = "{\"@type\":\"java.lang.ProcessBuilder\",\"command\":[\"calc.exe\"]}";
        SaJsonConvertException ex = Assertions.assertThrows(SaJsonConvertException.class, () ->
                SaManager.getSaJsonTemplate().jsonToObject(evilJson));
        Assertions.assertEquals(
                "无法反序列化的类型：java.lang.ProcessBuilder，请先将其注册到 JSON 全局类型白名单",
                ex.getMessage());
    }

    // 测试：Snack4 遇到 classpath 不存在的类型时，保留原始异常信息
    @Test
    public void testSnack4ClassNotFoundKeepsOriginalMessage() {
        SaJsonStrategy.instance.resetState();
        SaManager.setSaJsonTemplate(new SaJsonTemplateForSnack4());
        String json = "{\"@type\":\"cn.dev33.satoken.sso.model.SaSsoClientInfo\",\"mode\":3}";
        SaJsonConvertException ex = Assertions.assertThrows(SaJsonConvertException.class, () ->
                SaManager.getSaJsonTemplate().jsonToObject(json));
        Assertions.assertTrue(ex.getMessage().contains("Blocked type, class: cn.dev33.satoken.sso.model.SaSsoClientInfo"));
        Assertions.assertFalse(ex.getMessage().contains("JSON 全局类型白名单"));
    }

    // 测试：Snack4 初始化后不可再 register
    @Test
    public void testSnack4StrategyInit() {
        SaJsonStrategy.instance.resetState();
        SaManager.setSaJsonTemplate(new SaJsonTemplateForSnack4());
        Assertions.assertThrows(SaTokenException.class, () ->
                SaJsonStrategy.instance.registerAllowType(String.class));
    }

    // 测试：Jackson 允许 Session 中常见的 JDK 值类型（Date、金额、java.time 等）
    @Test
    public void testJacksonAllowCommonTypesInSession() {
        SaJsonStrategy.instance.resetState();
        SaManager.setSaJsonTemplate(new SaJsonTemplateForJackson());
        Date time = new Date(1234567890000L);
        BigDecimal amount = new BigDecimal("99.50");
        LocalDateTime loginTime = LocalDateTime.of(2026, 8, 15, 12, 30, 0);
        SaSession session = new SaSession("test-session");
        session.set("time", time);
        session.set("amount", amount);
        session.set("loginTime", loginTime);
        String json = SaManager.getSaJsonTemplate().objectToJson(session);
        SaSession session2 = SaManager.getSaJsonTemplate().jsonToObject(json, SaSession.class);
        Assertions.assertEquals(time, session2.get("time"));
        Assertions.assertEquals(amount, session2.get("amount"));
        Assertions.assertEquals(loginTime, session2.get("loginTime"));
    }

    // 测试：Jackson 允许 Session Map 中的基本类型包装类（如 Long 过期时间戳）
    @Test
    public void testJacksonAllowWrapperTypesInSessionMap() {
        SaJsonStrategy.instance.resetState();
        SaManager.setSaJsonTemplate(new SaJsonTemplateForJackson());
        java.util.LinkedHashMap<String, Long> tokenIndexMap = new java.util.LinkedHashMap<>();
        tokenIndexMap.put("access-token-value", 1786860583046L);
        SaSession session = new SaSession("oauth2-raw-session");
        session.set("__HD_ACCESS_TOKEN_MAP", tokenIndexMap);
        String json = SaManager.getSaJsonTemplate().objectToJson(session);
        SaSession session2 = SaManager.getSaJsonTemplate().jsonToObject(json, SaSession.class);
        @SuppressWarnings("unchecked")
        java.util.Map<String, Long> map2 = (java.util.Map<String, Long>) session2.get("__HD_ACCESS_TOKEN_MAP");
        Assertions.assertEquals(1786860583046L, map2.get("access-token-value"));
    }

    // 测试：Jackson 白名单拦截未授权 @class
    @Test
    public void testJacksonBlockUnknownAllowType() {
        SaJsonStrategy.instance.resetState();
        SaManager.setSaJsonTemplate(new SaJsonTemplateForJackson());
        String evilJson = "{\"@class\":\"java.lang.ProcessBuilder\",\"command\":[\"calc.exe\"]}";
        SaJsonConvertException ex = Assertions.assertThrows(SaJsonConvertException.class, () ->
                SaManager.getSaJsonTemplate().jsonToObject(evilJson));
        Assertions.assertEquals(
                "无法反序列化的类型：java.lang.ProcessBuilder，请先将其注册到 JSON 全局类型白名单",
                ex.getMessage());
    }

    // 测试：Jackson 遇到 classpath 不存在的类型时，保留原始异常信息
    @Test
    public void testJacksonClassNotFoundKeepsOriginalMessage() {
        SaJsonStrategy.instance.resetState();
        SaManager.setSaJsonTemplate(new SaJsonTemplateForJackson());
        String json = "{\"@class\":\"cn.dev33.satoken.sso.model.SaSsoClientInfo\",\"mode\":3}";
        SaJsonConvertException ex = Assertions.assertThrows(SaJsonConvertException.class, () ->
                SaManager.getSaJsonTemplate().jsonToObject(json));
        Assertions.assertTrue(ex.getMessage().contains("no such class found"));
        Assertions.assertFalse(ex.getMessage().contains("JSON 全局类型白名单"));
    }

    // 测试：Jackson 初始化后不可再 register
    @Test
    public void testJacksonStrategyInit() {
        SaJsonStrategy.instance.resetState();
        SaManager.setSaJsonTemplate(new SaJsonTemplateForJackson());
        Assertions.assertThrows(SaTokenException.class, () ->
                SaJsonStrategy.instance.registerAllowType(String.class));
    }

    // 测试 Map 的转换
    private void testMap() {

        // test   Map -> Json
        Map<String, Object> map = new HashMap<>();
        map.put("id", 10001);
        map.put("name", "张三");
        map.put("age", 18);
        String mapJson = SaManager.getSaJsonTemplate().objectToJson(map);
        Assertions.assertEquals("{\"name\":\"张三\",\"id\":10001,\"age\":18}", mapJson);

        // test   Json -> Map
        Map<String, Object> map2 = SaManager.getSaJsonTemplate().jsonToMap(mapJson);
        Assertions.assertEquals(map2.toString(), map.toString());

    }

    // 测试 Null 值
    private void testNull() {
        Assertions.assertNull(SaManager.getSaJsonTemplate().objectToJson(null));
        Assertions.assertNull(SaManager.getSaJsonTemplate().jsonToObject(null, SysUser.class));
        Assertions.assertNull(SaManager.getSaJsonTemplate().jsonToObject(null));
        Assertions.assertNull(SaManager.getSaJsonTemplate().jsonToMap(null));
    }

}
