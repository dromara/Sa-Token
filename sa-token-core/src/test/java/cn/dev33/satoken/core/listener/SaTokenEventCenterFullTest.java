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
package cn.dev33.satoken.core.listener;

import cn.dev33.satoken.annotation.handler.SaIgnoreHandler;
import cn.dev33.satoken.config.SaTokenConfig;
import cn.dev33.satoken.exception.SaTokenException;
import cn.dev33.satoken.listener.SaTokenEventCenter;
import cn.dev33.satoken.listener.SaTokenListener;
import cn.dev33.satoken.listener.SaTokenListenerForSimple;
import cn.dev33.satoken.stp.StpLogic;
import cn.dev33.satoken.stp.parameter.SaLoginParameter;
import cn.dev33.satoken.stp.parameter.SaLogoutParameter;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * SaTokenEventCenter 全量事件测试
 */
public class SaTokenEventCenterFullTest {

	private List<SaTokenListener> savedListeners;

	@BeforeEach
	void setUp() {
		savedListeners = new ArrayList<>(SaTokenEventCenter.getListenerList());
		SaTokenEventCenter.setListenerList(new ArrayList<>());
	}

	@AfterEach
	void tearDown() {
		SaTokenEventCenter.setListenerList(savedListeners);
	}

	/** 监听器注册、移除与清空应正常工作 */
	@Test
	void listenerManagement() {
		SaTokenListenerForSimple l1 = new SaTokenListenerForSimple();
		SaTokenListenerForSimple l2 = new SaTokenListenerForSimple();
		SaTokenEventCenter.registerListener(l1);
		SaTokenEventCenter.registerListenerList(Arrays.asList(l2));
		Assertions.assertTrue(SaTokenEventCenter.hasListener(l1));
		Assertions.assertTrue(SaTokenEventCenter.hasListener(l2));
		Assertions.assertTrue(SaTokenEventCenter.hasListener(SaTokenListenerForSimple.class));

		SaTokenEventCenter.removeListener(l1);
		Assertions.assertFalse(SaTokenEventCenter.hasListener(l1));

		SaTokenEventCenter.removeListener(SaTokenListenerForSimple.class);
		Assertions.assertFalse(SaTokenEventCenter.hasListener(l2));
		Assertions.assertFalse(SaTokenEventCenter.hasListener(SaTokenListenerForSimple.class));

		SaTokenEventCenter.registerListener(l1);
		SaTokenEventCenter.clearListener();
		Assertions.assertTrue(SaTokenEventCenter.getListenerList().isEmpty());
	}

	/** 注册 null 监听器或列表应抛出异常 */
	@Test
	void listenerRegistration_rejectsNull() {
		Assertions.assertThrows(SaTokenException.class, () -> SaTokenEventCenter.setListenerList(null));
		Assertions.assertThrows(SaTokenException.class, () -> SaTokenEventCenter.registerListener(null));
		Assertions.assertThrows(SaTokenException.class, () -> SaTokenEventCenter.registerListenerList(null));
		List<SaTokenListener> withNull = new ArrayList<>();
		withNull.add(new SaTokenListenerForSimple());
		withNull.add(null);
		Assertions.assertThrows(SaTokenException.class,
				() -> SaTokenEventCenter.registerListenerList(withNull));
	}

	/** before 与生命周期事件应通知监听器 */
	@Test
	void beforeAndLifecycleEvents() {
		AtomicBoolean beforeLogout = new AtomicBoolean();
		AtomicBoolean beforeKickout = new AtomicBoolean();
		AtomicBoolean beforeReplaced = new AtomicBoolean();
		AtomicBoolean replaced = new AtomicBoolean();
		AtomicBoolean untieDisable = new AtomicBoolean();
		AtomicBoolean createSession = new AtomicBoolean();
		AtomicBoolean logoutSession = new AtomicBoolean();
		AtomicBoolean renewTimeout = new AtomicBoolean();
		AtomicBoolean registerComponent = new AtomicBoolean();
		AtomicBoolean registerHandler = new AtomicBoolean();
		AtomicBoolean setStpLogic = new AtomicBoolean();
		AtomicBoolean setConfig = new AtomicBoolean();

		SaTokenListenerForSimple listener = new SaTokenListenerForSimple() {
			@Override
			public void doBeforeLogout(String loginType, Object loginId, String tokenValue, SaLogoutParameter p) {
				beforeLogout.set(true);
			}

			@Override
			public void doBeforeKickout(String loginType, Object loginId, String tokenValue, SaLogoutParameter p) {
				beforeKickout.set(true);
			}

			@Override
			public void doBeforeReplaced(String loginType, Object loginId, String tokenValue, SaLogoutParameter p) {
				beforeReplaced.set(true);
			}

			@Override
			public void doReplaced(String loginType, Object loginId, String tokenValue) {
				replaced.set(true);
			}

			@Override
			public void doUntieDisable(String loginType, Object loginId, String service) {
				untieDisable.set(true);
			}

			@Override
			public void doCreateSession(String id) {
				createSession.set(true);
			}

			@Override
			public void doLogoutSession(String id) {
				logoutSession.set(true);
			}

			@Override
			public void doRenewTimeout(String loginType, Object loginId, String tokenValue, long timeout) {
				renewTimeout.set(true);
			}

			@Override
			public void doRegisterComponent(String compName, Object compObj) {
				registerComponent.set(true);
			}

			@Override
			public void doRegisterAnnotationHandler(cn.dev33.satoken.annotation.handler.SaAnnotationHandlerInterface<?> handler) {
				registerHandler.set(true);
			}

			@Override
			public void doSetStpLogic(StpLogic stpLogic) {
				setStpLogic.set(true);
			}

			@Override
			public void doSetConfig(SaTokenConfig config) {
				setConfig.set(true);
			}
		};

		SaTokenEventCenter.registerListener(listener);
		SaLogoutParameter logoutParameter = new SaLogoutParameter();
		SaTokenEventCenter.doBeforeLogout("login", 1, "t1", logoutParameter);
		SaTokenEventCenter.doBeforeKickout("login", 1, "t1", logoutParameter);
		SaTokenEventCenter.doBeforeReplaced("login", 1, "t1", logoutParameter);
		SaTokenEventCenter.doReplaced("login", 1, "t1");
		SaTokenEventCenter.doUntieDisable("login", 1, "comment");
		SaTokenEventCenter.doCreateSession("sid-1");
		SaTokenEventCenter.doLogoutSession("sid-1");
		SaTokenEventCenter.doRenewTimeout("login", 1, "t1", 3600);
		SaTokenEventCenter.doRegisterComponent("dao", new Object());
		SaIgnoreHandler handler = new SaIgnoreHandler();
		SaTokenEventCenter.doRegisterAnnotationHandler(handler);
		SaTokenEventCenter.doSetStpLogic(new StpLogic("evt"));
		SaTokenEventCenter.doSetConfig(new SaTokenConfig());
		SaTokenEventCenter.doLogin("login", 1, "t1", new SaLoginParameter());

		Assertions.assertTrue(beforeLogout.get());
		Assertions.assertTrue(beforeKickout.get());
		Assertions.assertTrue(beforeReplaced.get());
		Assertions.assertTrue(replaced.get());
		Assertions.assertTrue(untieDisable.get());
		Assertions.assertTrue(createSession.get());
		Assertions.assertTrue(logoutSession.get());
		Assertions.assertTrue(renewTimeout.get());
		Assertions.assertTrue(registerComponent.get());
		Assertions.assertTrue(registerHandler.get());
		Assertions.assertTrue(setStpLogic.get());
		Assertions.assertTrue(setConfig.get());
	}

}
