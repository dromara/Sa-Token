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
package cn.dev33.satoken.loveqq.boot.context;

import cn.dev33.satoken.context.model.SaTokenContextModelBox;
import cn.dev33.satoken.fun.SaRetGenericFunction;
import cn.dev33.satoken.loveqq.boot.utils.SaTokenContextUtil;
import com.kfyty.loveqq.framework.web.core.http.ServerRequest;
import com.kfyty.loveqq.framework.web.core.http.ServerResponse;
import reactor.core.publisher.Mono;

/**
 * Reactor 上下文操作（异步），持有当前请求的 ServerWebExchange 全局引用
 *
 * @author click33
 * @since 1.19.0
 */
public class SaReactorHolder {
    public static final String REQUEST_CONTEXT_ATTRIBUTE = "com.kfyty.loveqq.framework.web.mvc.netty.request.support.RequestContextHolder.REQUEST_CONTEXT_ATTRIBUTE";
    public static final String RESPONSE_CONTEXT_ATTRIBUTE = "com.kfyty.loveqq.framework.web.mvc.netty.request.support.ResponseContextHolder.REQUEST_CONTEXT_ATTRIBUTE";

    /**
     * 获取 Mono < ServerRequest >
     *
     * @return /
     */
    public static Mono<ServerRequest> getRequest() {
        return Mono.deferContextual(Mono::just).map(e -> e.get(REQUEST_CONTEXT_ATTRIBUTE));
    }

    /**
     * 获取 Mono < ServerResponse >
     *
     * @return /
     */
    public static Mono<ServerResponse> getResponse() {
        return Mono.deferContextual(Mono::just).map(e -> e.get(RESPONSE_CONTEXT_ATTRIBUTE));
    }

    /**
     * 将 ServerRequest/ServerResponse 写入到同步上下文中，并执行一段代码，执行完毕清除上下文
     *
     * @return /
     */
    public static <R> Mono<R> sync(SaRetGenericFunction<R> fun) {
        return Mono.deferContextual(ctx -> {
            SaTokenContextModelBox prev = SaTokenContextUtil.setContext(ctx.get(REQUEST_CONTEXT_ATTRIBUTE), ctx.get(RESPONSE_CONTEXT_ATTRIBUTE));
            try {
                return Mono.just(fun.run());
            } finally {
				SaTokenContextUtil.clearContext(prev);
            }
        });
    }
}
