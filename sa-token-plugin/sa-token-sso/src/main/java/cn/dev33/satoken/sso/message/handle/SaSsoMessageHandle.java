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
package cn.dev33.satoken.sso.message.handle;


import cn.dev33.satoken.sso.message.SaSsoMessage;
import cn.dev33.satoken.sso.template.SaSsoTemplate;

/**
 * SSO 消息处理器 - 父接口
 *
 * @author click33
 * @since 1.43.0
 */
public interface SaSsoMessageHandle {

    /**
     * 获取所要处理的消息类型
     *
     * @return /
     */
    String getHandlerType();

    /**
     * 具体要执行的处理方法
     *
     * @param ssoTemplate /
     * @param message /
     * @return /
     */
    Object handle(SaSsoTemplate ssoTemplate, SaSsoMessage message);

}
