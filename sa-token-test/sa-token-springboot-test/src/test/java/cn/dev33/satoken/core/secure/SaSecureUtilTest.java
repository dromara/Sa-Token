/*
 * Copyright 2020-2099 sa-token.cc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cn.dev33.satoken.core.secure;

import java.util.HashMap;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import cn.dev33.satoken.secure.SaSecureUtil;

/**
 * SaSecureUtil 加密工具类 测试 
 * 
 * @author click33
 * @since 2022-2-9
 */
public class SaSecureUtilTest {
	
    @Test
    public void test() {
    	
    	// md5加密 
    	Assertions.assertEquals(SaSecureUtil.md5("123456"), "e10adc3949ba59abbe56e057f20f883e");

    	// sha1加密 
    	Assertions.assertEquals(SaSecureUtil.sha1("123456"), "7c4a8d09ca3762af61e59520943dc26494f8941b");

    	// sha256加密 
    	Assertions.assertEquals(SaSecureUtil.sha256("123456"), "8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92");

    	// md5加盐加密: md5(md5(str) + md5(salt)) 
    	Assertions.assertEquals(SaSecureUtil.md5BySalt("123456", "salt"), "f52020dca765fd3943ed40a615dc2c5c");
    	
    }

    @Test
    public void aesEncrypt() {
    	// 定义秘钥和明文
    	String key = "123456";
    	String text = "Sa-Token 一个轻量级java权限认证框架";

    	// 加密 
    	String ciphertext = SaSecureUtil.aesEncrypt(key, text);
    	Assertions.assertEquals(ciphertext, "KmSqfwxY5BRuWoHMWJqtebcOZ2lEEZaj2OSi1Ei8pRx4zdi24wsnwsTQVjbXRQ0M");

    	// 解密 
    	String text2 = SaSecureUtil.aesDecrypt(key, ciphertext);
    	Assertions.assertEquals(text2, "Sa-Token 一个轻量级java权限认证框架");
    }

    @Test
    public void rsaEncryptByPublic() {
    	// 定义私钥和公钥 
    	String privateKey = "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAO+wmt01pwm9lHMdq7A8gkEigk0XKMfjv+4IjAFhWCSiTeP7dtlnceFJbkWxvbc7Qo3fCOpwmfcskwUc3VSgyiJkNJDs9ivPbvlt8IU2bZ+PBDxYxSCJFrgouVOpAr8ar/b6gNuYTi1vt3FkGtSjACFb002/68RKUTye8/tdcVilAgMBAAECgYA1COmrSqTUJeuD8Su9ChZ0HROhxR8T45PjMmbwIz7ilDsR1+E7R4VOKPZKW4Kz2VvnklMhtJqMs4MwXWunvxAaUFzQTTg2Fu/WU8Y9ha14OaWZABfChMZlpkmpJW9arKmI22ZuxCEsFGxghTiJQ3tK8npj5IZq5vk+6mFHQ6aJAQJBAPghz91Dpuj+0bOUfOUmzi22obWCBncAD/0CqCLnJlpfOoa9bOcXSusGuSPuKy5KiGyblHMgKI6bq7gcM2DWrGUCQQD3SkOcmia2s/6i7DUEzMKaB0bkkX4Ela/xrfV+A3GzTPv9bIBamu0VIHznuiZbeNeyw7sVo4/GTItq/zn2QJdBAkEA8xHsVoyXTVeShaDIWJKTFyT5dJ1TR++/udKIcuiNIap34tZdgGPI+EM1yoTduBM7YWlnGwA9urW0mj7F9e9WIQJAFjxqSfmeg40512KP/ed/lCQVXtYqU7U2BfBTg8pBfhLtEcOg4wTNTroGITwe2NjL5HovJ2n2sqkNXEio6Ji0QQJAFLW1Kt80qypMqot+mHhS+0KfdOpaKeMWMSR4Ij5VfE63WzETEeWAMQESxzhavN1WOTb3/p6icgcVbgPQBaWhGg==";
    	String publicKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDvsJrdNacJvZRzHauwPIJBIoJNFyjH47/uCIwBYVgkok3j+3bZZ3HhSW5Fsb23O0KN3wjqcJn3LJMFHN1UoMoiZDSQ7PYrz275bfCFNm2fjwQ8WMUgiRa4KLlTqQK/Gq/2+oDbmE4tb7dxZBrUowAhW9NNv+vESlE8nvP7XXFYpQIDAQAB";

    	// 文本
    	String text = "Sa-Token 一个轻量级java权限认证框架";

    	// 使用公钥加密
    	String ciphertext = SaSecureUtil.rsaEncryptByPublic(publicKey, text);
//    	Assert.assertEquals(ciphertext, "d9e01fd105b059e975c524a1f4dccbe10dfc3a23b931a9e168ecb0a5758a29c45532254679f86cf83a63e5cc21ef631802fe70ea47e7519f5d96e0d1fab38a6f6dbebdb34b106ce7f27c341838e4e88a8ff3298c519c29a3f0944cf8f668bfecd9394f16945d85d84c4d813d12ecadf34bfb21850c383977b5b2de848fa40995");

    	// 使用私钥解密
    	String text2 = SaSecureUtil.rsaDecryptByPrivate(privateKey, ciphertext);
    	Assertions.assertEquals(text2, "Sa-Token 一个轻量级java权限认证框架");
    }

    @Test
    public void rsaEncryptByPrivate() throws Exception {
    	
    	// 生成私钥和公钥 
    	HashMap<String, String> map = SaSecureUtil.rsaGenerateKeyPair();
    	String privateKey = map.get("private"); 
    	String publicKey = map.get("public");

    	// 文本
    	String text = "Sa-Token 一个轻量级java权限认证框架";

    	// 使用公钥加密
    	String ciphertext = SaSecureUtil.rsaEncryptByPrivate(privateKey, text);
    	
    	// 使用私钥解密
    	String text2 = SaSecureUtil.rsaDecryptByPublic(publicKey, ciphertext);
    	Assertions.assertEquals(text2, "Sa-Token 一个轻量级java权限认证框架");
    }

}
