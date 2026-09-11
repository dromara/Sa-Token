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
import cn.dev33.satoken.annotation.SaCheckHttpDigest;
import cn.dev33.satoken.context.SaHolder;
import cn.dev33.satoken.context.mock.SaRequestForMock;
import cn.dev33.satoken.context.mock.SaResponseForMock;
import cn.dev33.satoken.context.mock.SaTokenContextMockUtil;
import cn.dev33.satoken.exception.NotHttpDigestAuthException;
import cn.dev33.satoken.exception.SaTokenException;
import cn.dev33.satoken.httpauth.digest.SaHttpDigestModel;
import cn.dev33.satoken.httpauth.digest.SaHttpDigestTemplate;
import cn.dev33.satoken.test.SaTokenTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * SaHttpDigestTemplate Http Digest 认证测试
 *
 * @author click33
 * @since 1.46.0
 */
@SaTokenTest
public class SaHttpDigestTemplateTest {

	private final SaHttpDigestTemplate template = new SaHttpDigestTemplate();

	/** 按 hope 里的账号与 realm 算好 response，并把合法的 Digest 请求头塞进当前 Mock 请求 */
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

	/** 缺失或非 Digest 前缀的请求头应返回 null */
	@Test
	void getAuthorizationValue_nullWhenMissingOrInvalidPrefix() {
		SaTokenContextMockUtil.setMockContext(() -> {
			Assertions.assertNull(template.getAuthorizationValue());
			SaRequestForMock req = (SaRequestForMock) SaHolder.getRequest();
			req.headerMap.put("Authorization", "Basic abc");
			Assertions.assertNull(template.getAuthorizationValue());
		});
	}

	/** URI 含等号查询参数时应完整解析 uri 字段 */
	@Test
	void getAuthorizationValueToModel_uriWithEqualsInQuery() {
		SaTokenContextMockUtil.setMockContext(() -> {
			SaRequestForMock req = (SaRequestForMock) SaHolder.getRequest();
			req.method = "GET";
			req.headerMap.put("Authorization",
					"Digest username=\"sa\", realm=\"Sa-Token\", nonce=\"n1\", "
							+ "uri=\"/test?name=zhangsan&age=18\", response=\"resp\", qop=auth, nc=00000001, cnonce=\"cn\"");

			SaHttpDigestModel model = template.getAuthorizationValueToModel();
			Assertions.assertNotNull(model);
			Assertions.assertEquals("/test?name=zhangsan&age=18", model.uri);
		});
	}

	/** throwNotHttpDigestAuthException 应设置 401 并抛出异常 */
	@Test
	void throwNotHttpDigestAuthException_sets401AndThrows() {
		SaTokenContextMockUtil.setMockContext(() -> {
			SaHttpDigestModel model = new SaHttpDigestModel();
			Assertions.assertThrows(NotHttpDigestAuthException.class,
					() -> template.throwNotHttpDigestAuthException(model));
			SaResponseForMock response = (SaResponseForMock) SaHolder.getResponse();
			Assertions.assertEquals(401, response.status);
			Assertions.assertNotNull(response.headerMap.get("WWW-Authenticate"));
		});
	}

	/** challenge 应保留调用方指定的 Digest 参数 */
	@Test
	void throwNotHttpDigestAuthException_keepsProvidedChallengeValues() {
		SaTokenContextMockUtil.setMockContext(() -> {
			SaHttpDigestModel model = new SaHttpDigestModel();
			model.realm = "Admin";
			model.qop = "auth-int";
			model.nonce = "nonce-fixed";
			model.nc = "00000009";
			model.opaque = "opaque-fixed";

			Assertions.assertThrows(NotHttpDigestAuthException.class,
					() -> template.throwNotHttpDigestAuthException(model));

			String header = ((SaResponseForMock) SaHolder.getResponse()).headerMap.get("WWW-Authenticate");
			Assertions.assertEquals("Digest realm=\"Admin\", qop=\"auth-int\", nonce=\"nonce-fixed\", "
					+ "nc=00000009, opaque=\"opaque-fixed\"", header);
		});
	}

	/** 缺少 Digest 请求头时 check 应抛出 NotHttpDigestAuthException */
	@Test
	void check_throwsWhenNoDigestHeader() {
		SaTokenContextMockUtil.setMockContext(() -> {
			SaHttpDigestModel hope = new SaHttpDigestModel("sa", "123456");
			Assertions.assertThrows(NotHttpDigestAuthException.class, () -> template.check(hope));
		});
	}

	/** response 摘要不匹配时 check 应抛出 NotHttpDigestAuthException */
	@Test
	void check_throwsWhenResponseMismatch() {
		SaTokenContextMockUtil.setMockContext(() -> {
			SaRequestForMock req = (SaRequestForMock) SaHolder.getRequest();
			req.method = "GET";
			req.headerMap.put("Authorization",
					"Digest username=\"sa\", realm=\"Sa-Token\", nonce=\"n1\", uri=\"/api\", "
							+ "response=\"wrong\", qop=auth, nc=00000001, cnonce=\"cn\"");

			SaHttpDigestModel hope = new SaHttpDigestModel("sa", "123456");
			Assertions.assertThrows(NotHttpDigestAuthException.class, () -> template.check(hope));
		});
	}

