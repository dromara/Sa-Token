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

import cn.dev33.satoken.exception.SaTokenException;
import cn.dev33.satoken.secure.SaSecureUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.HashMap;

/**
 * SaSecureUtil 全量路径补充测试
 */
public class SaSecureUtilFullTest {

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

	/** 已废弃的 md5BySalt/sha256BySalt 应返回固定长度哈希 */
	@Test
	void deprecatedSaltHashes() {
		Assertions.assertEquals(32, SaSecureUtil.md5BySalt("abc", "salt").length());
		Assertions.assertEquals(64, SaSecureUtil.sha256BySalt("abc", "salt").length());
	}

	/** AES 加密解密应可往返还原明文 */
	@Test
	void aesEncryptDecrypt_roundTrip() {
		String key = "test-key-12345";
		String plain = "sa-token-aes-test";
		String cipher = SaSecureUtil.aesEncrypt(key, plain);
		Assertions.assertEquals(plain, SaSecureUtil.aesDecrypt(key, cipher));
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
