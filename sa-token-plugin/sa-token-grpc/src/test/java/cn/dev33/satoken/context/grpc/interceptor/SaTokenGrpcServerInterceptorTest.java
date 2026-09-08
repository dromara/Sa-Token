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

import cn.dev33.satoken.SaManager;
import cn.dev33.satoken.context.SaHolder;
import cn.dev33.satoken.context.grpc.constants.GrpcContextConstants;
import cn.dev33.satoken.context.grpc.model.SaRequestForGrpc;
import cn.dev33.satoken.context.grpc.support.GrpcTestSupport;
import cn.dev33.satoken.context.grpc.support.GrpcTestSupport.RecordingServerCall;
import cn.dev33.satoken.exception.SameTokenInvalidException;
import cn.dev33.satoken.same.SaSameUtil;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.test.SaTokenTest;
import io.grpc.Metadata;
import io.grpc.ServerCall;
import io.grpc.ServerCallHandler;
import io.grpc.Status;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicReference;

/**
 * Server 拦截器：挂上下文、Same-Token、把客户端 token 写进会话、close 时回传新 token。
 */
@SaTokenTest
public class SaTokenGrpcServerInterceptorTest {

	private final SaTokenContextGrpcServerInterceptor contextInterceptor = new SaTokenContextGrpcServerInterceptor();
	private final SaTokenGrpcServerInterceptor interceptor = new SaTokenGrpcServerInterceptor();

	/** 每条用例后清掉 Sa 上下文 */
	@AfterEach
	public void cleanup() {
		GrpcTestSupport.cleanup();
	}

	/** 没开 checkSameToken 时应该直接放行，调用期间要有 gRPC 包装上下文 */
	@Test
	public void checkSameTokenOff_setsContextDuringStartCall() {
		SaManager.getConfig().setCheckSameToken(false);
		AtomicReference<Boolean> validDuringCall = new AtomicReference<>();
		chainIntercept(new RecordingServerCall(), new Metadata(), (call, headers) -> {
			validDuringCall.set(SaHolder.getContext().isValid());
			Assertions.assertTrue(SaHolder.getRequest() instanceof SaRequestForGrpc);
			return GrpcTestSupport.emptyServerListener();
		});
		Assertions.assertEquals(Boolean.TRUE, validDuringCall.get());
		Assertions.assertFalse(SaHolder.getContext().isValid());
	}

	/** interceptCall 返回之后目前会把上下文清掉（业务方法其实还没跑） */
	@Test
	public void interceptCall_clearsContextBeforeListenerRuns() {
		SaManager.getConfig().setCheckSameToken(false);
		ServerCall.Listener<String> listener = chainIntercept(new RecordingServerCall(), new Metadata(),
				(call, headers) -> GrpcTestSupport.emptyServerListener());
		Assertions.assertNotNull(listener);
		Assertions.assertFalse(SaHolder.getContext().isValid());
	}

	/** 开了校验且 Same-Token 正确时应该放行 */
	@Test
	public void validSameToken_passesThrough() {
		SaManager.getConfig().setCheckSameToken(true);
		Metadata headers = new Metadata();
		headers.put(GrpcContextConstants.SA_SAME_TOKEN, SaSameUtil.getToken());
		ServerCall.Listener<String> listener = chainIntercept(new RecordingServerCall(), headers,
				(call, h) -> GrpcTestSupport.emptyServerListener());
		Assertions.assertNotNull(listener);
	}

	/** 开了校验但没带 Same-Token 时应该抛 SameTokenInvalidException */
	@Test
	public void missingSameToken_throws() {
		SaManager.getConfig().setCheckSameToken(true);
		Assertions.assertThrows(SameTokenInvalidException.class,
				() -> chainIntercept(new RecordingServerCall(), new Metadata(),
						(call, headers) -> GrpcTestSupport.emptyServerListener()));
	}

	/** 开了校验但 Same-Token 不对时应该抛 SameTokenInvalidException */
	@Test
	public void wrongSameToken_throws() {
		SaManager.getConfig().setCheckSameToken(true);
		Metadata headers = new Metadata();
		headers.put(GrpcContextConstants.SA_SAME_TOKEN, "not-the-token");
		Assertions.assertThrows(SameTokenInvalidException.class,
				() -> chainIntercept(new RecordingServerCall(), headers,
						(call, h) -> GrpcTestSupport.emptyServerListener()));
	}

