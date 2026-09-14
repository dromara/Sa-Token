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
package cn.dev33.satoken.context.dubbo3.model;

import cn.dev33.satoken.context.dubbo3.support.Dubbo3TestSupport;
import org.apache.dubbo.rpc.RpcContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Request 包装：RPC 不传播 url / header / cookie / path。
 */
public class SaRequestForDubbo3Test {

	/** 每条用例后清掉 RpcContext */
	@AfterEach
	public void cleanup() {
		Dubbo3TestSupport.cleanup();
	}

	/** 构造后 getSource 应该就是传入的 RpcContext */
	@Test
	public void ctor_keepsSource() {
		RpcContext ctx = RpcContext.getServiceContext();
		SaRequestForDubbo3 req = new SaRequestForDubbo3(ctx);
		Assertions.assertSame(ctx, req.getSource());
	}

	/** param / header / cookie / path / url / method / host / forward 都应该是空操作 */
	@Test
	public void accessors_doNotPropagate() {
		SaRequestForDubbo3 req = new SaRequestForDubbo3(RpcContext.getServiceContext());
		Assertions.assertNull(req.getParam("a"));
		Assertions.assertNull(req.getParamNames());
		Assertions.assertNull(req.getParamMap());
		Assertions.assertNull(req.getHeader("h"));
		Assertions.assertNull(req.getCookieValue("c"));
		Assertions.assertNull(req.getCookieFirstValue("c"));
		Assertions.assertNull(req.getCookieLastValue("c"));
		Assertions.assertNull(req.getRequestPath());
		Assertions.assertNull(req.getUrl());
		Assertions.assertNull(req.getMethod());
		Assertions.assertNull(req.getHost());
		Assertions.assertNull(req.forward("/x"));
	}

}
