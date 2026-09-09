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
import cn.dev33.satoken.context.grpc.constants.GrpcContextConstants;
import cn.dev33.satoken.context.grpc.support.GrpcTestSupport;
import cn.dev33.satoken.context.grpc.support.GrpcTestSupport.RecordingChannel;
import cn.dev33.satoken.same.SaSameUtil;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.test.SaTokenTest;
import io.grpc.CallOptions;
import io.grpc.ClientCall;
import io.grpc.Metadata;
import io.grpc.Status;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.core.Ordered;

/**
 * Client 拦截器：Same-Token、会话 token 下传、回传写回。
 */
@SaTokenTest
public class SaTokenGrpcClientInterceptorTest {

	private final SaTokenGrpcClientInterceptor interceptor = new SaTokenGrpcClientInterceptor();

	/** 每条用例后清掉 mock 上下文 */
	@AfterEach
	public void cleanup() {
		GrpcTestSupport.cleanup();
	}

	/** 顺序必须是最高优先级，好让别的拦截器还能看到 header */
	@Test
	public void getOrder_isHighestPrecedence() {
		Assertions.assertEquals(Ordered.HIGHEST_PRECEDENCE, interceptor.getOrder());
	}

	/** 没 Web 上下文时不应该读会话，调用照样发出去 */
	@Test
	public void noContext_skipsSessionToken() {
		RecordingChannel channel = new RecordingChannel();
		ClientCall<String, String> call = interceptor.interceptCall(GrpcTestSupport.unary(), CallOptions.DEFAULT,
				channel);
		call.start(GrpcTestSupport.emptyClientListener(), new Metadata());
		Assertions.assertNull(channel.sentHeaders.get().get(GrpcContextConstants.SA_JUST_CREATED_NOT_PREFIX));
	}

	/** 没 Web 上下文但开了 checkSameToken 时，Same-Token 仍应下传 */
	@Test
	public void noContext_stillAttachesSameToken() {
		SaManager.getConfig().setCheckSameToken(true);
		RecordingChannel channel = new RecordingChannel();
		ClientCall<String, String> call = interceptor.interceptCall(GrpcTestSupport.unary(), CallOptions.DEFAULT,
				channel);
		call.start(GrpcTestSupport.emptyClientListener(), new Metadata());
		Assertions.assertEquals(SaSameUtil.getToken(),
				channel.sentHeaders.get().get(GrpcContextConstants.SA_SAME_TOKEN));
	}

	/** 开了 checkSameToken 且有上下文时，应该带上 Same-Token */
	@Test
	public void checkSameTokenOn_attachesSameToken() {
		SaManager.getConfig().setCheckSameToken(true);
		GrpcTestSupport.mockContext();
		RecordingChannel channel = startCall(new RecordingChannel());
		Assertions.assertEquals(SaSameUtil.getToken(),
				channel.sentHeaders.get().get(GrpcContextConstants.SA_SAME_TOKEN));
	}

	/** 没开 checkSameToken 时不应该往 header 塞 Same-Token */
	@Test
	public void checkSameTokenOff_doesNotAttachSameToken() {
		SaManager.getConfig().setCheckSameToken(false);
		GrpcTestSupport.mockContext();
		RecordingChannel channel = startCall(new RecordingChannel());
		Assertions.assertNull(channel.sentHeaders.get().get(GrpcContextConstants.SA_SAME_TOKEN));
	}

	/** 有上下文且已登录时，应该把当前 token 写到 JUST_CREATED header */
	@Test
	public void withContext_attachesSessionToken() {
		GrpcTestSupport.mockContext();
		StpUtil.login(10001);
		String token = StpUtil.getTokenValue();
		RecordingChannel channel = startCall(new RecordingChannel());
		Assertions.assertEquals(token,
				channel.sentHeaders.get().get(GrpcContextConstants.SA_JUST_CREATED_NOT_PREFIX));
	}

	/** 有上下文但没登录时，不应该下传会话 token */
	@Test
	public void withContext_noLogin_skipsSessionToken() {
		GrpcTestSupport.mockContext();
		RecordingChannel channel = startCall(new RecordingChannel());
		Assertions.assertNull(channel.sentHeaders.get().get(GrpcContextConstants.SA_JUST_CREATED_NOT_PREFIX));
	}

	/** 对端 trailers 带回 token 时，应该写回当前会话 */
	@Test
	public void onClose_writesBackToSession() {
		GrpcTestSupport.mockContext();
		RecordingChannel channel = startCall(new RecordingChannel());
		Metadata trailers = new Metadata();
		trailers.put(GrpcContextConstants.SA_JUST_CREATED_NOT_PREFIX, "back-token");
		channel.startedListener.get().onClose(Status.OK, trailers);
		Assertions.assertEquals("back-token", StpUtil.getTokenValue());
	}

	/** 对端没回传 token 时 setTokenValue 应该空操作 */
	@Test
	public void onClose_emptyTrailer_keepsSessionEmpty() {
		GrpcTestSupport.mockContext();
		RecordingChannel channel = startCall(new RecordingChannel());
		channel.startedListener.get().onClose(Status.OK, new Metadata());
		Assertions.assertNull(StpUtil.getTokenValue());
	}

	private RecordingChannel startCall(RecordingChannel channel) {
		ClientCall<String, String> call = interceptor.interceptCall(GrpcTestSupport.unary(), CallOptions.DEFAULT,
				channel);
		call.start(GrpcTestSupport.emptyClientListener(), new Metadata());
		return channel;
	}

}
