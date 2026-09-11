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
package cn.dev33.satoken.core.extension;

import cn.dev33.satoken.SaManager;
import cn.dev33.satoken.context.mock.SaTokenContextMockUtil;
import cn.dev33.satoken.exception.NotPermissionException;
import cn.dev33.satoken.listener.SaTokenEventCenter;
import cn.dev33.satoken.listener.SaTokenListener;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.strategy.SaStrategy;
import cn.dev33.satoken.test.SaTokenTest;
import cn.dev33.satoken.test.fixture.PrefixTokenCreator;
import cn.dev33.satoken.test.fixture.RecordingSaTokenListener;
import cn.dev33.satoken.test.fixture.RecordingStpInterface;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 权限、事件、策略扩展点按用户项目写法挂上去，副作用应可观察。
 *
 * @author click33
 * @since 1.46.0
 */
@SaTokenTest
public class UserExtensionPointTest {

	private List<SaTokenListener> savedListeners;

	/** 每个用例开始前把监听器列表换成空的，避免掺进别的侦听器 */
	@BeforeEach
	void setUp() {
		savedListeners = new ArrayList<>(SaTokenEventCenter.getListenerList());
		SaTokenEventCenter.setListenerList(new ArrayList<>());
	}

	/** 每个用例结束后把监听器列表还原 */
	@AfterEach
	void tearDown() {
		SaTokenEventCenter.setListenerList(savedListeners);
	}

	/** 挂上 RecordingStpInterface 后，有权限的该过、没权限的该拦，并且能看到问过哪个账号 */
	@Test
	void recordingStpInterface_drivesPermissionCheck() {
		RecordingStpInterface stpInterface = new RecordingStpInterface(
				Arrays.asList("user:add"),
				Arrays.asList("user"));
		SaManager.setStpInterface(stpInterface);

		SaTokenContextMockUtil.setMockContext(() -> {
			StpUtil.login(20001);
			Assertions.assertDoesNotThrow(() -> StpUtil.checkPermission("user:add"));
			Assertions.assertThrows(NotPermissionException.class, () -> StpUtil.checkPermission("user:delete"));
			Assertions.assertEquals("20001", String.valueOf(stpInterface.lastPermissionLoginId));
		});
	}

	/** 挂上 RecordingSaTokenListener 后，登录和踢人应按顺序记到事件列表里 */
	@Test
	void recordingListener_seesLoginThenKickout() {
		RecordingSaTokenListener listener = new RecordingSaTokenListener();
		SaTokenEventCenter.registerListener(listener);

		SaTokenContextMockUtil.setMockContext(() -> {
			StpUtil.login(20002);
			String token = StpUtil.getTokenValue();
			Assertions.assertEquals(Arrays.asList("login"), listener.events);
			Assertions.assertEquals(token, listener.lastToken);
			StpUtil.kickout(20002);
			Assertions.assertEquals(Arrays.asList("login", "kickout"), listener.events);
			Assertions.assertEquals(token, listener.lastToken);
		});
	}

	/** 挂上 PrefixTokenCreator 后，登录生成的 Token 应该带上用户指定的前缀 */
	@Test
	void prefixTokenCreator_writesObservableToken() {
		SaStrategy.instance.createToken = new PrefixTokenCreator("shop");

		SaTokenContextMockUtil.setMockContext(() -> {
			StpUtil.login(20003);
			String token = StpUtil.getTokenValue();
			Assertions.assertTrue(token.startsWith("shop-20003-"));
		});
	}

}