	/** 指定 username/password 且请求头合法时 check 应通过 */
	@Test
	void check_withUsernamePasswordAndGlobalConfig() {
		SaTokenContextMockUtil.setMockContext(() -> {
			SaHttpDigestModel hopeModel = new SaHttpDigestModel("sa", "123456");
			hopeModel.nonce = "nonce-global";
			hopeModel.uri = "/global";
			hopeModel.method = "GET";
			hopeModel.qop = "auth";
			hopeModel.nc = "00000001";
			hopeModel.cnonce = "cnonce-global";
			hopeModel.response = template.calcResponse(hopeModel);

			String authHeader = "Digest username=\"sa\", realm=\"Sa-Token\", nonce=\"nonce-global\", "
					+ "uri=\"/global\", response=\"" + hopeModel.response + "\", qop=auth, nc=00000001, cnonce=\"cnonce-global\"";
			SaRequestForMock req = (SaRequestForMock) SaHolder.getRequest();
			req.method = "GET";
			req.headerMap.put("Authorization", authHeader);

			Assertions.assertDoesNotThrow(() -> template.check("sa", "123456"));
		});
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

	/** 全局 httpDigest 为空或格式非法时 check 应抛出 SaTokenException */
	@Test
	void check_globalConfigErrors() {
		SaManager.getConfig().setHttpDigest("");
		Assertions.assertThrows(SaTokenException.class, () -> template.check());

		SaManager.getConfig().setHttpDigest("only-one-part");
		Assertions.assertThrows(SaTokenException.class, () -> template.check());
	}

	/** checkByAnnotation 应支持 value 路径并校验非法格式 */
	@Test
	void checkByAnnotation_valueUsernameAndGlobal() {
		SaTokenContextMockUtil.setMockContext(() -> {
			SaHttpDigestModel hopeModel = new SaHttpDigestModel("anno", "pwd");
			hopeModel.nonce = "nonce-anno";
			hopeModel.uri = "/anno";
			hopeModel.method = "GET";
			hopeModel.qop = "auth";
			hopeModel.nc = "00000001";
			hopeModel.cnonce = "cnonce-anno";
			hopeModel.response = template.calcResponse(hopeModel);

			String authHeader = "Digest username=\"anno\", realm=\"Sa-Token\", nonce=\"nonce-anno\", "
					+ "uri=\"/anno\", response=\"" + hopeModel.response + "\", qop=auth, nc=00000001, cnonce=\"cnonce-anno\"";
			SaRequestForMock req = (SaRequestForMock) SaHolder.getRequest();
			req.method = "GET";
			req.headerMap.put("Authorization", authHeader);

			SaCheckHttpDigest atValue = new SaCheckHttpDigest() {
				@Override public Class<? extends java.lang.annotation.Annotation> annotationType() { return SaCheckHttpDigest.class; }
				@Override public String value() { return "anno:pwd"; }
				@Override public String username() { return ""; }
				@Override public String password() { return ""; }
				@Override public String realm() { return ""; }
			};
			Assertions.assertDoesNotThrow(() -> template.checkByAnnotation(atValue));

			Assertions.assertThrows(SaTokenException.class, () -> template.checkByAnnotation(new SaCheckHttpDigest() {
				@Override public Class<? extends java.lang.annotation.Annotation> annotationType() { return SaCheckHttpDigest.class; }
				@Override public String value() { return "bad-format"; }
				@Override public String username() { return ""; }
				@Override public String password() { return ""; }
				@Override public String realm() { return ""; }
			}));
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

	/** copyHopeToReq 应合并 hope 字段且保留 req 已有 uri */
	@Test
	void copyHopeToReq_mergesFields() {
		SaHttpDigestModel hope = new SaHttpDigestModel("u", "p");
		hope.realm = "R";
		hope.nonce = "N";
		SaHttpDigestModel req = new SaHttpDigestModel();
		req.uri = "/keep";
		template.copyHopeToReq(hope, req);
		Assertions.assertEquals("u", req.username);
		Assertions.assertEquals("p", req.password);
		Assertions.assertEquals("R", req.realm);
		Assertions.assertEquals("/keep", req.uri);
	}

	/** copyHopeToReq 应以 hope 的非空字段覆盖请求字段 */
	@Test
	void copyHopeToReq_overridesAllSupportedFields() {
		SaHttpDigestModel hope = new SaHttpDigestModel("hope-user", "hope-password");
		hope.realm = "hope-realm";
		hope.nonce = "hope-nonce";
		hope.uri = "/hope";
		hope.method = "PUT";
		hope.qop = "auth";
		hope.nc = "00000002";
		hope.opaque = "hope-opaque";
		SaHttpDigestModel req = new SaHttpDigestModel("req-user", "req-password");
		req.realm = "req-realm";
		req.nonce = "req-nonce";
		req.uri = "/req";
		req.method = "GET";
		req.qop = "auth-int";
		req.nc = "00000001";
		req.opaque = "req-opaque";

		template.copyHopeToReq(hope, req);

		Assertions.assertEquals("hope-user", req.username);
		Assertions.assertEquals("hope-password", req.password);
		Assertions.assertEquals("hope-realm", req.realm);
		Assertions.assertEquals("hope-nonce", req.nonce);
		Assertions.assertEquals("/hope", req.uri);
		Assertions.assertEquals("PUT", req.method);
		Assertions.assertEquals("auth", req.qop);
		Assertions.assertEquals("00000002", req.nc);
		Assertions.assertEquals("hope-opaque", req.opaque);
	}

}
