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

import cn.dev33.satoken.annotation.SaCheckHttpDigest;
import cn.dev33.satoken.context.SaHolder;
import cn.dev33.satoken.context.mock.SaRequestForMock;
import cn.dev33.satoken.context.mock.SaTokenContextMockUtil;
import cn.dev33.satoken.httpauth.digest.SaHttpDigestModel;
import cn.dev33.satoken.httpauth.digest.SaHttpDigestTemplate;
import cn.dev33.satoken.test.SaTokenTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * SaHttpDigestTemplate check(username,password,realm) 与注解 username 路径测试
 *
 * @author click33
 * @since 1.46.0
 */
@SaTokenTest
public class SaHttpDigestTemplateFullTest {

	private final SaHttpDigestTemplate template = new SaHttpDigestTemplate();

	private void mockDigestRequest(SaHttpDigestModel hope) {
		hope.nonce = "nonce-full";
		hope.uri = "/realm-api";
		hope.method = "GET";
		hope.qop = "auth";
		hope.nc = "00000001";
		hope.cnonce = "cnonce-full";
		hope.response = template.calcResponse(hope);

		String authHeader = "Digest username=\"" + hope.username + "\", realm=\"" + hope.realm + "\", "
				+ "nonce=\"nonce-full\", uri=\"/realm-api\", response=\"" + hope.response
				+ "\", qop=auth, nc=00000001, cnonce=\"cnonce-full\"";
		SaRequestForMock req = (SaRequestForMock) SaHolder.getRequest();
		req.method = "GET";
		req.headerMap.put("Authorization", authHeader);
	}

	/** 指定自定义 realm 时 Digest 校验应通过 */
	@Test
	void check_withCustomRealm() {
		SaTokenContextMockUtil.setMockContext(() -> {
			SaHttpDigestModel hope = new SaHttpDigestModel("realm-user", "realm-pass", "CustomRealm");
			mockDigestRequest(hope);
			Assertions.assertDoesNotThrow(() -> template.check("realm-user", "realm-pass", "CustomRealm"));
		});
	}

	/** 注解 username/password/realm 路径校验应通过 */
	@Test
	void checkByAnnotation_usernamePasswordRealmPath() {
		SaTokenContextMockUtil.setMockContext(() -> {
			SaHttpDigestModel hope = new SaHttpDigestModel("anno-user", "anno-pass", "AnnoRealm");
			mockDigestRequest(hope);

			SaCheckHttpDigest at = new SaCheckHttpDigest() {
				@Override public Class<? extends java.lang.annotation.Annotation> annotationType() { return SaCheckHttpDigest.class; }
				@Override public String value() { return ""; }
				@Override public String username() { return "anno-user"; }
				@Override public String password() { return "anno-pass"; }
				@Override public String realm() { return "AnnoRealm"; }
			};
			Assertions.assertDoesNotThrow(() -> template.checkByAnnotation(at));
		});
	}

	/** 双参 check 应委托 Model 路径完成 Digest 校验 */
	@Test
	void check_twoArgDelegatesToModel() {
		SaTokenContextMockUtil.setMockContext(() -> {
			SaHttpDigestModel hope = new SaHttpDigestModel("two-arg", "secret");
			mockDigestRequest(hope);
			Assertions.assertDoesNotThrow(() -> template.check("two-arg", "secret"));
		});
	}

}
