/*
 * Copyright 2020-2099 sa-token.com
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
import reactor.util.context.ContextView;

/**
 * Reactor 上下文操作（异步），持有当前请求的 ServerWebExchange 全局引用
 *
 * @author click33
 * @since 1.19.0
 */
public class SaReactorHolder {
    /**
     * LoveQQ 1.1.5+ 把包名从 mvc.netty 改成了 mvc.reactor，Reactor Context 的 key 跟着变。
     * 两套都认，避免 Demo 还停在 1.1.2 时 sync 直接 NoSuchElementException。
     */
    public static final String REQUEST_CONTEXT_ATTRIBUTE = "com.kfyty.loveqq.framework.web.mvc.reactor.request.support.RequestContextHolder.REQUEST_CONTEXT_ATTRIBUTE";
    public static final String RESPONSE_CONTEXT_ATTRIBUTE = "com.kfyty.loveqq.framework.web.mvc.reactor.request.support.ResponseContextHolder.REQUEST_CONTEXT_ATTRIBUTE";
    private static final String REQUEST_CONTEXT_ATTRIBUTE_NETTY = "com.kfyty.loveqq.framework.web.mvc.netty.request.support.RequestContextHolder.REQUEST_CONTEXT_ATTRIBUTE";
    private static final String RESPONSE_CONTEXT_ATTRIBUTE_NETTY = "com.kfyty.loveqq.framework.web.mvc.netty.request.support.ResponseContextHolder.RESPONSE_CONTEXT_ATTRIBUTE";
    private static final String RESPONSE_CONTEXT_ATTRIBUTE_NETTY_LEGACY = "com.kfyty.loveqq.framework.web.mvc.netty.request.support.ResponseContextHolder.REQUEST_CONTEXT_ATTRIBUTE";
    private static final String RESPONSE_CONTEXT_ATTRIBUTE_REACTOR = "com.kfyty.loveqq.framework.web.mvc.reactor.request.support.ResponseContextHolder.RESPONSE_CONTEXT_ATTRIBUTE";

    /**
     * 获取 Mono < ServerRequest >
     *
     * @return /
     */
    public static Mono<ServerRequest> getRequest() {
        return Mono.deferContextual(ctx -> Mono.just(getRequired(ctx, REQUEST_CONTEXT_ATTRIBUTE, REQUEST_CONTEXT_ATTRIBUTE_NETTY)));
    }

    /**
     * 获取 Mono < ServerResponse >
     *
     * @return /
     */
    public static Mono<ServerResponse> getResponse() {
        return Mono.deferContextual(ctx -> Mono.just(getRequired(ctx, RESPONSE_CONTEXT_ATTRIBUTE, RESPONSE_CONTEXT_ATTRIBUTE_REACTOR,
                RESPONSE_CONTEXT_ATTRIBUTE_NETTY, RESPONSE_CONTEXT_ATTRIBUTE_NETTY_LEGACY)));
    }

    /**
     * 将 ServerRequest/ServerResponse 写入到同步上下文中，并执行一段代码，执行完毕清除上下文
     *
     * @return /
     */
    public static <R> Mono<R> sync(SaRetGenericFunction<R> fun) {
        return Mono.deferContextual(ctx -> {
            SaTokenContextModelBox prev = SaTokenContextUtil.setContext(
                    getRequired(ctx, REQUEST_CONTEXT_ATTRIBUTE, REQUEST_CONTEXT_ATTRIBUTE_NETTY),
                    getRequired(ctx, RESPONSE_CONTEXT_ATTRIBUTE, RESPONSE_CONTEXT_ATTRIBUTE_REACTOR,
                            RESPONSE_CONTEXT_ATTRIBUTE_NETTY, RESPONSE_CONTEXT_ATTRIBUTE_NETTY_LEGACY));
            try {
                return Mono.just(fun.run());
            } finally {
				SaTokenContextUtil.clearContext(prev);
            }
        });
    }

    @SuppressWarnings("unchecked")
    private static <T> T getRequired(ContextView ctx, String... keys) {
        for (String key : keys) {
            if (ctx.hasKey(key)) {
                return (T) ctx.get(key);
            }
        }
        return ctx.get(keys[0]);
    }
}
