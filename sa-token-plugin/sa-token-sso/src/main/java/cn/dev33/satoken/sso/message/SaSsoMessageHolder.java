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
package cn.dev33.satoken.sso.message;


import cn.dev33.satoken.sso.error.SaSsoErrorCode;
import cn.dev33.satoken.sso.exception.SaSsoException;
import cn.dev33.satoken.sso.function.SaSsoMessageHandleFunction;
import cn.dev33.satoken.sso.message.handle.SaSsoMessageHandle;
import cn.dev33.satoken.sso.message.handle.SaSsoMessageSimpleHandle;
import cn.dev33.satoken.sso.template.SaSsoTemplate;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * SSO 消息处理器 - 持有器
 *
 * @author click33
 * @since 1.43.0
 */
public class SaSsoMessageHolder {

    /**
     * 所有已注册的消息处理器
     */
    public final Map<String, SaSsoMessageHandle> messageHandleMap = new LinkedHashMap<>();

    /**
     * 判断是否具有指定类型的消息处理器
     *
     * @param type 消息类型
     * @return /
     */
    public boolean hasHandle(String type) {
        return messageHandleMap.containsKey(type);
    }

    /**
     * 删除指定类型的消息处理器
     *
     * @param type 消息类型
     */
    public SaSsoMessageHolder removeHandle(String type) {
        messageHandleMap.remove(type);
        return this;
    }

    /**
     * 添加指定类型的消息处理器
     *
     * @param handle /
     * @return 对象自身
     */
    public SaSsoMessageHolder addHandle(SaSsoMessageHandle handle) {
        messageHandleMap.put(handle.getHandlerType(), handle);
        return this;
    }

    /**
     * 添加指定类型的简单消息处理器
     *
     * @param type 要处理的消息类型
     * @param handle 要执行的方法
     * @return 对象自身
     */
    public SaSsoMessageHolder addHandle(String type, SaSsoMessageHandleFunction handle) {
        messageHandleMap.put(type, new SaSsoMessageSimpleHandle(type, handle));
        return this;
    }

    /**
     * 获取指定类型的消息处理器
     *
     * @param type /
     */
    public SaSsoMessageHandle getHandle(String type) {
        return messageHandleMap.get(type);
    }

    /**
     * 处理指定消息
     *
     * @param ssoTemplate /
     * @param message /
     * @return 处理结果
     */
    public Object handleMessage(SaSsoTemplate ssoTemplate, SaSsoMessage message) {
        SaSsoMessageHandle handle = messageHandleMap.get(message.getType());
        if(handle == null) {
            throw new SaSsoException("未能找到消息处理器: " + message.getType()).setCode(SaSsoErrorCode.CODE_30021);
        }
        return handle.handle(ssoTemplate, message);
    }

}
