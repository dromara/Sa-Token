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
package cn.dev33.satoken.solon.json;

import cn.dev33.satoken.json.SaJsonTemplate;
import org.noear.snack.ONode;

/**
 * @author noear
 * @since 2.0
 */
public class SaJsonTemplateForSnack3 implements SaJsonTemplate {

    /**
     * 序列化：对象 -> json 字符串
     *
     * @param obj /
     * @return /
     */
    @Override
    public String objectToJson(Object obj) {
        return ONode.stringify(obj);
    }

    /**
     * 反序列化：json 字符串 → 对象
     */
    @Override
    public <T> T jsonToObject(String jsonStr, Class<T> type) {
        return ONode.deserialize(jsonStr, type);
    }

    /**
     * 反序列化：json 字符串 → 对象
     *
     * @param jsonStr /
     * @return /
     */
    @Override
    public Object jsonToObject(String jsonStr) {
        return ONode.deserialize(jsonStr);
    }

}
