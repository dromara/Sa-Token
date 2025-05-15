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
package cn.dev33.satoken.sign.config;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * SaSignManyConfig 配置包装类，以更方便框架完成属性注入操作
 *
 * @author click33
 * @since 1.43.0
 */
public class SaSignManyConfigWrapper {

    public Map<String, SaSignConfig> signMany = new LinkedHashMap<>();

    /**
     * 获取
     *
     * @return signMany
     */
    public Map<String, SaSignConfig> getSignMany() {
        return this.signMany;
    }

    /**
     * 设置
     *
     * @param signMany
     */
    public void setSignMany(Map<String, SaSignConfig> signMany) {
        this.signMany = signMany;
    }

    @Override
    public String toString() {
        return "SaSignManyConfigWrapper{" +
                "signMany=" + signMany +
                '}';
    }

}