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
package cn.dev33.satoken.plugin;

import cn.dev33.satoken.dao.SaTokenDao;
import cn.dev33.satoken.oauth2.SaOAuth2Manager;
import cn.dev33.satoken.oauth2.data.model.AccessTokenModel;
import cn.dev33.satoken.oauth2.data.model.ClientTokenModel;
import cn.dev33.satoken.oauth2.data.model.RefreshTokenModel;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * OAuth2 Token Model：expiresTime=-1 时 getExpiresIn 应返回 -1，并可永久存储
 */
public class OAuth2TokenModelNeverExpireTest {

	@AfterEach
	public void cleanup() {
		SaOAuth2Manager.getDao().deleteAccessToken("never-at");
		SaOAuth2Manager.getDao().deleteRefreshToken("never-rt");
		SaOAuth2Manager.getDao().deleteClientToken("never-ct");
	}

	@Test
	public void getExpiresIn_neverExpire() {
		AccessTokenModel at = new AccessTokenModel();
		at.expiresTime = SaTokenDao.NEVER_EXPIRE;
		at.refreshExpiresTime = SaTokenDao.NEVER_EXPIRE;
		Assertions.assertEquals(SaTokenDao.NEVER_EXPIRE, at.getExpiresIn());
		Assertions.assertEquals(SaTokenDao.NEVER_EXPIRE, at.getRefreshExpiresIn());

		RefreshTokenModel rt = new RefreshTokenModel();
		rt.expiresTime = SaTokenDao.NEVER_EXPIRE;
		Assertions.assertEquals(SaTokenDao.NEVER_EXPIRE, rt.getExpiresIn());

		ClientTokenModel ct = new ClientTokenModel();
		ct.expiresTime = SaTokenDao.NEVER_EXPIRE;
		Assertions.assertEquals(SaTokenDao.NEVER_EXPIRE, ct.getExpiresIn());
	}

	@Test
	public void getExpiresIn_alreadyExpired_stillNotValue() {
		ClientTokenModel ct = new ClientTokenModel();
		ct.expiresTime = 1;
		Assertions.assertEquals(SaTokenDao.NOT_VALUE_EXPIRE, ct.getExpiresIn());
	}

	@Test
	public void save_neverExpire_canReadBack() {
		AccessTokenModel at = new AccessTokenModel();
		at.accessToken = "never-at";
		at.expiresTime = SaTokenDao.NEVER_EXPIRE;
		SaOAuth2Manager.getDao().saveAccessToken(at);
		Assertions.assertNotNull(SaOAuth2Manager.getDao().getAccessToken("never-at"));
		Assertions.assertEquals(SaTokenDao.NEVER_EXPIRE, SaOAuth2Manager.getDao().getAccessToken("never-at").getExpiresIn());

		RefreshTokenModel rt = new RefreshTokenModel();
		rt.refreshToken = "never-rt";
		rt.expiresTime = SaTokenDao.NEVER_EXPIRE;
		SaOAuth2Manager.getDao().saveRefreshToken(rt);
		Assertions.assertNotNull(SaOAuth2Manager.getDao().getRefreshToken("never-rt"));

		ClientTokenModel ct = new ClientTokenModel();
		ct.clientToken = "never-ct";
		ct.expiresTime = SaTokenDao.NEVER_EXPIRE;
		SaOAuth2Manager.getDao().saveClientToken(ct);
		Assertions.assertNotNull(SaOAuth2Manager.getDao().getClientToken("never-ct"));
		Assertions.assertEquals(SaTokenDao.NEVER_EXPIRE, SaOAuth2Manager.getDao().getClientToken("never-ct").getExpiresIn());
	}

}
