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

import cn.dev33.satoken.util.SaResult;

import java.util.function.BiFunction;

/**
 * 函数式接口：sso-server 端：在校验 ticket 后，给 sso-client 端追加返回信息的函数
 *
 * <p>  参数：loginId, SaResult 响应参数对象  </p>
 * <p>  返回：SaResult 响应参数对象  </p>
 *
 * @author click33
 * @since 1.38.0
 */
@FunctionalInterface
public interface CheckTicketAppendDataFunction extends BiFunction<Object, SaResult, SaResult> {

}