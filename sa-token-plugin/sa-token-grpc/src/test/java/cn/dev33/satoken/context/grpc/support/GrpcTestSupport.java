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
package cn.dev33.satoken.context.grpc.support;

import cn.dev33.satoken.context.grpc.context.SaTokenGrpcContext;
import cn.dev33.satoken.context.mock.SaTokenContextMockUtil;
import io.grpc.Attributes;
import io.grpc.CallOptions;
import io.grpc.Channel;
import io.grpc.ClientCall;
import io.grpc.Context;
import io.grpc.Metadata;
import io.grpc.MethodDescriptor;
import io.grpc.ServerCall;
import io.grpc.Status;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicReference;

/**
 * gRPC 插件单测现场：假 Channel / ServerCall、挂 grpc Context、清 Sa 上下文。
 */
public final class GrpcTestSupport {

	private GrpcTestSupport() {
	}

	/** 挂上 mock 请求上下文，好让拦截器当成「有 Web 现场」 */
	public static void mockContext() {
		SaTokenContextMockUtil.setMockContext();
	}

	/** 清掉 Sa 上下文，并把 grpc Context 拨回 ROOT，避免单测互相污染 */
	public static void cleanup() {
		SaTokenContextMockUtil.clearContext();
		Context.ROOT.attach();
	}

	/** 造一个 unary String 方法描述，单测里当 RPC 方法用 */
	public static MethodDescriptor<String, String> unary() {
		return MethodDescriptor.<String, String>newBuilder()
				.setType(MethodDescriptor.MethodType.UNARY)
				.setFullMethodName("demo.Demo/call")
				.setRequestMarshaller(STRING_MARSHALLER)
				.setResponseMarshaller(STRING_MARSHALLER)
				.build();
	}

	/** 挂上 grpc 的 sa-token Context 再跑一段逻辑，跑完拆掉 */
	public static void withGrpcContext(Runnable action) {
		Context ctx = SaTokenGrpcContext.create();
		Context previous = ctx.attach();
		try {
			action.run();
		} finally {
			ctx.detach(previous);
		}
	}

	/** 造一个空的 ClientCall.Listener */
	public static <T> ClientCall.Listener<T> emptyClientListener() {
		return new ClientCall.Listener<T>() {
		};
	}

	/** 造一个空的 ServerCall.Listener */
	public static <T> ServerCall.Listener<T> emptyServerListener() {
		return new ServerCall.Listener<T>() {
		};
	}

	/**
	 * 职责：记下发出去的 header，以及拦截器包过的 response listener，方便单测触发 onClose。
	 */
	public static final class RecordingChannel extends Channel {

		public final AtomicReference<Metadata> sentHeaders = new AtomicReference<>();
		public final AtomicReference<ClientCall.Listener<String>> startedListener = new AtomicReference<>();

		@Override
		@SuppressWarnings("unchecked")
		public <ReqT, RespT> ClientCall<ReqT, RespT> newCall(MethodDescriptor<ReqT, RespT> methodDescriptor,
				CallOptions callOptions) {
			return (ClientCall<ReqT, RespT>) new ClientCall<String, String>() {
				@Override
				public void start(Listener<String> responseListener, Metadata headers) {
					sentHeaders.set(headers);
					startedListener.set(responseListener);
				}

				@Override
				public void request(int numMessages) {
				}

				@Override
				public void cancel(String message, Throwable cause) {
				}

				@Override
				public void halfClose() {
				}

				@Override
				public void sendMessage(String message) {
				}
			};
		}

		@Override
		public String authority() {
			return "test";
		}
	}

	/**
	 * 职责：记下 close 时的 trailers，单测里当底层 ServerCall 用。
	 */
	public static final class RecordingServerCall extends ServerCall<String, String> {

		public final AtomicReference<Metadata> closedTrailers = new AtomicReference<>();
		public Status closedStatus;

		@Override
		public void request(int numMessages) {
		}

		@Override
		public void sendHeaders(Metadata headers) {
		}

		@Override
		public void sendMessage(String message) {
		}

		@Override
		public void close(Status status, Metadata trailers) {
			closedStatus = status;
			closedTrailers.set(trailers);
		}

		@Override
		public boolean isCancelled() {
			return false;
		}

		@Override
		public MethodDescriptor<String, String> getMethodDescriptor() {
			return unary();
		}

		@Override
		public Attributes getAttributes() {
			return Attributes.EMPTY;
		}
	}

	/** 职责：把 String 编成字节流，给 MethodDescriptor 当 marshaller */
	static final MethodDescriptor.Marshaller<String> STRING_MARSHALLER = new MethodDescriptor.Marshaller<String>() {
		@Override
		public InputStream stream(String value) {
			return new ByteArrayInputStream(value.getBytes(StandardCharsets.UTF_8));
		}

		@Override
		public String parse(InputStream stream) {
			try {
				byte[] buf = new byte[256];
				int n = stream.read(buf);
				return n <= 0 ? "" : new String(buf, 0, n, StandardCharsets.UTF_8);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
	};

}
