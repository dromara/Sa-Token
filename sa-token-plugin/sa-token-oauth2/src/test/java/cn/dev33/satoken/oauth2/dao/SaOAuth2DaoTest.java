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
package cn.dev33.satoken.oauth2.dao;

import cn.dev33.satoken.SaManager;
import cn.dev33.satoken.dao.SaTokenDao;
import cn.dev33.satoken.oauth2.SaOAuth2Manager;
import cn.dev33.satoken.oauth2.data.model.AccessTokenModel;
import cn.dev33.satoken.oauth2.data.model.ClientTokenModel;
import cn.dev33.satoken.oauth2.data.model.CodeModel;
import cn.dev33.satoken.oauth2.data.model.RefreshTokenModel;
import cn.dev33.satoken.oauth2.support.OAuth2TestSupport;
import cn.dev33.satoken.session.SaSession;
import cn.dev33.satoken.oauth2.support.OAuth2Test;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * OAuth2 Dao：code / AT / RT / CT / 索引溢出 / 授权记录 / state
 */
@OAuth2Test
public class SaOAuth2DaoTest {

	private SaOAuth2Dao dao;

	/** 每个用例前换干净 Dao */
	@BeforeEach
	public void reset() {
		OAuth2TestSupport.installDefaultConfig();
		dao = SaOAuth2Manager.getDao();
	}

	/** code 保存后再取应该能拿到，null 保存/删除都该直接返回 */
	@Test
	public void code_saveGetDelete_nullNoOp() {
		dao.saveCode(null);
		dao.deleteCode(null);
		Assertions.assertNull(dao.getCode(null));
		CodeModel cm = code("c1");
		dao.saveCode(cm);
		Assertions.assertEquals("c1", dao.getCode("c1").code);
		dao.deleteCode("c1");
		Assertions.assertNull(dao.getCode("c1"));
	}

	/** code 索引保存后再按 client+loginId 能取到 */
	@Test
	public void codeIndex_saveGetDelete() {
		dao.saveCodeIndex(null);
		CodeModel cm = code("c-idx");
		dao.saveCodeIndex(cm);
		Assertions.assertEquals("c-idx", dao.getCodeValue(OAuth2TestSupport.CLIENT_ID, OAuth2TestSupport.LOGIN_ID));
		dao.deleteCodeIndex(OAuth2TestSupport.CLIENT_ID, OAuth2TestSupport.LOGIN_ID);
		Assertions.assertNull(dao.getCodeValue(OAuth2TestSupport.CLIENT_ID, OAuth2TestSupport.LOGIN_ID));
	}

	/** AccessToken 保存/读取/删除，null 保存删除都该直接返回 */
	@Test
	public void accessToken_saveGetDelete() {
		dao.saveAccessToken(null);
		dao.deleteAccessToken(null);
		Assertions.assertNull(dao.getAccessToken(null));
		AccessTokenModel at = at("at-1", 3600_000);
		dao.saveAccessToken(at);
		Assertions.assertEquals("at-1", dao.getAccessToken("at-1").accessToken);
		dao.deleteAccessToken("at-1");
		Assertions.assertNull(dao.getAccessToken("at-1"));
	}

	/** AccessToken 索引：max=1 时后来的会挤掉先来的 */
	@Test
	public void accessTokenIndex_overflowMax1() {
		AccessTokenModel at1 = at("at-old", 3600_000);
		AccessTokenModel at2 = at("at-new", 3600_000);
		dao.saveAccessToken(at1);
		dao.saveAccessTokenIndex_AndAdjust(at1, 1);
		dao.saveAccessToken(at2);
		dao.saveAccessTokenIndex_AndAdjust(at2, 1);
		Assertions.assertNull(dao.getAccessToken("at-old"));
		Assertions.assertNotNull(dao.getAccessToken("at-new"));
		List<String> list = dao.getAccessTokenValueList_FromAdjustAfter(OAuth2TestSupport.CLIENT_ID, OAuth2TestSupport.LOGIN_ID);
		Assertions.assertEquals(Collections.singletonList("at-new"), list);
	}

