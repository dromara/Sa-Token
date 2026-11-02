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
package cn.dev33.satoken.integration.reactor.boot2.fixture.login;

import cn.dev33.satoken.reactor.context.SaReactorHolder;
import cn.dev33.satoken.reactor.context.SaReactorSyncHolder;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * 登录测试控制器（WebFlux 形式：通过 SaReactorHolder.sync 恢复请求上下文）。
 */
@RestController
@RequestMapping("/acc/")
public class LoginController {

    /** 登录（SaReactorHolder.sync 形式） */
    @RequestMapping("doLogin")
    public Mono<SaResult> doLogin(@RequestParam(defaultValue = "10001") Object id) {
        return SaReactorHolder.sync(() -> {
            StpUtil.login(id);
            return SaResult.ok("登录成功").set("token", StpUtil.getTokenValue());
        });
    }

    /** 直接返回 token 字符串，方便测试取用 */
    @RequestMapping("getToken")
    public Mono<String> getToken(@RequestParam(defaultValue = "10001") Object id) {
        return SaReactorHolder.sync(() -> {
            StpUtil.login(id);
            return StpUtil.getTokenValue();
        });
    }

    /** 查询登录状态（SaReactorHolder.sync 形式） */
    @RequestMapping("isLogin")
    public Mono<SaResult> isLogin() {
        return SaReactorHolder.sync(() -> SaResult.data(StpUtil.isLogin()));
    }

    /** 查询登录状态（SaReactorSyncHolder 手动形式，上下文由调用方管理） */
    @RequestMapping("isLogin2")
    public SaResult isLogin2(ServerWebExchange exchange) {
        return SaReactorSyncHolder.setContext(exchange, () -> SaResult.data(StpUtil.isLogin()));
    }

    /** 查询 Token 信息 */
    @RequestMapping("tokenInfo")
    public Mono<SaResult> tokenInfo() {
        return SaReactorHolder.sync(() -> SaResult.data(StpUtil.getTokenInfo()));
    }

    /** 注销 */
    @RequestMapping("logout")
    public Mono<SaResult> logout() {
        return SaReactorHolder.sync(() -> {
            StpUtil.logout();
            return SaResult.ok("注销成功");
        });
    }

}
