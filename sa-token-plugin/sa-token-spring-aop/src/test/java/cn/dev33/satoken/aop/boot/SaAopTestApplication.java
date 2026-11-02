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
package cn.dev33.satoken.aop.boot;

import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 测试用 Boot 启动类，模拟业务项目接入 spring-aop。
 *
 * <p>会扫到本包下的 {@code @Service}；插件本身走 {@code spring.factories} 自动装配。</p>
 *
 * <p>{@code @SpringBootApplication} 不等于起 Tomcat。这里配合 {@code WebEnvironment.NONE}，
 * 只起容器织 AOP，不搭 Web。</p>
 *
 * @author click33
 * @since 1.46.0
 */
@SpringBootApplication
public class SaAopTestApplication {

}