	/** 同一个 AccessToken 再存一遍索引不应该重复 */
	@Test
	public void accessTokenIndex_duplicateSkip() {
		AccessTokenModel at = at("at-dup", 3600_000);
		dao.saveAccessTokenIndex_AndAdjust(null, 12);
		dao.saveAccessTokenIndex_AndAdjust(at, 12);
		dao.saveAccessTokenIndex_AndAdjust(at, 12);
		Assertions.assertEquals(1, dao.getAccessTokenValueList_FromAdjustAfter(OAuth2TestSupport.CLIENT_ID, OAuth2TestSupport.LOGIN_ID).size());
	}

	/** maxTokenCount=-1 时索引不应该被裁掉 */
	@Test
	public void accessTokenIndex_neverExpireMaxCount() {
		dao.saveAccessTokenIndex_AndAdjust(at("at-a", 3600_000), -1);
		dao.saveAccessTokenIndex_AndAdjust(at("at-b", 3600_000), -1);
		Assertions.assertEquals(2, dao.getAccessTokenValueList_FromAdjustAfter(OAuth2TestSupport.CLIENT_ID, OAuth2TestSupport.LOGIN_ID).size());
	}

	/** 永不过期的 AccessToken 应该能存住 */
	@Test
	public void accessToken_neverExpire() {
		AccessTokenModel at = at("at-forever", 0);
		at.expiresTime = SaTokenDao.NEVER_EXPIRE;
		dao.saveAccessToken(at);
		Assertions.assertNotNull(dao.getAccessToken("at-forever"));
		dao.saveAccessTokenIndex_AndAdjust(at, 12);
		Assertions.assertTrue(dao.getAccessTokenValueList_FromAdjustAfter(OAuth2TestSupport.CLIENT_ID, OAuth2TestSupport.LOGIN_ID).contains("at-forever"));
	}

	/** 过期索引在读取时应该被剔掉 */
	@Test
	public void accessTokenIndex_removeExpired() {
		String rsd = dao.splicingAccessTokenRSDValue(OAuth2TestSupport.CLIENT_ID, OAuth2TestSupport.LOGIN_ID);
		SaSession session = dao.oauth2RSD.getSessionById(rsd, true);
		Map<String, Long> map = new LinkedHashMap<>();
		map.put("dead-at", System.currentTimeMillis() - 10_000);
		map.put("live-at", System.currentTimeMillis() + 3_600_000);
		session.set(SaOAuth2Dao.ACCESS_TOKEN_MAP, map);
		List<String> list = dao.getAccessTokenValueList_FromAdjustAfter(OAuth2TestSupport.CLIENT_ID, OAuth2TestSupport.LOGIN_ID);
		Assertions.assertEquals(Collections.singletonList("live-at"), list);
		Assertions.assertEquals(1, dao.getAccessTokenIndexMap_FromAdjustAfter(OAuth2TestSupport.CLIENT_ID, OAuth2TestSupport.LOGIN_ID).size());
	}

	/** 索引全过期时应该清空并注销 session */
	@Test
	public void accessTokenIndex_allExpiredClearsSession() {
		String rsd = dao.splicingAccessTokenRSDValue(OAuth2TestSupport.CLIENT_ID, OAuth2TestSupport.LOGIN_ID);
		SaSession session = dao.oauth2RSD.getSessionById(rsd, true);
		Map<String, Long> map = new LinkedHashMap<>();
		map.put("dead-at", System.currentTimeMillis() - 10_000);
		session.set(SaOAuth2Dao.ACCESS_TOKEN_MAP, map);
		Assertions.assertTrue(dao.getAccessTokenValueList_FromAdjustAfter(OAuth2TestSupport.CLIENT_ID, OAuth2TestSupport.LOGIN_ID).isEmpty());
		Assertions.assertNull(dao.oauth2RSD.getSessionById(rsd, false));
	}

	/** 没有 session 时读索引应该给空 map */
	@Test
	public void accessTokenIndex_noSession_empty() {
		Assertions.assertTrue(dao.getAccessTokenIndexMap_FromAdjustAfter("no", "no").isEmpty());
		dao.deleteAccessTokenIndex_BySingleData("no", "no", "at");
	}

