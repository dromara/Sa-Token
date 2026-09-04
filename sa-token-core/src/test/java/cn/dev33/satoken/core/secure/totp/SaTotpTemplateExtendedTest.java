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

import cn.dev33.satoken.secure.totp.SaTotpTemplate;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * SaTotpTemplate 剩余方法覆盖
 *
 * @author click33
 * @since 1.46.0
 */
public class SaTotpTemplateExtendedTest {

	/** 自定义 timeStep/codeDigits/hmacAlgorithm 的构造函数应生效并生成对应长度口令 */
	@Test
	void customConstructorParameters() {
		SaTotpTemplate template = new SaTotpTemplate(60, 8, "HmacSHA256", 32);
		Assertions.assertEquals(60, template.timeStep);
		Assertions.assertEquals(8, template.codeDigits);
		Assertions.assertEquals("HmacSHA256", template.hmacAlgorithm);
		Assertions.assertEquals(32, template.secretKeyLength);

		String secretKey = template.generateSecretKey();
		String code = template._generateTOTP(secretKey);
		Assertions.assertEquals(8, code.length());
		Assertions.assertTrue(template.validateTOTP(secretKey, code, 0));
	}

	/** _generateTOTP 单参数重载应使用当前时间生成 6 位口令 */
	@Test
	void generateTOTP_oneArgUsesCurrentTime() {
		SaTotpTemplate template = new SaTotpTemplate();
		String secretKey = template.generateSecretKey();
		String code = template._generateTOTP(secretKey);
		Assertions.assertEquals(6, code.length());
		Assertions.assertTrue(template.validateTOTP(secretKey, code, 0));
	}

	/** validateTOTP 在 window=1 时应接受相邻时间窗口内的口令 */
	@Test
	void validateTOTP_acceptsAdjacentWindow() {
		SaTotpTemplate template = new SaTotpTemplate();
		String secretKey = template.generateSecretKey();
		String code = template._generateTOTP(secretKey);
		Assertions.assertTrue(template.validateTOTP(secretKey, code, 1));
	}

	/** generateGoogleSecretKey 单参数重载应自动生成密钥并嵌入 URL */
	@Test
	void generateGoogleSecretKey_autoSecret() {
		SaTotpTemplate template = new SaTotpTemplate();
		String url = template.generateGoogleSecretKey("auto-user");
		Assertions.assertTrue(url.startsWith("otpauth://totp/auto-user?secret="));
		Assertions.assertTrue(url.length() > "otpauth://totp/auto-user?secret=".length());
	}

}
