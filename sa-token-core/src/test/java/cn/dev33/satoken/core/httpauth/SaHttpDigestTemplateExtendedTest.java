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
import cn.dev33.satoken.context.mock.SaResponseForMock;
import cn.dev33.satoken.context.mock.SaRequestForMock;
import cn.dev33.satoken.context.mock.SaTokenContextMockUtil;
import cn.dev33.satoken.exception.NotHttpDigestAuthException;
import cn.dev33.satoken.exception.SaTokenException;
import cn.dev33.satoken.httpauth.digest.SaHttpDigestModel;
import cn.dev33.satoken.httpauth.digest.SaHttpDigestTemplate;
import cn.dev33.satoken.test.SaTokenTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * SaHttpDigestTemplate 扩展测试
 *
 * @author click33
 * @since 1.46.0
 */
@SaTokenTest
public class SaHttpDigestTemplateExtendedTest {

	private final SaHttpDigestTemplate template = new SaHttpDigestTemplate();

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

}
