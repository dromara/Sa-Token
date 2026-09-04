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
package cn.dev33.satoken.core.temp;

import cn.dev33.satoken.SaManager;
import cn.dev33.satoken.dao.SaTokenDao;
import cn.dev33.satoken.exception.SaTokenException;
import cn.dev33.satoken.session.SaSession;
import cn.dev33.satoken.temp.SaTempTemplate;
import cn.dev33.satoken.test.SaTokenTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * SaTempTemplate 剩余路径覆盖
 */
@SaTokenTest
public class SaTempTemplateExtendedTest {

	/** 空 namespace 构造 SaTempTemplate 应抛出异常 */
	@Test
	void constructor_rejectsEmptyNamespace() {
		Assertions.assertThrows(SaTokenException.class, () -> new SaTempTemplate(""));
		Assertions.assertThrows(SaTokenException.class, () -> new SaTempTemplate(null));
	}

	/** 前缀不匹配时 parseToken 应返回 null */
	@Test
	void parseToken_wrongPrefix_returnsNull() {
		SaTempTemplate template = SaManager.getSaTempTemplate();
		String token = template.createToken("user-9001", 120, true);
		Assertions.assertNull(template.parseToken(token, "group-", String.class));
	}

	/** 无效 token 调用 deleteToken 应为无操作 */
	@Test
	void deleteToken_invalidToken_isNoOp() {
		SaTempTemplate template = SaManager.getSaTempTemplate();
		Assertions.assertDoesNotThrow(() -> template.deleteToken("invalid-temp-token"));
	}

	/** Session 不存在时 adjustIndex 应返回空 Map */
	@Test
	void adjustIndex_missingSession_returnsEmptyMap() {
		SaTempTemplate template = SaManager.getSaTempTemplate();
		Map<String, Long> index = template.adjustIndex("missing-value-id", null);
		Assertions.assertTrue(index.isEmpty());
	}

	/** 索引全部过期时 adjustIndex 应删除 Session */
	@Test
	void adjustIndex_allExpired_deletesSession() {
		SaTempTemplate template = SaManager.getSaTempTemplate();
		String value = "expire-all-9002";
		String token = template.createToken(value, 120, true);
		SaTokenDao dao = SaManager.getSaTokenDao();
		SaSession session = dao.getSession("satoken:raw-session:temp-token:" + value);
		Assertions.assertNotNull(session);

		Map<String, Long> expiredMap = new HashMap<>();
		expiredMap.put(token, System.currentTimeMillis() - 1000L);
		session.set(SaTempTemplate.TEMP_TOKEN_MAP, expiredMap);
		dao.updateSession(session);

		Map<String, Long> index = template.adjustIndex(value, null);
		Assertions.assertTrue(index.isEmpty());
		Assertions.assertNull(dao.getSession("satoken:raw-session:temp-token:" + value));
	}

	/** 前缀长度超过 32 时 parseToken 应抛出异常 */
	@Test
	void checkCutPrefixLength_rejectsLongPrefix() {
		SaTempTemplate template = SaManager.getSaTempTemplate();
		String token = template.createToken("9003", 120, true);
		String longPrefix = "01234567890123456789012345678901";
		Assertions.assertThrows(SaTokenException.class,
				() -> template.parseToken(token, longPrefix, String.class));
	}

}
