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
package cn.dev33.satoken.oauth2.data.loader;

import cn.dev33.satoken.oauth2.SaOAuth2Manager;
import cn.dev33.satoken.oauth2.error.SaOAuth2ErrorCode;
import cn.dev33.satoken.oauth2.exception.SaOAuth2ClientModelException;
import cn.dev33.satoken.oauth2.support.OAuth2TestSupport;
import cn.dev33.satoken.secure.SaSecureUtil;
import cn.dev33.satoken.test.SaTokenTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

/**
 * 默认 DataLoader：接口 default 方法走 DefaultImpl
 */
@SaTokenTest
public class SaOAuth2DataLoaderDefaultImplTest {

	private final SaOAuth2DataLoaderDefaultImpl loader = new SaOAuth2DataLoaderDefaultImpl();

	/** 每个用例前装默认 client */
	@BeforeEach
	public void reset() {
		OAuth2TestSupport.installDefaultConfig();
	}

	/** 已登记的 clientId 应该能拿到模型，没有的返回 null */
	@Test
	public void getClientModel_foundAndMissing() {
		Assertions.assertEquals(OAuth2TestSupport.CLIENT_ID, loader.getClientModel(OAuth2TestSupport.CLIENT_ID).getClientId());
		Assertions.assertNull(loader.getClientModel("no-such"));
	}

	/** 找不到 client 时 getClientModelNotNull 应该抛 30105 */
	@Test
	public void getClientModelNotNull_missing_throws30105() {
		Assertions.assertEquals(OAuth2TestSupport.CLIENT_ID, loader.getClientModelNotNull(OAuth2TestSupport.CLIENT_ID).getClientId());
		SaOAuth2ClientModelException ex = Assertions.assertThrows(SaOAuth2ClientModelException.class,
				() -> loader.getClientModelNotNull("no-such"));
		Assertions.assertEquals(SaOAuth2ErrorCode.CODE_30105, ex.getCode());
		Assertions.assertEquals("no-such", ex.getClientId());
	}

	/** getOpenid 应该按配置前缀 + clientId + loginId 做 md5 */
	@Test
	public void getOpenid_md5OfPrefix() {
		String prefix = SaOAuth2Manager.getServerConfig().getOpenidDigestPrefix();
		Assertions.assertEquals(
				SaSecureUtil.md5(prefix + "_" + OAuth2TestSupport.CLIENT_ID + "_" + OAuth2TestSupport.LOGIN_ID),
				loader.getOpenid(OAuth2TestSupport.CLIENT_ID, OAuth2TestSupport.LOGIN_ID));
	}

	/** getUnionid 应该按配置前缀 + subjectId + loginId 做 md5 */
	@Test
	public void getUnionid_md5OfPrefix() {
		String prefix = SaOAuth2Manager.getServerConfig().getUnionidDigestPrefix();
		Assertions.assertEquals(
				SaSecureUtil.md5(prefix + "_subj-1_" + OAuth2TestSupport.LOGIN_ID),
				loader.getUnionid("subj-1", OAuth2TestSupport.LOGIN_ID));
	}

	/** 没配高级/低级权限时应该给出空列表，配了逗号串就拆开 */
	@Test
	public void higherLowerScopeLists() {
		Assertions.assertTrue(loader.getHigherScopeList().isEmpty());
		Assertions.assertTrue(loader.getLowerScopeList().isEmpty());
		SaOAuth2Manager.getServerConfig().setHigherScope("admin,super");
		SaOAuth2Manager.getServerConfig().setLowerScope("user,guest");
		List<String> higher = loader.getHigherScopeList();
		List<String> lower = loader.getLowerScopeList();
		Assertions.assertTrue(higher.contains("admin"));
		Assertions.assertTrue(higher.contains("super"));
		Assertions.assertTrue(lower.contains("user"));
		Assertions.assertTrue(lower.contains("guest"));
	}

}
