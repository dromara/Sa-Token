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
package cn.dev33.satoken.core.strategy;

import cn.dev33.satoken.context.SaHolder;
import cn.dev33.satoken.context.mock.SaRequestForMock;
import cn.dev33.satoken.context.mock.SaTokenContextMockUtil;
import cn.dev33.satoken.context.model.SaRequest;
import cn.dev33.satoken.context.model.SaResponse;
import cn.dev33.satoken.exception.StopMatchException;
import cn.dev33.satoken.strategy.SaFirewallStrategy;
import cn.dev33.satoken.strategy.hooks.SaFirewallCheckHook;
import cn.dev33.satoken.strategy.hooks.SaFirewallCheckHookForDirectoryTraversal;
import cn.dev33.satoken.strategy.hooks.SaFirewallCheckHookForWhitePath;
import cn.dev33.satoken.test.SaTokenTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * SaFirewallStrategy 防火墙策略测试
 */
@SaTokenTest
public class SaFirewallStrategyTest {

	private final SaFirewallStrategy strategy = SaFirewallStrategy.instance;

	private List<SaFirewallCheckHook> originalHooks;
	private List<String> originalWhitePaths;

	@BeforeEach
	void saveState() {
		originalHooks = new ArrayList<>(strategy.checkHooks);
		originalWhitePaths = new ArrayList<>(SaFirewallCheckHookForWhitePath.instance.whitePaths);
	}

	@AfterEach
	void restoreState() {
		strategy.checkHooks.clear();
		strategy.checkHooks.addAll(originalHooks);
		SaFirewallCheckHookForWhitePath.instance.whitePaths.clear();
		SaFirewallCheckHookForWhitePath.instance.whitePaths.addAll(originalWhitePaths);
	}

	/** 路径合法性校验应正确识别合法与非法路径 */
	@Test
	void isPathValid() {
		Assertions.assertTrue(SaFirewallCheckHookForDirectoryTraversal.isPathValid("/"));
		Assertions.assertTrue(SaFirewallCheckHookForDirectoryTraversal.isPathValid("/user/info"));
		Assertions.assertTrue(SaFirewallCheckHookForDirectoryTraversal.isPathValid("/user/info.js"));
		Assertions.assertTrue(SaFirewallCheckHookForDirectoryTraversal.isPathValid("/.hidden"));

		Assertions.assertFalse(SaFirewallCheckHookForDirectoryTraversal.isPathValid(null));
		Assertions.assertFalse(SaFirewallCheckHookForDirectoryTraversal.isPathValid(""));
		Assertions.assertFalse(SaFirewallCheckHookForDirectoryTraversal.isPathValid("user/info"));
		Assertions.assertFalse(SaFirewallCheckHookForDirectoryTraversal.isPathValid("/user/../info"));
		Assertions.assertFalse(SaFirewallCheckHookForDirectoryTraversal.isPathValid("/user//info"));
		Assertions.assertFalse(SaFirewallCheckHookForDirectoryTraversal.isPathValid("//user"));
	}

	/** 白名单路径命中时应抛出 StopMatchException */
	@Test
	void whitePathHook_throwsStopMatchException() {
		SaFirewallCheckHookForWhitePath.instance.resetConfig("/health");
		SaTokenContextMockUtil.setMockContext(() -> {
			SaRequestForMock req = (SaRequestForMock) SaHolder.getRequest();
			req.requestPath = "/health";
			Assertions.assertThrows(StopMatchException.class,
					() -> SaFirewallCheckHookForWhitePath.instance.execute(req, SaHolder.getResponse(), null));
		});
	}

	/** 注册与移除防火墙 Hook 应正确更新 Hook 列表 */
	@Test
	void registerHook_and_removeHook() {
		CountingHook hook = new CountingHook();
		int sizeBefore = strategy.checkHooks.size();

		strategy.registerHook(hook);
		Assertions.assertEquals(sizeBefore + 1, strategy.checkHooks.size());
		Assertions.assertTrue(strategy.checkHooks.contains(hook));

		strategy.removeHook(CountingHook.class);
		Assertions.assertEquals(sizeBefore, strategy.checkHooks.size());
		Assertions.assertFalse(strategy.checkHooks.contains(hook));
	}

	/** check 执行时应依次运行已注册的 Hook */
	@Test
	void check_runsHooks() {
		CountingHook hook = new CountingHook();
		CountingHook.executed.set(false);
		strategy.registerHook(hook);

		SaTokenContextMockUtil.setMockContext(() -> {
			SaRequestForMock req = (SaRequestForMock) SaHolder.getRequest();
			req.requestPath = "/api/user";
			req.method = "GET";
			req.host = "localhost";

			strategy.check.execute(req, SaHolder.getResponse(), null);
			Assertions.assertTrue(CountingHook.executed.get());
		});

		strategy.removeHook(CountingHook.class);
	}

	static class CountingHook implements SaFirewallCheckHook {

		static final AtomicBoolean executed = new AtomicBoolean(false);

		@Override
		public void execute(SaRequest req, SaResponse res, Object extArg) {
			executed.set(true);
		}
	}

}
