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

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

/**
 * SaRouterStaff 链式路由匹配
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

}
