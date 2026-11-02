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

import cn.dev33.satoken.SaManager;
import cn.dev33.satoken.context.dubbo.support.DubboTestSupport;
import cn.dev33.satoken.same.SaSameUtil;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.test.SaTokenTest;
import cn.dev33.satoken.util.SaTokenConsts;
import org.apache.dubbo.rpc.AsyncRpcResult;
import org.apache.dubbo.rpc.Invoker;
import org.apache.dubbo.rpc.Result;
import org.apache.dubbo.rpc.RpcContext;
import org.apache.dubbo.rpc.RpcInvocation;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Consumer Filter：Same-Token、会话 token 下传、回传写回。
 */
@SaTokenTest
public class SaTokenDubboConsumerFilterTest {

	private final SaTokenDubboConsumerFilter filter = new SaTokenDubboConsumerFilter();

	/** 每条用例后清掉 mock 上下文和 RpcContext */
	@AfterEach
	public void cleanup() {
		DubboTestSupport.cleanup();
	}

	/** 没 Web 上下文时不应该下传会话 token，但调用还是要发出去 */
	@Test
	public void noContext_skipsSessionToken() {
		RpcInvocation invocation = DubboTestSupport.invocation();
		Result result = filter.invoke(DubboTestSupport.okInvoker("ok"), invocation);
		Assertions.assertEquals("ok", result.getValue());
		Assertions.assertNull(RpcContext.getContext().getAttachment(SaTokenConsts.JUST_CREATED));
	}

	/** 开了 checkSameToken 时，就算没上下文也应该带上 Same-Token */
	@Test
	public void noContext_stillAttachesSameToken() {
		SaManager.getConfig().setCheckSameToken(true);
		filter.invoke(DubboTestSupport.okInvoker("ok"), DubboTestSupport.invocation());
		Assertions.assertEquals(SaSameUtil.getToken(),
				RpcContext.getContext().getAttachment(SaSameUtil.SAME_TOKEN));
	}

	/** 没开 checkSameToken 时不应该往 RpcContext 塞 Same-Token */
	@Test
	public void checkSameTokenOff_doesNotAttachSameToken() {
		SaManager.getConfig().setCheckSameToken(false);
		DubboTestSupport.mockContext();
		filter.invoke(DubboTestSupport.okInvoker("ok"), DubboTestSupport.invocation());
		Assertions.assertNull(RpcContext.getContext().getAttachment(SaSameUtil.SAME_TOKEN));
	}

	/** 有上下文且已登录时，应该把当前 token 写到 JUST_CREATED attachment */
	@Test
	public void withContext_attachesSessionToken() {
		DubboTestSupport.mockContext();
		StpUtil.login(10001);
		String token = StpUtil.getTokenValueNotCut();
		filter.invoke(DubboTestSupport.okInvoker("ok"), DubboTestSupport.invocation());
		Assertions.assertEquals(token, RpcContext.getContext().getAttachment(SaTokenConsts.JUST_CREATED));
	}

	/** 对端回传 JUST_CREATED_NOT_PREFIX 时，应该写回当前会话 */
	@Test
	public void resultAttachment_writesBackToSession() {
		DubboTestSupport.mockContext();
		Invoker<Object> invoker = DubboTestSupport.invoker(invo -> {
			Result result = AsyncRpcResult.newDefaultAsyncResult("ok", invo);
			result.setAttachment(SaTokenConsts.JUST_CREATED_NOT_PREFIX, "back-token");
			return result;
		});
		filter.invoke(invoker, DubboTestSupport.invocation());
		Assertions.assertEquals("back-token", StpUtil.getTokenValue());
	}

	/** 对端没回传 token 时 setTokenValue 应该空操作，调用结果照样返回 */
	@Test
	public void emptyResultAttachment_keepsResult() {
		DubboTestSupport.mockContext();
		Result result = filter.invoke(DubboTestSupport.okInvoker("pong"), DubboTestSupport.invocation());
		Assertions.assertEquals("pong", result.getValue());
		Assertions.assertNull(StpUtil.getTokenValue());
	}

}
