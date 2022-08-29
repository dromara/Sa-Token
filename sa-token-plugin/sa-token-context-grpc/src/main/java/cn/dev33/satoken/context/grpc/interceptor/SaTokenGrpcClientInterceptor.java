package cn.dev33.satoken.context.grpc.interceptor;

import cn.dev33.satoken.SaManager;
import cn.dev33.satoken.context.SaTokenContextDefaultImpl;
import cn.dev33.satoken.context.grpc.constants.GrpcContextConstants;
import cn.dev33.satoken.id.SaIdUtil;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaFoxUtil;
import io.grpc.*;
import net.devh.boot.grpc.client.interceptor.GrpcGlobalClientInterceptor;
import org.springframework.core.Ordered;


/**
 * 客户端请求的时候，带上token
 * 
 * @author lym
 * @since 2022/8/24 15:45
 */
@GrpcGlobalClientInterceptor
public class SaTokenGrpcClientInterceptor implements ClientInterceptor, Ordered {
    @Override
    public <ReqT, RespT> ClientCall<ReqT, RespT> interceptCall(MethodDescriptor<ReqT, RespT> method,
                                                               CallOptions callOptions, Channel next) {
        return new ForwardingClientCall.SimpleForwardingClientCall<ReqT, RespT>(next.newCall(method, callOptions)) {
            @Override
            public void start(Listener<RespT> responseListener, Metadata headers) {

                // 追加 Id-Token 参数
                if (SaManager.getConfig().getCheckIdToken()) {
                    headers.put(GrpcContextConstants.SA_ID_TOKEN, SaIdUtil.getToken());
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
