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
import cn.dev33.satoken.context.model.SaResponse;
import org.apache.dubbo.rpc.RpcContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Response 包装：RPC 不回传 status / header / redirect。
 */
public class SaResponseForDubbo3Test {

	/** 每条用例后清掉 RpcContext */
	@AfterEach
	public void cleanup() {
		Dubbo3TestSupport.cleanup();
	}

	/** 构造后 getSource 应该就是传入的 RpcContext */
	@Test
	public void ctor_keepsSource() {
		RpcContext ctx = RpcContext.getServiceContext();
		SaResponseForDubbo3 resp = new SaResponseForDubbo3(ctx);
		Assertions.assertSame(ctx, resp.getSource());
	}

	/** setStatus / setHeader / addHeader 应该返回自身，redirect 返回 null */
	@Test
	public void writers_noOpAndChain() {
		SaResponseForDubbo3 resp = new SaResponseForDubbo3(RpcContext.getServiceContext());
		Assertions.assertSame(resp, resp.setStatus(200));
		Assertions.assertSame(resp, resp.setHeader("a", "b"));
		SaResponse add = resp.addHeader("c", "d");
		Assertions.assertSame(resp, add);
		Assertions.assertNull(resp.redirect("http://x"));
	}

}
