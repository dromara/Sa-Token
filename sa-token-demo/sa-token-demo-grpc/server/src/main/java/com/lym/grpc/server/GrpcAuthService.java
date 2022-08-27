package com.lym.grpc.server;

import cn.dev33.satoken.stp.StpUtil;
import com.google.protobuf.Empty;
import com.lym.grpc.auth.Auth;
import com.lym.grpc.auth.AuthServiceGrpc;
import com.lym.service.AuthService;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author lym
 * @description
 * @date 2022/8/26 11:29
 **/
@GrpcService
public class GrpcAuthService extends AuthServiceGrpc.AuthServiceImplBase {

    @Autowired
    private AuthService authService;

    @Override
    public void isLogin(Empty request, StreamObserver<Auth.GrpcBool> responseObserver) {
        boolean isLogin = authService.isLogin();
        responseObserver.onNext(Auth.GrpcBool.newBuilder().setVal(isLogin).build());
        responseObserver.onCompleted();
    }

    @Override
    public void login(Auth.GrpcInt request, StreamObserver<Auth.GrpcString> responseObserver) {
        StpUtil.login(request.getVal());
        Auth.GrpcString resp = Auth.GrpcString.newBuilder().setVal(StpUtil.getTokenValue()).build();
        responseObserver.onNext(resp);
        responseObserver.onCompleted();
    }
}
