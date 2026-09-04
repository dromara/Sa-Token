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
import java.util.concurrent.atomic.AtomicReference;

/**
 * SaRouterStaff 链式路由匹配
 *
 * @author click33
 * @since 1.46.0
 */
public class SaRouterStaffTest {

	@BeforeAll
	static void installRouteMatcher() {
		SaTestRouteMatcher.installAntStyleMatcher();
	}

	/** match/notMatch 链式组合应正确判定最终命中状态 */
	@Test
	void match_notMatch_chain() {
		SaTokenContextMockUtil.setMockContext(() -> {
			SaRequestForMock req = (SaRequestForMock) SaHolder.getRequest();
			req.requestPath = "/user/profile";
			req.method = "GET";

			SaRouterStaff staff = SaRouter.newMatch()
					.match("/user/**")
					.notMatch("/user/doLogin")
					.match(SaHttpMethod.GET);

			Assertions.assertTrue(staff.isHit());
		});
	}

	/** notMatch 应排除指定路径使命中变为 false */
	@Test
	void notMatch_excludesPath() {
		SaTokenContextMockUtil.setMockContext(() -> {
			SaRequestForMock req = (SaRequestForMock) SaHolder.getRequest();
			req.requestPath = "/user/doLogin";

			SaRouterStaff staff = SaRouter.match("/user/**").notMatch("/user/doLogin");
			Assertions.assertFalse(staff.isHit());
		});
	}

	/** 字符串 varargs 路径重载应按多个规则匹配与排除 */
	@Test
	void matchAndNotMatch_stringVarargs() {
		SaTokenContextMockUtil.setMockContext(() -> {
			SaRequestForMock req = (SaRequestForMock) SaHolder.getRequest();
			req.requestPath = "/user/profile";

			Assertions.assertTrue(SaRouter.newMatch().match("/admin/**", "/user/**").isHit());
			Assertions.assertFalse(SaRouter.newMatch()
					.match("/user/**")
					.notMatch("/admin/**", "/user/**")
					.isHit());
		});
	}

	/** 命中时 check 应执行回调，未命中时不执行 */
	@Test
	void check_runsOnlyWhenHit() {
		AtomicBoolean ran = new AtomicBoolean(false);
		SaTokenContextMockUtil.setMockContext(() -> {
			SaRequestForMock req = (SaRequestForMock) SaHolder.getRequest();
			req.requestPath = "/admin/list";
			SaRouter.match("/admin/**").check(() -> ran.set(true));
		});
		Assertions.assertTrue(ran.get());
	}

	/** 命中时 staff stop 应抛出 StopMatchException */
	@Test
	void staffStop_throwsWhenHit() {
		SaTokenContextMockUtil.setMockContext(() -> {
			SaRequestForMock req = (SaRequestForMock) SaHolder.getRequest();
			req.requestPath = "/admin/list";
			Assertions.assertThrows(StopMatchException.class,
					() -> SaRouter.match("/admin/**").stop());
		});
	}

	/** 未命中时 staff stop 不应抛异常 */
	@Test
	void staffStop_noThrowWhenMiss() {
		SaTokenContextMockUtil.setMockContext(() -> {
			SaRequestForMock req = (SaRequestForMock) SaHolder.getRequest();
			req.requestPath = "/public/list";
			Assertions.assertDoesNotThrow(() -> SaRouter.match("/admin/**").stop());
		});
	}

	/** 命中时 staff back 应抛出 BackResultException 并携带结果 */
	@Test
	void staffBack_returnsResultWhenHit() {
		SaTokenContextMockUtil.setMockContext(() -> {
			SaRequestForMock req = (SaRequestForMock) SaHolder.getRequest();
			req.requestPath = "/admin/list";
			BackResultException ex = Assertions.assertThrows(BackResultException.class,
					() -> SaRouter.match("/admin/**").back("deny"));
			Assertions.assertEquals("deny", ex.result);
		});
	}

	/** free 块内 stop 应被吞掉不影响后续 check 执行 */
	@Test
	void free_swallowsStopInsideBlock() {
		AtomicReference<String> phase = new AtomicReference<>("start");
		SaTokenContextMockUtil.setMockContext(() -> {
			SaRequestForMock req = (SaRequestForMock) SaHolder.getRequest();
			req.requestPath = "/any";
			SaRouter.newMatch()
					.match(true)
					.free(r -> {
						phase.set("inside-free");
						r.stop();
					})
					.check(() -> phase.set("after-free"));
		});
		Assertions.assertEquals("after-free", phase.get());
	}

	/** reset 应将命中标志恢复为 true */
	@Test
	void reset_restoresHitFlag() {
		SaRouterStaff staff = new SaRouterStaff().setHit(false);
		Assertions.assertFalse(staff.isHit());
		staff.reset();
		Assertions.assertTrue(staff.isHit());
	}

	/** List、请求方法和条件重载应正确更新命中状态 */
	@Test
	void matchingOverloads() {
		SaTokenContextMockUtil.setMockContext(() -> {
			SaRequestForMock req = (SaRequestForMock) SaHolder.getRequest();
			req.requestPath = "/user/profile";
			req.method = "PUT";

			Assertions.assertTrue(SaRouter.newMatch().match(Arrays.asList("/user/**")).isHit());
			Assertions.assertFalse(SaRouter.newMatch().notMatch(Arrays.asList("/user/**")).isHit());
			Assertions.assertTrue(SaRouter.newMatch().match(SaHttpMethod.PUT).isHit());
			Assertions.assertFalse(SaRouter.newMatch().notMatch(SaHttpMethod.PUT).isHit());
			Assertions.assertTrue(SaRouter.newMatch().matchMethod("PUT").isHit());
			Assertions.assertFalse(SaRouter.newMatch().notMatchMethod("PUT").isHit());
			Assertions.assertTrue(SaRouter.newMatch().match(true).isHit());
			Assertions.assertTrue(SaRouter.newMatch().notMatch(false).isHit());
			Assertions.assertTrue(SaRouter.newMatch().match(staff -> staff instanceof SaRouterStaff).isHit());
			Assertions.assertFalse(SaRouter.newMatch().notMatch(staff -> true).isHit());
		});
	}

