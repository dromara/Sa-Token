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
package cn.dev33.satoken.integration.beaninject.boot2;

import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Bean 注入专项集成测试启动类：与 {@code integration.boot2} 主集成上下文隔离，避免自定义 Bean 污染其它用例。
 * <p>自定义 Bean 位于 {@code override} 子包，由组件扫描自动注册。</p>
 */
@SpringBootApplication
public class Boot2BeanInjectApplication {
}
