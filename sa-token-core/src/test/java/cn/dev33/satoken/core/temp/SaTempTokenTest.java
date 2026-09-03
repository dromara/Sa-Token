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
import cn.dev33.satoken.temp.SaTempUtil;
import cn.dev33.satoken.test.SaTokenTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

/**
 * 临时 Token 模块测试
 */
@SaTokenTest
public class SaTempTokenTest {

	@Test
	public void createParseAndDeleteToken() {
		SaTokenDao dao = SaManager.getSaTokenDao();

		String token = SaTempUtil.createToken("group-1014", 200);
		Assertions.assertNotNull(token);
		Assertions.assertEquals("group-1014", dao.getObject("satoken:temp-token:" + token));

		Assertions.assertEquals("group-1014", SaTempUtil.parseToken(token, String.class));
		Assertions.assertEquals(1014L, SaTempUtil.parseToken(token, "group-", Long.class));
		Assertions.assertEquals("group-1014", SaTempUtil.parseToken(token));
		Assertions.assertEquals("group-1014", SaTempUtil.parseToken(token, String.class));

		long timeout = SaTempUtil.getTimeout(token);
		Assertions.assertTrue(timeout > 195);
		Assertions.assertTrue(timeout < 201);

		SaTempUtil.deleteToken(token);
		Assertions.assertNull(SaTempUtil.parseToken(token, String.class));
		Assertions.assertNull(dao.getObject("satoken:temp-token:" + token));
	}

	@Test
	public void tempTokenIndex() {
		SaTokenDao dao = SaManager.getSaTokenDao();

		String token1 = SaTempUtil.createToken("1001", 200, true);
		String token2 = SaTempUtil.createToken("1001", 300, true);
		String token3 = SaTempUtil.createToken("1001", 400, true);

		Assertions.assertNotNull(token1);
		Assertions.assertNotNull(token2);
		Assertions.assertNotNull(token3);

		Assertions.assertEquals("1001", SaTempUtil.parseToken(token1, String.class));
		Assertions.assertEquals("1001", SaTempUtil.parseToken(token2, String.class));
		Assertions.assertEquals("1001", SaTempUtil.parseToken(token3, String.class));

		Assertions.assertEquals("1001", dao.getObject("satoken:temp-token:" + token1));
		Assertions.assertEquals("1001", dao.getObject("satoken:temp-token:" + token2));
		Assertions.assertEquals("1001", dao.getObject("satoken:temp-token:" + token3));

		List<String> tempTokenList = SaTempUtil.getTempTokenList("1001");
		Assertions.assertEquals(3, tempTokenList.size());
		Assertions.assertTrue(tempTokenList.contains(token1));
		Assertions.assertTrue(tempTokenList.contains(token2));
		Assertions.assertTrue(tempTokenList.contains(token3));

		long sessionTimeout = dao.getSessionTimeout("satoken:raw-session:temp-token:" + "1001");
		Assertions.assertTrue(sessionTimeout > 395);
		Assertions.assertTrue(sessionTimeout < 401);

		SaTempUtil.deleteToken(token3);
		Assertions.assertNull(SaTempUtil.parseToken(token3, String.class));
		Assertions.assertNull(dao.getObject("satoken:temp-token:" + token3));

		List<String> tempTokenList2 = SaTempUtil.getTempTokenList("1001");
		Assertions.assertEquals(2, tempTokenList2.size());
		Assertions.assertFalse(tempTokenList2.contains(token3));

		long sessionTimeout2 = dao.getSessionTimeout("satoken:raw-session:temp-token:" + "1001");
		Assertions.assertTrue(sessionTimeout2 > 295);
		Assertions.assertTrue(sessionTimeout2 < 301);

		String token4 = SaTempUtil.createToken("1001", -1, true);
		Assertions.assertEquals("1001", SaTempUtil.parseToken(token4, String.class));

		List<String> tempTokenList3 = SaTempUtil.getTempTokenList("1001");
		Assertions.assertEquals(3, tempTokenList3.size());
		Assertions.assertTrue(tempTokenList3.contains(token4));

		long sessionTimeout4 = dao.getSessionTimeout("satoken:raw-session:temp-token:" + "1001");
		Assertions.assertEquals(-1, sessionTimeout4);
	}

	@Test
	public void jwtSecretKey_defaultsToNull() {
		Assertions.assertNull(SaManager.getSaTempTemplate().getJwtSecretKey());
	}

}
