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
package cn.dev33.satoken.context.grpc.util;

import cn.dev33.satoken.context.SaHolder;
import cn.dev33.satoken.context.grpc.model.SaRequestForGrpc;
import cn.dev33.satoken.context.grpc.model.SaResponseForGrpc;
import cn.dev33.satoken.context.grpc.model.SaStorageForGrpc;
import cn.dev33.satoken.context.grpc.support.GrpcTestSupport;
import cn.dev33.satoken.test.SaTokenTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Context 工具：setContext / clearContext。
 */
@SaTokenTest
public class SaTokenContextGrpcUtilTest {

	/** 每条用例后清掉 Sa 上下文 */
	@AfterEach
	public void cleanup() {
		GrpcTestSupport.cleanup();
	}

	/** 应测尽测：测试 SaTokenContextGrpcUtil 无参构造 */
	@Test
	public void ctor_canNew() {
		new SaTokenContextGrpcUtil();
	}

	/** setContext 应该把 Request / Response / Storage 都换成 gRPC 包装 */
	@Test
	public void setContext_wrapsGrpcModels() {
		SaTokenContextGrpcUtil.setContext();
		Assertions.assertTrue(SaHolder.getContext().isValid());
		Assertions.assertTrue(SaHolder.getRequest() instanceof SaRequestForGrpc);
		Assertions.assertTrue(SaHolder.getResponse() instanceof SaResponseForGrpc);
		Assertions.assertTrue(SaHolder.getStorage() instanceof SaStorageForGrpc);
	}

	/** clearContext 之后 isValid 应该是 false */
	@Test
	public void clearContext_dropsBox() {
		SaTokenContextGrpcUtil.setContext();
		SaTokenContextGrpcUtil.clearContext();
		Assertions.assertFalse(SaHolder.getContext().isValid());
	}

}
