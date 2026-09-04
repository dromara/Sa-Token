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
import cn.dev33.satoken.session.SaSession;
import cn.dev33.satoken.session.SaTerminalInfo;
import cn.dev33.satoken.test.SaTokenTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * SaSession 剩余覆盖率补充测试
 *
 * @author click33
 * @since 1.46.0
 */
@SaTokenTest
public class SaSessionMoreTest {

	/** keys 应返回 DataMap 中所有键的集合 */
	@Test
	void keys_returnsDataMapKeys() {
		SaSession session = new SaSession("sid-keys");
		session.set("k1", 1);
		session.set("k2", 2);
		Set<String> keys = session.keys();
		Assertions.assertEquals(2, keys.size());
		Assertions.assertTrue(keys.contains("k1"));
		Assertions.assertTrue(keys.contains("k2"));
	}

	/** refreshDataMap 传入空 Map 应清空 Session 数据但保留 Session 本身 */
	@Test
	void refreshDataMap_withEmptyMap() {
		SaSession session = new SaSession("sid-refresh-empty");
		SaManager.getSaTokenDao().setSession(session, 3600);
		session.set("old", "value");
		session.refreshDataMap(new HashMap<>());
		Assertions.assertTrue(session.keys().isEmpty());
		Assertions.assertNotNull(SaManager.getSaTokenDao().getSession(session.getId()));
	}

	/** setDataMap 应替换底层 Map 并可通过 get 读取新数据 */
	@Test
	void setDataMap_replacesUnderlyingMap() {
		SaSession session = new SaSession("sid-set-map");
		Map<String, Object> newMap = new ConcurrentHashMap<>();
		newMap.put("newKey", "newValue");
		session.setDataMap(newMap);
		Assertions.assertSame(newMap, session.getDataMap());
		Assertions.assertEquals("newValue", session.get("newKey"));
	}

	/** clear 应移除 Session 中全部数据 */
	@Test
	void clear_removesAllData() {
		SaSession session = new SaSession("sid-clear");
		SaManager.getSaTokenDao().setSession(session, 3600);
		session.set("a", 1);
		session.clear();
		Assertions.assertTrue(session.keys().isEmpty());
	}

	/** 终端列表非空时 logoutByTerminalCountToZero 不应注销 Session */
	@Test
	void logoutByTerminalCountToZero_whenTerminalListNotEmpty_doesNotLogout() {
		SaSession session = new SaSession("sid-keep");
		SaManager.getSaTokenDao().setSession(session, 3600);
		session.addTerminal(new SaTerminalInfo(1, "token-1", "PC", null));
		session.addTerminal(new SaTerminalInfo(2, "token-2", "APP", null));
		session.logoutByTerminalCountToZero();
		Assertions.assertNotNull(SaManager.getSaTokenDao().getSession(session.getId()));
		Assertions.assertEquals(2, session.getTerminalList().size());
	}

	/** 终端列表已空时 logoutByTerminalCountToZero 应注销 Session */
	@Test
	void logoutByTerminalCountToZero_whenAlreadyEmpty_logsOutSession() {
		SaSession session = new SaSession("sid-empty-terminals");
		SaManager.getSaTokenDao().setSession(session, 3600);
		session.logoutByTerminalCountToZero();
		Assertions.assertNull(SaManager.getSaTokenDao().getSession(session.getId()));
	}

	/** updateMaxTimeout 在当前剩余时间更短时不应延长有效期 */
	@Test
	void updateMaxTimeout_whenCurrentHigher_doesNotShorten() {
		SaSession session = new SaSession("sid-max-timeout");
		SaManager.getSaTokenDao().setSession(session, 3600);
		session.updateMaxTimeout(100);
		Assertions.assertTrue(session.timeout() <= 100);
		session.updateMaxTimeout(50);
		Assertions.assertTrue(session.timeout() <= 100);
	}

	/** updateMinTimeout 在当前剩余时间更长时不应缩短有效期 */
	@Test
	void updateMinTimeout_whenCurrentAlreadyHigher_doesNotExtend() {
		SaSession session = new SaSession("sid-min-timeout");
		SaManager.getSaTokenDao().setSession(session, 3600);
		long before = session.timeout();
		session.updateMinTimeout(100);
		Assertions.assertEquals(before, session.timeout());
	}

}
