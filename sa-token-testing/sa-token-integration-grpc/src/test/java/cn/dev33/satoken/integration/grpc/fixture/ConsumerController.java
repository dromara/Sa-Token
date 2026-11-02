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

import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaResult;
import io.grpc.CallOptions;
import io.grpc.Channel;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.ClientCalls;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Consumer HTTP：先有 Servlet 上下文，再走 gRPC，拦截器才能下传 token。
 */
@RestController
public class ConsumerController {

	@GrpcClient("demo")
	private Channel channel;

	/** Consumer 登录后再 RPC，看 Provider 能不能接到会话 */
	@RequestMapping("/consumer-login-then-rpc")
	public SaResult consumerLoginThenRpc() {
		StpUtil.login(10001);
		return SaResult.data(snapshot("after-consumer-login"));
	}

	/** Provider 登录后看 Consumer 能不能接到回传 token */
	@RequestMapping("/provider-login-then-check")
	public SaResult providerLoginThenCheck() {
		doLogin("10002");
		return SaResult.ok()
				.set("consumerLogin", StpUtil.isLogin())
				.set("consumerLoginId", StpUtil.getLoginIdDefaultNull())
				.set("consumerToken", StpUtil.getTokenValue());
	}

	/** 没登录直接 RPC，Provider 也应该是没登录 */
	@RequestMapping("/rpc-without-login")
	public SaResult rpcWithoutLogin() {
		return SaResult.data(snapshot("no-login"));
	}

	/** Consumer 登录后 RPC，自己这边会话还在 */
	@RequestMapping("/consumer-still-login")
	public SaResult consumerStillLogin() {
		StpUtil.login(10003);
		boolean rpcOk = true;
		String rpcError = null;
		try {
			snapshot("provider-side");
		} catch (StatusRuntimeException e) {
			rpcOk = false;
			rpcError = e.getStatus().getCode().name();
		}
		return SaResult.ok()
				.set("consumerLogin", StpUtil.isLogin())
				.set("consumerLoginId", StpUtil.getLoginIdDefaultNull())
				.set("rpcOk", rpcOk)
				.set("rpcError", rpcError);
	}

	private LoginSnapshot snapshot(String tag) {
		String raw = ClientCalls.blockingUnaryCall(channel, DemoGrpc.SNAPSHOT, CallOptions.DEFAULT, tag);
		return LoginSnapshot.parse(raw);
	}

	private String doLogin(String loginId) {
		return ClientCalls.blockingUnaryCall(channel, DemoGrpc.DO_LOGIN, CallOptions.DEFAULT, loginId);
	}

}
