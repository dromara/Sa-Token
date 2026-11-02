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
package cn.dev33.satoken.context.dubbo3.util;

import cn.dev33.satoken.context.SaHolder;
import cn.dev33.satoken.context.dubbo3.model.SaRequestForDubbo3;
import cn.dev33.satoken.context.dubbo3.model.SaResponseForDubbo3;
import cn.dev33.satoken.context.dubbo3.model.SaStorageForDubbo3;
import cn.dev33.satoken.context.dubbo3.support.Dubbo3TestSupport;
import cn.dev33.satoken.test.SaTokenTest;
import org.apache.dubbo.rpc.RpcContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Context 工具：setContext / clearContext。
 */
@SaTokenTest
public class SaTokenContextDubbo3UtilTest {

	/** 每条用例后清掉上下文和 RpcContext */
	@AfterEach
	public void cleanup() {
		Dubbo3TestSupport.cleanup();
	}

	/** 应测尽测：测试 SaTokenContextDubbo3Util 无参构造 */
	@Test
	public void ctor_canNew() {
		new SaTokenContextDubbo3Util();
	}

	/** setContext 应该把 Request / Response / Storage 都换成 Dubbo3 包装 */
	@Test
	public void setContext_wrapsServiceContext() {
		SaTokenContextDubbo3Util.setContext(RpcContext.getServiceContext());
		Assertions.assertTrue(SaHolder.getContext().isValid());
		Assertions.assertTrue(SaHolder.getRequest() instanceof SaRequestForDubbo3);
		Assertions.assertTrue(SaHolder.getResponse() instanceof SaResponseForDubbo3);
		Assertions.assertTrue(SaHolder.getStorage() instanceof SaStorageForDubbo3);
	}

	/** setContext 应该用传入的 rpcContext 包装 */
	@Test
	public void setContext_usesPassedArgument() {
		RpcContext ctx = RpcContext.getServiceContext();
		SaTokenContextDubbo3Util.setContext(ctx);
		Assertions.assertSame(ctx, SaHolder.getRequest().getSource());
	}

	/** clearContext 之后 isValid 应该是 false */
	@Test
	public void clearContext_dropsBox() {
		SaTokenContextDubbo3Util.setContext(RpcContext.getServiceContext());
		SaTokenContextDubbo3Util.clearContext();
		Assertions.assertFalse(SaHolder.getContext().isValid());
	}

}
