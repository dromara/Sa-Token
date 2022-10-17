package com.pj.cases.up;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cn.dev33.satoken.secure.SaBase64Util;
import cn.dev33.satoken.secure.SaSecureUtil;
import cn.dev33.satoken.util.SaResult;

/**
 * Sa-Token 密码加密示例
 * 
 * @author kong
 * @since 2022-10-17
 */
@RestController
@RequestMapping("/secure/")
public class SecureController {

	// 摘要加密   ---- http://localhost:8081/secure/digest
	@RequestMapping("digest")
	public SaResult digest() {
		// md5加密 
		System.out.println(SaSecureUtil.md5("123456"));

		// sha1加密 
		System.out.println(SaSecureUtil.sha1("123456"));

		// sha256加密 
		System.out.println(SaSecureUtil.sha256("123456"));

		return SaResult.ok();
	}

	// AES加密   ---- http://localhost:8081/secure/aes
	@RequestMapping("aes")
	public SaResult aes() {
		// 定义秘钥和明文
		String key = "123456";
		String text = "Sa-Token 一个轻量级java权限认证框架";

		// 加密 
		String ciphertext = SaSecureUtil.aesEncrypt(key, text);
		System.out.println("AES加密后：" + ciphertext);

		// 解密 
		String text2 = SaSecureUtil.aesDecrypt(key, ciphertext);
		System.out.println("AES解密后：" + text2);

		return SaResult.ok();
	}

	// RSA加密   ---- http://localhost:8081/secure/rsa
	@RequestMapping("rsa")
	public SaResult rsa() {
		// 定义私钥和公钥 
		String privateKey = "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAO+wmt01pwm9lHMdq7A8gkEigk0XKMfjv+4IjAFhWCSiTeP7dtlnceFJbkWxvbc7Qo3fCOpwmfcskwUc3VSgyiJkNJDs9ivPbvlt8IU2bZ+PBDxYxSCJFrgouVOpAr8ar/b6gNuYTi1vt3FkGtSjACFb002/68RKUTye8/tdcVilAgMBAAECgYA1COmrSqTUJeuD8Su9ChZ0HROhxR8T45PjMmbwIz7ilDsR1+E7R4VOKPZKW4Kz2VvnklMhtJqMs4MwXWunvxAaUFzQTTg2Fu/WU8Y9ha14OaWZABfChMZlpkmpJW9arKmI22ZuxCEsFGxghTiJQ3tK8npj5IZq5vk+6mFHQ6aJAQJBAPghz91Dpuj+0bOUfOUmzi22obWCBncAD/0CqCLnJlpfOoa9bOcXSusGuSPuKy5KiGyblHMgKI6bq7gcM2DWrGUCQQD3SkOcmia2s/6i7DUEzMKaB0bkkX4Ela/xrfV+A3GzTPv9bIBamu0VIHznuiZbeNeyw7sVo4/GTItq/zn2QJdBAkEA8xHsVoyXTVeShaDIWJKTFyT5dJ1TR++/udKIcuiNIap34tZdgGPI+EM1yoTduBM7YWlnGwA9urW0mj7F9e9WIQJAFjxqSfmeg40512KP/ed/lCQVXtYqU7U2BfBTg8pBfhLtEcOg4wTNTroGITwe2NjL5HovJ2n2sqkNXEio6Ji0QQJAFLW1Kt80qypMqot+mHhS+0KfdOpaKeMWMSR4Ij5VfE63WzETEeWAMQESxzhavN1WOTb3/p6icgcVbgPQBaWhGg==";
		String publicKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDvsJrdNacJvZRzHauwPIJBIoJNFyjH47/uCIwBYVgkok3j+3bZZ3HhSW5Fsb23O0KN3wjqcJn3LJMFHN1UoMoiZDSQ7PYrz275bfCFNm2fjwQ8WMUgiRa4KLlTqQK/Gq/2+oDbmE4tb7dxZBrUowAhW9NNv+vESlE8nvP7XXFYpQIDAQAB";

		// 文本
		String text = "Sa-Token 一个轻量级java权限认证框架";

		// 使用公钥加密
		String ciphertext = SaSecureUtil.rsaEncryptByPublic(publicKey, text);
		System.out.println("公钥加密后：" + ciphertext);

		// 使用私钥解密
		String text2 = SaSecureUtil.rsaDecryptByPrivate(privateKey, ciphertext);
		System.out.println("私钥解密后：" + text2); 

		return SaResult.ok();
	}

	// Base64 编码   ---- http://localhost:8081/secure/base64
	@RequestMapping("base64")
	public SaResult base64() {
		// 文本
		String text = "Sa-Token 一个轻量级java权限认证框架";

		// 使用Base64编码
		String base64Text = SaBase64Util.encode(text);
		System.out.println("Base64编码后：" + base64Text);

		// 使用Base64解码
		String text2 = SaBase64Util.decode(base64Text);
		System.out.println("Base64解码后：" + text2); 

		return SaResult.ok();
	}
	
}
