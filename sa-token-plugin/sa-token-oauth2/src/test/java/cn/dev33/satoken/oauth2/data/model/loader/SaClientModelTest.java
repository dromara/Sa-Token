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
package cn.dev33.satoken.oauth2.data.model.loader;

import cn.dev33.satoken.oauth2.support.OAuth2TestSupport;
import cn.dev33.satoken.oauth2.support.OAuth2Test;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

/**
 * Client 模型：无参走策略默认值、四参构造、list 为 null 时 add*、getter/setter、toString
 */
@OAuth2Test
public class SaClientModelTest {

	/** 每个用例前先装默认配置，无参构造才能从全局配置抄超时 */
	@BeforeEach
	public void reset() {
		OAuth2TestSupport.installDefaultConfig();
	}

	/** 无参构造应该从 ServerConfig 抄一套默认超时和数量 */
	@Test
	public void noArgCtor_copiesStrategyDefaults() {
		SaClientModel m = new SaClientModel();
		Assertions.assertEquals(7200, m.getAccessTokenTimeout());
		Assertions.assertEquals(2592000, m.getRefreshTokenTimeout());
		Assertions.assertEquals(7200, m.getClientTokenTimeout());
		Assertions.assertEquals(12, m.getMaxAccessTokenCount());
		Assertions.assertEquals(12, m.getMaxRefreshTokenCount());
		Assertions.assertEquals(12, m.getMaxClientTokenCount());
		Assertions.assertEquals(Boolean.FALSE, m.getIsNewRefresh());
		Assertions.assertEquals(Boolean.FALSE, m.getIsAutoConfirm());
		Assertions.assertNotNull(m.getContractScopes());
		Assertions.assertNotNull(m.getAllowRedirectUris());
		Assertions.assertNotNull(m.getAllowGrantTypes());
	}

	/** 四参构造应该把 id、secret、签约权限、回调地址塞进去 */
	@Test
	public void fourArgCtor_setsFields() {
		List<String> scopes = Arrays.asList("userinfo");
		List<String> uris = Arrays.asList("http://c/cb");
		SaClientModel m = new SaClientModel("1001", "secret", scopes, uris);
		Assertions.assertEquals("1001", m.getClientId());
		Assertions.assertEquals("secret", m.getClientSecret());
		Assertions.assertSame(scopes, m.getContractScopes());
		Assertions.assertSame(uris, m.getAllowRedirectUris());
	}

	/** 三个 list 被置空后再 add，应该自己 new 再追加 */
	@Test
	public void addXxx_whenListsNull_createsThenAppends() {
		SaClientModel m = new SaClientModel();
		m.setContractScopes(null);
		m.setAllowRedirectUris(null);
		m.setAllowGrantTypes(null);
		m.addContractScopes("openid", "userinfo");
		m.addAllowRedirectUris("http://a.com", "http://b.com");
		m.addAllowGrantTypes("password", "implicit");
		Assertions.assertEquals(Arrays.asList("openid", "userinfo"), m.getContractScopes());
		Assertions.assertEquals(Arrays.asList("http://a.com", "http://b.com"), m.getAllowRedirectUris());
		Assertions.assertEquals(Arrays.asList("password", "implicit"), m.getAllowGrantTypes());
	}

	/** 三个 list 本来就有值时 add 应该往后面追加 */
	@Test
	public void addXxx_whenListsExist_appends() {
		SaClientModel m = new SaClientModel()
				.addContractScopes("a")
				.addAllowRedirectUris("http://a")
				.addAllowGrantTypes("password");
		m.addContractScopes("b");
		m.addAllowRedirectUris("http://b");
		m.addAllowGrantTypes("implicit");
		Assertions.assertTrue(m.getContractScopes().contains("a"));
		Assertions.assertTrue(m.getContractScopes().contains("b"));
		Assertions.assertTrue(m.getAllowRedirectUris().contains("http://b"));
		Assertions.assertTrue(m.getAllowGrantTypes().contains("implicit"));
	}

	/** setter 应该能连缀写回，isAutoConfirm 也要点一下 */
	@Test
	public void gettersAndSetters_roundTrip() {
		List<String> scopes = Arrays.asList("userinfo");
		List<String> uris = Arrays.asList("http://c/cb");
		List<String> grants = Arrays.asList("password");
		SaClientModel m = new SaClientModel()
				.setClientId("cid")
				.setClientSecret("sec")
				.setContractScopes(scopes)
				.setAllowRedirectUris(uris)
				.setAllowGrantTypes(grants)
				.setSubjectId("subj")
				.setAccessTokenTimeout(11)
				.setRefreshTokenTimeout(22)
				.setClientTokenTimeout(33)
				.setMaxAccessTokenCount(1)
				.setMaxRefreshTokenCount(2)
				.setMaxClientTokenCount(3)
				.setIsNewRefresh(true)
				.setIsAutoConfirm(true);
		Assertions.assertEquals("cid", m.getClientId());
		Assertions.assertEquals("sec", m.getClientSecret());
		Assertions.assertSame(scopes, m.getContractScopes());
		Assertions.assertSame(uris, m.getAllowRedirectUris());
		Assertions.assertSame(grants, m.getAllowGrantTypes());
		Assertions.assertEquals("subj", m.getSubjectId());
		Assertions.assertEquals(11, m.getAccessTokenTimeout());
		Assertions.assertEquals(22, m.getRefreshTokenTimeout());
		Assertions.assertEquals(33, m.getClientTokenTimeout());
		Assertions.assertEquals(1, m.getMaxAccessTokenCount());
		Assertions.assertEquals(2, m.getMaxRefreshTokenCount());
		Assertions.assertEquals(3, m.getMaxClientTokenCount());
		Assertions.assertEquals(Boolean.TRUE, m.getIsNewRefresh());
		Assertions.assertEquals(Boolean.TRUE, m.getIsAutoConfirm());
	}

	/** toString 里应该能看到 clientId 和 isAutoConfirm */
	@Test
	public void toString_containsMainFields() {
		String text = new SaClientModel().setClientId("1001").setIsAutoConfirm(true).toString();
		Assertions.assertTrue(text.startsWith("SaClientModel{"));
		Assertions.assertTrue(text.contains("clientId='1001'"));
		Assertions.assertTrue(text.contains("isAutoConfirm=true"));
		Assertions.assertTrue(text.contains("accessTokenTimeout="));
	}

}
