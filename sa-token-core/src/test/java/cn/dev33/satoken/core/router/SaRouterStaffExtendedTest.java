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
import cn.dev33.satoken.exception.BackResultException;
import cn.dev33.satoken.exception.StopMatchException;
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
 * SaRouterStaff 扩展路由匹配测试
 */
public class SaRouterStaffExtendedTest {

	@BeforeAll
	static void installRouteMatcher() {
		SaTestRouteMatcher.installAntStyleMatcher();
	}

	/** 链式 match/notMatch 列表重载应正确判定命中与排除 */
	@Test
	void matchListAndNotMatchList() {
		SaTokenContextMockUtil.setMockContext(() -> {
			SaRequestForMock req = (SaRequestForMock) SaHolder.getRequest();
			req.requestPath = "/api/user/list";

			SaRouterStaff hit = SaRouter.newMatch().match(Arrays.asList("/api/**", "/public/**"));
			Assertions.assertTrue(hit.isHit());

			req.requestPath = "/other";
			SaRouterStaff miss = SaRouter.newMatch().match(Arrays.asList("/api/**"));
			Assertions.assertFalse(miss.isHit());

			req.requestPath = "/api/user/list";
			SaRouterStaff excluded = SaRouter.newMatch()
					.match("/api/**")
					.notMatch(Arrays.asList("/api/user/list", "/api/admin/**"));
			Assertions.assertFalse(excluded.isHit());
		});
	}

	/** matchMethod/notMatchMethod 应正确匹配与排除 HTTP 方法 */
	@Test
	void matchMethodAndNotMatchMethod() {
		SaTokenContextMockUtil.setMockContext(() -> {
			SaRequestForMock req = (SaRequestForMock) SaHolder.getRequest();
			req.requestPath = "/any";
			req.method = "POST";

			SaRouterStaff postHit = SaRouter.newMatch().matchMethod("POST", "PUT");
			Assertions.assertTrue(postHit.isHit());

			SaRouterStaff getMiss = SaRouter.newMatch().matchMethod("GET");
			Assertions.assertFalse(getMiss.isHit());

			req.method = "DELETE";
			SaRouterStaff deleteExcluded = SaRouter.newMatch()
					.match(true)
					.notMatchMethod("DELETE");
			Assertions.assertFalse(deleteExcluded.isHit());
		});
	}

	/** SaHttpMethod 枚举匹配与 notMatch 组合应正确 */
	@Test
	void matchSaHttpMethodEnum() {
		SaTokenContextMockUtil.setMockContext(() -> {
			SaRequestForMock req = (SaRequestForMock) SaHolder.getRequest();
			req.requestPath = "/any";
			req.method = "GET";

			Assertions.assertTrue(SaRouter.newMatch().match(SaHttpMethod.GET).isHit());
			Assertions.assertFalse(SaRouter.newMatch().match(SaHttpMethod.POST).isHit());
			Assertions.assertFalse(SaRouter.newMatch().match(SaHttpMethod.GET).notMatch(SaHttpMethod.GET).isHit());
		});
	}

	/** isMatchCurrURI/Method 与 staff 链式匹配应一致 */
	@Test
	void isMatchCurrUriAndMethod_viaStaff() {
		SaTokenContextMockUtil.setMockContext(() -> {
			SaRequestForMock req = (SaRequestForMock) SaHolder.getRequest();
			req.requestPath = "/order/detail";
			req.method = "PUT";

			Assertions.assertTrue(SaRouter.isMatchCurrURI("/order/**"));
			Assertions.assertTrue(SaRouter.isMatchCurrURI(new String[] {"/order/**", "/cart/**"}));
			Assertions.assertTrue(SaRouter.isMatchCurrURI(Arrays.asList("/order/**")));
			Assertions.assertFalse(SaRouter.isMatchCurrURI("/cart/**"));

			Assertions.assertTrue(SaRouter.isMatchCurrMethod(new SaHttpMethod[] {SaHttpMethod.PUT}));
			Assertions.assertFalse(SaRouter.isMatchCurrMethod(new SaHttpMethod[] {SaHttpMethod.GET}));

			SaRouterStaff staff = SaRouter.newMatch()
					.match("/order/**")
					.match(SaHttpMethod.PUT);
			Assertions.assertTrue(staff.isHit());
		});
	}

	/** 布尔与自定义函数链式匹配应正确更新命中状态 */
	@Test
	void matchBooleanAndCustomFunction() {
		SaRouterStaff flagHit = new SaRouterStaff().match(true).notMatch(false);
		Assertions.assertTrue(flagHit.isHit());

		SaRouterStaff flagMiss = new SaRouterStaff().match(false);
		Assertions.assertFalse(flagMiss.isHit());
		Assertions.assertFalse(new SaRouterStaff().setHit(false).match(true).isHit());

		SaRouterStaff custom = new SaRouterStaff().match(r -> true).notMatch(r -> true);
		Assertions.assertFalse(custom.isHit());
	}

	/** match 带 check 重载命中时应执行回调 */
	@Test
	void matchWithCheckOverload() {
		AtomicBoolean ran = new AtomicBoolean(false);
		SaTokenContextMockUtil.setMockContext(() -> {
			SaRequestForMock req = (SaRequestForMock) SaHolder.getRequest();
			req.requestPath = "/pay/submit";
			SaRouter.match("/pay/**", () -> ran.set(true));
		});
		Assertions.assertTrue(ran.get());
	}

	/** stop 与无参/有参 back 在命中时应抛出对应异常 */
	@Test
	void stopAndBackVariants() {
		SaTokenContextMockUtil.setMockContext(() -> {
			SaRequestForMock req = (SaRequestForMock) SaHolder.getRequest();
			req.requestPath = "/secure/data";

			Assertions.assertThrows(StopMatchException.class,
					() -> SaRouter.match("/secure/**").stop());

			BackResultException emptyBack = Assertions.assertThrows(BackResultException.class,
					() -> SaRouter.match("/secure/**").back());
			Assertions.assertEquals("", emptyBack.result);

			BackResultException valueBack = Assertions.assertThrows(BackResultException.class,
					() -> SaRouter.match("/secure/**").back(403));
			Assertions.assertEquals(403, valueBack.result);
		});
	}

	/** 带排除路径的 match 重载应跳过排除路径 */
	@Test
	void matchWithExcludePatternOverload() {
		AtomicBoolean ran = new AtomicBoolean(false);
		SaTokenContextMockUtil.setMockContext(() -> {
			SaRequestForMock req = (SaRequestForMock) SaHolder.getRequest();
			req.requestPath = "/user/info";
			SaRouter.match("/user/**", "/user/doLogin", () -> ran.set(true));
		});
		Assertions.assertTrue(ran.get());

		ran.set(false);
		SaTokenContextMockUtil.setMockContext(() -> {
			SaRequestForMock req = (SaRequestForMock) SaHolder.getRequest();
			req.requestPath = "/user/doLogin";
			SaRouter.match("/user/**", "/user/doLogin", () -> ran.set(true));
		});
		Assertions.assertFalse(ran.get());
	}

}
