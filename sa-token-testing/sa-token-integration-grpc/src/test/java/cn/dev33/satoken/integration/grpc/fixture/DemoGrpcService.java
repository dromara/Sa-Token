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
import io.grpc.BindableService;
import io.grpc.ServerServiceDefinition;
import io.grpc.stub.ServerCalls;
import net.devh.boot.grpc.server.service.GrpcService;

/**
 * Provider：用 StpUtil 登录 / 读会话，靠拦截器接上下文。
 */
@GrpcService
public class DemoGrpcService implements BindableService {

	@Override
	public ServerServiceDefinition bindService() {
		return ServerServiceDefinition.builder(DemoGrpc.SERVICE_NAME)
				.addMethod(DemoGrpc.SNAPSHOT, ServerCalls.asyncUnaryCall((tag, observer) -> {
					LoginSnapshot snapshot = new LoginSnapshot();
					snapshot.setTag(tag);
					snapshot.setLogin(StpUtil.isLogin());
					snapshot.setToken(StpUtil.getTokenValue());
					Object loginId = StpUtil.getLoginIdDefaultNull();
					snapshot.setLoginId(loginId == null ? null : String.valueOf(loginId));
					observer.onNext(snapshot.encode());
					observer.onCompleted();
				}))
				.addMethod(DemoGrpc.DO_LOGIN, ServerCalls.asyncUnaryCall((loginId, observer) -> {
					StpUtil.login(Long.parseLong(loginId));
					observer.onNext(StpUtil.getTokenValue());
					observer.onCompleted();
				}))
				.build();
	}

}
