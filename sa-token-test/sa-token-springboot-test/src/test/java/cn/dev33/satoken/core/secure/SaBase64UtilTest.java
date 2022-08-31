package cn.dev33.satoken.core.secure;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import cn.dev33.satoken.secure.SaBase64Util;

/**
 * SaBase64Util 测试 
 * 
 * @author kong
 * @since: 2022-2-9
 */
public class SaBase64UtilTest {

    @Test
    public void test() {
    	// 文本
    	String text = "Sa-Token 一个轻量级java权限认证框架";

    	// 使用Base64编码
    	String base64Text = SaBase64Util.encode(text);
    	Assertions.assertEquals(base64Text, "U2EtVG9rZW4g5LiA5Liq6L276YeP57qnamF2Yeadg+mZkOiupOivgeahhuaetg==");

    	// 使用Base64解码
    	String text2 = SaBase64Util.decode(base64Text);
    	Assertions.assertEquals(text2, "Sa-Token 一个轻量级java权限认证框架");
    }

}
