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
package cn.dev33.satoken.oauth2.data.model.request;

import cn.dev33.satoken.oauth2.error.SaOAuth2ErrorCode;
import cn.dev33.satoken.oauth2.exception.SaOAuth2Exception;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * 授权请求模型：getter/setter、checkModel 错误码、成功路径、toString
 */
public class RequestAuthModelTest {

	/** setter 应该连缀写回，getter 能读到 */
	@Test
	public void gettersAndSetters_roundTrip() {
		RequestAuthModel m = new RequestAuthModel()
				.setClientId("1001")
				.setScopes(Arrays.asList("userinfo"))
				.setLoginId(10001)
				.setRedirectUri("http://c/cb")
				.setResponseType("code")
				.setState("s1")
				.setNonce("n1");
		Assertions.assertEquals("1001", m.getClientId());
		Assertions.assertEquals(Arrays.asList("userinfo"), m.getScopes());
		Assertions.assertEquals(10001, m.getLoginId());
		Assertions.assertEquals("http://c/cb", m.getRedirectUri());
		Assertions.assertEquals("code", m.getResponseType());
		Assertions.assertEquals("s1", m.getState());
		Assertions.assertEquals("n1", m.getNonce());
	}

	/** clientId 空时 checkModel 应该抛 30101 */
	@Test
	public void checkModel_emptyClientId_throws30101() {
		SaOAuth2Exception ex = Assertions.assertThrows(SaOAuth2Exception.class, () -> valid().setClientId("").checkModel());
		Assertions.assertEquals(SaOAuth2ErrorCode.CODE_30101, ex.getCode());
	}

	/** scopes 为 null 时 checkModel 应该抛 30102 */
	@Test
	public void checkModel_nullScopes_throws30102() {
		SaOAuth2Exception ex = Assertions.assertThrows(SaOAuth2Exception.class, () -> valid().setScopes(null).checkModel());
		Assertions.assertEquals(SaOAuth2ErrorCode.CODE_30102, ex.getCode());
	}

	/** scopes 空列表时 isEmpty(Object) 认不出来，目前不会抛 30102 */
	@Test
	public void checkModel_emptyScopesList_currentlyPasses() {
		Assertions.assertDoesNotThrow(() -> valid().setScopes(new ArrayList<>()).checkModel());
	}

	/** redirectUri 空时 checkModel 应该抛 30103 */
	@Test
	public void checkModel_emptyRedirectUri_throws30103() {
		SaOAuth2Exception ex = Assertions.assertThrows(SaOAuth2Exception.class, () -> valid().setRedirectUri("").checkModel());
		Assertions.assertEquals(SaOAuth2ErrorCode.CODE_30103, ex.getCode());
	}

	/** loginId 空串时 String.valueOf 还是空，应该抛 30104 */
	@Test
	public void checkModel_emptyLoginIdString_throws30104() {
		SaOAuth2Exception ex = Assertions.assertThrows(SaOAuth2Exception.class, () -> valid().setLoginId("").checkModel());
		Assertions.assertEquals(SaOAuth2ErrorCode.CODE_30104, ex.getCode());
	}

	/** loginId=null 时 String.valueOf 变成 "null" 字符串，目前不会抛 30104 */
	@Test
	public void checkModel_nullLoginId_currentlyPasses() {
		Assertions.assertDoesNotThrow(() -> valid().setLoginId(null).checkModel());
	}

	/** 字段齐全时 checkModel 应该返回自身 */
	@Test
	public void checkModel_success_returnsSelf() {
		RequestAuthModel m = valid();
		Assertions.assertSame(m, m.checkModel());
	}

	/** toString 里应该能看到 clientId 和 nonce */
	@Test
	public void toString_containsMainFields() {
		String text = valid().toString();
		Assertions.assertTrue(text.startsWith("RequestAuthModel{"));
		Assertions.assertTrue(text.contains("clientId='1001'"));
		Assertions.assertTrue(text.contains("nonce='n1'"));
	}

	private RequestAuthModel valid() {
		return new RequestAuthModel()
				.setClientId("1001")
				.setScopes(Arrays.asList("userinfo"))
				.setLoginId(10001)
				.setRedirectUri("http://c/cb")
				.setResponseType("code")
				.setState("s1")
				.setNonce("n1");
	}

}
