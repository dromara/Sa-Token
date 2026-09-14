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
package cn.dev33.satoken.integration.reactor.boot2.fixture.guard;

import cn.dev33.satoken.reactor.context.SaReactorHolder;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

/**
 * 受全局过滤器 / 权限校验保护的路由。
 */
@RestController
@RequestMapping("/guard/")
public class GuardController {

    /** 全局过滤器已做 checkLogin，走到这里说明路由守卫放行 */
    @RequestMapping("check")
    public Mono<SaResult> check() {
        return SaReactorHolder.sync(() -> SaResult.ok("已通过路由守卫"));
    }

    /** 需要权限 article:add 的路由 */
    @RequestMapping("permission")
    public Mono<SaResult> permission() {
        return SaReactorHolder.sync(() -> {
            StpUtil.checkPermission("article:add");
            return SaResult.ok("已通过权限校验");
        });
    }

}
