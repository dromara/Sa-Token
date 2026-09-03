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
package cn.dev33.satoken.core.session;

import cn.dev33.satoken.SaManager;
import cn.dev33.satoken.dao.SaTokenDao;
import cn.dev33.satoken.session.SaSession;
import cn.dev33.satoken.session.SaSessionCustomUtil;
import cn.dev33.satoken.test.SaTokenTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * SaSessionCustomUtil 测试
 */
@SaTokenTest
public class SaSessionCustomUtilTest {

	@Test
	public void customSession_createExistsAndDelete() {
		SaTokenDao dao = SaManager.getSaTokenDao();
		String sessionId = "art-1";
		String sessionKey = SaSessionCustomUtil.splicingSessionKey(sessionId);

		Assertions.assertFalse(SaSessionCustomUtil.isExists(sessionId));
		Assertions.assertNull(dao.getSession(sessionKey));

		SaSessionCustomUtil.getSessionById(sessionId);
		SaSessionCustomUtil.getSessionById(sessionId, false);

		Assertions.assertTrue(SaSessionCustomUtil.isExists(sessionId));
		Assertions.assertNotNull(dao.getSession(sessionKey));

		SaSessionCustomUtil.deleteSessionById(sessionId);

		Assertions.assertFalse(SaSessionCustomUtil.isExists(sessionId));
		Assertions.assertNull(dao.getSession(sessionKey));

		String notCreateId = "art-4";
		SaSessionCustomUtil.getSessionById(notCreateId, false);
		Assertions.assertFalse(SaSessionCustomUtil.isExists(notCreateId));
		Assertions.assertNull(dao.getSession(SaSessionCustomUtil.splicingSessionKey(notCreateId)));
	}

}
