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
package cn.dev33.satoken.context.grpc.context;

import io.grpc.*;

import java.util.HashMap;
import java.util.Map;

/**
 * @author lym
 * @since 2022/8/25 11:02
 **/
public class SaTokenGrpcContext {
    /**
     * grpc请求上下文。请求完成后会由grpc自动清空
     *
     * @see Contexts#interceptCall(Context, ServerCall, Metadata, ServerCallHandler)
     */
    private static final Context.Key<Map<String, Object>> SA_TOKEN_CONTEXT_KEY =
            Context.key("sa-token-context");

    public static Object get(String key) {
        return SA_TOKEN_CONTEXT_KEY.get().get(key);
    }

    public static void set(String key, Object value) {
        SA_TOKEN_CONTEXT_KEY.get().put(key, value);
    }

    public static void removeKey(String key) {
        SA_TOKEN_CONTEXT_KEY.get().remove(key);
    }

    public static Map<String, Object> getContext() {
        return SA_TOKEN_CONTEXT_KEY.get();
    }

    public static boolean isNotNull() {
        return SA_TOKEN_CONTEXT_KEY.get() != null;
    }

    public static Context create() {
        return Context.current().withValue(SaTokenGrpcContext.SA_TOKEN_CONTEXT_KEY, new HashMap<>());
    }
}
