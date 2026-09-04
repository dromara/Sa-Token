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
package cn.dev33.satoken.core.annotation.handler;

import cn.dev33.satoken.SaManager;
import cn.dev33.satoken.annotation.handler.SaCheckHttpDigestHandler;
import cn.dev33.satoken.context.SaHolder;
import cn.dev33.satoken.context.mock.SaRequestForMock;
import cn.dev33.satoken.context.mock.SaTokenContextMockUtil;
import cn.dev33.satoken.exception.SaTokenException;
import cn.dev33.satoken.httpauth.digest.SaHttpDigestModel;
import cn.dev33.satoken.httpauth.digest.SaHttpDigestTemplate;
import cn.dev33.satoken.httpauth.digest.SaHttpDigestUtil;
import cn.dev33.satoken.test.SaTokenTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * SaCheckHttpDigestHandler 注解处理器测试
 */
@SaTokenTest
public class SaCheckHttpDigestHandlerTest {

	private static void setDigestHeader(SaHttpDigestTemplate template, SaHttpDigestModel hope) {
		hope.nonce = "nonce-handler";
		hope.uri = "/handler";
		hope.method = "GET";
		hope.qop = "auth";
		hope.nc = "00000001";
		hope.cnonce = "cnonce-handler";
		hope.response = template.calcResponse(hope);

		String authHeader = "Digest username=\"" + hope.username + "\", realm=\"" + hope.realm + "\", "
				+ "nonce=\"nonce-handler\", uri=\"/handler\", response=\"" + hope.response
				+ "\", qop=auth, nc=00000001, cnonce=\"cnonce-handler\"";
		SaRequestForMock req = (SaRequestForMock) SaHolder.getRequest();
		req.method = "GET";
		req.headerMap.put("Authorization", authHeader);
	}

	/** 指定 username/password/realm 时 Digest 校验应通过 */
	@Test
	void checkMethod_usernamePasswordRealmPath() {
		SaHttpDigestTemplate template = new SaHttpDigestTemplate();
		SaTokenContextMockUtil.setMockContext(() -> {
			SaHttpDigestModel hope = new SaHttpDigestModel("digest-user", "digest-pass", "MyRealm");
			setDigestHeader(template, hope);
			Assertions.assertDoesNotThrow(() ->
					SaCheckHttpDigestHandler._checkMethod("digest-user", "digest-pass", "MyRealm", ""));
		});
	}

	/** 使用全局 httpDigest 配置时校验应通过 */
	@Test
	void checkMethod_globalConfigPath() {
		SaManager.getConfig().setHttpDigest("global-user:global-pass");
		SaHttpDigestTemplate template = new SaHttpDigestTemplate();
		SaTokenContextMockUtil.setMockContext(() -> {
			SaHttpDigestModel hope = new SaHttpDigestModel("global-user", "global-pass");
			setDigestHeader(template, hope);
			Assertions.assertDoesNotThrow(() ->
					SaCheckHttpDigestHandler._checkMethod("", "", "", ""));
		});
	}

	/** value 路径应委托模板执行 Digest 校验 */
	@Test
	void checkMethod_valuePath_delegatesToTemplate() {
		SaHttpDigestTemplate original = SaHttpDigestUtil.saHttpDigestTemplate;
		try {
			SaHttpDigestUtil.saHttpDigestTemplate = new SaHttpDigestTemplate() {
				@Override
				public void check(String username, String password) {
					Assertions.assertEquals("value-user", username);
					Assertions.assertEquals("value-pass", password);
				}
			};
			Assertions.assertDoesNotThrow(() ->
					SaCheckHttpDigestHandler._checkMethod("", "", "", "value-user:value-pass"));
		} finally {
			SaHttpDigestUtil.saHttpDigestTemplate = original;
		}
	}

	/** value 格式非法时 checkMethod 应抛出 SaTokenException */
	@Test
	void checkMethod_invalidValueFormat() {
		Assertions.assertThrows(SaTokenException.class,
				() -> SaCheckHttpDigestHandler._checkMethod("", "", "", "only-one-part"));
	}

	/** getHandlerAnnotationClass 应返回 SaCheckHttpDigest 类型 */
	@Test
	void handlerAnnotationClass() {
		SaCheckHttpDigestHandler handler = new SaCheckHttpDigestHandler();
		Assertions.assertNotNull(handler.getHandlerAnnotationClass());
	}

}
