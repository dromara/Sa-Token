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
package cn.dev33.satoken.integration.solon.filterapp;

import cn.dev33.satoken.SaManager;
import cn.dev33.satoken.apikey.SaApiKeyManager;
import cn.dev33.satoken.config.SaTokenConfig;
import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.oauth2.SaOAuth2Manager;
import cn.dev33.satoken.sign.SaSignManager;
import cn.dev33.satoken.solon.SaBeanInject;
import cn.dev33.satoken.solon.SaBeanRegister;
import cn.dev33.satoken.solon.integration.SaFirewallCheckFilterForSolon;
import cn.dev33.satoken.solon.integration.SaTokenContextFilterForSolon;
import cn.dev33.satoken.solon.integration.SaTokenCorsFilterForSolon;
import cn.dev33.satoken.solon.integration.SaTokenFilter;
import cn.dev33.satoken.sso.SaSsoManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.noear.solon.Solon;
import org.noear.solon.core.handle.Filter;
import org.noear.solon.test.HttpTester;
import org.noear.solon.test.SolonTest;

/**
 * 真实 Solon 容器 + HTTP：插件 SPI、yml 配置、SaTokenFilter 登录鉴权
 */
@SolonTest(FilterApp.class)
public class FilterHttpTest extends HttpTester {

	/** 插件启动后应该注册出配置 Bean，并写进 SaManager */
	@Test
	public void plugin_shouldRegisterConfigAndCoreBeans() {
		SaTokenConfig config = Solon.context().getBean(SaTokenConfig.class);
		Assertions.assertNotNull(config);
		Assertions.assertEquals("satoken", config.getTokenName());
		Assertions.assertEquals(2_592_000L, config.getTimeout());
		Assertions.assertSame(config, SaManager.getConfig());
		Assertions.assertNotNull(Solon.context().getBean(SaBeanRegister.class));
		Assertions.assertNotNull(Solon.context().getBean(SaBeanInject.class));
	}

	/** 三个内置 Filter 应该作为 Bean 注册出来 */
	@Test
	public void builtinFilters_shouldBeRegistered() {
		Assertions.assertNotNull(Solon.context().getBean(SaTokenContextFilterForSolon.class));
		Assertions.assertNotNull(Solon.context().getBean(SaTokenCorsFilterForSolon.class));
		Assertions.assertNotNull(Solon.context().getBean(SaFirewallCheckFilterForSolon.class));
		Filter authFilter = Solon.context().getBean(SaTokenFilter.class);
		Assertions.assertNotNull(authFilter);
	}

	/** SSO / OAuth2 / ApiKey / Sign 默认配置应该由插件 Register 创建并注入 */
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
		String token = path("/login").get();
		Assertions.assertNotNull(token);
		Assertions.assertFalse(token.isEmpty());
		Assertions.assertEquals("10001", path("/user").header("satoken", token).get());
	}

	/** 未登录访问业务接口应该被 Filter 拦住 */
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

	/** @SaIgnore 接口未登录也应该能访问（注解校验被忽略） */
	@Test
	public void saIgnore_shouldSkipAnnotation() {
		Assertions.assertEquals("ignored", path("/ignored").get());
	}

	/** Gateway 子路由应该能被找到并执行 */
	@Test
	public void gateway_ping() {
		String token = path("/login").get();
		Assertions.assertEquals("pong", path("/gw/ping").header("satoken", token).get());
	}

	/** forward 适配应该转到公开接口 */
	@Test
	public void forward_shouldReachOpen() {
		Assertions.assertEquals("open", path("/fwd").get());
	}

	/** Filter auth 抛 BackResultException 时应该把消息写回响应 */
	@Test
	public void backResult_shouldWriteMessage() {
		Assertions.assertEquals("back-ok", path("/back").get());
	}

	/** CORS 策略抛 BackResultException 时应该写回响应 */
	@Test
	public void corsBack_shouldWriteMessage() {
		Assertions.assertEquals("cors-back", path("/cors-back").get());
	}

	/** 防火墙抛 BackResultException 时应该写回响应 */
	@Test
	public void firewallBack_shouldWriteMessage() {
		Assertions.assertEquals("fw-back", path("/fw-back").get());
	}

	/** 防火墙校验失败且没有自定义 failHandle 时应该写回异常信息 */
	@Test
	public void firewallFail_shouldWriteMessage() {
		Assertions.assertEquals("fw-fail", path("/fw-fail").get());
	}

}