	/** 客户端带来的 token，在 startCall 期间应该已经写进会话 */
	@Test
	public void clientToken_writtenDuringStartCall() {
		SaManager.getConfig().setCheckSameToken(false);
		Metadata headers = new Metadata();
		headers.put(GrpcContextConstants.SA_JUST_CREATED_NOT_PREFIX, "from-client");
		AtomicReference<String> tokenDuringCall = new AtomicReference<>();
		chainIntercept(new RecordingServerCall(), headers, (call, h) -> {
			tokenDuringCall.set(StpUtil.getTokenValue());
			return GrpcTestSupport.emptyServerListener();
		});
		Assertions.assertEquals("from-client", tokenDuringCall.get());
	}

	/** startCall 里登录后 close，应该把新 token 写进 trailers */
	@Test
	public void close_writesBackNewToken() {
		SaManager.getConfig().setCheckSameToken(false);
		RecordingServerCall call = new RecordingServerCall();
		chainIntercept(call, new Metadata(), (wrapped, headers) -> {
			StpUtil.login(10002);
			wrapped.close(Status.OK, new Metadata());
			return GrpcTestSupport.emptyServerListener();
		});
		Assertions.assertEquals(StpUtil.getTokenValueByLoginId(10002),
				call.closedTrailers.get().get(GrpcContextConstants.SA_JUST_CREATED_NOT_PREFIX));
	}

	/** close 时 token 没变，就不该往 trailers 里塞 */
	@Test
	public void close_sameToken_skipsTrailer() {
		SaManager.getConfig().setCheckSameToken(false);
		Metadata headers = new Metadata();
		headers.put(GrpcContextConstants.SA_JUST_CREATED_NOT_PREFIX, "same");
		RecordingServerCall call = new RecordingServerCall();
		chainIntercept(call, headers, (wrapped, h) -> {
			wrapped.close(Status.OK, new Metadata());
			return GrpcTestSupport.emptyServerListener();
		});
		Assertions.assertNull(call.closedTrailers.get().get(GrpcContextConstants.SA_JUST_CREATED_NOT_PREFIX));
	}

	/** 客户端带了 token，但 close 前会话又空了，也不该往 trailers 里塞 */
	@Test
	public void close_clientTokenThenEmptySession_skipsTrailer() {
		SaManager.getConfig().setCheckSameToken(false);
		Metadata headers = new Metadata();
		headers.put(GrpcContextConstants.SA_JUST_CREATED_NOT_PREFIX, "from-client");
		RecordingServerCall call = new RecordingServerCall();
		chainIntercept(call, headers, (wrapped, h) -> {
			StpUtil.logout();
			wrapped.close(Status.OK, new Metadata());
			return GrpcTestSupport.emptyServerListener();
		});
		Assertions.assertNull(call.closedTrailers.get().get(GrpcContextConstants.SA_JUST_CREATED_NOT_PREFIX));
	}

	/** close 时会话是空的，也不该往 trailers 里塞 */
	@Test
	public void close_emptyToken_skipsTrailer() {
		SaManager.getConfig().setCheckSameToken(false);
		RecordingServerCall call = new RecordingServerCall();
		chainIntercept(call, new Metadata(), (wrapped, headers) -> {
			wrapped.close(Status.OK, new Metadata());
			return GrpcTestSupport.emptyServerListener();
		});
		Assertions.assertNull(call.closedTrailers.get().get(GrpcContextConstants.SA_JUST_CREATED_NOT_PREFIX));
	}

	/** 先挂 grpc Context，再跑 token 拦截器，对齐生产里两个全局拦截器的顺序 */
	private ServerCall.Listener<String> chainIntercept(RecordingServerCall call, Metadata headers,
			ServerCallHandler<String, String> inner) {
		return contextInterceptor.interceptCall(call, headers,
				(c, h) -> interceptor.interceptCall(c, h, inner));
	}

}
