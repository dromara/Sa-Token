/*
 * Copyright 2020-2099 sa-token.cc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cn.dev33.satoken.oauth2.template;

import cn.dev33.satoken.oauth2.SaOAuth2Manager;
import cn.dev33.satoken.oauth2.data.model.AccessTokenModel;
import cn.dev33.satoken.oauth2.data.model.ClientTokenModel;
import cn.dev33.satoken.oauth2.data.model.CodeModel;
import cn.dev33.satoken.oauth2.data.model.RefreshTokenModel;
import cn.dev33.satoken.oauth2.data.model.request.RequestAuthModel;
import cn.dev33.satoken.oauth2.SaClientModel;
import cn.dev33.satoken.util.SaFoxUtil;

import java.util.List;

/**
 * Sa-Token OAuth2 数据构建器
 *
 * @author click33
 * @since 1.39.0
 */
public class SaOAuth2DataGenerateTemplate {

	public ClientTokenModel generateClientToken(String clientId, List<String> scopes) {

		SaClientModel cm = SaOAuth2Manager.getDataLoader().getClientModel(clientId);
		SaOAuth2Manager.getDataLoader().checkClientModel(cm);

		ClientTokenModel ct = new ClientTokenModel();
		ct.clientToken = randomClientToken(clientId, scopes);
		ct.scopes = scopes;
		ct.clientId = clientId;
		ct.expiresTime = cm.getClientTokenTimeout() == -1 ? -1 : System.currentTimeMillis() + cm.getClientTokenTimeout() * 1000;

		SaOAuth2Manager.getDao().saveClientToken(ct);
		return ct;
	}

	public String randomClientToken(String clientId, List<String> scopes) {
		return SaFoxUtil.getRandomString(60);
	}

	public CodeModel generateCode(RequestAuthModel ra) {
		return SaOAuth2Manager.getDataGenerate().generateCode(ra);
	}

	public AccessTokenModel generateAccessToken(RequestAuthModel ra, boolean isCreateRt) {
		return SaOAuth2Manager.getDataGenerate().generateAccessToken(ra, isCreateRt);
	}

	public AccessTokenModel refreshAccessToken(String refreshToken) {
		return SaOAuth2Manager.getDataGenerate().refreshAccessToken(refreshToken);
	}

	public RefreshTokenModel convertCodeToRefreshToken(String code) {
		return SaOAuth2Manager.getDataGenerate().convertCodeToRefreshToken(code);
	}

}
