package cn.dev33.satoken.core.context.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import cn.dev33.satoken.context.SaTokenContextDefaultImpl;
import cn.dev33.satoken.exception.SaTokenException;

/**
 * 默认上下文测试 
 * 
 * @author kong
 * @since: 2022-9-5
 */
public class SaTokenContextDefaultImplTest {

	@Test
	public void testSaTokenContextDefaultImpl() {
		SaTokenContextDefaultImpl context = new SaTokenContextDefaultImpl();
		Assertions.assertThrows(SaTokenException.class, () -> context.getStorage());
		Assertions.assertThrows(SaTokenException.class, () -> context.getRequest());
		Assertions.assertThrows(SaTokenException.class, () -> context.getResponse());
	}
	
}
