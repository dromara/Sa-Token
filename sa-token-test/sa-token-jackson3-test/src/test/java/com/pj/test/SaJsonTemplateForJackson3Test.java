package com.pj.test;

import cn.dev33.satoken.SaManager;
import cn.dev33.satoken.json.SaJsonTemplateForJackson3;
import com.pj.test.model.SysUser;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * Sa-Token-jackson3 序列化模块测试
 *
 * <pre>
 * 为什么单独写一个模块来测试 Jackson 3 ？
 *
 * 在同一个项目里同时引入 jackson 2 和 jackson 3 后，
 * 执行：
 *      SysUser user3 = (SysUser)SaManager.getSaJsonTemplate().jsonToObject(objectJson);
 * 会报错：
 *      java.lang.NoSuchFieldError: POJO
 * 	        at tools.jackson.databind.deser.DeserializerCache._createDeserializer2(DeserializerCache.java:399)
 * 	        at tools.jackson.databind.deser.DeserializerCache._createDeserializer(DeserializerCache.java:361)
 * 	        at tools.jackson.databind.deser.DeserializerCache._createAndCache2(DeserializerCache.java:265)
 * 	        at tools.jackson.databind.deser.DeserializerCache._createAndCacheValueDeserializer(DeserializerCache.java:244)
 * 	        at tools.jackson.databind.deser.DeserializerCache.findValueDeserializer(DeserializerCache.java:158)
 * 	        at tools.jackson.databind.DeserializationContext.findNonContextualValueDeserializer(DeserializationContext.java:733)
 * 	        at tools.jackson.databind.deser.jdk.UntypedObjectDeserializer._findCustomDeser(UntypedObjectDeserializer.java:179)
 * 	        at tools.jackson.databind.deser.jdk.UntypedObjectDeserializer.resolve(UntypedObjectDeserializer.java:152)
 *
 * 暂未找到解决方案，所以只能单独写一个测试类来测试 Jackson 3 的功能了。
 *
 * </pre>
 * @author click33 
 *
 */
public class SaJsonTemplateForJackson3Test {

	// 开始 
	@BeforeAll
    public static void beforeClass() {
    	System.out.println("\n\n------------------------ SaJsonTemplateForJackson3 Test star ...");
    }

	// 结束 
    @AfterAll
    public static void afterClass() {
    	System.out.println("\n\n------------------------ SaJsonTemplateForJackson3 Test end ... \n");
    }

    // 测试：Jackson3
    @Test
    public void testJackson3() {
        SaManager.setSaJsonTemplate(new SaJsonTemplateForJackson3());
        Assertions.assertEquals(SaJsonTemplateForJackson3.class, SaManager.getSaJsonTemplate().getClass());

        // test   Object -> Json
        SysUser user = new SysUser(10001, "张三", 18);
        String objectJson = SaManager.getSaJsonTemplate().objectToJson(user);
        // 与 json2 不同点：Jackson 3 默认按字母序排列属性
        Assertions.assertEquals("{\"@class\":\"com.pj.test.model.SysUser\",\"age\":18,\"id\":10001,\"name\":\"张三\",\"role\":null}", objectJson);

        // test   Json -> Object
        SysUser user2 = SaManager.getSaJsonTemplate().jsonToObject(objectJson, SysUser.class);
        Assertions.assertEquals(user2.toString(), user.toString());

        SysUser user3 = (SysUser)SaManager.getSaJsonTemplate().jsonToObject(objectJson);
        Assertions.assertEquals(user3.toString(), user.toString());

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
