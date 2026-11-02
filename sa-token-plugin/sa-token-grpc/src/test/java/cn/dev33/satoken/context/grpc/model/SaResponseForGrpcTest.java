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
package cn.dev33.satoken.context.grpc.model;

import cn.dev33.satoken.context.grpc.context.SaTokenGrpcContext;
import cn.dev33.satoken.context.grpc.support.GrpcTestSupport;
import cn.dev33.satoken.context.model.SaResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Response 包装：RPC 不回传 status / header / redirect。
 */
public class SaResponseForGrpcTest {

	/** 每条用例后把 grpc Context 拨回 ROOT */
	@AfterEach
	public void cleanup() {
		GrpcTestSupport.cleanup();
	}

	/** 挂上 grpc Context 后 getSource 应该就是那份 Map */
	@Test
	public void getSource_isGrpcMap() {
		GrpcTestSupport.withGrpcContext(() -> {
			SaResponseForGrpc resp = new SaResponseForGrpc();
			Assertions.assertSame(SaTokenGrpcContext.getContext(), resp.getSource());
		});
	}

	/** setStatus / setHeader / addHeader 应该返回自身，redirect 返回 null */
	@Test
	public void writers_noOpAndChain() {
		SaResponseForGrpc resp = new SaResponseForGrpc();
		Assertions.assertSame(resp, resp.setStatus(200));
		Assertions.assertSame(resp, resp.setHeader("a", "b"));
		SaResponse add = resp.addHeader("c", "d");
		Assertions.assertSame(resp, add);
		Assertions.assertNull(resp.redirect("http://x"));
	}

}