	/** 删单个 AccessToken 索引，删光后 session 应该注销 */
	@Test
	public void accessTokenIndex_deleteSingleThenLogout() {
		AccessTokenModel at1 = at("at-x", 3600_000);
		AccessTokenModel at2 = at("at-y", 3600_000);
		dao.saveAccessTokenIndex_AndAdjust(at1, 12);
		dao.saveAccessTokenIndex_AndAdjust(at2, 12);
		dao.deleteAccessTokenIndex_BySingleData(OAuth2TestSupport.CLIENT_ID, OAuth2TestSupport.LOGIN_ID, "at-x");
		Assertions.assertEquals(Collections.singletonList("at-y"),
				dao.getAccessTokenValueList_FromAdjustAfter(OAuth2TestSupport.CLIENT_ID, OAuth2TestSupport.LOGIN_ID));
		dao.deleteAccessTokenIndex_BySingleData(OAuth2TestSupport.CLIENT_ID, OAuth2TestSupport.LOGIN_ID, "at-y");
		Assertions.assertTrue(dao.getAccessTokenValueList_FromAdjustAfter(OAuth2TestSupport.CLIENT_ID, OAuth2TestSupport.LOGIN_ID).isEmpty());
		dao.deleteAccessTokenIndex(OAuth2TestSupport.CLIENT_ID, OAuth2TestSupport.LOGIN_ID);
	}

	/** RefreshToken 保存/读取/删除 */
	@Test
	public void refreshToken_saveGetDelete() {
		dao.saveRefreshToken(null);
		dao.deleteRefreshToken(null);
		Assertions.assertNull(dao.getRefreshToken(null));
		RefreshTokenModel rt = rt("rt-1", 3600_000);
		dao.saveRefreshToken(rt);
		Assertions.assertEquals("rt-1", dao.getRefreshToken("rt-1").refreshToken);
		dao.deleteRefreshToken("rt-1");
		Assertions.assertNull(dao.getRefreshToken("rt-1"));
	}

	/** RefreshToken 索引溢出、删除、永不过期 */
	@Test
	public void refreshTokenIndex() {
		dao.saveRefreshTokenIndex_AndAdjust(null, 1);
		RefreshTokenModel rt1 = rt("rt-old", 3600_000);
		RefreshTokenModel rt2 = rt("rt-new", 3600_000);
		dao.saveRefreshToken(rt1);
		dao.saveRefreshTokenIndex_AndAdjust(rt1, 1);
		dao.saveRefreshToken(rt2);
		dao.saveRefreshTokenIndex_AndAdjust(rt2, 1);
		Assertions.assertNull(dao.getRefreshToken("rt-old"));
		Assertions.assertEquals(Collections.singletonList("rt-new"),
				dao.getRefreshTokenValueList_FromAdjustAfter(OAuth2TestSupport.CLIENT_ID, OAuth2TestSupport.LOGIN_ID));

		dao.deleteRefreshTokenIndex_BySingleData("no", "no", "rt");
		dao.deleteRefreshTokenIndex_BySingleData(OAuth2TestSupport.CLIENT_ID, OAuth2TestSupport.LOGIN_ID, "rt-new");
		dao.deleteRefreshTokenIndex(OAuth2TestSupport.CLIENT_ID, OAuth2TestSupport.LOGIN_ID);

		RefreshTokenModel forever = rt("rt-forever", 0);
		forever.expiresTime = SaTokenDao.NEVER_EXPIRE;
		dao.saveRefreshToken(forever);
		dao.saveRefreshTokenIndex_AndAdjust(forever, 12);
		Assertions.assertTrue(dao.getRefreshTokenIndexMap_FromAdjustAfter(OAuth2TestSupport.CLIENT_ID, OAuth2TestSupport.LOGIN_ID).containsKey("rt-forever"));
	}

