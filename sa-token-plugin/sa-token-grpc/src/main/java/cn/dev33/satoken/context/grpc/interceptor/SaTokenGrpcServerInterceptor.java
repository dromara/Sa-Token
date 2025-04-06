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

import cn.dev33.satoken.SaManager;
import cn.dev33.satoken.context.grpc.constants.GrpcContextConstants;
import cn.dev33.satoken.context.grpc.util.SaGrpcContextUtil;
import cn.dev33.satoken.same.SaSameUtil;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaFoxUtil;
import io.grpc.*;
import net.devh.boot.grpc.server.interceptor.GrpcGlobalServerInterceptor;

/**
 * 鉴权，设置token
 * 
 * @author lym
 * @since 1.34.0
 **/
@GrpcGlobalServerInterceptor
public class SaTokenGrpcServerInterceptor implements ServerInterceptor {
    @Override
    public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(ServerCall<ReqT, RespT> call, Metadata headers, ServerCallHandler<ReqT, RespT> next) {
        try{
            // 初始化上下文
            SaGrpcContextUtil.setContext();

            // RPC 调用鉴权
            if (SaManager.getConfig().getCheckSameToken()) {
                String sameToken = headers.get(GrpcContextConstants.SA_SAME_TOKEN);
                SaSameUtil.checkToken(sameToken);
            }
            String tokenFromClient = headers.get(GrpcContextConstants.SA_JUST_CREATED_NOT_PREFIX);
            StpUtil.setTokenValue(tokenFromClient);

            return next.startCall(new ForwardingServerCall.SimpleForwardingServerCall<ReqT, RespT>(call) {
                /**
                 * 结束响应时，若本服务生成了新token，将其传回客户端
                 */
                @Override
                public void close(Status status, Metadata responseHeaders) {
                    String justCreateToken = StpUtil.getTokenValue();
                    if (!SaFoxUtil.equals(justCreateToken, tokenFromClient) && SaFoxUtil.isNotEmpty(justCreateToken)) {
                        responseHeaders.put(GrpcContextConstants.SA_JUST_CREATED_NOT_PREFIX, justCreateToken);
                    }
                    super.close(status, responseHeaders);
                }
            }, headers);
        }finally {
            // 清除上下文
            SaGrpcContextUtil.clearContext();
        }
    }
}
