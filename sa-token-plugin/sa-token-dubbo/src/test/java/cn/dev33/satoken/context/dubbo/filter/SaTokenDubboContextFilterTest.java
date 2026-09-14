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
package cn.dev33.satoken.context.dubbo.filter;

import cn.dev33.satoken.context.SaHolder;
import cn.dev33.satoken.context.dubbo.model.SaRequestForDubbo;
import cn.dev33.satoken.context.dubbo.support.DubboTestSupport;
import cn.dev33.satoken.test.SaTokenTest;
import org.apache.dubbo.rpc.AsyncRpcResult;
import org.apache.dubbo.rpc.Invoker;
import org.apache.dubbo.rpc.Result;
import org.apache.dubbo.rpc.RpcException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Context Filter：没上下文时挂 Dubbo 包装，finally 必须清掉。
 */
@SaTokenTest
public class SaTokenDubboContextFilterTest {

	private final SaTokenDubboContextFilter filter = new SaTokenDubboContextFilter();

	/** 每条用例后清掉 mock 上下文和 RpcContext */
	@AfterEach
	public void cleanup() {
		DubboTestSupport.cleanup();
	}

	/** 已经有上下文时不应该再换成 Dubbo 包装，调完现场还在 */
	@Test
	public void alreadyValid_skipsDubboContext() {
		DubboTestSupport.mockContext();
		Invoker<Object> invoker = DubboTestSupport.invoker(invo -> {
			Assertions.assertFalse(SaHolder.getRequest() instanceof SaRequestForDubbo);
			return AsyncRpcResult.newDefaultAsyncResult("ok", invo);
		});
		Result result = filter.invoke(invoker, DubboTestSupport.invocation());
		Assertions.assertEquals("ok", result.getValue());
		Assertions.assertTrue(SaHolder.getContext().isValid());
	}

	/** 没上下文时调用期间应该挂上 Dubbo Request 包装，返回后清掉 */
	@Test
	public void noContext_setsAndClearsDubboContext() {
		Invoker<Object> invoker = DubboTestSupport.invoker(invo -> {
			Assertions.assertTrue(SaHolder.getContext().isValid());
			Assertions.assertTrue(SaHolder.getRequest() instanceof SaRequestForDubbo);
			return AsyncRpcResult.newDefaultAsyncResult("ok", invo);
		});
		Result result = filter.invoke(invoker, DubboTestSupport.invocation());
		Assertions.assertEquals("ok", result.getValue());
		Assertions.assertFalse(SaHolder.getContext().isValid());
	}

	/** Invoker 抛异常时 finally 也必须把上下文清掉 */
	@Test
	public void invokeThrows_stillClearsContext() {
		Invoker<Object> invoker = DubboTestSupport.invoker(invo -> {
			throw new RpcException("boom");
		});
		Assertions.assertThrows(RpcException.class,
				() -> filter.invoke(invoker, DubboTestSupport.invocation()));
		Assertions.assertFalse(SaHolder.getContext().isValid());
	}

}
