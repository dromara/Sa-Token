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
import cn.dev33.satoken.SaManager;
import cn.dev33.satoken.context.SaHolder;
import cn.dev33.satoken.context.mock.SaRequestForMock;
import cn.dev33.satoken.context.mock.SaTokenContextMockUtil;
import cn.dev33.satoken.httpauth.digest.SaHttpDigestModel;
import cn.dev33.satoken.httpauth.digest.SaHttpDigestTemplate;
import cn.dev33.satoken.httpauth.digest.SaHttpDigestUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * SaHttpDigestUtil Http Digest 认证门面测试
 *
 * @author click33
 * @since 1.46.0
 */
public class SaHttpDigestUtilTest {

	private final SaHttpDigestTemplate template = SaHttpDigestUtil.saHttpDigestTemplate;

	/** getAuthorizationValue / getAuthorizationValueToModel 应正确委托 */
	@Test
	void getAuthorizationValue_delegates() {
		SaTokenContextMockUtil.setMockContext(() -> {
			SaRequestForMock req = (SaRequestForMock) SaHolder.getRequest();
			req.headerMap.put("Authorization",
					"Digest username=\"sa\", realm=\"test\", nonce=\"abc\", uri=\"/\", response=\"xyz\"");

			String value = SaHttpDigestUtil.getAuthorizationValue();
			Assertions.assertNotNull(value);
			Assertions.assertTrue(value.contains("username=\"sa\""));

			SaHttpDigestModel model = SaHttpDigestUtil.getAuthorizationValueToModel();
			Assertions.assertEquals("sa", model.username);
		});
	}

	/** check 各重载应委托到 saHttpDigestTemplate */
	@Test
	void check_delegatesToTemplate() {
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
			Assertions.assertDoesNotThrow(() -> SaHttpDigestUtil.check(checkModel));
			Assertions.assertDoesNotThrow(() -> SaHttpDigestUtil.check("sa", "123456"));
			Assertions.assertDoesNotThrow(() -> SaHttpDigestUtil.check("sa", "123456", hopeModel.realm));

			SaManager.getConfig().setHttpDigest("sa:123456");
			Assertions.assertDoesNotThrow(() -> SaHttpDigestUtil.check());
		});
	}

	/** checkByAnnotation 应委托（已过期 API） */
	@Test
	void checkByAnnotation_delegates() {
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
				@Override
				public Class<? extends java.lang.annotation.Annotation> annotationType() {
					return SaCheckHttpDigest.class;
				}

				@Override
				public String value() {
					return "anno:pwd";
				}

				@Override
				public String username() {
					return "";
				}

				@Override
				public String password() {
					return "";
				}

				@Override
				public String realm() {
					return "";
				}
			};
			Assertions.assertDoesNotThrow(() -> SaHttpDigestUtil.checkByAnnotation(atValue));
		});
	}

}
