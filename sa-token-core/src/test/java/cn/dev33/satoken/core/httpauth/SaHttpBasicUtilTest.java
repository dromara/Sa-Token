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
package cn.dev33.satoken.core.httpauth;

import cn.dev33.satoken.context.SaHolder;
import cn.dev33.satoken.context.mock.SaRequestForMock;
import cn.dev33.satoken.context.mock.SaTokenContextMockUtil;
import cn.dev33.satoken.exception.NotHttpBasicAuthException;
import cn.dev33.satoken.httpauth.basic.SaHttpBasicAccount;
import cn.dev33.satoken.httpauth.basic.SaHttpBasicUtil;
import cn.dev33.satoken.secure.SaBase64Util;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * SaHttpBasicUtil Http Basic 认证门面测试
 *
 * @author click33
 * @since 1.46.0
 */
public class SaHttpBasicUtilTest {

	/** getAuthorizationValue / getHttpBasicAccount 应正确委托 */
	@Test
	void getAuthorizationValue_andAccount() {
		SaTokenContextMockUtil.setMockContext(() -> {
			SaRequestForMock req = (SaRequestForMock) SaHolder.getRequest();
			req.headerMap.put("Authorization", "Basic " + SaBase64Util.encode("admin:secret"));

			Assertions.assertEquals("admin:secret", SaHttpBasicUtil.getAuthorizationValue());
			SaHttpBasicAccount account = SaHttpBasicUtil.getHttpBasicAccount();
			Assertions.assertEquals("admin", account.getUsername());
			Assertions.assertEquals("secret", account.getPassword());
		});
	}

	/** check 各重载应正确委托 */
	@Test
	void check_delegatesToTemplate() {
		SaTokenContextMockUtil.setMockContext(() -> {
			SaRequestForMock req = (SaRequestForMock) SaHolder.getRequest();
			req.headerMap.put("Authorization", "Basic " + SaBase64Util.encode("sa:123456"));

			Assertions.assertDoesNotThrow(() -> SaHttpBasicUtil.check("sa:123456"));
			Assertions.assertDoesNotThrow(() -> SaHttpBasicUtil.check("Realm", "sa:123456"));

			req.headerMap.put("Authorization", "Basic " + SaBase64Util.encode("sa:wrong"));
			Assertions.assertThrows(NotHttpBasicAuthException.class, () -> SaHttpBasicUtil.check("sa:123456"));
		});
	}

}
