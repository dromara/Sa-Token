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
import cn.dev33.satoken.test.SaTokenTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * SaSameTemplate 同源系统身份认证测试
 *
 * @author click33
 * @since 1.46.0
 */
@SaTokenTest
public class SaSameTemplateTest {

	private final SaSameTemplate template = SaManager.getSaSameTemplate();

	/** 无 token 时 getToken 应自动创建 */
	@Test
	void getToken_createsTokenWhenMissing() {
		String token = template.getToken();
		Assertions.assertNotNull(token);
		Assertions.assertEquals(64, token.length());
		Assertions.assertEquals(token, template.getTokenNh());
	}

	/** isValid/checkToken 应校验 token 有效性 */
	@Test
	void isValid_and_checkToken() {
		String token = template.refreshToken();
		Assertions.assertTrue(template.isValid(token));
		Assertions.assertDoesNotThrow(() -> template.checkToken(token));

		Assertions.assertFalse(template.isValid(null));
		Assertions.assertFalse(template.isValid(""));
		Assertions.assertFalse(template.isValid("invalid-token"));
		Assertions.assertThrows(SameTokenInvalidException.class, () -> template.checkToken("invalid-token"));
	}

	/** refreshToken 应保留旧 token 并生成新 token */
	@Test
	void refreshToken_savesPastToken() {
		String oldToken = template.refreshToken();
		String newToken = template.refreshToken();

		Assertions.assertNotEquals(oldToken, newToken);
		Assertions.assertEquals(newToken, template.getTokenNh());
		Assertions.assertTrue(template.isValid(oldToken));
		Assertions.assertTrue(template.isValid(newToken));
	}

	/** saveToken 应持久化 token 到 DAO */
	@Test
	void saveToken() {
		SaTokenDao dao = SaManager.getSaTokenDao();
		String key = template.splicingTokenSaveKey();

		template.saveToken("");
		Assertions.assertNull(dao.get(key));

		String token = template.createToken();
		template.saveToken(token);
		Assertions.assertEquals(token, dao.get(key));
	}

}
