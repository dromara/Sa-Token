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
package cn.dev33.satoken.integration.boot2;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Spring Boot 2 集成测试统一启动类。
 * <p>所有集成测试共用此上下文，避免历史上多个 {@code StartUpApplication} 导致组件扫描不一致。</p>
 */
@SpringBootApplication
public class IntegrationBoot2Application {

    public static void main(String[] args) {
        SpringApplication.run(IntegrationBoot2Application.class, args);
    }

}
