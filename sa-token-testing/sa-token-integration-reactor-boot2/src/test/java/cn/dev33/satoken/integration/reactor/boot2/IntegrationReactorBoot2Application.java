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
package cn.dev33.satoken.integration.reactor.boot2;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Spring Boot 2 WebFlux 集成测试统一启动类。
 * <p>starter 的自动装配会注册上下文 / CORS / 防火墙三个 Reactor Filter，
 * 全局鉴权过滤器由 {@link cn.dev33.satoken.integration.reactor.boot2.config.ReactorAuthConfig} 提供。</p>
 */
@SpringBootApplication
public class IntegrationReactorBoot2Application {

    public static void main(String[] args) {
        SpringApplication.run(IntegrationReactorBoot2Application.class, args);
    }

}
