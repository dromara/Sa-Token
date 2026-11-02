/*
 * Copyright 2020-2099 sa-token.com
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
import cn.dev33.satoken.exception.SaTokenException;
import cn.dev33.satoken.secure.SaSecureUtil;
/**
 * SaSecureUtil 加密工具类 测试 
 * 
 * @author click33
 * @since 2022-2-9
 */
public class SaSecureUtilTest {
	
	/** md5/sha1/sha256 及 md5BySalt 应返回预期摘要值 */
    @Test
    public void digestHashes() {
    	Assertions.assertEquals("e10adc3949ba59abbe56e057f20f883e", SaSecureUtil.md5("123456"));
    	Assertions.assertEquals("7c4a8d09ca3762af61e59520943dc26494f8941b", SaSecureUtil.sha1("123456"));
    	Assertions.assertEquals("8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92", SaSecureUtil.sha256("123456"));
    	Assertions.assertEquals("f52020dca765fd3943ed40a615dc2c5c", SaSecureUtil.md5BySalt("123456", "salt"));
    }
	/** AES 加密解密应可往返还原明文 */
    @Test
    public void aesEncrypt() {
    	// 定义秘钥和明文
    	String key = "123456";
    	String text = "Sa-Token 一个轻量级java权限认证框架";
    	// 加密 
    	String ciphertext = SaSecureUtil.aesEncrypt(key, text);
    	Assertions.assertEquals("KmSqfwxY5BRuWoHMWJqtebcOZ2lEEZaj2OSi1Ei8pRx4zdi24wsnwsTQVjbXRQ0M", ciphertext);
    	String text2 = SaSecureUtil.aesDecrypt(key, ciphertext);
    	Assertions.assertEquals(text, text2);
    }
	/** RSA 公钥加密、私钥解密应可往返还原明文 */
    @Test
    public void rsaEncryptByPublic() {
    	// 定义私钥和公钥 
    	String privateKey = "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAO+wmt01pwm9lHMdq7A8gkEigk0XKMfjv+4IjAFhWCSiTeP7dtlnceFJbkWxvbc7Qo3fCOpwmfcskwUc3VSgyiJkNJDs9ivPbvlt8IU2bZ+PBDxYxSCJFrgouVOpAr8ar/b6gNuYTi1vt3FkGtSjACFb002/68RKUTye8/tdcVilAgMBAAECgYA1COmrSqTUJeuD8Su9ChZ0HROhxR8T45PjMmbwIz7ilDsR1+E7R4VOKPZKW4Kz2VvnklMhtJqMs4MwXWunvxAaUFzQTTg2Fu/WU8Y9ha14OaWZABfChMZlpkmpJW9arKmI22ZuxCEsFGxghTiJQ3tK8npj5IZq5vk+6mFHQ6aJAQJBAPghz91Dpuj+0bOUfOUmzi22obWCBncAD/0CqCLnJlpfOoa9bOcXSusGuSPuKy5KiGyblHMgKI6bq7gcM2DWrGUCQQD3SkOcmia2s/6i7DUEzMKaB0bkkX4Ela/xrfV+A3GzTPv9bIBamu0VIHznuiZbeNeyw7sVo4/GTItq/zn2QJdBAkEA8xHsVoyXTVeShaDIWJKTFyT5dJ1TR++/udKIcuiNIap34tZdgGPI+EM1yoTduBM7YWlnGwA9urW0mj7F9e9WIQJAFjxqSfmeg40512KP/ed/lCQVXtYqU7U2BfBTg8pBfhLtEcOg4wTNTroGITwe2NjL5HovJ2n2sqkNXEio6Ji0QQJAFLW1Kt80qypMqot+mHhS+0KfdOpaKeMWMSR4Ij5VfE63WzETEeWAMQESxzhavN1WOTb3/p6icgcVbgPQBaWhGg==";
    	String publicKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDvsJrdNacJvZRzHauwPIJBIoJNFyjH47/uCIwBYVgkok3j+3bZZ3HhSW5Fsb23O0KN3wjqcJn3LJMFHN1UoMoiZDSQ7PYrz275bfCFNm2fjwQ8WMUgiRa4KLlTqQK/Gq/2+oDbmE4tb7dxZBrUowAhW9NNv+vESlE8nvP7XXFYpQIDAQAB";
    	// 文本
    	String text = "Sa-Token 一个轻量级java权限认证框架";
    	// 使用公钥加密
    	String ciphertext = SaSecureUtil.rsaEncryptByPublic(publicKey, text);
    	// 使用私钥解密
    	String text2 = SaSecureUtil.rsaDecryptByPrivate(privateKey, ciphertext);
    	Assertions.assertEquals(text, text2);
    }
	/** RSA 私钥加密、公钥解密应可往返还原明文 */
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
    	Assertions.assertEquals(text, text2);
    }

	/** sha384 与 sha512 应对上固定明文的黄金摘要，不能只看长度 */
	@Test
	void sha384AndSha512() {
		Assertions.assertEquals("0a989ebc4a77b56a6e2bb7b19d995d185ce44090c13e2984b7ecc6d446d4b61ea9991b76a4c2f04b1b4d244841449454",
				SaSecureUtil.sha384("123456"));
		Assertions.assertEquals("ba3253876aed6bc22d4a6ff53d8406c6ad864195ed144ab5c87621b6c233b548baeae6956df346ec8c17f5ea10f35ee3cbc514797ed7ddd3145464e2a0bab413",
				SaSecureUtil.sha512("123456"));
	}

	/** sha256BySalt 应对上 sha256(sha256(str)+sha256(salt)) 的黄金值 */
	@Test
	void sha256BySalt() {
		Assertions.assertEquals("a3fed8029c5354306e1238a3ce4b4e7e5bef05c9a90ef3bd240954bfa26c09f6",
				SaSecureUtil.sha256BySalt("abc", "salt"));
	}

	/** 摘要方法在 null 入参时应等同空字符串哈希 */
	@Test
	void digestHashes_nullInput_treatedAsEmpty() {
		String emptyMd5 = SaSecureUtil.md5("");
		Assertions.assertEquals(emptyMd5, SaSecureUtil.md5(null));
		Assertions.assertEquals(SaSecureUtil.sha1(""), SaSecureUtil.sha1(null));
		Assertions.assertEquals(SaSecureUtil.sha256(""), SaSecureUtil.sha256(null));
		Assertions.assertEquals(SaSecureUtil.sha384(""), SaSecureUtil.sha384(null));
		Assertions.assertEquals(SaSecureUtil.sha512(""), SaSecureUtil.sha512(null));
	}

	/** AES 解密非法密文应抛出 SaTokenException */
	@Test
	void aesDecrypt_invalidCipher_throws() {
		Assertions.assertThrows(SaTokenException.class,
				() -> SaSecureUtil.aesDecrypt("key", "not-valid-base64-cipher!!!"));
	}

	/** 含换行符的 RSA 密钥仍应正常加解密 */
	@Test
	void rsaKeysWithLineBreaks_stillWork() throws Exception {
		HashMap<String, String> keys = SaSecureUtil.rsaGenerateKeyPair();
		String privateKey = keys.get("private").replace("\n", "\r\n");
		String publicKey = keys.get("public").replace("\n", "\r\n");
		String text = "line-break-key-test";

		String cipher = SaSecureUtil.rsaEncryptByPublic(publicKey, text);
		Assertions.assertEquals(text, SaSecureUtil.rsaDecryptByPrivate(privateKey, cipher));
	}

	/** RSA 公钥加密时传入非法密钥应抛出 SaTokenException */
	@Test
	void rsaEncryptByPublic_invalidKey_throws() {
		Assertions.assertThrows(SaTokenException.class,
				() -> SaSecureUtil.rsaEncryptByPublic("invalid-key", "data"));
	}

	/** RSA 私钥加密时传入非法密钥应抛出 SaTokenException */
	@Test
	void rsaEncryptByPrivate_invalidKey_throws() {
		Assertions.assertThrows(SaTokenException.class,
				() -> SaSecureUtil.rsaEncryptByPrivate("invalid-key", "data"));
	}

	/** RSA 公钥解密时传入非法密钥应抛出 SaTokenException */
	@Test
	void rsaDecryptByPublic_invalidKey_throws() {
		Assertions.assertThrows(SaTokenException.class,
				() -> SaSecureUtil.rsaDecryptByPublic("invalid-key", "001122"));
	}

	/** RSA 私钥解密时传入非法密钥应抛出 SaTokenException */
	@Test
	void rsaDecryptByPrivate_invalidKey_throws() {
		Assertions.assertThrows(SaTokenException.class,
				() -> SaSecureUtil.rsaDecryptByPrivate("invalid-key", "001122"));
	}
}
