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
package cn.dev33.satoken.core.strategy;

import cn.dev33.satoken.SaManager;
import cn.dev33.satoken.config.SaTokenConfig;
import cn.dev33.satoken.error.SaErrorCode;
import cn.dev33.satoken.exception.NotImplException;
import cn.dev33.satoken.exception.SaTokenException;
import cn.dev33.satoken.fun.strategy.SaAutoRenewFunction;
import cn.dev33.satoken.fun.strategy.SaCorsHandleFunction;
import cn.dev33.satoken.fun.strategy.SaCreateSaRequestFunction;
import cn.dev33.satoken.fun.strategy.SaCreateSaResponseFunction;
import cn.dev33.satoken.fun.strategy.SaCreateSaStorageFunction;
import cn.dev33.satoken.fun.strategy.SaCreateSessionFunction;
import cn.dev33.satoken.fun.strategy.SaCreateStpLogicFunction;
import cn.dev33.satoken.fun.strategy.SaCreateTokenFunction;
import cn.dev33.satoken.fun.strategy.SaGenerateUniqueTokenFunction;
import cn.dev33.satoken.fun.strategy.SaGetSaTokenConfigFunction;
import cn.dev33.satoken.fun.strategy.SaHasElementFunction;
import cn.dev33.satoken.fun.strategy.SaRouteMatchFunction;
import cn.dev33.satoken.session.SaSession;
import cn.dev33.satoken.strategy.SaStrategy;
import cn.dev33.satoken.stp.StpLogic;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.test.SaTokenTest;
import cn.dev33.satoken.util.SaTokenConsts;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * SaStrategy 默认策略函数测试
 *
 * @author click33
 * @since 1.46.0
 */
@SaTokenTest
public class SaStrategyTest {

	private SaCreateTokenFunction savedCreateToken;
	private SaCreateSessionFunction savedCreateSession;
	private SaHasElementFunction savedHasElement;
	private SaGenerateUniqueTokenFunction savedGenerateUniqueToken;
	private SaAutoRenewFunction savedAutoRenew;
	private SaCreateStpLogicFunction savedCreateStpLogic;
	private SaRouteMatchFunction savedRouteMatcher;
	private SaCreateSaRequestFunction savedCreateSaRequest;
	private SaCreateSaResponseFunction savedCreateSaResponse;
	private SaCreateSaStorageFunction savedCreateSaStorage;
	private SaCorsHandleFunction savedCorsHandle;
	private SaGetSaTokenConfigFunction savedGetSaTokenConfig;

	@BeforeEach
	void saveStrategy() {
		SaStrategy s = SaStrategy.instance;
		savedCreateToken = s.createToken;
		savedCreateSession = s.createSession;
		savedHasElement = s.hasElement;
		savedGenerateUniqueToken = s.generateUniqueToken;
		savedAutoRenew = s.autoRenew;
		savedCreateStpLogic = s.createStpLogic;
		savedRouteMatcher = s.routeMatcher;
		savedCreateSaRequest = s.createSaRequest;
		savedCreateSaResponse = s.createSaResponse;
		savedCreateSaStorage = s.createSaStorage;
		savedCorsHandle = s.corsHandle;
		savedGetSaTokenConfig = s.getSaTokenConfig;
	}

	@AfterEach
	void restoreStrategy() {
		SaStrategy s = SaStrategy.instance;
		s.createToken = savedCreateToken;
		s.createSession = savedCreateSession;
		s.hasElement = savedHasElement;
		s.generateUniqueToken = savedGenerateUniqueToken;
		s.autoRenew = savedAutoRenew;
		s.createStpLogic = savedCreateStpLogic;
		s.routeMatcher = savedRouteMatcher;
		s.createSaRequest = savedCreateSaRequest;
		s.createSaResponse = savedCreateSaResponse;
		s.createSaStorage = savedCreateSaStorage;
		s.corsHandle = savedCorsHandle;
		s.getSaTokenConfig = savedGetSaTokenConfig;
	}

	/** createToken 应按 tokenStyle 配置生成不同格式的 Token */
	@Test
	void createToken_byTokenStyle() {
		StpLogic stpLogic = StpUtil.stpLogic;
		stpLogic.setConfig(null);
		SaTokenConfig config = SaManager.getConfig();

		config.setTokenStyle(SaTokenConsts.TOKEN_STYLE_UUID);
		String uuid = SaStrategy.instance.createToken.apply(10001, stpLogic.getLoginType());
		Assertions.assertTrue(uuid.contains("-"));

		config.setTokenStyle(SaTokenConsts.TOKEN_STYLE_SIMPLE_UUID);
		String simpleUuid = SaStrategy.instance.createToken.apply(10001, stpLogic.getLoginType());
		Assertions.assertEquals(32, simpleUuid.length());
		Assertions.assertFalse(simpleUuid.contains("-"));

		config.setTokenStyle(SaTokenConsts.TOKEN_STYLE_RANDOM_32);
		Assertions.assertEquals(32, SaStrategy.instance.createToken.apply(10001, stpLogic.getLoginType()).length());

		config.setTokenStyle(SaTokenConsts.TOKEN_STYLE_RANDOM_64);
		Assertions.assertEquals(64, SaStrategy.instance.createToken.apply(10001, stpLogic.getLoginType()).length());

		config.setTokenStyle(SaTokenConsts.TOKEN_STYLE_RANDOM_128);
		Assertions.assertEquals(128, SaStrategy.instance.createToken.apply(10001, stpLogic.getLoginType()).length());

		config.setTokenStyle(SaTokenConsts.TOKEN_STYLE_TIK);
		String tik = SaStrategy.instance.createToken.apply(10001, stpLogic.getLoginType());
		Assertions.assertTrue(tik.endsWith("__"));

		config.setTokenStyle("invalid-style");
		String fallback = SaStrategy.instance.createToken.apply(10001, stpLogic.getLoginType());
		Assertions.assertTrue(fallback.contains("-"));
	}

