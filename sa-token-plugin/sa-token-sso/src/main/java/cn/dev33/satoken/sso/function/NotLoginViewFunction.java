/*
 * Copyright 2020-2099 sa-token.cc
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
package cn.dev33.satoken.sso.function;

import java.util.function.Supplier;

/**
 * 函数式接口：sso-server 端：未登录时返回的 View
 *
 * <p>  参数：无  </p>
 * <p>  返回：未登录时的 View 视图  </p>
 *
 * @author click33
 * @since 1.38.0
 */
@FunctionalInterface
public interface NotLoginViewFunction extends Supplier<Object> {

}