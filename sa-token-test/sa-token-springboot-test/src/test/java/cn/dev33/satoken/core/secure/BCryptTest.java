package cn.dev33.satoken.core.secure;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import cn.dev33.satoken.secure.BCrypt;

/**
 * BCrypt 加密测试
 * 
 * @author dream.
 * @since 2022/1/20
 */
public class BCryptTest {

	@Test
	public void testCheckpw() {
		final String hashed = BCrypt.hashpw("12345");
//		System.out.println(hashed);
		Assertions.assertTrue(BCrypt.checkpw("12345", hashed));
		Assertions.assertFalse(BCrypt.checkpw("123456", hashed));
	}
	
}