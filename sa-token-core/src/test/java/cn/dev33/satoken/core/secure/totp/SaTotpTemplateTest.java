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
package cn.dev33.satoken.core.secure.totp;

import cn.dev33.satoken.exception.TotpAuthException;
import cn.dev33.satoken.secure.totp.SaTotpTemplate;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * SaTotpTemplate TOTP 动态口令测试
 */
public class SaTotpTemplateTest {

	private final SaTotpTemplate template = new SaTotpTemplate();

	/** generateSecretKey 应生成不含等号且长度足够的 Base32 密钥 */
	@Test
	void generateSecretKey() {
		String secretKey = template.generateSecretKey();
		Assertions.assertNotNull(secretKey);
		Assertions.assertFalse(secretKey.contains("="));
		Assertions.assertTrue(secretKey.length() >= 16);
	}

	/** _generateTOTP 应生成 6 位口令，validateTOTP 应校验正确与错误口令 */
	@Test
	void generateTOTP_and_validateTOTP() {
		String secretKey = template.generateSecretKey();
		String code = template._generateTOTP(secretKey);

		Assertions.assertNotNull(code);
		Assertions.assertEquals(6, code.length());
		Assertions.assertTrue(template.validateTOTP(secretKey, code, 0));
		Assertions.assertFalse(template.validateTOTP(secretKey, "000000", 0));
	}

	/** checkTOTP 在口令正确时不抛异常，错误时抛出 TotpAuthException */
	@Test
	void checkTOTP_throwsOnWrongCode() {
		String secretKey = template.generateSecretKey();
		Assertions.assertDoesNotThrow(() -> template.checkTOTP(secretKey, template._generateTOTP(secretKey), 0));
		Assertions.assertThrows(TotpAuthException.class, () -> template.checkTOTP(secretKey, "000000", 0));
	}

	/** generateGoogleSecretKey 应生成符合 otpauth 协议的 URL */
	@Test
	void generateGoogleSecretKey() {
		String secretKey = "JBSWY3DPEHPK3PXP";
		String url = template.generateGoogleSecretKey("zhangsan", secretKey);
		Assertions.assertEquals("otpauth://totp/zhangsan?secret=JBSWY3DPEHPK3PXP", url);

		String urlWithIssuer = template.generateGoogleSecretKey("zhangsan", "Sa-Token", secretKey);
		Assertions.assertEquals("otpauth://totp/Sa-Token:zhangsan?secret=JBSWY3DPEHPK3PXP&issuer=Sa-Token", urlWithIssuer);

		String autoUrl = template.generateGoogleSecretKey("lisi");
		Assertions.assertTrue(autoUrl.startsWith("otpauth://totp/lisi?secret="));
	}

}
