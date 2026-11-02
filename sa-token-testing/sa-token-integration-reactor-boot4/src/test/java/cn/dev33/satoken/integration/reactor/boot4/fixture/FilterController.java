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
package cn.dev33.satoken.integration.reactor.boot4.fixture;

import cn.dev33.satoken.reactor.context.SaReactorHolder;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

/**
 * Boot 4 SaReactorFilter 测试端点：提供拦截与放行两类路由。
 */
@RestController
@RequestMapping("/filter/")
public class FilterController {

    /** 放行路由：模拟登录，返回 token */
    @GetMapping("/open/doLogin")
    public Mono<SaResult> doLogin() {
        return SaReactorHolder.sync(() -> {
            StpUtil.login(10001);
            return SaResult.ok().set("token", StpUtil.getTokenValue());
        });
    }

    /** 需要登录才能访问的路由 */
    @GetMapping("/protected")
    public Mono<SaResult> protectedRoute() {
        return SaReactorHolder.sync(SaResult::ok);
    }

    /** 放行路由，无需登录 */
    @GetMapping("/open/free")
    public Mono<SaResult> openRoute() {
        return SaReactorHolder.sync(SaResult::ok);
    }

}
