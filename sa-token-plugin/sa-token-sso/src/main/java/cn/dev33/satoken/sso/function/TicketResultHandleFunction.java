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

import cn.dev33.satoken.sso.processor.SaSsoClientProcessor;

import java.util.function.BiFunction;

/**
 * 函数式接口：SSO-Client端：自定义校验Ticket返回值的处理逻辑 （每次从认证中心获取校验Ticket的结果后调用）
 *
 * <p>  参数：loginId, back  </p>
 * <p>  返回：返回给前端的值  </p>
 *
 * @author click33
 * @since 1.38.0
 */
@FunctionalInterface
public interface TicketResultHandleFunction extends BiFunction<SaSsoClientProcessor.CheckTicketResult, String, Object> {

}