	/** createSession、hasElement 与 generateUniqueToken 默认行为应正确 */
	@Test
	void createSession_hasElement_generateUniqueToken() {
		SaSession session = SaStrategy.instance.createSession.apply("sid-1");
		Assertions.assertEquals("sid-1", session.getId());

		List<String> list = Arrays.asList("user:*", "admin");
		Assertions.assertFalse(SaStrategy.instance.hasElement.apply(null, "user:1"));
		Assertions.assertFalse(SaStrategy.instance.hasElement.apply(Collections.emptyList(), "user:1"));
		Assertions.assertTrue(SaStrategy.instance.hasElement.apply(list, "admin"));
		Assertions.assertTrue(SaStrategy.instance.hasElement.apply(list, "user:1001"));

		AtomicInteger counter = new AtomicInteger();
		String unique = SaStrategy.instance.generateUniqueToken.execute(
				"token",
				3,
				() -> "tk-" + counter.incrementAndGet(),
				token -> token.endsWith("3")
		);
		Assertions.assertEquals("tk-3", unique);

		Assertions.assertThrows(SaTokenException.class, () ->
				SaStrategy.instance.generateUniqueToken.execute(
						"token",
						2,
						() -> "same",
						token -> false
				)
		);

		String direct = SaStrategy.instance.generateUniqueToken.execute(
				"token",
				-1,
				() -> "once",
				token -> false
		);
		Assertions.assertEquals("once", direct);
	}

	/** autoRenew、createStpLogic 及未实现策略的默认行为应正确 */
	@Test
	void autoRenew_createStpLogic_andNotImplStrategies() {
		SaTokenConfig config = SaManager.getConfig();
		config.setAutoRenew(false);
		StpLogic stpLogic = StpUtil.stpLogic;
		stpLogic.setConfig(null);
		Assertions.assertFalse(SaStrategy.instance.autoRenew.apply(stpLogic));

		StpLogic custom = SaStrategy.instance.createStpLogic.apply("custom");
		Assertions.assertEquals("custom", custom.getLoginType());

		SaStrategy.instance.routeMatcher = (pattern, path) -> {
			throw new NotImplException("未实现具体路由匹配策略").setCode(SaErrorCode.CODE_12401);
		};
		Assertions.assertThrows(NotImplException.class,
				() -> SaStrategy.instance.routeMatcher.apply("/a/**", "/a/b"));
		Assertions.assertThrows(NotImplException.class,
				() -> SaStrategy.instance.createSaRequest.apply(new Object()));
		Assertions.assertThrows(NotImplException.class,
				() -> SaStrategy.instance.createSaResponse.apply(new Object()));
		Assertions.assertThrows(NotImplException.class,
				() -> SaStrategy.instance.createSaStorage.apply(new Object()));

		Assertions.assertDoesNotThrow(() ->
				SaStrategy.instance.corsHandle.execute(null, null, null));

		Assertions.assertNull(SaStrategy.instance.getSaTokenConfig);
	}

	/** 链式 set 方法应正确替换策略函数并返回自身 */
	@Test
	void setChainMethods() {
		SaCreateTokenFunction customToken = (loginId, loginType) -> "custom-token";
		AtomicBoolean renewed = new AtomicBoolean(false);

		SaStrategy strategy = SaStrategy.instance
				.setCreateToken(customToken)
				.setCreateSession(sessionId -> new SaSession("custom-" + sessionId))
				.setHasElement((list, element) -> true)
				.setAutoRenew(stp -> {
					renewed.set(true);
					return true;
				})
				.setCreateStpLogic(loginType -> new StpLogic("chain-" + loginType));

		Assertions.assertEquals("custom-token", strategy.createToken.apply(1, "login"));
		Assertions.assertEquals("custom-sid", strategy.createSession.apply("sid").getId());
		Assertions.assertTrue(strategy.hasElement.apply(Collections.emptyList(), "x"));
		strategy.autoRenew.apply(StpUtil.stpLogic);
		Assertions.assertTrue(renewed.get());
		Assertions.assertEquals("chain-app", strategy.createStpLogic.apply("app").getLoginType());
		Assertions.assertSame(strategy, strategy.setGenerateUniqueToken(strategy.generateUniqueToken));
	}

	/** 上下文与配置相关的链式 set 方法应正确生效 */
	@Test
	void setCreateContextAndConfigChainMethods() {
		SaTokenConfig config = new SaTokenConfig();
		config.setTokenName("chain-config");

		SaStrategy strategy = SaStrategy.instance
				.setCreateSaRequest(source -> null)
				.setCreateSaResponse(source -> null)
				.setCreateSaStorage(source -> null)
				.setGetSaTokenConfig(() -> config);

		Assertions.assertNull(strategy.createSaRequest.apply(new Object()));
		Assertions.assertNull(strategy.createSaResponse.apply(new Object()));
		Assertions.assertNull(strategy.createSaStorage.apply(new Object()));
		Assertions.assertEquals("chain-config", strategy.getSaTokenConfig.get().getTokenName());
	}

}
