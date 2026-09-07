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
package cn.dev33.satoken.loveqq.boot.utils;

import cn.dev33.satoken.context.SaHolder;
import cn.dev33.satoken.loveqq.boot.testsupport.LoveqqTestHelper;
import cn.dev33.satoken.loveqq.boot.testsupport.TestServerRequest;
import cn.dev33.satoken.loveqq.boot.testsupport.TestServerResponse;
import cn.dev33.satoken.test.SaTokenTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * {@link SaTokenContextUtil} 上下文读写测试
 */
@SaTokenTest
public class SaTokenContextUtilTest {

	/** 每个用例开始前挂上 LoveQQ 策略 */
	@BeforeEach
	public void setUp() {
		LoveqqTestHelper.ensureLoveqqStrategy();
	}

	/** 无回调的 setContext 应该写入上下文，手动清理后失效 */
	@Test
	public void setContext_withoutCallback_manualClear() {
		TestServerRequest request = LoveqqTestHelper.newGetRequest("/ctx");
		TestServerResponse response = LoveqqTestHelper.newResponse();
		SaTokenContextUtil.setContext(request, response);
		try {
			Assertions.assertNotNull(SaTokenContextUtil.getModelBox());
			Assertions.assertSame(request, SaTokenContextUtil.getRequest());
			Assertions.assertSame(response, SaTokenContextUtil.getResponse());
			Assertions.assertEquals("/ctx", SaHolder.getRequest().getRequestPath());
		} finally {
			SaTokenContextUtil.clearContext(null);
		}
		Assertions.assertThrows(Exception.class, () -> SaHolder.getRequest().getRequestPath());
	}

	/** 有外层上下文时 clearContext 应该把外层加回去 */
	@Test
	public void clearContext_restorePrevious() {
		TestServerRequest outerReq = LoveqqTestHelper.newGetRequest("/outer");
		TestServerResponse outerRes = LoveqqTestHelper.newResponse();
		TestServerRequest innerReq = LoveqqTestHelper.newGetRequest("/inner");
		TestServerResponse innerRes = LoveqqTestHelper.newResponse();

		cn.dev33.satoken.context.model.SaTokenContextModelBox outer = SaTokenContextUtil.setContext(outerReq, outerRes);
		try {
			cn.dev33.satoken.context.model.SaTokenContextModelBox innerPrev = SaTokenContextUtil.setContext(innerReq, innerRes);
			Assertions.assertEquals("/inner", SaHolder.getRequest().getRequestPath());
			SaTokenContextUtil.clearContext(innerPrev);
			Assertions.assertEquals("/outer", SaHolder.getRequest().getRequestPath());
		} finally {
			SaTokenContextUtil.clearContext(outer);
		}
	}

	/** Runnable 版 setContext 应该在执行后自动清理上下文 */
	@Test
	public void setContext_withRunnable_autoClear() {
		TestServerRequest request = LoveqqTestHelper.newGetRequest("/ctx");
		TestServerResponse response = LoveqqTestHelper.newResponse();
		SaTokenContextUtil.setContext(request, response, () ->
				Assertions.assertEquals("/ctx", SaHolder.getRequest().getRequestPath()));
		Assertions.assertThrows(Exception.class, () -> SaHolder.getRequest().getRequestPath());
	}

	/** Runnable 版抛异常时也应该清理上下文 */
	@Test
	public void setContext_withRunnable_clearOnException() {
		TestServerRequest request = LoveqqTestHelper.newGetRequest("/ctx");
		TestServerResponse response = LoveqqTestHelper.newResponse();
		Assertions.assertThrows(RuntimeException.class, () ->
				SaTokenContextUtil.setContext(request, response, () -> {
					throw new RuntimeException("boom");
				}));
		Assertions.assertThrows(Exception.class, () -> SaHolder.getRequest().getRequestPath());
	}

	/** 泛型版 setContext 应该返回函数结果并在结束后清理上下文 */
	@Test
	public void setContext_withGenericFunction_returnValue() {
		TestServerRequest request = LoveqqTestHelper.newGetRequest("/ctx");
		TestServerResponse response = LoveqqTestHelper.newResponse();
		String value = SaTokenContextUtil.setContext(request, response, () -> "ok");
		Assertions.assertEquals("ok", value);
		Assertions.assertThrows(Exception.class, () -> SaHolder.getRequest().getRequestPath());
	}

	/** 工具类默认构造应该能 new 出来 */
	@Test
	public void constructor_shouldCreateInstance() {
		Assertions.assertNotNull(new SaTokenContextUtil());
	}

}
