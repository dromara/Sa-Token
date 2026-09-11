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
import cn.dev33.satoken.test.SaTokenTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

/**
 * SaTokenEventCenter 事件发布测试
 *
 * @author click33
 * @since 1.46.0
 */
@SaTokenTest
public class SaTokenEventCenterTest {

	private List<SaTokenListener> savedListeners;

	/** 每个用例开始前准备测试现场 */
	@BeforeEach
	void setUp() {
		savedListeners = new ArrayList<>(SaTokenEventCenter.getListenerList());
		SaTokenEventCenter.setListenerList(new ArrayList<>());
	}

	/** 每个用例结束后把测试现场清掉 */
	@AfterEach
	void tearDown() {
		SaTokenEventCenter.setListenerList(savedListeners);
	}

	/** 注册监听器后 doLogin 事件应被接收 */
	@Test
	void registerListener_receivesDoLoginEvent() {
		AtomicBoolean received = new AtomicBoolean(false);
		AtomicReference<Object> loginIdRef = new AtomicReference<>();
		SaLoginParameter loginParameter = new SaLoginParameter();

		SaTokenListenerForSimple listener = new SaTokenListenerForSimple() {
			@Override
			public void doLogin(String loginType, Object loginId, String tokenValue, SaLoginParameter parameter) {
				received.set(true);
				loginIdRef.set(loginId);
				Assertions.assertEquals("login", loginType);
				Assertions.assertEquals("token-abc", tokenValue);
				Assertions.assertSame(loginParameter, parameter);
			}
		};

		SaTokenEventCenter.registerListener(listener);
		SaTokenEventCenter.doLogin("login", 10001, "token-abc", loginParameter);

		Assertions.assertTrue(received.get());
		Assertions.assertEquals(10001, loginIdRef.get());
		Assertions.assertTrue(SaTokenEventCenter.hasListener(listener));
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

	/** doLogout/doKickout/doDisable 事件应通知监听器 */
	@Test
	void doLogout_doKickout_doDisable() {
		AtomicBoolean logout = new AtomicBoolean(false);
		AtomicBoolean kickout = new AtomicBoolean(false);
		AtomicBoolean disable = new AtomicBoolean(false);
		AtomicReference<Object> loginIdRef = new AtomicReference<>();
		AtomicReference<String> tokenRef = new AtomicReference<>();
		AtomicReference<String> serviceRef = new AtomicReference<>();
		AtomicInteger levelRef = new AtomicInteger();
		AtomicLong disableTimeRef = new AtomicLong();

		SaTokenListenerForSimple listener = new SaTokenListenerForSimple() {
			@Override
			public void doLogout(String loginType, Object loginId, String tokenValue) {
				logout.set(true);
				loginIdRef.set(loginId);
				tokenRef.set(tokenValue);
				Assertions.assertEquals("login", loginType);
			}

			@Override
			public void doKickout(String loginType, Object loginId, String tokenValue) {
				kickout.set(true);
				Assertions.assertEquals("login", loginType);
				Assertions.assertEquals(20002, loginId);
				Assertions.assertEquals("token-kick", tokenValue);
			}

			@Override
			public void doDisable(String loginType, Object loginId, String service, int level, long disableTime) {
				disable.set(true);
				serviceRef.set(service);
				levelRef.set(level);
				disableTimeRef.set(disableTime);
				Assertions.assertEquals("login", loginType);
				Assertions.assertEquals(30003, loginId);
			}
		};

		SaTokenEventCenter.registerListener(listener);
		SaTokenEventCenter.doLogout("login", 10001, "token-logout");
		SaTokenEventCenter.doKickout("login", 20002, "token-kick");
		SaTokenEventCenter.doDisable("login", 30003, "comment", 2, 3600);

		Assertions.assertTrue(logout.get());
		Assertions.assertEquals(10001, loginIdRef.get());
		Assertions.assertEquals("token-logout", tokenRef.get());
		Assertions.assertTrue(kickout.get());
		Assertions.assertTrue(disable.get());
		Assertions.assertEquals("comment", serviceRef.get());
		Assertions.assertEquals(2, levelRef.get());
		Assertions.assertEquals(3600, disableTimeRef.get());
	}

	/** doOpenSafe/doCloseSafe 事件应通知监听器 */
	@Test
	void doOpenSafe_doCloseSafe() {
		AtomicBoolean openSafe = new AtomicBoolean(false);
		AtomicBoolean closeSafe = new AtomicBoolean(false);
		AtomicReference<String> serviceRef = new AtomicReference<>();
		AtomicLong safeTimeRef = new AtomicLong();

		SaTokenListenerForSimple listener = new SaTokenListenerForSimple() {
			@Override
			public void doOpenSafe(String loginType, String tokenValue, String service, long safeTime) {
				openSafe.set(true);
				serviceRef.set(service);
				safeTimeRef.set(safeTime);
				Assertions.assertEquals("login", loginType);
				Assertions.assertEquals("token-safe", tokenValue);
			}

			@Override
			public void doCloseSafe(String loginType, String tokenValue, String service) {
				closeSafe.set(true);
				Assertions.assertEquals("login", loginType);
				Assertions.assertEquals("token-safe", tokenValue);
				Assertions.assertEquals("pay", service);
			}
		};

		SaTokenEventCenter.registerListener(listener);
		SaTokenEventCenter.doOpenSafe("login", "token-safe", "pay", 120);
		SaTokenEventCenter.doCloseSafe("login", "token-safe", "pay");

		Assertions.assertTrue(openSafe.get());
		Assertions.assertTrue(closeSafe.get());
		Assertions.assertEquals("pay", serviceRef.get());
		Assertions.assertEquals(120, safeTimeRef.get());
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

	/** 默认构造函数应可正常创建实例 */
	@Test
	void defaultConstructor() {
		Assertions.assertDoesNotThrow(SaTokenEventCenter::new);
	}

}
