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
package cn.dev33.satoken.apikey.loader;

import cn.dev33.satoken.SaManager;
import cn.dev33.satoken.apikey.model.ApiKeyModel;

/**
 * ApiKey 数据加载器
 *
 * @author click33
 * @since 1.42.0
 */
public interface SaApiKeyDataLoader {

    /**
     * 获取：框架是否保存索引信息
     *
     * @return /
     */
    default Boolean getIsRecordIndex() {
        return SaManager.getConfig().getApiKey().getIsRecordIndex();
    }

    /**
     * 根据 apiKey 从数据库获取 ApiKeyModel 信息 （实现此方法无需为数据做缓存处理，框架内部已包含缓存逻辑）
     *
     * @param apiKey /
     * @return ApiKeyModel
     */
    default ApiKeyModel getApiKeyModelFromDatabase(String apiKey) {
        return null;
    }

}
