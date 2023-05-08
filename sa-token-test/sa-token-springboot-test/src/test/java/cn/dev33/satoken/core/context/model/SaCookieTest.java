package cn.dev33.satoken.core.context.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import cn.dev33.satoken.context.model.SaCookie;

/**
 * SaFoxUtil 工具类测试 
 * 
 * @author click33
 * @since: 2022-2-8 22:14:25
 */
public class SaCookieTest {

    @Test
    public void test() {
    	SaCookie cookie = new SaCookie("satoken", "xxxx-xxxx-xxxx-xxxx")
    			.setDomain("https://sa-token.cc/")
    			.setMaxAge(-1)
    			.setPath("/")
    			.setSameSite("Lax")
    			.setHttpOnly(true)
    			.setSecure(true);

    	Assertions.assertEquals(cookie.getName(), "satoken");
    	Assertions.assertEquals(cookie.getValue(), "xxxx-xxxx-xxxx-xxxx");
    	Assertions.assertEquals(cookie.getDomain(), "https://sa-token.cc/");
    	Assertions.assertEquals(cookie.getMaxAge(), -1);
    	Assertions.assertEquals(cookie.getPath(), "/");
    	Assertions.assertEquals(cookie.getSameSite(), "Lax");
    	Assertions.assertEquals(cookie.getHttpOnly(), true);
    	Assertions.assertEquals(cookie.getSecure(), true);
    	Assertions.assertEquals(cookie.toHeaderValue(), "satoken=xxxx-xxxx-xxxx-xxxx; Domain=https://sa-token.cc/; Path=/; Secure; HttpOnly; SameSite=Lax");
    	
    	Assertions.assertNotNull(cookie.toString());
    }

}
