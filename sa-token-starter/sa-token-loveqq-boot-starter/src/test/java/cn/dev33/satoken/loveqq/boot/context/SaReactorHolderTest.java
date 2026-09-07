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
package cn.dev33.satoken.loveqq.boot.context;

import cn.dev33.satoken.context.SaHolder;
import cn.dev33.satoken.loveqq.boot.testsupport.LoveqqTestHelper;
import cn.dev33.satoken.loveqq.boot.testsupport.TestServerRequest;
import cn.dev33.satoken.loveqq.boot.testsupport.TestServerResponse;
import cn.dev33.satoken.test.SaTokenTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.util.context.Context;

/**
 * {@link SaReactorHolder} 响应式上下文读写测试
 */
@SaTokenTest
public class SaReactorHolderTest {

	/** 每个用例开始前挂上 LoveQQ 策略 */
	@BeforeEach
	public void setUp() {
		LoveqqTestHelper.ensureLoveqqStrategy();
	}

	/** getRequest / getResponse 应该能从 Reactor Context 里取出请求响应 */
	@Test
	public void getRequestAndResponse_fromReactorContext() {
		TestServerRequest request = LoveqqTestHelper.newGetRequest("/rx");
		TestServerResponse response = LoveqqTestHelper.newResponse();
		Context ctx = Context.of(SaReactorHolder.REQUEST_CONTEXT_ATTRIBUTE, request)
				.put(SaReactorHolder.RESPONSE_CONTEXT_ATTRIBUTE, response);

		ServerRequestHolderAsserts(request, response, ctx);
	}

	/** sync 应该把 Reactor 上下文写进同步 SaHolder，执行完再清掉 */
	@Test
	public void sync_writeAndClearThreadContext() {
		TestServerRequest request = LoveqqTestHelper.newGetRequest("/rx");
		TestServerResponse response = LoveqqTestHelper.newResponse();
		Context ctx = Context.of(SaReactorHolder.REQUEST_CONTEXT_ATTRIBUTE, request)
				.put(SaReactorHolder.RESPONSE_CONTEXT_ATTRIBUTE, response);

		String value = SaReactorHolder.sync(() -> {
			Assertions.assertEquals("/rx", SaHolder.getRequest().getRequestPath());
			return "ok";
		}).contextWrite(ctx).block();

		Assertions.assertEquals("ok", value);
		Assertions.assertThrows(Exception.class, () -> SaHolder.getRequest().getRequestPath());
	}

	/** 默认构造应该能 new 出来 */
	@Test
	public void constructor_shouldCreateInstance() {
		Assertions.assertNotNull(new SaReactorHolder());
	}

	private void ServerRequestHolderAsserts(TestServerRequest request, TestServerResponse response, Context ctx) {
		Assertions.assertSame(request, SaReactorHolder.getRequest().contextWrite(ctx).block());
		Assertions.assertSame(response, SaReactorHolder.getResponse().contextWrite(ctx).block());
	}

}
