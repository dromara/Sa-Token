package com.pj.mock;

import cn.dev33.satoken.apikey.model.ApiKeyModel;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * 模拟数据库操作类
 *
 * @author click33
 * @since 2025/4/4
 */
@Service
public class SaApiKeyMockMapper {

    // 添加模拟测试数据
    public static final Map<String, ApiKeyModel> map = new HashMap<>();
    static {
        ApiKeyModel ak1 = new ApiKeyModel();
        ak1.setLoginId(10001);  // 设置绑定的用户 id
        ak1.setApiKey("AK-NAO6u57zbOWCmLaiVQuVW2tyt3rHpZrXkaQp");  // 设置 API Key 值
        ak1.setTitle("test");	  // 设置名称
        ak1.setExpiresTime(System.currentTimeMillis() + 2592000);  // 设置失效时间，13位时间戳，-1=永不失效
        map.put(ak1.getApiKey(), ak1);

        ApiKeyModel ak2 = new ApiKeyModel();
        ak2.setLoginId(10001);  // 设置绑定的用户 id
        ak2.setApiKey("AK-NxcO63u57zbOWCmLaiVQuVWXssRwAxFcAxcFF");  // 设置 API Key 值
        ak2.setTitle("commit2");	  // 设置名称
        ak1.addScope("commit", "pull");  // 设置权限范围
        ak2.setExpiresTime(System.currentTimeMillis() + 2592000);  // 设置失效时间，13位时间戳，-1=永不失效
        map.put(ak2.getApiKey(), ak2);
    }

    // 返回指定 API Key 对应的 ApiKeyModel
    public ApiKeyModel getApiKeyModel(String apiKey) {
        return map.get(apiKey);
    }

}