	/** ClientToken 保存/读取/删除 */
	@Test
	public void clientToken_saveGetDelete() {
		dao.saveClientToken(null);
		dao.deleteClientToken(null);
		Assertions.assertNull(dao.getClientToken(null));
		ClientTokenModel ct = ct("ct-1", 3600_000);
		dao.saveClientToken(ct);
		Assertions.assertEquals("ct-1", dao.getClientToken("ct-1").clientToken);
		dao.deleteClientToken("ct-1");
		Assertions.assertNull(dao.getClientToken("ct-1"));
	}

	/** ClientToken 索引溢出、删除、永不过期 */
	@Test
	public void clientTokenIndex() {
		dao.saveClientTokenIndex_AndAdjust(null, 1);
		ClientTokenModel ct1 = ct("ct-old", 3600_000);
		ClientTokenModel ct2 = ct("ct-new", 3600_000);
		dao.saveClientToken(ct1);
		dao.saveClientTokenIndex_AndAdjust(ct1, 1);
		dao.saveClientToken(ct2);
		dao.saveClientTokenIndex_AndAdjust(ct2, 1);
		Assertions.assertNull(dao.getClientToken("ct-old"));
		Assertions.assertEquals(Collections.singletonList("ct-new"),
				dao.getClientTokenValueList_FromAdjustAfter(OAuth2TestSupport.CLIENT_ID));

		dao.deleteClientTokenIndex_BySingleData("no", "ct");
		dao.deleteClientTokenIndex_BySingleData(OAuth2TestSupport.CLIENT_ID, "ct-new");
		dao.deleteClientTokenIndex(OAuth2TestSupport.CLIENT_ID);

		ClientTokenModel forever = ct("ct-forever", 0);
		forever.expiresTime = SaTokenDao.NEVER_EXPIRE;
		dao.saveClientToken(forever);
		dao.saveClientTokenIndex_AndAdjust(forever, 12);
		Assertions.assertTrue(dao.getClientTokenIndexMap_FromAdjustAfter(OAuth2TestSupport.CLIENT_ID, OAuth2TestSupport.LOGIN_ID).containsKey("ct-forever"));
	}

	/** 授权记录空列表/null 不该写入，有 scope 才能存取删 */
	@Test
	public void grantScope() {
		dao.saveGrantScope(OAuth2TestSupport.CLIENT_ID, OAuth2TestSupport.LOGIN_ID, null);
		Assertions.assertTrue(dao.getGrantScope(OAuth2TestSupport.CLIENT_ID, OAuth2TestSupport.LOGIN_ID).isEmpty());
		dao.saveGrantScope(OAuth2TestSupport.CLIENT_ID, OAuth2TestSupport.LOGIN_ID, Collections.emptyList());
		dao.saveGrantScope(OAuth2TestSupport.CLIENT_ID, OAuth2TestSupport.LOGIN_ID, Arrays.asList("openid", "userid"));
		Assertions.assertEquals(Arrays.asList("openid", "userid"),
				dao.getGrantScope(OAuth2TestSupport.CLIENT_ID, OAuth2TestSupport.LOGIN_ID));
		dao.deleteGrantScope(OAuth2TestSupport.CLIENT_ID, OAuth2TestSupport.LOGIN_ID);
		Assertions.assertTrue(dao.getGrantScope(OAuth2TestSupport.CLIENT_ID, OAuth2TestSupport.LOGIN_ID).isEmpty());
	}

	/** state 空的不存，有值才能存取删 */
	@Test
	public void state_saveGetDelete() {
		dao.saveState(null);
		dao.saveState("");
		Assertions.assertNull(dao.getState(null));
		Assertions.assertNull(dao.getState(""));
		dao.saveState("st-1");
		Assertions.assertEquals("st-1", dao.getState("st-1"));
		dao.deleteState("st-1");
		Assertions.assertNull(dao.getState("st-1"));
	}

	/** code-nonce 索引：没 nonce 不存，有就能取 */
	@Test
	public void codeNonceIndex() {
		dao.saveCodeNonceIndex(null);
		CodeModel noNonce = code("c-n0");
		dao.saveCodeNonceIndex(noNonce);
		Assertions.assertNull(dao.getNonce("c-n0"));
		Assertions.assertNull(dao.getNonce(null));
		Assertions.assertNull(dao.getNonce(""));
		CodeModel withNonce = code("c-n1");
		withNonce.nonce = "nonce-1";
		dao.saveCodeNonceIndex(withNonce);
		Assertions.assertEquals("nonce-1", dao.getNonce("c-n1"));
	}

