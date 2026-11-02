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
package cn.dev33.satoken.integration.loveqq.interceptorapp;

import cn.dev33.satoken.integration.loveqq.support.LoveqqHttp;
import cn.dev33.satoken.loveqq.boot.interceptor.SaInterceptor;
import com.kfyty.loveqq.framework.boot.K;
import com.kfyty.loveqq.framework.core.autoconfig.ApplicationContext;
import com.kfyty.loveqq.framework.web.core.WebServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/**
 * 真实 LoveQQ 容器 + HTTP：SaInterceptor 登录鉴权
 */
public class InterceptorHttpTest {

	private static ApplicationContext ctx;
	private static int port;

	/** 起 Interceptor 版应用并记下端口 */
	@BeforeAll
	public static void start() {
		ctx = K.start(InterceptorApp.class);
		port = ctx.getBean(WebServer.class).getPort();
	}

	/** 测完把容器关掉 */
	@AfterAll
	public static void stop() throws Exception {
		if (ctx != null) {
			ctx.close();
		}
	}

	/** 容器里应该能拿到 SaInterceptor Bean */
	@Test
	public void interceptor_shouldBeRegistered() {
		Assertions.assertNotNull(ctx.getBean(SaInterceptor.class));
	}

	/** 登录后带 token 访问业务接口应该放行 */
	@Test
	public void login_thenAccessUser() {
		String token = LoveqqHttp.get(port, "/login");
		Assertions.assertEquals("10001", LoveqqHttp.get(port, "/user", token));
	}

	/** 未登录访问业务接口应该被拦截器拦住并写回文案 */
	@Test
	public void user_withoutLogin_shouldBeBlocked() {
		String body = LoveqqHttp.get(port, "/user");
		Assertions.assertNotEquals("10001", body);
		Assertions.assertFalse(body.isEmpty());
	}

	/** exclude 的公开路径未登录也应该放行 */
	@Test
	public void open_shouldPassWithoutLogin() {
		Assertions.assertEquals("open", LoveqqHttp.get(port, "/open"));
	}

	/** 登录后访问带 @SaCheckLogin 的接口应该通过 */
	@Test
	public void annotation_afterLogin_shouldPass() {
		String token = LoveqqHttp.get(port, "/login");
		Assertions.assertEquals("anno-ok", LoveqqHttp.get(port, "/anno", token));
	}

	/** @SaIgnore 接口未登录也应该能访问（注解校验被忽略） */
	@Test
	public void saIgnore_shouldSkipAnnotation() {
		Assertions.assertEquals("ignored", LoveqqHttp.get(port, "/ignored"));
	}

	/** Interceptor auth 抛 BackResultException 时应该把消息写回响应 */
	@Test
	public void backResult_shouldWriteMessage() {
		Assertions.assertEquals("back-ok", LoveqqHttp.get(port, "/back"));
	}

}
