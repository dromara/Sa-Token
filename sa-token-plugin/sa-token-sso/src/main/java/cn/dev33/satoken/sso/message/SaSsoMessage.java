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


import cn.dev33.satoken.application.SaSetValueInterface;
import cn.dev33.satoken.sso.error.SaSsoErrorCode;
import cn.dev33.satoken.sso.exception.SaSsoException;
import cn.dev33.satoken.util.SaFoxUtil;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * SSO 消息 Model
 *
 * @author click33
 * @since 1.43.0
 */
public class SaSsoMessage extends LinkedHashMap<String, Object> implements SaSetValueInterface, Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    /**
     * KEY：TYPE
     */
    public static final String MSG_TYPE = "msg_type";

    public SaSsoMessage() {

    }

    /**
     * 构造函数
     * @param type 消息类型
     */
    public SaSsoMessage(String type) {
        setType(type);
    }

    /**
     * 构造函数
     * @param map 消息参数
     */
    public SaSsoMessage(Map<String, ?> map) {
        this.putAll(map);
    }

    /**
     * 获取消息类型
     * @return /
     */
    public String getType() {
        return getString(MSG_TYPE);
    }

    /**
     * 设置消息类型
     * @param type /
     * @return /
     */
    public SaSsoMessage setType(String type) {
        return set(MSG_TYPE, type);
    }

    /**
     * 校验消息类型
     */
    public void checkType() {
        if(SaFoxUtil.isEmpty(getString(MSG_TYPE))) {
            throw new SaSsoException("消息类型不可为空").setCode(SaSsoErrorCode.CODE_30022);
        }
    }

    // -----------

    @Override
    public Object get(String key) {
        return super.get(key);
    }

    @Override
    public SaSsoMessage set(String key, Object value) {
        super.put(key, value);
        return this;
    }

    @Override
    public SaSsoMessage delete(String key) {
        super.remove(key);
        return this;
    }

    // -----------

    /**
     * 获取一个值 （此值必须存在，否则抛出异常 ）
     * @param key 键
     * @return 参数值
     */
    public Object getValueNotNull(String key) {
        Object value = get(key);
        if(SaFoxUtil.isEmpty(value)) {
            throw new SaSsoException("缺少参数：" + key).setCode(SaSsoErrorCode.CODE_30024);
        }
        return value;
    }

}
