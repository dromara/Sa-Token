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
package cn.dev33.satoken.core.session.raw;

import cn.dev33.satoken.session.SaSession;
import cn.dev33.satoken.session.raw.SaRawSessionDelegator;
import cn.dev33.satoken.session.raw.SaRawSessionUtil;
import cn.dev33.satoken.test.SaTokenTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * SaRawSessionUtil 与 SaRawSessionDelegator 测试
 *
 * @author click33
 * @since 1.46.0
 */
@SaTokenTest
public class SaRawSessionUtilTest {

	/** Raw Session 应按类型和标识创建、查询及删除 */
	@Test
	void rawSessionLifecycle() {
		String type = "cart";
		long valueId = 10001;

		Assertions.assertEquals("satoken:raw-session:cart:10001", SaRawSessionUtil.splicingSessionKey(type, valueId));
		Assertions.assertFalse(SaRawSessionUtil.isExists(type, valueId));
		Assertions.assertNull(SaRawSessionUtil.getSessionById(type, valueId, false));

		SaSession session = SaRawSessionUtil.getSessionById(type, valueId);
		Assertions.assertEquals(type, session.getType());
		Assertions.assertTrue(SaRawSessionUtil.isExists(type, valueId));
		Assertions.assertSame(session, SaRawSessionUtil.getSessionById(type, valueId, false));

		SaRawSessionUtil.deleteSessionById(type, valueId);
		Assertions.assertFalse(SaRawSessionUtil.isExists(type, valueId));
	}

	/** 委托对象应使用其指定的 Raw Session 类型 */
	@Test
	void delegatorUsesConfiguredType() {
		SaRawSessionDelegator delegator = new SaRawSessionDelegator("notice");

		Assertions.assertFalse(delegator.isExists("u10002"));
		SaSession session = delegator.getSessionById("u10002");
		Assertions.assertEquals("notice", session.getType());
		Assertions.assertSame(session, delegator.getSessionById("u10002", false));

		delegator.deleteSessionById("u10002");
		Assertions.assertNull(delegator.getSessionById("u10002", false));
	}

}
