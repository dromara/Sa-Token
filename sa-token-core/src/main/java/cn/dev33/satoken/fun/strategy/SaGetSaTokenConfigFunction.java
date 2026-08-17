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
package cn.dev33.satoken.fun.strategy;

import cn.dev33.satoken.config.SaTokenConfig;

import java.util.function.Supplier;

/**
 * 函数式接口：获取 SaTokenConfig 的策略
 *
 * <p>  返回：SaTokenConfig 对象  </p>
 * <p>  由 {@link cn.dev33.satoken.strategy.SaStrategy#getSaTokenConfig} 持有，赋值后 {@link cn.dev33.satoken.SaManager#getConfig()} 每次调用都会执行此函数  </p>
 * <p>  注意：实现中请勿回调 {@link cn.dev33.satoken.SaManager#getConfig()}，否则会无限递归  </p>
 *
 * @author click33
 * @since 1.46.0
 */
@FunctionalInterface
public interface SaGetSaTokenConfigFunction extends Supplier<SaTokenConfig> {

}
