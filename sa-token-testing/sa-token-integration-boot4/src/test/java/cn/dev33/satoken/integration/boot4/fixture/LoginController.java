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
package cn.dev33.satoken.integration.boot4.fixture;

import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Boot 4 登录冒烟端点。
 */
@RestController
@RequestMapping("/acc/")
public class LoginController {

    /** 模拟登录，返回 token */
    @GetMapping("/doLogin")
    public SaResult doLogin() {
        StpUtil.login(10001);
        return SaResult.ok().set("token", StpUtil.getTokenValue());
    }

    /** 当前请求是否已登录 */
    @GetMapping("/isLogin")
    public SaResult isLogin() {
        return SaResult.ok().set("data", StpUtil.isLogin());
    }

}
