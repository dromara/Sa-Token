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
package cn.dev33.satoken.context.dubbo3.support;

import cn.dev33.satoken.context.mock.SaTokenContextMockUtil;
import org.apache.dubbo.common.URL;
import org.apache.dubbo.rpc.AsyncRpcResult;
import org.apache.dubbo.rpc.Invocation;
import org.apache.dubbo.rpc.Invoker;
import org.apache.dubbo.rpc.Result;
import org.apache.dubbo.rpc.RpcContext;
import org.apache.dubbo.rpc.RpcInvocation;

import java.util.function.Function;

/**
 * Dubbo3 插件单测现场：假 Invoker、假 Invocation、清 RpcContext。
 */
public final class Dubbo3TestSupport {

	private Dubbo3TestSupport() {
	}

	/** 挂上 mock 请求上下文，好让 Filter 当成「有 Web 现场」 */
	public static void mockContext() {
		SaTokenContextMockUtil.setMockContext();
	}

	/** 清掉 Sa 上下文和 Dubbo 线程里的 RpcContext，避免单测互相污染 */
	public static void cleanup() {
		SaTokenContextMockUtil.clearContext();
		RpcContext.removeContext();
		RpcContext.removeServerContext();
	}

	/** 造一条空的 RpcInvocation */
	public static RpcInvocation invocation() {
		RpcInvocation invocation = new RpcInvocation();
		invocation.setMethodName("demo");
		invocation.setTargetServiceUniqueName("demo/demo");
		return invocation;
	}

	/** 造一个直接返回指定结果的 Invoker */
	public static Invoker<Object> invoker(Function<Invocation, Result> fn) {
		return new StubInvoker(fn);
	}

	/** 造一个调用成功、值为 value 的 Invoker */
	public static Invoker<Object> okInvoker(Object value) {
		return invoker(invo -> AsyncRpcResult.newDefaultAsyncResult(value, invo));
	}

	/** 职责：把 Function 包成 Dubbo Invoker，单测里当对端用 */
	static final class StubInvoker implements Invoker<Object> {

		private final Function<Invocation, Result> fn;

		StubInvoker(Function<Invocation, Result> fn) {
			this.fn = fn;
		}

		@Override
		public Class<Object> getInterface() {
			return Object.class;
		}

		@Override
		public Result invoke(Invocation invocation) {
			return fn.apply(invocation);
		}

		@Override
		public URL getUrl() {
			return URL.valueOf("dubbo://127.0.0.1:20880/demo");
		}

		@Override
		public boolean isAvailable() {
			return true;
		}

		@Override
		public void destroy() {
		}
	}

}
