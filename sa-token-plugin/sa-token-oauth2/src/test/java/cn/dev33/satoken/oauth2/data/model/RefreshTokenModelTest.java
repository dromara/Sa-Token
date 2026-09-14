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
 * RefreshTokenModel：构造、getter/setter、剩余有效期、toString
 */
public class RefreshTokenModelTest {

	/** 无参构造应该带上 createTime */
	@Test
	public void noArgCtor_fillsCreateTime() {
		long before = System.currentTimeMillis();
		RefreshTokenModel m = new RefreshTokenModel();
		Assertions.assertTrue(m.getCreateTime() >= before);
	}

	/** setter 应该连缀写回，getter 能读到 */
	@Test
	public void gettersAndSetters_roundTrip() {
		Map<String, Object> extra = new LinkedHashMap<>();
		extra.put("k", "v");
		RefreshTokenModel m = new RefreshTokenModel()
				.setRefreshToken("rt")
				.setExpiresTime(9)
				.setClientId("c")
				.setLoginId("u")
				.setScopes(Arrays.asList("a"))
				.setExtraData(extra)
				.setCreateTime(123L);
		Assertions.assertEquals("rt", m.getRefreshToken());
		Assertions.assertEquals(9, m.getExpiresTime());
		Assertions.assertEquals("c", m.getClientId());
		Assertions.assertEquals("u", m.getLoginId());
		Assertions.assertEquals(Arrays.asList("a"), m.getScopes());
		Assertions.assertSame(extra, m.getExtraData());
		Assertions.assertEquals(123L, m.getCreateTime());
	}

	/** 永不过期时 getExpiresIn 应该返回 NEVER_EXPIRE */
	@Test
	public void getExpiresIn_neverExpire() {
		RefreshTokenModel m = new RefreshTokenModel().setExpiresTime(SaTokenDao.NEVER_EXPIRE);
		Assertions.assertEquals(SaTokenDao.NEVER_EXPIRE, m.getExpiresIn());
	}

	/** 已经过期时 getExpiresIn 应该返回 NOT_VALUE_EXPIRE */
	@Test
	public void getExpiresIn_alreadyExpired() {
		RefreshTokenModel m = new RefreshTokenModel().setExpiresTime(System.currentTimeMillis() - 1000);
		Assertions.assertEquals(SaTokenDao.NOT_VALUE_EXPIRE, m.getExpiresIn());
	}

	/** 还没过期时 getExpiresIn 应该给出大概剩余秒数 */
	@Test
	public void getExpiresIn_remainingSeconds() {
		RefreshTokenModel m = new RefreshTokenModel().setExpiresTime(System.currentTimeMillis() + 10_000);
		long in = m.getExpiresIn();
		Assertions.assertTrue(in >= 8 && in <= 10);
	}

	/** toString 里应该能看到 refreshToken 和 clientId */
	@Test
	public void toString_containsMainFields() {
		String text = new RefreshTokenModel().setRefreshToken("rt").setClientId("c").toString();
		Assertions.assertTrue(text.startsWith("RefreshTokenModel ["));
		Assertions.assertTrue(text.contains("refreshToken=rt"));
		Assertions.assertTrue(text.contains("clientId=c"));
	}

}
