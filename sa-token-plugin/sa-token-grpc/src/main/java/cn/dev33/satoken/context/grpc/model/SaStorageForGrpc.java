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
package cn.dev33.satoken.context.grpc.model;

import cn.dev33.satoken.context.grpc.context.SaTokenGrpcContext;
import cn.dev33.satoken.context.model.SaStorage;

/**
 * Storage for grpc
 *
 * @author lym
 * @since <= 1.34.0
 */
public class SaStorageForGrpc implements SaStorage {

    /**
     * 获取底层源对象
     */
    @Override
    public Object getSource() {
        return SaTokenGrpcContext.getContext();
    }

    /**
     * 在 [Request作用域] 里写入一个值
     */
    @Override
    public SaStorage set(String key, Object value) {
        SaTokenGrpcContext.set(key, value);
        return this;
    }

    /**
     * 在 [Request作用域] 里获取一个值
     */
    @Override
    public Object get(String key) {
        return SaTokenGrpcContext.get(key);
    }

    /**
     * 在 [Request作用域] 里删除一个值
     */
    @Override
    public SaStorage delete(String key) {
        SaTokenGrpcContext.removeKey(key);
        return this;
    }

}
