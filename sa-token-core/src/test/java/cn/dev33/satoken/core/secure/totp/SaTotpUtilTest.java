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
import cn.dev33.satoken.secure.totp.SaTotpUtil;
import cn.dev33.satoken.test.SaTokenTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * SaTotpUtil TOTP 门面测试
 *
 * @author click33
 * @since 1.46.0
 */
@SaTokenTest
public class SaTotpUtilTest {

	/** generateSecretKey / generateTOTP / validateTOTP 应正确委托 */
	@Test
	void generateAndValidate() {
		String secretKey = SaTotpUtil.generateSecretKey();
		Assertions.assertNotNull(secretKey);
		String code = SaTotpUtil.generateTOTP(secretKey);
		Assertions.assertEquals(6, code.length());
		Assertions.assertTrue(SaTotpUtil.validateTOTP(secretKey, code, 0));
		Assertions.assertFalse(SaTotpUtil.validateTOTP(secretKey, "000000", 0));
	}

	/** checkTOTP 在口令错误时应抛出 TotpAuthException */
	@Test
	void checkTOTP_throwsOnWrongCode() {
		String secretKey = SaTotpUtil.generateSecretKey();
		Assertions.assertDoesNotThrow(() -> SaTotpUtil.checkTOTP(secretKey, SaTotpUtil.generateTOTP(secretKey), 0));
		Assertions.assertThrows(TotpAuthException.class, () -> SaTotpUtil.checkTOTP(secretKey, "000000", 0));
	}

	/** generateGoogleSecretKey 各重载应返回 otpauth URL */
	@Test
	void generateGoogleSecretKey() {
		String secretKey = "JBSWY3DPEHPK3PXP";
		Assertions.assertEquals("otpauth://totp/zhangsan?secret=JBSWY3DPEHPK3PXP",
				SaTotpUtil.generateGoogleSecretKey("zhangsan", secretKey));
		Assertions.assertEquals("otpauth://totp/Sa-Token:zhangsan?secret=JBSWY3DPEHPK3PXP&issuer=Sa-Token",
				SaTotpUtil.generateGoogleSecretKey("zhangsan", "Sa-Token", secretKey));
		String autoUrl = SaTotpUtil.generateGoogleSecretKey("lisi");
		Assertions.assertTrue(autoUrl.startsWith("otpauth://totp/lisi?secret="));
	}

}
