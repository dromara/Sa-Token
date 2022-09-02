package cn.dev33.satoken.core.util;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import cn.dev33.satoken.util.SaResult;

/**
 * SaResult 结果集 测试 
 * 
 * @author kong
 * @since: 2022-2-8 22:14:25
 */
public class SaResultTest {

	// 构造函数构建 
    @Test
    public void test() {
    	// 无参构造时，默认所有参数为null 
    	SaResult res = new SaResult();
    	Assertions.assertEquals(res.getCode(), null);
    	Assertions.assertEquals(res.getMsg(), null);
    	Assertions.assertEquals(res.getData(), null);
    	
    	// 全参数构造 
    	SaResult res2 = new SaResult(200, "ok", "zhangsan");
    	Assertions.assertEquals((int)res2.getCode(), 200);
    	Assertions.assertEquals(res2.getMsg(), "ok");
    	Assertions.assertEquals(res2.getData(), "zhangsan");
    	
    	// 自定义写值取值 
    	res.set("age", 18);
    	Assertions.assertEquals(res.get("age"), 18);
    	Assertions.assertEquals(res.get("age", String.class), "18");
    	Assertions.assertEquals(res.getOrDefault("age", 20), 18);
    	Assertions.assertEquals(res.getOrDefault("age2", 20), 20);
    }

    // 静态函数快速构建 
    @Test
    public void test2() {
    	// ok 和 error
    	Assertions.assertEquals((int)SaResult.ok().getCode(), 200);
    	Assertions.assertEquals((int)SaResult.error().getCode(), 500);
    	Assertions.assertEquals(SaResult.error("错误").getMsg(), "错误");

    	// 指定code
    	SaResult res = SaResult.code(201);
    	Assertions.assertEquals((int)res.getCode(), 201);
    	
    	// 
    	// 全参数构造 
    	SaResult res2 = SaResult.get(200, "ok", "zhangsan");
    	Assertions.assertEquals((int)res2.getCode(), 200);
    	Assertions.assertEquals(res2.getMsg(), "ok");
    	Assertions.assertEquals(res2.getData(), "zhangsan");
    	// 序列化
    	Assertions.assertEquals(res2.toString(), "{\"code\": 200, \"msg\": \"ok\", \"data\": \"zhangsan\"}");
    	// data 为 int 时的序列化 
    	res2.setData(1);
    	Assertions.assertEquals(res2.toString(), "{\"code\": 200, \"msg\": \"ok\", \"data\": 1}");
    	
    	// Map 构造
    	Map<String, Object> map = new HashMap<>();
    	map.put("key1", "value1");
    	map.put("key2", "value2");
    	SaResult res4 = new SaResult(map);
    	Assertions.assertEquals(res4.get("key1"), "value1");
    	Assertions.assertEquals(res4.get("key2"), "value2");
    }
	
}
