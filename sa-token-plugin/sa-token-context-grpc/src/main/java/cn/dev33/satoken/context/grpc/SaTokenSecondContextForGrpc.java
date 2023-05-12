package cn.dev33.satoken.context.grpc;

import cn.dev33.satoken.context.grpc.context.SaTokenGrpcContext;
import cn.dev33.satoken.context.grpc.model.SaRequestForGrpc;
import cn.dev33.satoken.context.grpc.model.SaResponseForGrpc;
import cn.dev33.satoken.context.grpc.model.SaStorageForGrpc;
import cn.dev33.satoken.context.model.SaRequest;
import cn.dev33.satoken.context.model.SaResponse;
import cn.dev33.satoken.context.model.SaStorage;
import cn.dev33.satoken.context.second.SaTokenSecondContext;
import cn.dev33.satoken.exception.ApiDisabledException;

/**
 * Sa-Token 上下文 [grpc版本]
 *
 * @author lym
 * @since <= 1.34.0
 */
public class SaTokenSecondContextForGrpc implements SaTokenSecondContext {

    @Override
    public SaRequest getRequest() {
        return new SaRequestForGrpc();
    }

    @Override
    public SaResponse getResponse() {
        return new SaResponseForGrpc();
    }

    @Override
    public SaStorage getStorage() {
        return new SaStorageForGrpc();
    }

    @Override
    public boolean matchPath(String pattern, String path) {
        throw new ApiDisabledException();
    }

    @Override
    public boolean isValid() {
        return SaTokenGrpcContext.isNotNull();
    }

}
