package cn.dev33.satoken.secure;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * BCrypt 加密测试
 * 
 * @author dream.
 * @since 2022/1/20
 */
public class BCryptTest {

	@Test
	public void checkpwTest() {
		final String hashed = BCrypt.hashpw("12345");
//		System.out.println(hashed);
		Assertions.assertTrue(BCrypt.checkpw("12345", hashed));
		Assertions.assertFalse(BCrypt.checkpw("123456", hashed));
	}
	
}