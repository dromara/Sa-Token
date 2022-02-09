package cn.dev33.satoken.fun;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * IsRunFunction 测试 
 * 
 * @author kong
 * @date: 2022-2-9 16:11:10
 */
@RunWith(SpringRunner.class)
public class IsRunFunctionTest {

    @Test
    public void test() {
    	
    	class TempClass{
    		int count = 1;
    	}
    	TempClass obj = new TempClass();

    	IsRunFunction fun = new IsRunFunction(true);
    	fun.exe(()->{
    		obj.count = 2;
    	}).noExe(()->{
    		obj.count = 3;
    	});
    	
    	Assert.assertEquals(obj.count, 2);
    }

}
