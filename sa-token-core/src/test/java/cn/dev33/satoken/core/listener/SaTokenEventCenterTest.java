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
import cn.dev33.satoken.listener.SaTokenListener;
import cn.dev33.satoken.listener.SaTokenListenerForSimple;
import cn.dev33.satoken.stp.parameter.SaLoginParameter;
import cn.dev33.satoken.test.SaTokenTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

/**
 * SaTokenEventCenter 事件发布测试
 */
@SaTokenTest
public class SaTokenEventCenterTest {

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

}
