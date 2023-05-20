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

import org.springframework.core.Ordered;

import cn.dev33.satoken.SaManager;
import cn.dev33.satoken.context.SaTokenContextDefaultImpl;
import cn.dev33.satoken.context.grpc.constants.GrpcContextConstants;
import cn.dev33.satoken.same.SaSameUtil;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaFoxUtil;
import io.grpc.CallOptions;
import io.grpc.Channel;
import io.grpc.ClientCall;
import io.grpc.ClientInterceptor;
import io.grpc.ForwardingClientCall;
import io.grpc.ForwardingClientCallListener;
import io.grpc.Metadata;
import io.grpc.MethodDescriptor;
import io.grpc.Status;
import net.devh.boot.grpc.client.interceptor.GrpcGlobalClientInterceptor;


/**
 * 客户端请求的时候，带上token
 * 
 * @author lym
 * @since 1.34.0
 */
@GrpcGlobalClientInterceptor
public class SaTokenGrpcClientInterceptor implements ClientInterceptor, Ordered {
    @Override
    public <ReqT, RespT> ClientCall<ReqT, RespT> interceptCall(MethodDescriptor<ReqT, RespT> method,
                                                               CallOptions callOptions, Channel next) {
        return new ForwardingClientCall.SimpleForwardingClientCall<ReqT, RespT>(next.newCall(method, callOptions)) {
            @Override
            public void start(Listener<RespT> responseListener, Metadata headers) {

                // 追加 Same-Token 参数
                if (SaManager.getConfig().getCheckSameToken()) {
                    headers.put(GrpcContextConstants.SA_SAME_TOKEN, SaSameUtil.getToken());
                }

                // 调用前，传递会话Token
                String tokenValue = StpUtil.getTokenValue();
                if (SaFoxUtil.isNotEmpty(tokenValue)
                        && SaManager.getSaTokenContextOrSecond() != SaTokenContextDefaultImpl.defaultContext) {
                    headers.put(GrpcContextConstants.SA_JUST_CREATED_NOT_PREFIX, tokenValue);
                }

                super.start(new ForwardingClientCallListener.SimpleForwardingClientCallListener<RespT>(responseListener) {
                    /**
                     * 服务端结束响应后，解析回传的Token值
                     */
                    @Override
                    public void onClose(Status status, Metadata responseHeader) {
                        StpUtil.setTokenValue(responseHeader.get(GrpcContextConstants.SA_JUST_CREATED_NOT_PREFIX));
                        super.onClose(status, responseHeader);
                    }
                }, headers);
            }
        };
    }


    @Override
    public int getOrder() {
        return HIGHEST_PRECEDENCE;
    }
}
