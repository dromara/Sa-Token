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

import cn.dev33.satoken.secure.SaSecureUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.HashMap;

/**
 * SaSecureUtil 扩展测试
 */
public class SaSecureUtilExtendedTest {

	/** sha384 与 sha512 应返回固定长度的十六进制摘要 */
	@Test
	void sha384AndSha512() {
		Assertions.assertEquals(96, SaSecureUtil.sha384("123456").length());
		Assertions.assertEquals(128, SaSecureUtil.sha512("123456").length());
	}

	/** sha256BySalt 应返回 64 位十六进制加盐哈希 */
	@Test
	void sha256BySalt() {
		String hash = SaSecureUtil.sha256BySalt("abc", "salt");
		Assertions.assertEquals(64, hash.length());
	}

	/** RSA 公钥加密/私钥解密与私钥加密/公钥解密应互逆 */
	@Test
	void rsaEncryptDecryptByPrivateAndPublic() throws Exception {
		HashMap<String, String> keys = SaSecureUtil.rsaGenerateKeyPair();
		String privateKey = keys.get("private");
		String publicKey = keys.get("public");
		String text = "hello-sa-token";

		String cipherByPrivate = SaSecureUtil.rsaEncryptByPrivate(privateKey, text);
		Assertions.assertEquals(text, SaSecureUtil.rsaDecryptByPublic(publicKey, cipherByPrivate));

		String cipherByPublic = SaSecureUtil.rsaEncryptByPublic(publicKey, text);
		Assertions.assertEquals(text, SaSecureUtil.rsaDecryptByPrivate(privateKey, cipherByPublic));
	}

}