	/** 排除规则不命中时应保持命中状态 */
	@Test
	void notMatchOverloads_keepHitWhenRuleMisses() {
		SaTokenContextMockUtil.setMockContext(() -> {
			SaRequestForMock req = (SaRequestForMock) SaHolder.getRequest();
			req.requestPath = "/user/profile";
			req.method = "PUT";

			Assertions.assertTrue(SaRouter.newMatch().notMatch(Arrays.asList("/admin/**")).isHit());
			Assertions.assertTrue(SaRouter.newMatch().notMatch(SaHttpMethod.POST).isHit());
			Assertions.assertTrue(SaRouter.newMatch().notMatchMethod("POST").isHit());
			Assertions.assertTrue(SaRouter.newMatch().notMatch(staff -> false).isHit());
		});
	}

	/** 已失配的链不应再计算后续条件或执行回调 */
	@Test
	void missedChain_skipsSubsequentOperations() {
		AtomicBoolean evaluated = new AtomicBoolean(false);
		SaRouter.newMatch()
				.match(false)
				.match(staff -> {
					evaluated.set(true);
					return true;
				})
				.notMatch(staff -> {
					evaluated.set(true);
					return false;
				})
				.check(() -> evaluated.set(true))
				.free(staff -> evaluated.set(true));
		Assertions.assertFalse(evaluated.get());
	}

	/** 已失配时各公共匹配 API 应短路，不访问请求或执行回调 */
	@Test
	void missedChain_shortCircuitsPublicMatchingApis() {
		AtomicBoolean callbackCalled = new AtomicBoolean(false);
		SaRouterStaff staff = SaRouter.newMatch().match(false);
		Assertions.assertSame(staff, staff.match("/user/**"));
		Assertions.assertSame(staff, staff.notMatch("/admin/**"));
		Assertions.assertSame(staff, staff.match(Arrays.asList("/user/**")));
		Assertions.assertSame(staff, staff.notMatch(Arrays.asList("/admin/**")));
		Assertions.assertSame(staff, staff.match(SaHttpMethod.GET));
		Assertions.assertSame(staff, staff.notMatch(SaHttpMethod.POST));
		Assertions.assertSame(staff, staff.matchMethod("GET"));
		Assertions.assertSame(staff, staff.notMatchMethod("POST"));
		Assertions.assertSame(staff, staff.notMatch(false));
		Assertions.assertSame(staff, staff.match(ignored -> {
			callbackCalled.set(true);
			return true;
		}));
		Assertions.assertSame(staff, staff.notMatch(ignored -> {
			callbackCalled.set(true);
			return false;
		}));
		Assertions.assertSame(staff, staff.check(ignored -> callbackCalled.set(true)));
		Assertions.assertSame(staff, staff.match("/user/**", () -> callbackCalled.set(true)));
		Assertions.assertSame(staff, staff.match("/user/**", ignored -> callbackCalled.set(true)));
		Assertions.assertSame(staff, staff.match("/user/**", "/login", () -> callbackCalled.set(true)));
		Assertions.assertSame(staff, staff.match("/user/**", "/login", ignored -> callbackCalled.set(true)));

		Assertions.assertFalse(staff.isHit());
		Assertions.assertFalse(callbackCalled.get());
	}

	/** 带参 check、free 与直接匹配重载应暴露当前 staff */
	@Test
	void callbackOverloads_receiveStaff() {
		AtomicBoolean checkCalled = new AtomicBoolean(false);
		AtomicBoolean freeCalled = new AtomicBoolean(false);
		AtomicBoolean matchCalled = new AtomicBoolean(false);
		AtomicBoolean excludedMatchCalled = new AtomicBoolean(false);
		AtomicBoolean matchWithExcludeCalled = new AtomicBoolean(false);
		SaTokenContextMockUtil.setMockContext(() -> {
			SaRequestForMock req = (SaRequestForMock) SaHolder.getRequest();
			req.requestPath = "/user/profile";

			SaRouter.newMatch().check(staff -> checkCalled.set(staff.isHit()));
			SaRouter.newMatch().free(staff -> freeCalled.set(staff.isHit()));
			SaRouter.newMatch().match("/user/**", staff -> matchCalled.set(staff.isHit()));
			SaRouter.newMatch().match("/user/**", "/user/profile", () -> excludedMatchCalled.set(true));
			SaRouter.newMatch().match("/user/**", "/admin/**", () -> matchWithExcludeCalled.set(true));
		});
		Assertions.assertTrue(checkCalled.get());
		Assertions.assertTrue(freeCalled.get());
		Assertions.assertTrue(matchCalled.get());
		Assertions.assertFalse(excludedMatchCalled.get());
		Assertions.assertTrue(matchWithExcludeCalled.get());
	}

	/** 未命中时 back 应返回同一链对象 */
	@Test
	void staffBack_returnsSelfWhenMiss() {
		SaRouterStaff staff = SaRouter.newMatch().match(false);
		Assertions.assertSame(staff, staff.stop());
		Assertions.assertSame(staff, staff.back());
		Assertions.assertSame(staff, staff.back("ignored"));
	}

}
