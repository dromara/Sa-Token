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
package cn.dev33.satoken.context.dubbo.util;

import cn.dev33.satoken.context.SaHolder;
import cn.dev33.satoken.context.dubbo.model.SaRequestForDubbo;
import cn.dev33.satoken.context.dubbo.model.SaResponseForDubbo;
import cn.dev33.satoken.context.dubbo.model.SaStorageForDubbo;
import cn.dev33.satoken.context.dubbo.support.DubboTestSupport;
import cn.dev33.satoken.test.SaTokenTest;
import org.apache.dubbo.rpc.RpcContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Context 工具：setContext / clearContext。
 */
@SaTokenTest
public class SaTokenContextDubboUtilTest {

	/** 每条用例后清掉上下文和 RpcContext */
	@AfterEach
	public void cleanup() {
		DubboTestSupport.cleanup();
	}

	/** 工具类默认构造应该能 new */
	@Test
	public void ctor_canNew() {
		new SaTokenContextDubboUtil();
	}

	/** setContext 应该把 Request / Response / Storage 都换成 Dubbo 包装 */
	@Test
	public void setContext_wrapsRpcContext() {
		SaTokenContextDubboUtil.setContext(RpcContext.getContext());
		Assertions.assertTrue(SaHolder.getContext().isValid());
		Assertions.assertTrue(SaHolder.getRequest() instanceof SaRequestForDubbo);
		Assertions.assertTrue(SaHolder.getResponse() instanceof SaResponseForDubbo);
		Assertions.assertTrue(SaHolder.getStorage() instanceof SaStorageForDubbo);
	}

	/** 传入的 rpcContext 参数目前没用上，实际总是读 RpcContext.getContext() */
	@Test
	public void setContext_ignoresPassedArgument() {
		SaTokenContextDubboUtil.setContext(null);
		Assertions.assertTrue(SaHolder.getRequest() instanceof SaRequestForDubbo);
		Assertions.assertSame(RpcContext.getContext(), SaHolder.getRequest().getSource());
	}

	/** clearContext 之后 isValid 应该是 false */
	@Test
	public void clearContext_dropsBox() {
		SaTokenContextDubboUtil.setContext(RpcContext.getContext());
		SaTokenContextDubboUtil.clearContext();
		Assertions.assertFalse(SaHolder.getContext().isValid());
	}

}
