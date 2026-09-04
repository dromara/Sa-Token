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
import cn.dev33.satoken.test.SaTestRouteMatcher;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * SaRouter 路由匹配
 */
public class SaRouterTest {

	@BeforeAll
	static void installRouteMatcher() {
		SaTestRouteMatcher.installAntStyleMatcher();
	}

	/** isMatch 应正确匹配路径模式并处理 null 与空集合 */
	@Test
	void isMatch_pathPatterns() {
		Assertions.assertTrue(SaRouter.isMatch("/user/**", "/user/list"));
		Assertions.assertTrue(SaRouter.isMatch(new String[] {"/admin/**", "/user/**"}, "/admin/config"));
		Assertions.assertTrue(SaRouter.isMatch(Arrays.asList("/api/**"), "/api/v1/info"));
		Assertions.assertFalse(SaRouter.isMatch("/user/**", "/order/list"));
		Assertions.assertFalse(SaRouter.isMatch((String[]) null, "/user/list"));
		Assertions.assertFalse(SaRouter.isMatch(Collections.<String>emptyList(), "/user/list"));
	}

	/** isMatch 应正确匹配 HTTP 方法枚举 */
	@Test
	void isMatch_httpMethod() {
		Assertions.assertTrue(SaRouter.isMatch(new SaHttpMethod[] {SaHttpMethod.GET}, "GET"));
		Assertions.assertTrue(SaRouter.isMatch(new SaHttpMethod[] {SaHttpMethod.ALL}, "POST"));
		Assertions.assertFalse(SaRouter.isMatch(new SaHttpMethod[] {SaHttpMethod.POST}, "GET"));
		Assertions.assertFalse(SaRouter.isMatch((SaHttpMethod[]) null, "GET"));
	}

	/** Mock 上下文中 isMatchCurrURI 应匹配当前请求路径 */
	@Test
	void isMatchCurrURI_withMockContext() {
		SaTokenContextMockUtil.setMockContext(() -> {
			SaRequestForMock req = (SaRequestForMock) SaHolder.getRequest();
			req.requestPath = "/user/doLogin";

			Assertions.assertTrue(SaRouter.isMatchCurrURI("/user/**"));
			Assertions.assertFalse(SaRouter.isMatchCurrURI("/admin/**"));
		});
	}

	/** Mock 上下文中 isMatchCurrMethod 应匹配当前请求方法 */
	@Test
	void isMatchCurrMethod_withMockContext() {
		SaTokenContextMockUtil.setMockContext(() -> {
			SaRequestForMock req = (SaRequestForMock) SaHolder.getRequest();
			req.method = "POST";

			Assertions.assertTrue(SaRouter.isMatchCurrMethod(new SaHttpMethod[] {SaHttpMethod.POST}));
			Assertions.assertFalse(SaRouter.isMatchCurrMethod(new SaHttpMethod[] {SaHttpMethod.GET}));
		});
	}

	/** stop 应抛出 StopMatchException */
	@Test
	void stop_throwsStopMatchException() {
		Assertions.assertThrows(StopMatchException.class, SaRouter::stop);
	}

	/** back 应抛出 BackResultException 并携带返回结果 */
	@Test
	void back_throwsBackResultException() {
		Assertions.assertThrows(BackResultException.class, SaRouter::back);
		BackResultException ex = Assertions.assertThrows(BackResultException.class, () -> SaRouter.back("ok"));
		Assertions.assertEquals("ok", ex.result);
	}

	/** 路径命中时 match 应执行 check 回调 */
	@Test
	void matchPattern_runsCheckWhenHit() {
		AtomicBoolean checked = new AtomicBoolean(false);
		SaTokenContextMockUtil.setMockContext(() -> {
			SaRequestForMock req = (SaRequestForMock) SaHolder.getRequest();
			req.requestPath = "/user/info";
			SaRouter.match("/user/**", () -> checked.set(true));
		});
		Assertions.assertTrue(checked.get());
	}

	/** 路径未命中时 match 不应执行 check 回调 */
	@Test
	void matchPattern_skipsCheckWhenMiss() {
		AtomicBoolean checked = new AtomicBoolean(false);
		SaTokenContextMockUtil.setMockContext(() -> {
			SaRequestForMock req = (SaRequestForMock) SaHolder.getRequest();
			req.requestPath = "/public/info";
			SaRouter.match("/user/**", () -> checked.set(true));
		});
		Assertions.assertFalse(checked.get());
	}

}
