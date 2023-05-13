package cn.dev33.satoken.context.grpc.interceptor;

import cn.dev33.satoken.SaManager;
import cn.dev33.satoken.context.grpc.constants.GrpcContextConstants;
import cn.dev33.satoken.same.SaSameUtil;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaFoxUtil;
import io.grpc.ForwardingServerCall;
import io.grpc.Metadata;
import io.grpc.ServerCall;
import io.grpc.ServerCallHandler;
import io.grpc.ServerInterceptor;
import io.grpc.Status;
import net.devh.boot.grpc.server.interceptor.GrpcGlobalServerInterceptor;

/**
 * 鉴权，设置token
 * 
 * @author lym
 * @since 2022/8/25 11:33
 **/
@GrpcGlobalServerInterceptor
public class SaTokenGrpcServerInterceptor implements ServerInterceptor {
    @Override
    public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(ServerCall<ReqT, RespT> call, Metadata headers, ServerCallHandler<ReqT, RespT> next) {
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
    }
}
