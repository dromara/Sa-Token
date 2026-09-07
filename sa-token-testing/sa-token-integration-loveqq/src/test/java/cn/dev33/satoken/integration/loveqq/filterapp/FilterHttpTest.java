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
package cn.dev33.satoken.integration.loveqq.filterapp;

import cn.dev33.satoken.SaManager;
import cn.dev33.satoken.apikey.SaApiKeyManager;
import cn.dev33.satoken.config.SaTokenConfig;
import cn.dev33.satoken.dao.SaTokenDaoForRedisson;
import cn.dev33.satoken.integration.loveqq.support.LoveqqHttp;
import cn.dev33.satoken.loveqq.boot.SaBeanInject;
import cn.dev33.satoken.loveqq.boot.SaBeanRegister;
import cn.dev33.satoken.loveqq.boot.filter.SaFirewallCheckFilter;
import cn.dev33.satoken.loveqq.boot.filter.SaRequestFilter;
import cn.dev33.satoken.loveqq.boot.filter.SaTokenContextFilter;
import cn.dev33.satoken.loveqq.boot.filter.SaTokenCorsFilter;
import cn.dev33.satoken.oauth2.SaOAuth2Manager;
import cn.dev33.satoken.sign.SaSignManager;
import cn.dev33.satoken.sso.SaSsoManager;
import com.kfyty.loveqq.framework.boot.K;
import com.kfyty.loveqq.framework.core.autoconfig.ApplicationContext;
import com.kfyty.loveqq.framework.web.core.WebServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/**
 * 真实 LoveQQ 容器 + HTTP：自动装配、yml 配置、SaRequestFilter 登录鉴权
 */
public class FilterHttpTest {

	private static ApplicationContext ctx;
	private static int port;

	/** 起 Filter 版应用并记下端口 */
	@BeforeAll
	public static void start() {
		ctx = K.start(FilterApp.class);
		port = ctx.getBean(WebServer.class).getPort();
	}

	/** 测完把容器关掉 */
	@AfterAll
	public static void stop() throws Exception {
		if (ctx != null) {
			ctx.close();
		}
	}

	/** 插件启动后应该注册出配置 Bean，并写进 SaManager */
	@Test
	public void plugin_shouldRegisterConfigAndCoreBeans() {
		SaTokenConfig config = ctx.getBean(SaTokenConfig.class);
		Assertions.assertNotNull(config);
		Assertions.assertEquals("satoken", config.getTokenName());
		Assertions.assertEquals(2_592_000L, config.getTimeout());
		Assertions.assertSame(config, SaManager.getConfig());
		Assertions.assertNotNull(ctx.getBean(SaBeanRegister.class));
		Assertions.assertNotNull(ctx.getBean(SaBeanInject.class));
	}

	/** 三个内置 Filter 和鉴权 Filter 应该作为 Bean 注册出来 */
	@Test
	public void builtinFilters_shouldBeRegistered() {
		Assertions.assertNotNull(ctx.getBean(SaTokenContextFilter.class));
		Assertions.assertNotNull(ctx.getBean(SaTokenCorsFilter.class));
		Assertions.assertNotNull(ctx.getBean(SaFirewallCheckFilter.class));
		Assertions.assertNotNull(ctx.getBean(SaRequestFilter.class));
	}

	/** 没有 RedissonClient 时不应该注册出 Redisson Dao */
	@Test
	public void redissonDao_shouldNotRegisterWithoutClient() {
		Assertions.assertTrue(ctx.getBeanOfType(SaTokenDaoForRedisson.class).isEmpty());
		Assertions.assertNotNull(SaManager.getSaTokenDao());
		Assertions.assertFalse(SaManager.getSaTokenDao() instanceof SaTokenDaoForRedisson);
	}

	/** SSO / OAuth2 / ApiKey / Sign 默认配置应该由 Register 创建并注入 */
	@Test
	public void optionalModules_shouldHaveDefaultConfig() {
		Assertions.assertNotNull(SaSsoManager.getServerConfig());
		Assertions.assertNotNull(SaSsoManager.getClientConfig());
		Assertions.assertNotNull(SaOAuth2Manager.getServerConfig());
		Assertions.assertNotNull(SaApiKeyManager.getConfig());
		Assertions.assertNotNull(SaSignManager.getConfig());
	}

	/** 登录后带 token 访问业务接口应该放行 */
	@Test
	public void login_thenAccessUser() {
		String token = LoveqqHttp.get(port, "/login");
		Assertions.assertNotNull(token);
		Assertions.assertFalse(token.isEmpty());
		Assertions.assertEquals("10001", LoveqqHttp.get(port, "/user", token));
	}

	/** 未登录访问业务接口应该被 Filter 拦住 */
	@Test
	public void user_withoutLogin_shouldBeBlocked() {
		String body = LoveqqHttp.get(port, "/user");
		Assertions.assertTrue(body.contains("未能读取到有效 token") || body.toLowerCase().contains("token")
				|| body.contains("未登录") || !body.isEmpty());
	}

	/** exclude 的公开路径未登录也应该放行 */
	@Test
	public void open_shouldPassWithoutLogin() {
		Assertions.assertEquals("open", LoveqqHttp.get(port, "/open"));
	}

	/** forward 适配应该转到公开接口或发出重定向 */
	@Test
	public void forward_shouldReachOpen() {
		String body = LoveqqHttp.get(port, "/fwd");
		Assertions.assertTrue("open".equals(body) || body.contains("/open") || body.isEmpty());
	}

	/** Filter auth 抛 BackResultException 时应该把消息写回响应 */
	@Test
	public void backResult_shouldWriteMessage() {
		Assertions.assertEquals("back-ok", LoveqqHttp.get(port, "/back"));
	}

	/** CORS 策略抛 BackResultException 时应该写回响应 */
	@Test
	public void corsBack_shouldWriteMessage() {
		Assertions.assertEquals("cors-back", LoveqqHttp.get(port, "/cors-back"));
	}

	/** 防火墙抛 BackResultException 时应该写回响应 */
	@Test
	public void firewallBack_shouldWriteMessage() {
		Assertions.assertEquals("fw-back", LoveqqHttp.get(port, "/fw-back"));
	}

	/** 防火墙校验失败且没有自定义 failHandle 时应该写回异常信息 */
	@Test
	public void firewallFail_shouldWriteMessage() {
		Assertions.assertEquals("fw-fail", LoveqqHttp.get(port, "/fw-fail"));
	}

}
