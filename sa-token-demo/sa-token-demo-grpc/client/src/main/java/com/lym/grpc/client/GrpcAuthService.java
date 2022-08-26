package com.lym.grpc.client;

import com.google.protobuf.Empty;
import com.lym.grpc.auth.Auth;
import com.lym.grpc.auth.AuthServiceGrpc;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;

/**
 * @author lym
 * @description
 * @date 2022/8/26 11:02
 **/
@Service
public class GrpcAuthService {
    @GrpcClient("test-server")
    private AuthServiceGrpc.AuthServiceBlockingStub grpcAuthService;

    public boolean isLogin() {
        Auth.GrpcBool resp = grpcAuthService.isLogin(Empty.getDefaultInstance());
        return resp.getVal();
    }

    public String login(Integer id) {
        Auth.GrpcString resp = grpcAuthService.login(Auth.GrpcInt.newBuilder().setVal(id).build());
        return resp.getVal();
    }
}
