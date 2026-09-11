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
package cn.dev33.satoken.core.stp;

import cn.dev33.satoken.SaManager;
import cn.dev33.satoken.context.SaHolder;
import cn.dev33.satoken.context.mock.SaRequestForMock;
import cn.dev33.satoken.context.mock.SaTokenContextMockUtil;
import cn.dev33.satoken.dao.SaTokenDao;
import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.stp.StpLogic;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.test.SaMockAssert;
import cn.dev33.satoken.test.SaTokenTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * 顶号 / 踢人 / 续期按步骤核对 DAO 和下次请求，不只看最终 isLogin。
 *
 * @author click33
 * @since 1.46.0
 */
@SaTokenTest
public class StpLogicSessionSequenceTest {

	/** isConcurrent=false 时二次登录应按步顶掉旧 Token */
	@Test
	void isConcurrentFalse_replace_steps() {
		SaManager.getConfig().setIsConcurrent(false);
		SaManager.getConfig().setIsWriteHeader(true);
		SaTokenDao dao = SaManager.getSaTokenDao();
		StpLogic stpLogic = StpUtil.getStpLogic();
		String tokenName = StpUtil.getTokenName();

		String t1 = SaTokenContextMockUtil.setMockContext(() -> {
			StpUtil.login(10001);
			String token = StpUtil.getTokenValue();
			assertTokenMapsToLoginId(dao, token, "10001");
			SaMockAssert.current()
					.assertCookieEquals(tokenName, token)
					.assertHeaderEquals(tokenName, token);
			return token;
		});

		String t2 = SaTokenContextMockUtil.setMockContext(() -> {
			StpUtil.login(10001);
			String token = StpUtil.getTokenValue();
			Assertions.assertNotEquals(t1, token);
			Assertions.assertEquals(NotLoginException.BE_REPLACED, dao.get(stpLogic.splicingKeyTokenValue(t1)));
			assertTokenMapsToLoginId(dao, token, "10001");
			SaMockAssert.current()
					.assertCookieEquals(tokenName, token)
					.assertHeaderEquals(tokenName, token);
			return token;
		});

		SaTokenContextMockUtil.setMockContext(() -> {
			SaRequestForMock req = (SaRequestForMock) SaHolder.getRequest();
			req.method = "GET";
			req.headerMap.put(tokenName, t1);
			req.parameterMap.put("from", "old-device");
			SaMockAssert.current()
					.assertMethodEquals("GET")
					.assertQueryEquals("from", "old-device")
					.assertRequestHeaderEquals(tokenName, t1);
			NotLoginException e = Assertions.assertThrows(NotLoginException.class, StpUtil::checkLogin);
			Assertions.assertEquals(NotLoginException.BE_REPLACED, e.getType());
		});

		SaTokenContextMockUtil.setMockContext(() -> {
			((SaRequestForMock) SaHolder.getRequest()).headerMap.put(tokenName, t2);
			Assertions.assertDoesNotThrow(StpUtil::checkLogin);
			Assertions.assertEquals("10001", StpUtil.getLoginIdAsString());
		});
	}

	/** kickout 后旧 Token 应标成 KICK_OUT，下次带着它请求要失败 */
	@Test
	void kickout_steps() {
		SaTokenDao dao = SaManager.getSaTokenDao();
		StpLogic stpLogic = StpUtil.getStpLogic();
		String tokenName = StpUtil.getTokenName();

		String t1 = SaTokenContextMockUtil.setMockContext(() -> {
			StpUtil.login(10002);
			String token = StpUtil.getTokenValue();
			assertTokenMapsToLoginId(dao, token, "10002");
			return token;
		});

		SaTokenContextMockUtil.setMockContext(() -> {
			StpUtil.kickout(10002);
			Assertions.assertEquals(NotLoginException.KICK_OUT, dao.get(stpLogic.splicingKeyTokenValue(t1)));
		});

		SaTokenContextMockUtil.setMockContext(() -> {
			((SaRequestForMock) SaHolder.getRequest()).headerMap.put(tokenName, t1);
			NotLoginException e = Assertions.assertThrows(NotLoginException.class, StpUtil::checkLogin);
			Assertions.assertEquals(NotLoginException.KICK_OUT, e.getType());
		});
	}

	/** renewTimeout 后 DAO 超时应变长，Token 映射还在，下次 checkLogin 仍能过 */
	@Test
	void renewTimeout_steps() {
		SaTokenDao dao = SaManager.getSaTokenDao();
		StpLogic stpLogic = StpUtil.getStpLogic();
		String tokenName = StpUtil.getTokenName();

		String t1 = SaTokenContextMockUtil.setMockContext(() -> {
			StpUtil.login(10003, 100);
			String token = StpUtil.getTokenValue();
			long timeout = dao.getTimeout(stpLogic.splicingKeyTokenValue(token));
			Assertions.assertTrue(timeout <= 100 && timeout >= 95);
			assertTokenMapsToLoginId(dao, token, "10003");
			StpUtil.renewTimeout(400);
			long renewed = dao.getTimeout(stpLogic.splicingKeyTokenValue(token));
			Assertions.assertTrue(renewed <= 400 && renewed >= 395);
			assertTokenMapsToLoginId(dao, token, "10003");
			SaMockAssert.current().assertCookieEquals(tokenName, token);
			return token;
		});

		SaTokenContextMockUtil.setMockContext(() -> {
			((SaRequestForMock) SaHolder.getRequest()).headerMap.put(tokenName, t1);
			Assertions.assertDoesNotThrow(StpUtil::checkLogin);
			long timeout = dao.getTimeout(stpLogic.splicingKeyTokenValue(t1));
			Assertions.assertTrue(timeout <= 400 && timeout >= 390);
		});
	}

	/** DAO 里 token 映射应该指向这个 loginId */
	private void assertTokenMapsToLoginId(SaTokenDao dao, String token, String loginId) {
		Assertions.assertEquals(loginId, dao.get(StpUtil.getStpLogic().splicingKeyTokenValue(token)));
	}

}
