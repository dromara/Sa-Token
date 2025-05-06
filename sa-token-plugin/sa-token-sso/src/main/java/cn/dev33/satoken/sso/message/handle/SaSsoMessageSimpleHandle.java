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


import cn.dev33.satoken.sso.function.SaSsoMessageHandleFunction;
import cn.dev33.satoken.sso.message.SaSsoMessage;
import cn.dev33.satoken.sso.template.SaSsoTemplate;

/**
 * SSO 消息处理器 - 简单实现，方便 lambda 表达式编程
 *
 * @author click33
 * @since 1.43.0
 */
public class SaSsoMessageSimpleHandle implements SaSsoMessageHandle{

    public String type;

    public SaSsoMessageHandleFunction handle;

    /**
     * SSO 消息处理器 - 简单实现，方便 lambda 表达式编程
     * @param type 要处理的消息类型
     * @param handle 要执行的方法
     */
    public SaSsoMessageSimpleHandle(String type, SaSsoMessageHandleFunction handle) {
        this.type = type;
        this.handle = handle;
    }

    /**
     * 获取所要处理的消息类型
     *
     * @return /
     */
    @Override
    public String getHandlerType() {
        return type;
    }

    /**
     * 具体要执行的处理方法
     *
     * @param ssoTemplate /
     * @param message /
     * @return /
     */
    @Override
    public Object handle(SaSsoTemplate ssoTemplate, SaSsoMessage message){
        return handle.execute(ssoTemplate, message);
    }

}
