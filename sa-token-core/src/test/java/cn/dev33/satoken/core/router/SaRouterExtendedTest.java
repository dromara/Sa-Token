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
package cn.dev33.satoken.core.router;

import cn.dev33.satoken.context.SaHolder;
import cn.dev33.satoken.context.mock.SaRequestForMock;
import cn.dev33.satoken.context.mock.SaTokenContextMockUtil;
import cn.dev33.satoken.router.SaHttpMethod;
import cn.dev33.satoken.router.SaRouter;
import cn.dev33.satoken.router.SaRouterStaff;
import cn.dev33.satoken.test.SaTestRouteMatcher;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * SaRouter 静态方法补充测试
 */
public class SaRouterExtendedTest {

	@BeforeAll
	static void installRouteMatcher() {
		SaTestRouteMatcher.installAntStyleMatcher();
	}

	/** 静态 match/notMatch 列表重载应正确判定命中与排除 */
	@Test
	void staticMatchListAndNotMatchList() {
		SaTokenContextMockUtil.setMockContext(() -> {
			SaRequestForMock req = (SaRequestForMock) SaHolder.getRequest();
			req.requestPath = "/api/list";

			SaRouterStaff hit = SaRouter.match(Arrays.asList("/api/**", "/public/**"));
			Assertions.assertTrue(hit.isHit());

			req.requestPath = "/other";
			SaRouterStaff miss = SaRouter.match(Arrays.asList("/api/**"));
			Assertions.assertFalse(miss.isHit());

			req.requestPath = "/api/list";
			SaRouterStaff excluded = SaRouter.notMatch(Arrays.asList("/api/list"));
			Assertions.assertFalse(excluded.isHit());
		});
	}

	/** 静态 HTTP 方法与字符串方法匹配应正确 */
	@Test
	void staticMatchHttpMethodAndStringMethod() {
		SaTokenContextMockUtil.setMockContext(() -> {
			SaRequestForMock req = (SaRequestForMock) SaHolder.getRequest();
			req.requestPath = "/any";
			req.method = "POST";

			Assertions.assertTrue(SaRouter.match(SaHttpMethod.POST).isHit());
			Assertions.assertFalse(SaRouter.match(SaHttpMethod.GET).isHit());
			Assertions.assertFalse(SaRouter.notMatch(SaHttpMethod.POST).isHit());

			Assertions.assertTrue(SaRouter.matchMethod("POST", "PUT").isHit());
			Assertions.assertFalse(SaRouter.notMatchMethod("POST").isHit());
		});
	}

	/** 静态布尔与自定义函数匹配应正确组合 notMatch */
	@Test
	void staticMatchBooleanAndCustomFunction() {
		Assertions.assertTrue(SaRouter.match(true).isHit());
		Assertions.assertFalse(SaRouter.match(false).isHit());
		Assertions.assertFalse(SaRouter.notMatch(true).isHit());

		SaRouterStaff custom = SaRouter.match(r -> true).notMatch(r -> true);
		Assertions.assertFalse(custom.isHit());
	}

	/** 静态 match 带 check 重载应在命中时执行回调 */
	@Test
	void staticMatchWithCheckOverloads() {
		AtomicBoolean ran = new AtomicBoolean(false);
		SaTokenContextMockUtil.setMockContext(() -> {
			SaRequestForMock req = (SaRequestForMock) SaHolder.getRequest();
			req.requestPath = "/order/submit";
			SaRouter.match("/order/**", r -> ran.set(true));
		});
		Assertions.assertTrue(ran.get());

		ran.set(false);
		SaTokenContextMockUtil.setMockContext(() -> {
			SaRequestForMock req = (SaRequestForMock) SaHolder.getRequest();
			req.requestPath = "/order/submit";
			SaRouter.match("/order/**", "/order/guest", () -> ran.set(true));
		});
		Assertions.assertTrue(ran.get());
	}

	/** newMatch 应返回新的 SaRouterStaff 实例 */
	@Test
	void newMatchReturnsFreshStaff() {
		SaRouterStaff staff = SaRouter.newMatch();
		Assertions.assertNotNull(staff);
		Assertions.assertTrue(staff.isHit());
	}

}
