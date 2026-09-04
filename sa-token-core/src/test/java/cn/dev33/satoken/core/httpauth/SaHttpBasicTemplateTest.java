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
import cn.dev33.satoken.context.mock.SaResponseForMock;
import cn.dev33.satoken.context.mock.SaTokenContextMockUtil;
import cn.dev33.satoken.exception.NotHttpBasicAuthException;
import cn.dev33.satoken.httpauth.basic.SaHttpBasicAccount;
import cn.dev33.satoken.httpauth.basic.SaHttpBasicTemplate;
import cn.dev33.satoken.secure.SaBase64Util;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * SaHttpBasicTemplate Http Basic 认证测试
 */
public class SaHttpBasicTemplateTest {

	private final SaHttpBasicTemplate template = new SaHttpBasicTemplate();

	/** 应从 Basic Authorization 头解析出账号凭证 */
	@Test
	void getAuthorizationValue_fromBasicHeader() {
		SaTokenContextMockUtil.setMockContext(() -> {
			SaRequestForMock req = (SaRequestForMock) SaHolder.getRequest();
			req.headerMap.put("Authorization", "Basic " + SaBase64Util.encode("sa:123456"));

			Assertions.assertEquals("sa:123456", template.getAuthorizationValue());
		});
	}

	/** 非 Basic 前缀的请求头应返回 null */
	@Test
	void getAuthorizationValue_returnsNullWhenInvalid() {
		SaTokenContextMockUtil.setMockContext(() -> {
			SaRequestForMock req = (SaRequestForMock) SaHolder.getRequest();
			req.headerMap.put("Authorization", "Bearer token");
			Assertions.assertNull(template.getAuthorizationValue());
		});
	}

	/** getHttpBasicAccount 应解析出 SaHttpBasicAccount 对象 */
	@Test
	void getHttpBasicAccount() {
		SaTokenContextMockUtil.setMockContext(() -> {
			SaRequestForMock req = (SaRequestForMock) SaHolder.getRequest();
			req.headerMap.put("Authorization", "Basic " + SaBase64Util.encode("admin:secret"));

			SaHttpBasicAccount account = template.getHttpBasicAccount();
			Assertions.assertNotNull(account);
			Assertions.assertEquals("admin", account.getUsername());
			Assertions.assertEquals("secret", account.getPassword());
		});
	}

	/** 凭证匹配时 check 应通过，不匹配应抛出 NotHttpBasicAuthException */
	@Test
	void check_successAndFail() {
		SaTokenContextMockUtil.setMockContext(() -> {
			SaRequestForMock req = (SaRequestForMock) SaHolder.getRequest();
			req.headerMap.put("Authorization", "Basic " + SaBase64Util.encode("sa:123456"));

			Assertions.assertDoesNotThrow(() -> template.check("sa:123456"));

			req.headerMap.put("Authorization", "Basic " + SaBase64Util.encode("sa:wrong"));
			Assertions.assertThrows(NotHttpBasicAuthException.class, () -> template.check("sa:123456"));
		});
	}

	/** throwNotBasicAuthException 应设置 401 并写入 WWW-Authenticate 头 */
	@Test
	void throwNotBasicAuthException() {
		SaTokenContextMockUtil.setMockContext(() -> {
			SaResponseForMock res = (SaResponseForMock) SaHolder.getResponse();
			Assertions.assertThrows(NotHttpBasicAuthException.class,
					() -> template.throwNotBasicAuthException("TestRealm"));
			Assertions.assertEquals(401, res.status);
			Assertions.assertEquals("Basic Realm=TestRealm", res.headerMap.get("WWW-Authenticate"));
		});
	}

}
