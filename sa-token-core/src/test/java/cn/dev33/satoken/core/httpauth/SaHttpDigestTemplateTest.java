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
import cn.dev33.satoken.httpauth.digest.SaHttpDigestModel;
import cn.dev33.satoken.httpauth.digest.SaHttpDigestTemplate;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * SaHttpDigestTemplate Http Digest 认证测试
 */
public class SaHttpDigestTemplateTest {

	private final SaHttpDigestTemplate template = new SaHttpDigestTemplate();

	/** buildResponseHeaderValue 应生成合法的 Digest 响应头 */
	@Test
	void buildResponseHeaderValue() {
		SaHttpDigestModel model = new SaHttpDigestModel();
		model.realm = "TestRealm";
		model.qop = "auth";
		model.nonce = "test-nonce";
		model.nc = "00000001";
		model.opaque = "test-opaque";

		String headerValue = template.buildResponseHeaderValue(model);
		Assertions.assertTrue(headerValue.startsWith("Digest "));
		Assertions.assertTrue(headerValue.contains("realm=\"TestRealm\""));
		Assertions.assertTrue(headerValue.contains("nonce=\"test-nonce\""));
		Assertions.assertTrue(headerValue.contains("opaque=\"test-opaque\""));
	}

	/** calcResponse 应计算出 32 位 MD5 摘要响应值 */
	@Test
	void calcResponse() {
		SaHttpDigestModel model = new SaHttpDigestModel("sa", "123456");
		model.nonce = "dcd98b7102dd2f0e8b11d0f600bfb0c093";
		model.uri = "/test/digest";
		model.method = "GET";
		model.qop = "auth";
		model.nc = "00000002";
		model.cnonce = "f3ca6bfc0b2f59c4";

		String response = template.calcResponse(model);
		Assertions.assertNotNull(response);
		Assertions.assertEquals(32, response.length());
	}

	/** getAuthorizationValueToModel 应解析 Digest 请求头为 Model */
	@Test
	void getAuthorizationValueToModel() {
		SaTokenContextMockUtil.setMockContext(() -> {
			SaRequestForMock req = (SaRequestForMock) SaHolder.getRequest();
			req.method = "GET";
			req.headerMap.put("Authorization",
					"Digest username=\"sa\", realm=\"Sa-Token\", nonce=\"abc\", uri=\"/test\", "
							+ "response=\"resp\", opaque=\"opaque\", qop=auth, nc=00000001, cnonce=\"cn\"");

			SaHttpDigestModel model = template.getAuthorizationValueToModel();
			Assertions.assertNotNull(model);
			Assertions.assertEquals("sa", model.username);
			Assertions.assertEquals("Sa-Token", model.realm);
			Assertions.assertEquals("abc", model.nonce);
			Assertions.assertEquals("/test", model.uri);
			Assertions.assertEquals("GET", model.method);
			Assertions.assertEquals("auth", model.qop);
			Assertions.assertEquals("00000001", model.nc);
			Assertions.assertEquals("cn", model.cnonce);
			Assertions.assertEquals("resp", model.response);
		});
	}

	/** 合法 Digest 请求头时 check 应通过校验 */
	@Test
	void check_withValidDigestHeader() {
		SaHttpDigestModel hopeModel = new SaHttpDigestModel("sa", "123456");
		hopeModel.nonce = "test-nonce-value";
		hopeModel.uri = "/api/digest";
		hopeModel.method = "GET";
		hopeModel.qop = "auth";
		hopeModel.nc = "00000001";
		hopeModel.cnonce = "client-nonce";
		hopeModel.opaque = "opaque-value";
		hopeModel.response = template.calcResponse(hopeModel);

		String authHeader = "Digest "
				+ "username=\"" + hopeModel.username + "\", "
				+ "realm=\"" + hopeModel.realm + "\", "
				+ "nonce=\"" + hopeModel.nonce + "\", "
				+ "uri=\"" + hopeModel.uri + "\", "
				+ "response=\"" + hopeModel.response + "\", "
				+ "opaque=\"" + hopeModel.opaque + "\", "
				+ "qop=" + hopeModel.qop + ", "
				+ "nc=" + hopeModel.nc + ", "
				+ "cnonce=\"" + hopeModel.cnonce + "\"";

		SaTokenContextMockUtil.setMockContext(() -> {
			SaRequestForMock req = (SaRequestForMock) SaHolder.getRequest();
			req.method = "GET";
			req.headerMap.put("Authorization", authHeader);

			SaHttpDigestModel checkModel = new SaHttpDigestModel("sa", "123456");
			Assertions.assertDoesNotThrow(() -> template.check(checkModel));
		});
	}

}
