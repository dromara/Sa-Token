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

import cn.dev33.satoken.oauth2.consts.SaOAuth2Consts;
import cn.dev33.satoken.oauth2.data.model.AccessTokenModel;
import cn.dev33.satoken.oauth2.data.model.ClientTokenModel;
import cn.dev33.satoken.oauth2.scope.CommonScope;
import cn.dev33.satoken.oauth2.support.OAuth2TestSupport;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.LinkedHashMap;

/**
 * UserId 权限处理器：把 loginId 原样塞进 extra.userid
 */
public class UserIdScopeHandlerTest {

	private final UserIdScopeHandler handler = new UserIdScopeHandler();

	/** 处理的 scope 应该就是 userid */
	@Test
	public void getHandlerScope_isUserid() {
		Assertions.assertEquals(CommonScope.USERID, handler.getHandlerScope());
	}

	/** 没覆盖 refreshAccessTokenIsWork 时默认应该是 false */
	@Test
	public void refreshAccessTokenIsWork_defaultFalse() {
		Assertions.assertFalse(handler.refreshAccessTokenIsWork());
	}

	/** workAccessToken 应该把 loginId 放到 userid 字段 */
	@Test
	public void workAccessToken_putsLoginId() {
		AccessTokenModel at = new AccessTokenModel("at", OAuth2TestSupport.CLIENT_ID, OAuth2TestSupport.LOGIN_ID, Arrays.asList("userid"));
		at.extraData = new LinkedHashMap<>();
		handler.workAccessToken(at);
		Assertions.assertEquals(OAuth2TestSupport.LOGIN_ID, at.extraData.get(SaOAuth2Consts.ExtraField.userid));
	}

	/** workClientToken 目前是空实现 */
	@Test
	public void workClientToken_noOp() {
		handler.workClientToken(new ClientTokenModel("ct", OAuth2TestSupport.CLIENT_ID, Arrays.asList("userid")));
	}

}
