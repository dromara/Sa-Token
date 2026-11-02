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
package cn.dev33.satoken.test;

import cn.dev33.satoken.fun.strategy.SaCreateTokenFunction;
import cn.dev33.satoken.fun.strategy.SaGetSaTokenConfigFunction;
import cn.dev33.satoken.fun.strategy.SaRouteMatchFunction;
import cn.dev33.satoken.session.SaSession;
import cn.dev33.satoken.strategy.SaStrategy;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * SaTokenTestContext 对 SaStrategy 的快照复位测试
 */
@SaTokenTest
public class SaTokenTestContextTest {

	/** capture/restore 应该把策略字段写回原引用 */
	@Test
	void captureRestore_shouldRevertSaStrategyFields() {
		SaStrategy s = SaStrategy.instance;
		SaCreateTokenFunction originCreateToken = s.createToken;
		SaRouteMatchFunction originRouteMatcher = s.routeMatcher;
		SaGetSaTokenConfigFunction originGetSaTokenConfig = s.getSaTokenConfig;
		Class<? extends SaSession> originSessionClassType = s.sessionClassType;

		SaTokenTestContext.Snapshot snapshot = SaTokenTestContext.capture();
		s.createToken = (loginId, loginType) -> "mutated-token";
		s.routeMatcher = (pattern, path) -> true;
		s.getSaTokenConfig = () -> null;
		s.sessionClassType = SaSession.class;
		snapshot.restore();

		Assertions.assertSame(originCreateToken, s.createToken);
		Assertions.assertSame(originRouteMatcher, s.routeMatcher);
		Assertions.assertSame(originGetSaTokenConfig, s.getSaTokenConfig);
		Assertions.assertSame(originSessionClassType, s.sessionClassType);
	}

	/** reset 应该把 getSaTokenConfig 写回默认（null） */
	@Test
	void reset_shouldRestoreDefaultGetSaTokenConfig() {
		SaStrategy.instance.getSaTokenConfig = () -> null;
		SaTokenTestContext.reset();
		Assertions.assertNull(SaStrategy.instance.getSaTokenConfig);
	}

}
