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

import cn.dev33.satoken.listener.SaTokenEventCenter;
import cn.dev33.satoken.listener.SaTokenListenerForSimple;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

/**
 * SaTokenEventCenter 扩展事件发布测试
 */
public class SaTokenEventCenterExtendedTest {

	private List<cn.dev33.satoken.listener.SaTokenListener> savedListeners;

	@BeforeEach
	void setUp() {
		savedListeners = new ArrayList<>(SaTokenEventCenter.getListenerList());
		SaTokenEventCenter.setListenerList(new ArrayList<>());
	}

	@AfterEach
	void tearDown() {
		SaTokenEventCenter.setListenerList(savedListeners);
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

}
