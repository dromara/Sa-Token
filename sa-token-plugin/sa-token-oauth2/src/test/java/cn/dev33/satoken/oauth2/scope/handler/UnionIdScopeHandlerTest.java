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
 * UnionId 权限处理器：client 有 subjectId 时往 AT extra 里塞 unionid
 */
@SaTokenTest
public class UnionIdScopeHandlerTest {

	private final UnionIdScopeHandler handler = new UnionIdScopeHandler();

	/** 每个用例前装默认配置，并给测试 client 补上主体 id */
	@BeforeEach
	public void reset() {
		OAuth2TestSupport.installDefaultConfig();
		SaOAuth2Manager.getServerConfig().getClients().get(OAuth2TestSupport.CLIENT_ID).setSubjectId("subj-1");
	}

	/** 处理的 scope 应该就是 unionid */
	@Test
	public void getHandlerScope_isUnionid() {
		Assertions.assertEquals(CommonScope.UNIONID, handler.getHandlerScope());
	}

	/** workAccessToken 应该按 subjectId + loginId 算出 unionid */
	@Test
	public void workAccessToken_putsUnionid() {
		AccessTokenModel at = new AccessTokenModel("at", OAuth2TestSupport.CLIENT_ID, OAuth2TestSupport.LOGIN_ID, Arrays.asList("unionid"));
		at.extraData = new LinkedHashMap<>();
		handler.workAccessToken(at);
		Assertions.assertEquals(
				SaOAuth2Manager.getDataLoader().getUnionid("subj-1", OAuth2TestSupport.LOGIN_ID),
				at.extraData.get(SaOAuth2Consts.ExtraField.unionid));
	}

	/** workClientToken 目前是空实现 */
	@Test
	public void workClientToken_noOp() {
		handler.workClientToken(new ClientTokenModel("ct", OAuth2TestSupport.CLIENT_ID, Arrays.asList("unionid")));
	}

}
