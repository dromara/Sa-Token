package cn.dev33.satoken.core.util;

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

    @Test
    public void test() {
    	SaResult res = new SaResult(200, "ok", "zhangsan");
    	Assertions.assertEquals((int)res.getCode(), 200);
    	Assertions.assertEquals(res.getMsg(), "ok");
    	Assertions.assertEquals(res.getData(), "zhangsan");
    	
    	res.set("age", 18);
    	Assertions.assertEquals(res.get("age"), 18);
    	Assertions.assertEquals(res.getOrDefault("age", 20), 18);
    	Assertions.assertEquals(res.getOrDefault("age2", 20), 20);
    }

    @Test
    public void test2() {
    	Assertions.assertEquals((int)SaResult.ok().getCode(), 200);
    	Assertions.assertEquals((int)SaResult.error().getCode(), 500);
    }
	
}
