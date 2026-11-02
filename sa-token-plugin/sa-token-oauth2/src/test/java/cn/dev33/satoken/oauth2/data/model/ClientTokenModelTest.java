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
 * ClientTokenModel：无参/三参构造、剩余有效期、getter/setter、toString
 */
public class ClientTokenModelTest {

	/** 无参构造应该带上 createTime */
	@Test
	public void noArgCtor_fillsCreateTime() {
		long before = System.currentTimeMillis();
		ClientTokenModel m = new ClientTokenModel();
		Assertions.assertTrue(m.getCreateTime() >= before);
	}

	/** 三参构造应该把 token、client、scopes 塞进去 */
	@Test
	public void threeArgCtor_setsFields() {
		ClientTokenModel m = new ClientTokenModel("ct", "1001", Arrays.asList("userinfo"));
		Assertions.assertEquals("ct", m.getClientToken());
		Assertions.assertEquals("1001", m.getClientId());
		Assertions.assertEquals(Arrays.asList("userinfo"), m.getScopes());
	}

	/** setter 应该连缀写回，getter 能读到 */
	@Test
	public void gettersAndSetters_roundTrip() {
		Map<String, Object> extra = new LinkedHashMap<>();
		extra.put("k", "v");
		ClientTokenModel m = new ClientTokenModel()
				.setClientToken("ct")
				.setExpiresTime(9)
				.setClientId("c")
				.setScopes(Arrays.asList("a"))
				.setTokenType("Bearer")
				.setGrantType("client_credentials")
				.setExtraData(extra)
				.setCreateTime(123L);
		Assertions.assertEquals("ct", m.getClientToken());
		Assertions.assertEquals(9, m.getExpiresTime());
		Assertions.assertEquals("c", m.getClientId());
		Assertions.assertEquals(Arrays.asList("a"), m.getScopes());
		Assertions.assertEquals("Bearer", m.getTokenType());
		Assertions.assertEquals("client_credentials", m.getGrantType());
		Assertions.assertSame(extra, m.getExtraData());
		Assertions.assertEquals(123L, m.getCreateTime());
	}

	/** 永不过期时 getExpiresIn 应该返回 NEVER_EXPIRE */
	@Test
	public void getExpiresIn_neverExpire() {
		ClientTokenModel m = new ClientTokenModel().setExpiresTime(SaTokenDao.NEVER_EXPIRE);
		Assertions.assertEquals(SaTokenDao.NEVER_EXPIRE, m.getExpiresIn());
	}

	/** 已经过期时 getExpiresIn 应该返回 NOT_VALUE_EXPIRE */
	@Test
	public void getExpiresIn_alreadyExpired() {
		ClientTokenModel m = new ClientTokenModel().setExpiresTime(System.currentTimeMillis() - 1000);
		Assertions.assertEquals(SaTokenDao.NOT_VALUE_EXPIRE, m.getExpiresIn());
	}

	/** 还没过期时 getExpiresIn 应该给出大概剩余秒数 */
	@Test
	public void getExpiresIn_remainingSeconds() {
		ClientTokenModel m = new ClientTokenModel().setExpiresTime(System.currentTimeMillis() + 10_000);
		long in = m.getExpiresIn();
		Assertions.assertTrue(in >= 8 && in <= 10);
	}

	/** toString 里应该能看到 clientToken 和 clientId */
	@Test
	public void toString_containsMainFields() {
		String text = new ClientTokenModel("ct", "1001", Arrays.asList("userinfo")).toString();
		Assertions.assertTrue(text.startsWith("ClientTokenModel{"));
		Assertions.assertTrue(text.contains("clientToken='ct"));
		Assertions.assertTrue(text.contains("clientId='1001"));
	}

}
