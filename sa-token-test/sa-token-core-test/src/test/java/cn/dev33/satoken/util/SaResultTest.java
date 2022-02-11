package cn.dev33.satoken.util;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * SaResult 结果集 测试 
 * 
 * @author kong
 * @date: 2022-2-8 22:14:25
 */
@RunWith(SpringRunner.class)
public class SaResultTest {

    @Test
    public void test() {
    	SaResult res = new SaResult(200, "ok", "zhangsan");
    	Assert.assertEquals((int)res.getCode(), 200);
    	Assert.assertEquals(res.getMsg(), "ok");
    	Assert.assertEquals(res.getData(), "zhangsan");
    	
    	res.set("age", 18);
    	Assert.assertEquals(res.get("age"), 18);
    	Assert.assertEquals(res.getOrDefault("age", 20), 18);
    	Assert.assertEquals(res.getOrDefault("age2", 20), 20);
    }

    @Test
    public void test2() {
    	Assert.assertEquals((int)SaResult.ok().getCode(), 200);
    	Assert.assertEquals((int)SaResult.error().getCode(), 500);
    }
	
}
