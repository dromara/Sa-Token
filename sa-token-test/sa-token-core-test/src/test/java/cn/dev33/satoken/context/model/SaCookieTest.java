package cn.dev33.satoken.context.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * SaFoxUtil 工具类测试 
 * 
 * @author kong
 * @since: 2022-2-8 22:14:25
 */
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
    			
    	Assertions.assertEquals(cookie.toHeaderValue(), "satoken=xxxx-xxxx-xxxx-xxxx; Domain=https://sa-token.dev33.cn/; Path=/; Secure; HttpOnly; sameSite=Lax");
    }

}
