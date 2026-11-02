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
package cn.dev33.satoken.integration.jfinal.httptest;

import cn.dev33.satoken.SaManager;
import cn.dev33.satoken.integration.jfinal.support.JfinalHttp;
import cn.dev33.satoken.jfinal.SaTokenContextForJfinal;
import com.jfinal.server.undertow.UndertowServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.ServerSocket;

/**
 * 真实 JFinal Undertow + HTTP：SaTokenActionHandler 上下文、SaAnnotationInterceptor 鉴权
 */
public class JfinalHttpTest {

	private static UndertowServer server;
	private static int port;

	/** 起一个不带 Redis 的 JFinal 服务并记下端口 */
	@BeforeAll
	public static void start() {
		port = freePort();
		server = UndertowServer.create(JfinalHttpTestConfig.class)
				.setHost("127.0.0.1")
				.setPort(port)
				.setDevMode(false);
		server.start();
	}

	/** 测完把 Undertow 停掉 */
	@AfterAll
	public static void stop() {
		if (server != null) {
			server.stop();
		}
	}

	/** 启动后 SaManager 上应该是 JFinal 版上下文 */
	@Test
	public void context_shouldBeJfinal() {
		Assertions.assertTrue(SaManager.getSaTokenContext() instanceof SaTokenContextForJfinal);
	}

	/** 首页未登录也应该放行 */
	@Test
	public void index_shouldPassWithoutLogin() {
		Assertions.assertEquals("index-ok", JfinalHttp.get(port, "/"));
	}

	/** 登录后带 token 访问业务接口应该放行 */
	@Test
	public void login_thenAccessUser() {
		String token = JfinalHttp.get(port, "/login");
		Assertions.assertNotNull(token);
		Assertions.assertFalse(token.isEmpty());
		Assertions.assertEquals("10001", JfinalHttp.get(port, "/user", token));
	}

	/** 未登录访问业务接口应该写回未登录文案，而不是 500 页 */
	@Test
	public void user_withoutLogin_shouldBeBlocked() {
		JfinalHttp.Resp resp = JfinalHttp.exchange(port, "/user");
		Assertions.assertEquals(200, resp.status);
		Assertions.assertNotEquals("10001", resp.body);
		Assertions.assertFalse(resp.body.isEmpty());
	}

	/** 公开路径未登录也应该放行 */
	@Test
	public void open_shouldPassWithoutLogin() {
		Assertions.assertEquals("open", JfinalHttp.get(port, "/open"));
	}

	/** 登录后访问带 @SaCheckLogin 的接口应该通过 */
	@Test
	public void annotation_afterLogin_shouldPass() {
		String token = JfinalHttp.get(port, "/login");
		Assertions.assertEquals("anno-ok", JfinalHttp.get(port, "/anno", token));
	}

	/** 未登录打注解接口应该写回未登录文案 */
	@Test
	public void annotation_withoutLogin_shouldBeBlocked() {
		JfinalHttp.Resp resp = JfinalHttp.exchange(port, "/anno");
		Assertions.assertEquals(200, resp.status);
		Assertions.assertNotEquals("anno-ok", resp.body);
		Assertions.assertFalse(resp.body.isEmpty());
	}

	/** @SaIgnore 应该跳过登录校验，返回业务文案 */
	@Test
	public void saIgnore_shouldReturnIgnored() {
		JfinalHttp.Resp resp = JfinalHttp.exchange(port, "/ignored");
		Assertions.assertEquals(200, resp.status);
		Assertions.assertEquals("ignored", resp.body);
	}

	/** 超管登录后访问角色接口应该通过 */
	@Test
	public void role_afterLogin_shouldPass() {
		String token = JfinalHttp.get(port, "/login");
		Assertions.assertEquals("role-ok", JfinalHttp.get(port, "/role", token));
	}

	/** 没角色的账号打角色接口应该写回无角色文案 */
	@Test
	public void role_withoutRole_shouldBeBlocked() {
		String token = JfinalHttp.get(port, "/loginPlain");
		JfinalHttp.Resp resp = JfinalHttp.exchange(port, "/role", token);
		Assertions.assertEquals(200, resp.status);
		Assertions.assertNotEquals("role-ok", resp.body);
		Assertions.assertTrue(resp.body.contains("无此角色"));
	}

	/** 未登记过的 controller 路径应该走 404 */
	@Test
	public void missing_shouldRender404() {
		JfinalHttp.Resp resp = JfinalHttp.exchange(port, "/api/no-such-action");
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
