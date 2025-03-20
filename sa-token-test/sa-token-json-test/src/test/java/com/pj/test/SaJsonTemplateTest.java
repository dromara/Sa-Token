package com.pj.test;

import cn.dev33.satoken.SaManager;
import cn.dev33.satoken.exception.NotImplException;
import cn.dev33.satoken.json.*;
import com.pj.test.model.SysUser;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

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
        SaManager.setSaJsonTemplate(new SaJsonTemplateForJackson());
        Assertions.assertEquals(SaManager.getSaJsonTemplate().getClass(), SaJsonTemplateForJackson.class);

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