	/** splicing* 拼出来的 key 应该带上约定前缀 */
	@Test
	public void splicingKeys() {
		String tokenName = dao.getSaTokenConfig().getTokenName();
		Assertions.assertEquals(tokenName + ":oauth2:code:c1", dao.splicingCodeSaveKey("c1"));
		Assertions.assertEquals(tokenName + ":oauth2:code-index:1001:10001",
				dao.splicingCodeIndexKey("1001", 10001));
		Assertions.assertEquals(tokenName + ":oauth2:access-token:at", dao.splicingAccessTokenSaveKey("at"));
		Assertions.assertEquals("access-token:1001:10001", dao.splicingAccessTokenRSDValue("1001", 10001));
		Assertions.assertEquals(tokenName + ":oauth2:refresh-token:rt", dao.splicingRefreshTokenSaveKey("rt"));
		Assertions.assertEquals("refresh-token:1001:10001", dao.splicingRefreshTokenRSDValue("1001", 10001));
		Assertions.assertEquals(tokenName + ":oauth2:client-token:ct", dao.splicingClientTokenSaveKey("ct"));
		Assertions.assertEquals("client-token:1001", dao.splicingClientTokenRSDValue("1001"));
		Assertions.assertEquals(tokenName + ":oauth2:grant-scope:1001:10001", dao.splicingGrantScopeKey("1001", 10001));
		Assertions.assertEquals(tokenName + ":oauth2:state:st", dao.splicingStateSaveKey("st"));
		Assertions.assertEquals(tokenName + ":oauth2:code-nonce-index:c1", dao.splicingCodeNonceIndexSaveKey("c1"));
	}

	/** getSaTokenDao / getSaTokenConfig 应该代理到 SaManager */
	@Test
	public void getSaTokenDaoAndConfig() {
		Assertions.assertSame(SaManager.getSaTokenDao(), dao.getSaTokenDao());
		Assertions.assertSame(SaManager.getConfig(), dao.getSaTokenConfig());
	}

	/** 造一个 CodeModel */
	private CodeModel code(String value) {
		return new CodeModel(value, OAuth2TestSupport.CLIENT_ID, Collections.singletonList("openid"),
				OAuth2TestSupport.LOGIN_ID, OAuth2TestSupport.REDIRECT, null);
	}

	/** 造一个 AccessTokenModel，ttlMs 是剩余毫秒 */
	private AccessTokenModel at(String value, long ttlMs) {
		AccessTokenModel at = new AccessTokenModel(value, OAuth2TestSupport.CLIENT_ID, OAuth2TestSupport.LOGIN_ID,
				Collections.singletonList("openid"));
		at.expiresTime = ttlMs <= 0 ? SaTokenDao.NEVER_EXPIRE : System.currentTimeMillis() + ttlMs;
		return at;
	}

	/** 造一个 RefreshTokenModel */
	private RefreshTokenModel rt(String value, long ttlMs) {
		RefreshTokenModel rt = new RefreshTokenModel();
		rt.refreshToken = value;
		rt.clientId = OAuth2TestSupport.CLIENT_ID;
		rt.loginId = OAuth2TestSupport.LOGIN_ID;
		rt.scopes = Collections.singletonList("openid");
		rt.expiresTime = ttlMs <= 0 ? SaTokenDao.NEVER_EXPIRE : System.currentTimeMillis() + ttlMs;
		return rt;
	}

	/** 造一个 ClientTokenModel */
	private ClientTokenModel ct(String value, long ttlMs) {
		ClientTokenModel ct = new ClientTokenModel(value, OAuth2TestSupport.CLIENT_ID, Collections.singletonList("openid"));
		ct.expiresTime = ttlMs <= 0 ? SaTokenDao.NEVER_EXPIRE : System.currentTimeMillis() + ttlMs;
		return ct;
	}

}
