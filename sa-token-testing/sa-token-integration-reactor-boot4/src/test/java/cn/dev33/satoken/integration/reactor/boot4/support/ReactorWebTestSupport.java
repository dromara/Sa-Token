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
package cn.dev33.satoken.integration.reactor.boot4.support;

import org.springframework.context.ApplicationContext;
import org.springframework.test.web.reactive.server.WebTestClient;

/**
 * Boot 4 WebFlux 集成测试辅助：按应用上下文组装 WebTestClient。
 */
public final class ReactorWebTestSupport {

    private ReactorWebTestSupport() {
    }

    /** 基于应用上下文组装 WebTestClient，走真实 WebFilter 链 */
    public static WebTestClient create(ApplicationContext applicationContext) {
        return WebTestClient.bindToApplicationContext(applicationContext).build();
    }

}
