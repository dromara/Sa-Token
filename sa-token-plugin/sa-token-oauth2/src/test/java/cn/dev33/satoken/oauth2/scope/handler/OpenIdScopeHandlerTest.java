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
package cn.dev33.satoken.oauth2.scope.handler;

import cn.dev33.satoken.oauth2.SaOAuth2Manager;
import cn.dev33.satoken.oauth2.consts.SaOAuth2Consts;
import cn.dev33.satoken.oauth2.data.model.AccessTokenModel;
import cn.dev33.satoken.oauth2.data.model.ClientTokenModel;
import cn.dev33.satoken.oauth2.scope.CommonScope;
import cn.dev33.satoken.oauth2.support.OAuth2TestSupport;
import cn.dev33.satoken.test.SaTokenTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.LinkedHashMap;

/**
 * OpenId 权限处理器：给 AT 塞 openid，CT 什么都不做
 */
@SaTokenTest
public class OpenIdScopeHandlerTest {

	private final OpenIdScopeHandler handler = new OpenIdScopeHandler();

	/** 每个用例前装默认配置 */
	@BeforeEach
	public void reset() {
		OAuth2TestSupport.installDefaultConfig();
	}

	/** 处理的 scope 应该就是 openid */
	@Test
	public void getHandlerScope_isOpenid() {
		Assertions.assertEquals(CommonScope.OPENID, handler.getHandlerScope());
	}

	/** workAccessToken 应该往 extra 里放算出的 openid */
	@Test
	public void workAccessToken_putsOpenid() {
		AccessTokenModel at = new AccessTokenModel("at", OAuth2TestSupport.CLIENT_ID, OAuth2TestSupport.LOGIN_ID, Arrays.asList("openid"));
		at.extraData = new LinkedHashMap<>();
		handler.workAccessToken(at);
		Assertions.assertEquals(
				SaOAuth2Manager.getDataLoader().getOpenid(OAuth2TestSupport.CLIENT_ID, OAuth2TestSupport.LOGIN_ID),
				at.extraData.get(SaOAuth2Consts.ExtraField.openid));
	}

	/** workClientToken 目前是空实现，调用不炸就行 */
	@Test
	public void workClientToken_noOp() {
		handler.workClientToken(new ClientTokenModel("ct", OAuth2TestSupport.CLIENT_ID, Arrays.asList("openid")));
	}

}
