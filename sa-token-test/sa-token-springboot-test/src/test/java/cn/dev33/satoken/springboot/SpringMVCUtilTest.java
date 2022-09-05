package cn.dev33.satoken.springboot;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import cn.dev33.satoken.exception.SaTokenException;
import cn.dev33.satoken.spring.SpringMVCUtil;

/**
 * SpringMVCUtil 测试 
 * 
 * @author kong  
 *
 */
public class SpringMVCUtilTest {

	// 开始 
	@BeforeAll
    public static void beforeClass() {
    	
    }

	// 结束 
    @AfterAll
    public static void afterClass() {
    	
    }

    // 测试，上下文 API 
    @Test
    public void testSaTokenContext() {
    	Assertions.assertThrows(SaTokenException.class, () -> SpringMVCUtil.getRequest());
    	Assertions.assertThrows(SaTokenException.class, () -> SpringMVCUtil.getResponse());
    	Assertions.assertFalse(SpringMVCUtil.isWeb());
    }

}
