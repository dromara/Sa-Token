/*
 * Copyright 2020-2099 sa-token.cc
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
import io.grpc.*;
import net.devh.boot.grpc.server.interceptor.GrpcGlobalServerInterceptor;
import org.springframework.core.Ordered;

/**
 * 处理请求前，创建上下文
 * 
 * @author lym
 * @since 2022/8/24 10:09
 */
@GrpcGlobalServerInterceptor
public class SaTokenContextGrpcServerInterceptor implements ServerInterceptor, Ordered {
    @Override
    public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(ServerCall<ReqT, RespT> call, Metadata headers, ServerCallHandler<ReqT, RespT> next) {
        Context ctx = SaTokenGrpcContext.create();
        return Contexts.interceptCall(ctx, call, headers, next);
    }

    /**
     * 必须最先创建上下文，后面的拦截器才能获取到上下文
     */
    @Override
    public int getOrder() {
        return HIGHEST_PRECEDENCE;
    }
}
