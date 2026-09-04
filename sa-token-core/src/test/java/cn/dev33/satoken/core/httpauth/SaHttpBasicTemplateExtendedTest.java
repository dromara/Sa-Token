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

import cn.dev33.satoken.SaManager;
import cn.dev33.satoken.annotation.SaCheckHttpBasic;
import cn.dev33.satoken.annotation.handler.SaCheckHttpBasicHandler;
import cn.dev33.satoken.context.SaHolder;
import cn.dev33.satoken.context.mock.SaRequestForMock;
import cn.dev33.satoken.context.mock.SaResponseForMock;
import cn.dev33.satoken.context.mock.SaTokenContextMockUtil;
import cn.dev33.satoken.exception.NotHttpBasicAuthException;
import cn.dev33.satoken.httpauth.basic.SaHttpBasicTemplate;
import cn.dev33.satoken.secure.SaBase64Util;
import cn.dev33.satoken.test.SaTokenTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * SaHttpBasicTemplate 扩展测试
 *
 * @author click33
 * @since 1.46.0
 */
@SaTokenTest
public class SaHttpBasicTemplateExtendedTest {

	private final SaHttpBasicTemplate template = new SaHttpBasicTemplate();

	/** 无参 check 应使用全局 httpBasic 配置校验 */
	@Test
	void check_noArg_usesGlobalConfig() {
		SaManager.getConfig().setHttpBasic("global:pwd");
		SaTokenContextMockUtil.setMockContext(() -> {
			SaRequestForMock req = (SaRequestForMock) SaHolder.getRequest();
			req.headerMap.put("Authorization", "Basic " + SaBase64Util.encode("global:pwd"));
			Assertions.assertDoesNotThrow(() -> template.check());
		});
	}

	/** 仅传 account 时应校验请求头与指定账号是否一致 */
	@Test
	void check_withAccount_only() {
		SaTokenContextMockUtil.setMockContext(() -> {
			SaRequestForMock req = (SaRequestForMock) SaHolder.getRequest();
			req.headerMap.put("Authorization", "Basic " + SaBase64Util.encode("user:pass"));
			Assertions.assertDoesNotThrow(() -> template.check("user:pass"));
			Assertions.assertThrows(NotHttpBasicAuthException.class, () -> template.check("other:pass"));
		});
	}

	/** 指定 realm 与 account 时校验失败应返回 401 */
	@Test
	void check_withRealmAndAccount() {
		SaTokenContextMockUtil.setMockContext(() -> {
			SaRequestForMock req = (SaRequestForMock) SaHolder.getRequest();
			req.headerMap.put("Authorization", "Basic " + SaBase64Util.encode("realm-user:secret"));
			Assertions.assertDoesNotThrow(() -> template.check("CustomRealm", "realm-user:secret"));
			Assertions.assertThrows(NotHttpBasicAuthException.class,
					() -> template.check("CustomRealm", "realm-user:wrong"));
			SaResponseForMock res = (SaResponseForMock) SaHolder.getResponse();
			Assertions.assertEquals(401, res.status);
		});
	}

	/** account 为空时应回退使用全局 httpBasic 配置 */
	@Test
	void check_emptyAccount_fallsBackToGlobalConfig() {
		SaManager.getConfig().setHttpBasic("fallback:123");
		SaTokenContextMockUtil.setMockContext(() -> {
			SaRequestForMock req = (SaRequestForMock) SaHolder.getRequest();
			req.headerMap.put("Authorization", "Basic " + SaBase64Util.encode("fallback:123"));
			Assertions.assertDoesNotThrow(() -> template.check(SaHttpBasicTemplate.DEFAULT_REALM, ""));
		});
	}

	/** 请求凭证与全局配置不一致时应抛出 NotHttpBasicAuthException */
	@Test
	void check_globalConfigMismatch_throws() {
		SaManager.getConfig().setHttpBasic("cfg-user:cfg-pass");
		SaTokenContextMockUtil.setMockContext(() -> {
			SaRequestForMock req = (SaRequestForMock) SaHolder.getRequest();
			req.headerMap.put("Authorization", "Basic " + SaBase64Util.encode("other:pass"));
			Assertions.assertThrows(NotHttpBasicAuthException.class, () -> template.check());
		});
	}

	/** 通过注解 handler 路径执行 Basic 校验应正确匹配账号 */
	@Test
	void checkByAnnotation_viaHandler() {
		SaTokenContextMockUtil.setMockContext(() -> {
			SaRequestForMock req = (SaRequestForMock) SaHolder.getRequest();
			req.headerMap.put("Authorization", "Basic " + SaBase64Util.encode("anno:pwd"));

			SaCheckHttpBasic annotation = new SaCheckHttpBasic() {
				@Override
				public Class<? extends java.lang.annotation.Annotation> annotationType() {
					return SaCheckHttpBasic.class;
				}

				@Override
				public String realm() {
					return "AnnoRealm";
				}

				@Override
				public String account() {
					return "anno:pwd";
				}
			};
			Assertions.assertDoesNotThrow(() -> SaCheckHttpBasicHandler._checkMethod(
					annotation.realm(), annotation.account()));
			Assertions.assertThrows(NotHttpBasicAuthException.class,
					() -> SaCheckHttpBasicHandler._checkMethod("AnnoRealm", "anno:wrong"));
		});
	}

}
