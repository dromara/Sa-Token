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
package cn.dev33.satoken.core.same;

import cn.dev33.satoken.SaManager;
import cn.dev33.satoken.dao.SaTokenDao;
import cn.dev33.satoken.exception.SameTokenInvalidException;
import cn.dev33.satoken.same.SaSameTemplate;
import cn.dev33.satoken.same.SaSameUtil;
import cn.dev33.satoken.test.SaTokenTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * SaSameUtil 同源系统身份认证门面测试
 *
 * @author click33
 * @since 1.46.0
 */
@SaTokenTest
public class SaSameUtilTest {

	private final SaSameTemplate template = SaManager.getSaSameTemplate();

	/** SAME_TOKEN 常量应与 Template 一致 */
	@Test
	void sameTokenConstant() {
		Assertions.assertEquals(SaSameTemplate.SAME_TOKEN, SaSameUtil.SAME_TOKEN);
	}

	/** getToken / isValid / checkToken 应正确委托 */
	@Test
	void getToken_and_checkToken() {
		String token = SaSameUtil.getToken();
		Assertions.assertNotNull(token);
		Assertions.assertTrue(SaSameUtil.isValid(token));
		Assertions.assertDoesNotThrow(() -> SaSameUtil.checkToken(token));
		Assertions.assertThrows(SameTokenInvalidException.class, () -> SaSameUtil.checkToken("bad-token"));
	}

	/** refreshToken / getTokenNh / getPastTokenNh 应正确委托 */
	@Test
	void refresh_and_getTokenNh() {
		String oldToken = SaSameUtil.refreshToken();
		String newToken = SaSameUtil.refreshToken();
		Assertions.assertNotEquals(oldToken, newToken);
		Assertions.assertEquals(newToken, SaSameUtil.getTokenNh());
		Assertions.assertNotNull(SaSameUtil.getPastTokenNh());
	}

	/** saveToken 后 getToken 应能读取 */
	@Test
	void saveToken_persistsToDao() {
		SaTokenDao dao = SaManager.getSaTokenDao();
		String key = template.splicingTokenSaveKey();
		String token = template.createToken();
		template.saveToken(token);
		Assertions.assertEquals(token, dao.get(key));
		Assertions.assertEquals(token, SaSameUtil.getTokenNh());
	}

}
