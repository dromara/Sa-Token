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
package cn.dev33.satoken.quick.boot;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 测试用 Boot 启动类，模拟业务项目接入 quick-login。
 *
 * <p>放在 {@code .boot} 包下，避免把插件自己的 {@code @Configuration} 扫进去，
 * 插件仍然走 {@code spring.factories} 自动装配，和正式项目一样。</p>
 *
 * <p>{@code @SpringBootApplication} 只声明「这是个 Boot 应用」，本身不会起 Tomcat。
 * 测例用 {@code WebEnvironment.MOCK} 拉起它：有完整 Servlet/Filter 链，但没有监听端口。</p>
 *
 * @author click33
 * @since 1.46.0
 */
@SpringBootApplication
@RestController
public class SaQuickLoginTestApplication {

    /** 受保护的首页，未登录时应该被过滤器拦住 */
    @GetMapping("/")
    public String index() {
        return "index";
    }

    /** 给 exclude 用例用的开放接口 */
    @GetMapping("/open")
    public String open() {
        return "open";
    }

}
