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
package cn.dev33.satoken.integration.jboot.httptest;

import cn.dev33.satoken.SaManager;
import cn.dev33.satoken.integration.jboot.support.JbootHttp;
import cn.dev33.satoken.jboot.SaTokenContextForJboot;
import com.jfinal.server.undertow.UndertowServer;
import io.jboot.app.JbootApplication;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.ServerSocket;

/**
 * 真实 JBoot Undertow + HTTP：SaTokenContextForJboot 上下文、SaAnnotationInterceptor 鉴权
 */
public class JbootHttpTest {

	private static UndertowServer server;
	private static int port;

	/** 起一个不带 Redis 的 JBoot 服务并记下端口 */
	@BeforeAll
	public static void start() {
		port = freePort();
		JbootApplication.setBootArg("undertow.port", port);
		JbootApplication.setBootArg("undertow.host", "127.0.0.1");
		JbootApplication.setBootArg("undertow.devMode", false);
		JbootApplication.setBootArg("jboot.app.mode", "product");
		JbootApplication.setBootArg("jboot.app.bannerEnable", false);
		JbootApplication.setBootArg("jboot.app.proxy", "javassist");
		JbootApplication.setBootArg("jboot.app.listener", JbootHttpTestListener.class.getName());
		server = JbootApplication.createServer(new String[0]);
		server.start();
	}

	/** 测完把 Undertow 停掉 */
	@AfterAll
	public static void stop() {
		if (server != null) {
			server.stop();
		}
	}

	/** 启动后 SaManager 上应该是 JBoot 版上下文 */
	@Test
	public void context_shouldBeJboot() {
		Assertions.assertTrue(SaManager.getSaTokenContext() instanceof SaTokenContextForJboot);
	}

	/** 首页未登录也应该放行 */
	@Test
	public void index_shouldPassWithoutLogin() {
		Assertions.assertEquals("index-ok", JbootHttp.get(port, "/"));
	}

	/** 登录后带 token 访问业务接口应该放行 */
	@Test
	public void login_thenAccessUser() {
		String token = JbootHttp.get(port, "/login");
		Assertions.assertNotNull(token);
		Assertions.assertFalse(token.isEmpty());
		Assertions.assertEquals("10001", JbootHttp.get(port, "/user", token));
	}

	/** 未登录访问业务接口不应该返回登录 id */
	@Test
	public void user_withoutLogin_shouldBeBlocked() {
		JbootHttp.Resp resp = JbootHttp.exchange(port, "/user");
		Assertions.assertNotEquals("10001", resp.body);
		Assertions.assertNotEquals(200, Integer.valueOf(resp.status));
	}

	/** 公开路径未登录也应该放行 */
	@Test
	public void open_shouldPassWithoutLogin() {
		Assertions.assertEquals("open", JbootHttp.get(port, "/open"));
	}

	/** 登录后访问带 @SaCheckLogin 的接口应该通过 */
	@Test
	public void annotation_afterLogin_shouldPass() {
		String token = JbootHttp.get(port, "/login");
		Assertions.assertEquals("anno-ok", JbootHttp.get(port, "/anno", token));
	}

	/** 未登录打注解接口不应该返回业务文案 */
	@Test
	public void annotation_withoutLogin_shouldBeBlocked() {
		JbootHttp.Resp resp = JbootHttp.exchange(port, "/anno");
		Assertions.assertNotEquals("anno-ok", resp.body);
		Assertions.assertNotEquals(200, Integer.valueOf(resp.status));
	}

	/** @SaIgnore 目前会被 StopMatchException 打成 500：拦截器没有接住就当未知异常渲染了 */
	@Test
	public void saIgnore_currentlyBecomes500() {
		JbootHttp.Resp resp = JbootHttp.exchange(port, "/ignored");
		Assertions.assertNotEquals("ignored", resp.body);
		Assertions.assertEquals(500, resp.status);
	}

	/** 超管登录后访问角色接口应该通过 */
	@Test
	public void role_afterLogin_shouldPass() {
		String token = JbootHttp.get(port, "/login");
		Assertions.assertEquals("role-ok", JbootHttp.get(port, "/role", token));
	}

	/** 没角色的账号打角色接口不应该返回业务文案 */
	@Test
	public void role_withoutRole_shouldBeBlocked() {
		String token = JbootHttp.get(port, "/loginPlain");
		JbootHttp.Resp resp = JbootHttp.exchange(port, "/role", token);
		Assertions.assertNotEquals("role-ok", resp.body);
		Assertions.assertNotEquals(200, Integer.valueOf(resp.status));
	}

	/** 未登记过的 controller 路径应该走 404 */
	@Test
	public void missing_shouldRender404() {
		JbootHttp.Resp resp = JbootHttp.exchange(port, "/api/no-such-action");
		Assertions.assertEquals(404, resp.status);
	}

	/** 找一个空闲端口给 Undertow 用 */
	private static int freePort() {
		try (ServerSocket socket = new ServerSocket(0)) {
			socket.setReuseAddress(true);
			return socket.getLocalPort();
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
	}
}
