package cn.dev33.satoken.secure;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * BCrypt 加密测试
 * 
 * @author dream.
 * @date 2022/1/20
 */
@RunWith(SpringRunner.class)
public class BCryptTest {

	@Test
	public void checkpwTest() {
		final String hashed = BCrypt.hashpw("12345");
//		System.out.println(hashed);
		Assert.assertTrue(BCrypt.checkpw("12345", hashed));
		Assert.assertFalse(BCrypt.checkpw("123456", hashed));
	}
	
}