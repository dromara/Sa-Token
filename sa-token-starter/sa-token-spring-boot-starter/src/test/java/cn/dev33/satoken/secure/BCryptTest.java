package cn.dev33.satoken.secure;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author dream.
 * @className BCryptTest
 * @description TODO 类描述
 * @date 2022/1/20
 **/
public class BCryptTest {

    @Test
    public void checkpwTest() {
        final String hashed = BCrypt.hashpw("12345");
        System.out.println(hashed);
        Assert.assertTrue(BCrypt.checkpw("12345", hashed));
        Assert.assertFalse(BCrypt.checkpw("123456", hashed));
    }
}