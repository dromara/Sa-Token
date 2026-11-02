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
package cn.dev33.satoken.integration.solon.interceptorapp;

import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.solon.integration.SaTokenInterceptor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.noear.solon.Solon;
import org.noear.solon.test.HttpTester;
import org.noear.solon.test.SolonTest;

/**
 * 真实 Solon 容器 + HTTP：SaTokenInterceptor 登录鉴权
 */
@SolonTest(InterceptorApp.class)
public class InterceptorHttpTest extends HttpTester {

	/** 容器里应该能拿到 SaTokenInterceptor Bean */
	@Test
	public void interceptor_shouldBeRegistered() {
		Assertions.assertNotNull(Solon.context().getBean(SaTokenInterceptor.class));
	}

	/** 登录后带 token 访问业务接口应该放行 */
	@Test
	public void login_thenAccessUser() {
		String token = path("/login").get();
		Assertions.assertEquals("10001", path("/user").header("satoken", token).get());
	}

	/** 未登录访问业务接口应该被拦截器拦住 */
	@Test
	public void user_withoutLogin_shouldBeBlocked() {
		Assertions.assertEquals(NotLoginException.NOT_TOKEN_MESSAGE, path("/user").get());
	}

	/** exclude 的公开路径未登录也应该放行 */
	@Test
	public void open_shouldPassWithoutLogin() {
		Assertions.assertEquals("open", path("/open").get());
	}

	/** 登录后访问带 @SaCheckLogin 的接口应该通过 */
	@Test
	public void annotation_afterLogin_shouldPass() {
		String token = path("/login").get();
		Assertions.assertEquals("anno-ok", path("/anno").header("satoken", token).get());
	}

	/** Gateway 子路由应该能被拦截器找到 */
	@Test
	public void gateway_ping() {
		String token = path("/login").get();
		Assertions.assertEquals("pong", path("/gw/ping").header("satoken", token).get());
	}

	/** @SaIgnore 接口未登录也应该能访问（注解校验被忽略） */
	@Test
	public void saIgnore_shouldSkipAnnotation() {
		Assertions.assertEquals("ignored", path("/ignored").get());
	}

	/** Interceptor auth 抛 BackResultException 时应该把消息写回响应 */
	@Test
	public void backResult_shouldWriteMessage() {
		Assertions.assertEquals("back-ok", path("/back").get());
	}

}
