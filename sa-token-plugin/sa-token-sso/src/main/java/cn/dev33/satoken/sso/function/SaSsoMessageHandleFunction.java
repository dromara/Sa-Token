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

import cn.dev33.satoken.sso.message.SaSsoMessage;
import cn.dev33.satoken.sso.template.SaSsoTemplate;

/**
 * 函数式接口：处理 SSO 消息的函数式接口
 *
 * <p>  参数：ssoTemplate 模板对象, 要处理的 message 消息  </p>
 * <p>  返回：任意值   </p>
 *
 * @author click33
 * @since 1.38.0
 */
@FunctionalInterface
public interface SaSsoMessageHandleFunction {

    Object execute(SaSsoTemplate ssoTemplate, SaSsoMessage message);

}