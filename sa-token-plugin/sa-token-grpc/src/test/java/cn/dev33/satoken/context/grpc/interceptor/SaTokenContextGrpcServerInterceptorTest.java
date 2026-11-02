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
package cn.dev33.satoken.context.grpc.interceptor;

import cn.dev33.satoken.context.grpc.context.SaTokenGrpcContext;
import cn.dev33.satoken.context.grpc.support.GrpcTestSupport;
import cn.dev33.satoken.context.grpc.support.GrpcTestSupport.RecordingServerCall;
import io.grpc.Metadata;
import io.grpc.ServerCall;
import io.grpc.ServerCallHandler;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.core.Ordered;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 必须最先挂 grpc Context，后面的拦截器才能读写那份 Map。
 */
public class SaTokenContextGrpcServerInterceptorTest {

	private final SaTokenContextGrpcServerInterceptor interceptor = new SaTokenContextGrpcServerInterceptor();

	/** 每条用例后把 grpc Context 拨回 ROOT */
	@AfterEach
	public void cleanup() {
		GrpcTestSupport.cleanup();
	}

	/** 顺序必须是最高优先级 */
	@Test
	public void getOrder_isHighestPrecedence() {
		Assertions.assertEquals(Ordered.HIGHEST_PRECEDENCE, interceptor.getOrder());
	}

	/** startCall 期间应该已经能读到 grpc Context */
	@Test
	public void interceptCall_createsGrpcContext() {
		AtomicBoolean seen = new AtomicBoolean(false);
		RecordingServerCall call = new RecordingServerCall();
		ServerCallHandler<String, String> next = (c, h) -> {
			Assertions.assertTrue(SaTokenGrpcContext.isNotNull());
			seen.set(true);
			return GrpcTestSupport.emptyServerListener();
		};
		ServerCall.Listener<String> listener = interceptor.interceptCall(call, new Metadata(), next);
		Assertions.assertNotNull(listener);
		Assertions.assertTrue(seen.get());
	}

}
