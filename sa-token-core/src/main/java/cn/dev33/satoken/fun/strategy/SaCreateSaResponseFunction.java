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

import cn.dev33.satoken.context.model.SaResponse;

import java.util.function.Function;

/**
 * 函数式接口：创建 SaResponse 的策略
 *
 * <p>  参数：底层框架原生响应对象（如 HttpServletResponse、ServerHttpResponse、Context 等）  </p>
 * <p>  返回：SaResponse 对象  </p>
 *
 * @author click33
 * @since 1.46.0
 */
@FunctionalInterface
public interface SaCreateSaResponseFunction extends Function<Object, SaResponse> {

}
