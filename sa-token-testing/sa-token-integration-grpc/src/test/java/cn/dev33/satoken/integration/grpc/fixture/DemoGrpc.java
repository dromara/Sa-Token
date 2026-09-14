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
package cn.dev33.satoken.integration.grpc.fixture;

import io.grpc.MethodDescriptor;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/**
 * 集成测用的两个 unary 方法，不走 protobuf，避免再拉一套 protoc。
 */
public final class DemoGrpc {

	public static final String SERVICE_NAME = "satoken.Demo";

	/** 职责：把 String 编成字节流，给 MethodDescriptor 当 marshaller */
	static final MethodDescriptor.Marshaller<String> STRING_MARSHALLER = new MethodDescriptor.Marshaller<String>() {
		@Override
		public InputStream stream(String value) {
			byte[] bytes = value == null ? new byte[0] : value.getBytes(StandardCharsets.UTF_8);
			return new ByteArrayInputStream(bytes);
		}

		@Override
		public String parse(InputStream stream) {
			try {
				ByteArrayOutputStream out = new ByteArrayOutputStream();
				byte[] buf = new byte[256];
				int n;
				while ((n = stream.read(buf)) >= 0) {
					out.write(buf, 0, n);
				}
				return out.toString(StandardCharsets.UTF_8.name());
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
	};

	public static final MethodDescriptor<String, String> SNAPSHOT = MethodDescriptor.<String, String>newBuilder()
			.setType(MethodDescriptor.MethodType.UNARY)
			.setFullMethodName(MethodDescriptor.generateFullMethodName(SERVICE_NAME, "snapshot"))
			.setRequestMarshaller(STRING_MARSHALLER)
			.setResponseMarshaller(STRING_MARSHALLER)
			.build();

	public static final MethodDescriptor<String, String> DO_LOGIN = MethodDescriptor.<String, String>newBuilder()
			.setType(MethodDescriptor.MethodType.UNARY)
			.setFullMethodName(MethodDescriptor.generateFullMethodName(SERVICE_NAME, "doLogin"))
			.setRequestMarshaller(STRING_MARSHALLER)
			.setResponseMarshaller(STRING_MARSHALLER)
			.build();

	private DemoGrpc() {
	}

}
