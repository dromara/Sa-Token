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
package cn.dev33.satoken.loveqq.boot.interceptor;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.exception.BackResultException;
import cn.dev33.satoken.exception.StopMatchException;
import cn.dev33.satoken.loveqq.boot.testsupport.LoveqqTestHelper;
import cn.dev33.satoken.loveqq.boot.testsupport.TestServerRequest;
import cn.dev33.satoken.loveqq.boot.testsupport.TestServerResponse;
import cn.dev33.satoken.test.SaTokenTest;
import com.kfyty.loveqq.framework.core.lang.Lazy;
import com.kfyty.loveqq.framework.web.core.request.RequestMethod;
import com.kfyty.loveqq.framework.web.core.route.HandlerMethodRoute;
import com.kfyty.loveqq.framework.web.core.route.Route;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.lang.reflect.Method;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

/**
 * {@link SaInterceptor} 前置处理、注解鉴权和异常分支测试
 */
@SaTokenTest
public class SaInterceptorTest {

	/** 每个用例开始前挂上 LoveQQ 策略 */
	@BeforeEach
	public void setUp() {
		LoveqqTestHelper.ensureLoveqqStrategy();
	}

	/** 无参构造和带 auth 的构造都应该能 new 出来 */
	@Test
	public void constructors_shouldCreate() {
		Assertions.assertNotNull(new SaInterceptor());
		Assertions.assertNotNull(new SaInterceptor(h -> {}));
	}

	/** 链式配置 isAnnotation / beforeAuth / auth 应该能写回去 */
	@Test
	public void configure_hooks() {
		AtomicBoolean before = new AtomicBoolean(false);
		AtomicBoolean auth = new AtomicBoolean(false);
		SaInterceptor interceptor = new SaInterceptor()
				.isAnnotation(false)
				.setBeforeAuth(h -> before.set(true))
				.setAuth(h -> auth.set(true));

		Assertions.assertFalse(interceptor.isAnnotation);
		interceptor.beforeAuth.run(null);
		interceptor.auth.run(null);
		Assertions.assertTrue(before.get());
		Assertions.assertTrue(auth.get());
	}

	/** 关掉注解鉴权后 auth 通过时应该返回 true */
	@Test
	public void preHandle_authPass_returnTrue() {
		AtomicReference<Object> authArg = new AtomicReference<Object>();
		SaInterceptor interceptor = new SaInterceptor().isAnnotation(false).setAuth(authArg::set);
		Route route = Mockito.mock(Route.class);
		TestServerRequest request = LoveqqTestHelper.newGetRequest("/api");
		TestServerResponse response = LoveqqTestHelper.newResponse();

		Assertions.assertTrue(interceptor.preHandle(request, response, route));
		Assertions.assertSame(route, authArg.get());
	}

	/** auth 里抛 StopMatchException 时应该吞掉并放行 */
	@Test
	public void preHandle_stopMatch_returnTrue() {
		SaInterceptor interceptor = new SaInterceptor()
				.isAnnotation(false)
				.setAuth(h -> {
					throw new StopMatchException();
				});

		Assertions.assertTrue(interceptor.preHandle(LoveqqTestHelper.newGetRequest("/api"), LoveqqTestHelper.newResponse(),
				Mockito.mock(Route.class)));
	}

	/** auth 里抛 BackResultException 时应该写回响应并返回 false */
	@Test
	public void preHandle_backResult_writeResponse() {
		SaInterceptor interceptor = new SaInterceptor()
				.isAnnotation(false)
				.setAuth(h -> {
					throw new BackResultException("blocked");
				});
		TestServerResponse response = LoveqqTestHelper.newResponse();

		boolean pass = interceptor.preHandle(LoveqqTestHelper.newGetRequest("/api"), response, Mockito.mock(Route.class));

		Assertions.assertFalse(pass);
		Assertions.assertEquals("blocked", response.bodyText());
	}

	/** handler 不是 HandlerMethodRoute 时即使开着注解鉴权也不该去解析方法 */
	@Test
	public void preHandle_notHandlerMethodRoute_skipAnnotation() {
		AtomicBoolean authCalled = new AtomicBoolean(false);
		SaInterceptor interceptor = new SaInterceptor().setAuth(h -> authCalled.set(true));

		Assertions.assertTrue(interceptor.preHandle(LoveqqTestHelper.newGetRequest("/api"), LoveqqTestHelper.newResponse(),
				Mockito.mock(Route.class)));
		Assertions.assertTrue(authCalled.get());
	}

	/** 开着注解鉴权时，没登录访问带 @SaCheckLogin 的方法应该写回异常并拦住 */
	@Test
	public void preHandle_checkLoginAnnotation_notLogin() throws Exception {
		Method method = AnnoController.class.getDeclaredMethod("loginRequired");
		HandlerMethodRoute route = HandlerMethodRoute.create("/anno", RequestMethod.GET, new Lazy<Object>(AnnoController::new), method);
		SaInterceptor interceptor = new SaInterceptor().setAuth(h -> {});
		TestServerResponse response = LoveqqTestHelper.newResponse();

		Assertions.assertFalse(interceptor.preHandle(LoveqqTestHelper.newGetRequest("/anno"), response, route));
		Assertions.assertFalse(response.bodyText().isEmpty());
	}

	/** 开着注解鉴权时，没有鉴权注解的方法应该直接过 */
	@Test
	public void preHandle_plainMethod_passAnnotation() throws Exception {
		Method method = AnnoController.class.getDeclaredMethod("plain");
		HandlerMethodRoute route = HandlerMethodRoute.create("/plain", RequestMethod.GET, new Lazy<Object>(AnnoController::new), method);
		SaInterceptor interceptor = new SaInterceptor().setAuth(h -> {});

		Assertions.assertTrue(interceptor.preHandle(LoveqqTestHelper.newGetRequest("/plain"), LoveqqTestHelper.newResponse(), route));
	}

	/** 不 setAuth 时应该走默认空认证函数 */
	@Test
	public void preHandle_defaultAuth_empty() {
		SaInterceptor interceptor = new SaInterceptor().isAnnotation(false);
		Assertions.assertTrue(interceptor.preHandle(LoveqqTestHelper.newGetRequest("/api"), LoveqqTestHelper.newResponse(),
				Mockito.mock(Route.class)));
	}

	static class AnnoController {
		@SaCheckLogin
		public void loginRequired() {
		}

		public void plain() {
		}
	}

}
