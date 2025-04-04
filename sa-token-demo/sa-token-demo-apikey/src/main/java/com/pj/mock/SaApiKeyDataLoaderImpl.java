package com.pj.mock;

import cn.dev33.satoken.apikey.loader.SaApiKeyDataLoader;
import cn.dev33.satoken.apikey.model.ApiKeyModel;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * API Key 数据加载器实现类 （从数据库查询）
 *
 * @author click33
 * @since 2025/4/4
 */
//@Component  // 打开此注解后，springboot 会自动注入此组件，打开 Sa-Token API Key 模块的数据库模式
public class SaApiKeyDataLoaderImpl implements SaApiKeyDataLoader {

    @Autowired
    SaApiKeyMockMapper apiKeyMockMapper;

    // 指定框架不再维护 API Key 索引信息，而是由我们手动从数据库维护
    @Override
    public Boolean getIsRecordIndex() {
        return false;
    }

    // 根据 apiKey 从数据库获取 ApiKeyModel 信息 （实现此方法无需为数据做缓存处理，框架内部已包含缓存逻辑）
    @Override
    public ApiKeyModel getApiKeyModelFromDatabase(String apiKey) {
        return apiKeyMockMapper.getApiKeyModel(apiKey);
    }

}