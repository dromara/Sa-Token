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
package cn.dev33.satoken.oauth2.data.generate;

import cn.dev33.satoken.oauth2.SaOAuth2Manager;
import cn.dev33.satoken.oauth2.data.model.AccessTokenModel;
import cn.dev33.satoken.oauth2.data.model.ClientTokenModel;
import cn.dev33.satoken.oauth2.data.model.CodeModel;
import cn.dev33.satoken.oauth2.data.model.request.RequestAuthModel;
import cn.dev33.satoken.oauth2.error.SaOAuth2ErrorCode;
import cn.dev33.satoken.oauth2.exception.SaOAuth2Exception;
import cn.dev33.satoken.oauth2.support.OAuth2TestSupport;
import cn.dev33.satoken.oauth2.support.OAuth2Test;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 默认数据构建器：code / AT / RT / CT / 回调 URL / state
 */
@OAuth2Test
public class SaOAuth2DataGenerateDefaultImplTest {

	private SaOAuth2DataGenerate gen;

	/** 每个用例前换干净构建器 */
	@BeforeEach
	public void reset() {
		OAuth2TestSupport.installDefaultConfig();
		gen = SaOAuth2Manager.getDataGenerate();
	}

	/** generateCode 应该能存下 code，再生成会顶掉旧的 */
	@Test
	public void generateCode() {
		CodeModel first = gen.generateCode(ra());
		Assertions.assertNotNull(first.code);
		Assertions.assertEquals(first.code, SaOAuth2Manager.getDao().getCode(first.code).code);
		CodeModel second = gen.generateCode(ra());
		Assertions.assertNull(SaOAuth2Manager.getDao().getCode(first.code));
		Assertions.assertNotNull(SaOAuth2Manager.getDao().getCode(second.code));
	}

	/** 用 code 换 AT 只能用一次，第二次应该抛 30110 */
	@Test
	public void generateAccessToken_fromCode_oneTimeUse() {
		CodeModel cm = gen.generateCode(ra());
		AccessTokenModel at = gen.generateAccessToken(cm.code);
		Assertions.assertNotNull(at.accessToken);
		Assertions.assertNotNull(at.refreshToken);
		Assertions.assertNull(SaOAuth2Manager.getDao().getCode(cm.code));
		SaOAuth2Exception ex = Assertions.assertThrows(SaOAuth2Exception.class, () -> gen.generateAccessToken(cm.code));
		Assertions.assertEquals(SaOAuth2ErrorCode.CODE_30110, ex.getCode());
	}

	/** 无效 code 换 AT 应该抛 30110 */
	@Test
	public void generateAccessToken_invalidCode_30110() {
		SaOAuth2Exception ex = Assertions.assertThrows(SaOAuth2Exception.class, () -> gen.generateAccessToken("no-code"));
		Assertions.assertEquals(SaOAuth2ErrorCode.CODE_30110, ex.getCode());
	}

	/** generateAccessToken(ra) 带 RT、不带 RT、appendWork 都该按开关走 */
	@Test
	public void generateAccessToken_fromRa() {
		AccessTokenModel noRt = gen.generateAccessToken(ra(), false, null);
		Assertions.assertNotNull(noRt.accessToken);
		Assertions.assertNull(noRt.refreshToken);

		AtomicBoolean appended = new AtomicBoolean(false);
		AccessTokenModel withRt = gen.generateAccessToken(ra(), true, at -> {
			at.grantType = "password";
			appended.set(true);
		});
		Assertions.assertTrue(appended.get());
		Assertions.assertEquals("password", withRt.grantType);
		Assertions.assertNotNull(withRt.refreshToken);
	}

	/** 无效 refresh_token 刷新应该抛 30111 */
	@Test
	public void refreshAccessToken_invalid_30111() {
		SaOAuth2Exception ex = Assertions.assertThrows(SaOAuth2Exception.class, () -> gen.refreshAccessToken("no-rt"));
		Assertions.assertEquals(SaOAuth2ErrorCode.CODE_30111, ex.getCode());
	}

	/** isNewRefresh=true 时刷新应该产出新的 RT */
	@Test
	public void refreshAccessToken_isNewRefresh() {
		AccessTokenModel at = gen.generateAccessToken(ra(), true, null);
		AccessTokenModel sameRt = gen.refreshAccessToken(at.refreshToken);
		Assertions.assertEquals(at.refreshToken, sameRt.refreshToken);

		SaOAuth2Manager.getServerConfig().getClients().get(OAuth2TestSupport.CLIENT_ID).setIsNewRefresh(true);
		AccessTokenModel neu = gen.refreshAccessToken(at.refreshToken);
		Assertions.assertNotEquals(at.refreshToken, neu.refreshToken);
		Assertions.assertNotNull(SaOAuth2Manager.getDao().getRefreshToken(neu.refreshToken));
	}

	/** generateClientToken 应该能签发并落库 */
	@Test
	public void generateClientToken() {
		ClientTokenModel ct = gen.generateClientToken(OAuth2TestSupport.CLIENT_ID, Collections.singletonList("openid"));
		Assertions.assertNotNull(ct.clientToken);
		Assertions.assertEquals(ct.clientToken, SaOAuth2Manager.getDao().getClientToken(ct.clientToken).clientToken);
	}

	/** 授权码回调：没 state 只拼 code，有 state 再拼 state */
	@Test
	public void buildRedirectUri_withAndWithoutState() {
		Assertions.assertEquals(OAuth2TestSupport.REDIRECT + "?code=abc",
				gen.buildRedirectUri(OAuth2TestSupport.REDIRECT, "abc", null));
		String withState = gen.buildRedirectUri(OAuth2TestSupport.REDIRECT, "abc", "st-1");
		Assertions.assertTrue(withState.contains("code=abc"));
		Assertions.assertTrue(withState.contains("state=st-1"));
	}

	/** 隐藏式回调片段参数应该是 #token= */
	@Test
	public void buildImplicitRedirectUri() {
		String url = gen.buildImplicitRedirectUri(OAuth2TestSupport.REDIRECT, "tok", null);
		Assertions.assertTrue(url.contains("#token=tok"));
		Assertions.assertFalse(url.contains("access_token="));
		String withState = gen.buildImplicitRedirectUri(OAuth2TestSupport.REDIRECT, "tok", "st-2");
		Assertions.assertTrue(withState.contains("#token=tok"));
		Assertions.assertTrue(withState.contains("state=st-2"));
	}

	/** 同一个 state 用第二次应该抛 30127 */
	@Test
	public void checkState_duplicate_30127() {
		gen.checkState("dup");
		SaOAuth2Exception ex = Assertions.assertThrows(SaOAuth2Exception.class, () -> gen.checkState("dup"));
		Assertions.assertEquals(SaOAuth2ErrorCode.CODE_30127, ex.getCode());
	}

	/** 默认请求授权模型 */
	private RequestAuthModel ra() {
		return new RequestAuthModel()
				.setClientId(OAuth2TestSupport.CLIENT_ID)
				.setLoginId(OAuth2TestSupport.LOGIN_ID)
				.setRedirectUri(OAuth2TestSupport.REDIRECT)
				.setResponseType("code")
				.setScopes(Collections.singletonList("openid"))
				.setNonce("n-1");
	}

}
