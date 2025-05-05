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

import java.util.function.BiFunction;

/**
 * 函数式接口：sso-server 端：登录处理函数
 *
 * <p>  参数：账号、密码  </p>
 * <p>  返回：登录结果  </p>
 *
 * @author click33
 * @since 1.38.0
 */
@FunctionalInterface
public interface DoLoginHandleFunction extends BiFunction<String, String, Object> {

}