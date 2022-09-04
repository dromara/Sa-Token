package cn.dev33.satoken.core.session;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import cn.dev33.satoken.session.TokenSign;

/**
 * TokenSign 相关测试 
 * 
 * @author kong
 * @since: 2022-9-4
 */
public class TokenSignTest {

	// 测试 
	@Test
	public void testTokenSign() {
		TokenSign tokenSign = new TokenSign();
		tokenSign.setDevice("PC");
		tokenSign.setValue("ttt-value");
		
		Assertions.assertEquals(tokenSign.getDevice(), "PC");
		Assertions.assertEquals(tokenSign.getValue(), "ttt-value");

		Assertions.assertNotNull(tokenSign.toString());
	}
	
}
