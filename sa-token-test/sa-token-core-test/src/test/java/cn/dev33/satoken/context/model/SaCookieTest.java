package cn.dev33.satoken.context.model;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * SaFoxUtil 工具类测试 
 * 
 * @author kong
 * @date: 2022-2-8 22:14:25
 */
@RunWith(SpringRunner.class)
public class SaCookieTest {

    @Test
    public void test() {
    	SaCookie cookie = new SaCookie("satoken", "xxxx-xxxx-xxxx-xxxx")
    			.setDomain("https://sa-token.dev33.cn/")
    			.setMaxAge(-1)
    			.setPath("/")
    			.setSameSite("Lax")
    			.setHttpOnly(true)
    			.setSecure(true);
    			
    	Assert.assertEquals(cookie.toHeaderValue(), "satoken=xxxx-xxxx-xxxx-xxxx; Domain=https://sa-token.dev33.cn/; Path=/; Secure; HttpOnly; sameSite=Lax");
    }

}
