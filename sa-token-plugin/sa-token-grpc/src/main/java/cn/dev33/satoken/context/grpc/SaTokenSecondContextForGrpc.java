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
package cn.dev33.satoken.context.grpc;

import cn.dev33.satoken.context.grpc.context.SaTokenGrpcContext;
import cn.dev33.satoken.context.grpc.model.SaRequestForGrpc;
import cn.dev33.satoken.context.grpc.model.SaResponseForGrpc;
import cn.dev33.satoken.context.grpc.model.SaStorageForGrpc;
import cn.dev33.satoken.context.model.SaRequest;
import cn.dev33.satoken.context.model.SaResponse;
import cn.dev33.satoken.context.model.SaStorage;
import cn.dev33.satoken.context.second.SaTokenSecondContext;

/**
 * Sa-Token 上下文 [grpc版本]
 *
 * @author lym
 * @since 1.34.0
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
    public boolean isValid() {
        return SaTokenGrpcContext.isNotNull();
    }

}
