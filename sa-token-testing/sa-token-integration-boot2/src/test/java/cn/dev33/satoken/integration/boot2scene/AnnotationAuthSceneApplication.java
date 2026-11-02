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
package cn.dev33.satoken.integration.boot2scene;

import cn.dev33.satoken.integration.boot2.config.AnnotationAuthConfig;
import cn.dev33.satoken.integration.boot2.config.GlobalExceptionHandler;
import cn.dev33.satoken.integration.boot2.support.StpInterfaceImpl;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

/**
 * 注解鉴权场景自己的启动类：只扫注解夹具，不把登录 / 路由 Controller 带进来。
 * <p>放在 {@code boot2} 扫描树外面，避免被 {@code IntegrationBoot2Application} 当成配置类扫进去。</p>
 */
@SpringBootApplication(scanBasePackages = "cn.dev33.satoken.integration.boot2.fixture.annotation")
@Import({ AnnotationAuthConfig.class, GlobalExceptionHandler.class, StpInterfaceImpl.class })
public class AnnotationAuthSceneApplication {
}
