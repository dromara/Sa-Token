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
package cn.dev33.satoken.oauth2.data.model;

import cn.dev33.satoken.dao.SaTokenDao;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * AccessTokenModel：构造、getter/setter、剩余有效期、toString
 */
public class AccessTokenModelTest {

	/** 无参构造应该带上 createTime */
	@Test
	public void noArgCtor_fillsCreateTime() {
		long before = System.currentTimeMillis();
		AccessTokenModel m = new AccessTokenModel();
		Assertions.assertTrue(m.getCreateTime() >= before);
	}

	/** 四参构造应该把 token、client、loginId、scopes 塞进去 */
	@Test
	public void fullCtor_setsFields() {
		AccessTokenModel m = new AccessTokenModel("at", "1001", 10001, Arrays.asList("userinfo"));
		Assertions.assertEquals("at", m.getAccessToken());
		Assertions.assertEquals("1001", m.getClientId());
		Assertions.assertEquals(10001, m.getLoginId());
		Assertions.assertEquals(Arrays.asList("userinfo"), m.getScopes());
		Assertions.assertTrue(m.getCreateTime() > 0);
	}

	/** setter 应该连缀写回，getter 能读到 */
	@Test
	public void gettersAndSetters_roundTrip() {
		Map<String, Object> extra = new LinkedHashMap<>();
		extra.put("k", "v");
		AccessTokenModel m = new AccessTokenModel()
				.setAccessToken("at")
				.setRefreshToken("rt")
				.setExpiresTime(1)
				.setRefreshExpiresTime(2)
				.setClientId("c")
				.setLoginId("u")
				.setScopes(Arrays.asList("a", "b"))
				.setTokenType("Bearer")
				.setGrantType("password")
				.setExtraData(extra)
				.setCreateTime(123L);
		Assertions.assertEquals("at", m.getAccessToken());
		Assertions.assertEquals("rt", m.getRefreshToken());
		Assertions.assertEquals(1, m.getExpiresTime());
		Assertions.assertEquals(2, m.getRefreshExpiresTime());
		Assertions.assertEquals("c", m.getClientId());
		Assertions.assertEquals("u", m.getLoginId());
		Assertions.assertEquals(Arrays.asList("a", "b"), m.getScopes());
		Assertions.assertEquals("Bearer", m.getTokenType());
		Assertions.assertEquals("password", m.getGrantType());
		Assertions.assertSame(extra, m.getExtraData());
		Assertions.assertEquals(123L, m.getCreateTime());
	}

	/** 永不过期时 getExpiresIn 应该返回 NEVER_EXPIRE */
	@Test
	public void getExpiresIn_neverExpire() {
		AccessTokenModel m = new AccessTokenModel().setExpiresTime(SaTokenDao.NEVER_EXPIRE);
		Assertions.assertEquals(SaTokenDao.NEVER_EXPIRE, m.getExpiresIn());
	}

	/** 已经过期时 getExpiresIn 应该返回 NOT_VALUE_EXPIRE */
	@Test
	public void getExpiresIn_alreadyExpired() {
		AccessTokenModel m = new AccessTokenModel().setExpiresTime(System.currentTimeMillis() - 1000);
		Assertions.assertEquals(SaTokenDao.NOT_VALUE_EXPIRE, m.getExpiresIn());
	}

	/** 还没过期时 getExpiresIn 应该给出大概剩余秒数 */
	@Test
	public void getExpiresIn_remainingSeconds() {
		AccessTokenModel m = new AccessTokenModel().setExpiresTime(System.currentTimeMillis() + 10_000);
		long in = m.getExpiresIn();
		Assertions.assertTrue(in >= 8 && in <= 10);
	}

	/** Refresh 永不过期时 getRefreshExpiresIn 应该返回 NEVER_EXPIRE */
	@Test
	public void getRefreshExpiresIn_neverExpire() {
		AccessTokenModel m = new AccessTokenModel().setRefreshExpiresTime(SaTokenDao.NEVER_EXPIRE);
		Assertions.assertEquals(SaTokenDao.NEVER_EXPIRE, m.getRefreshExpiresIn());
	}

	/** Refresh 已经过期时应该返回 NOT_VALUE_EXPIRE */
	@Test
	public void getRefreshExpiresIn_alreadyExpired() {
		AccessTokenModel m = new AccessTokenModel().setRefreshExpiresTime(System.currentTimeMillis() - 1000);
		Assertions.assertEquals(SaTokenDao.NOT_VALUE_EXPIRE, m.getRefreshExpiresIn());
	}

	/** Refresh 还没过期时应该给出大概剩余秒数 */
	@Test
	public void getRefreshExpiresIn_remainingSeconds() {
		AccessTokenModel m = new AccessTokenModel().setRefreshExpiresTime(System.currentTimeMillis() + 10_000);
		long in = m.getRefreshExpiresIn();
		Assertions.assertTrue(in >= 8 && in <= 10);
	}

	/** toString 里应该能看到 token 和 clientId */
	@Test
	public void toString_containsMainFields() {
		String text = new AccessTokenModel("at", "1001", 1, Arrays.asList("userinfo")).toString();
		Assertions.assertTrue(text.startsWith("AccessTokenModel{"));
		Assertions.assertTrue(text.contains("accessToken='at"));
		Assertions.assertTrue(text.contains("clientId='1001"));
		Assertions.assertTrue(text.contains("extraData="));
	}

}
