/*
 * Copyright 2020-2099 sa-token.com
 *
 * Licensed under the Apache License, Version  2.0 (the "License");
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
package cn.dev33.satoken.apikey.template;

import cn.dev33.satoken.apikey.SaApiKeyManager;
import cn.dev33.satoken.apikey.config.SaApiKeyConfig;
import cn.dev33.satoken.apikey.exception.ApiKeyException;
import cn.dev33.satoken.apikey.loader.SaApiKeyDataLoader;
import cn.dev33.satoken.apikey.model.ApiKeyModel;
import cn.dev33.satoken.context.mock.SaRequestForMock;
import cn.dev33.satoken.context.mock.SaTokenContextMockUtil;
import cn.dev33.satoken.dao.SaTokenDao;
import cn.dev33.satoken.secure.SaBase64Util;
import cn.dev33.satoken.test.SaTokenTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * {@link SaApiKeyTemplate} 请求读取相关方法测试
 *
 * @author click33
 * @since 1.46.0
 */
@SaTokenTest
public class SaApiKeyTemplateRequestTest {

    private SaApiKeyConfig backupConfig;
    private SaApiKeyDataLoader backupLoader;
    private SaApiKeyTemplate backupTemplate;

    @BeforeEach
    public void backup() {
        backupConfig = SaApiKeyManager.getConfig();
        backupLoader = SaApiKeyManager.getSaApiKeyDataLoader();
        backupTemplate = SaApiKeyManager.getSaApiKeyTemplate();
        SaApiKeyManager.setConfig(new SaApiKeyConfig().setIsRecordIndex(false));
    }

    @AfterEach
    public void restore() {
        SaApiKeyManager.setConfig(backupConfig);
        SaApiKeyManager.setSaApiKeyDataLoader(backupLoader);
        SaApiKeyManager.setSaApiKeyTemplate(backupTemplate);
    }

    private ApiKeyModel buildAk(String apiKey, Object loginId) {
        return new ApiKeyModel()
                .setApiKey(apiKey).setLoginId(loginId).setCreateTime(System.currentTimeMillis())
                .setExpiresTime(SaTokenDao.NEVER_EXPIRE).setIsValid(true);
    }

    /** 应该从请求参数读取 ApiKey */
    @Test
    public void readApiKeyValue_fromParam() {
        SaApiKeyTemplate t = new SaApiKeyTemplate();
        Map<String, String> params = new LinkedHashMap<>();
        params.put("apikey", "AK-param");
        SaTokenContextMockUtil.setMockContext();
        ((SaRequestForMock) cn.dev33.satoken.context.SaHolder.getRequest()).parameterMap = params;
        try {
            Assertions.assertEquals("AK-param", t.readApiKeyValue(cn.dev33.satoken.context.SaHolder.getRequest()));
        } finally {
            SaTokenContextMockUtil.clearContext();
        }
    }

    /** 应该从请求头读取 ApiKey */
    @Test
    public void readApiKeyValue_fromHeader() {
        SaApiKeyTemplate t = new SaApiKeyTemplate();
        SaTokenContextMockUtil.setMockContext();
        try {
            SaRequestForMock req = (SaRequestForMock) cn.dev33.satoken.context.SaHolder.getRequest();
            req.headerMap.put("apikey", "AK-header");
            Assertions.assertEquals("AK-header", t.readApiKeyValue(req));
        } finally {
            SaTokenContextMockUtil.clearContext();
        }
    }

    /** 应该从 Authorization 头读取 ApiKey（带末尾冒号时应该去掉） */
    @Test
    public void readApiKeyValue_fromAuthorization_withColon() {
        SaApiKeyTemplate t = new SaApiKeyTemplate();
        SaTokenContextMockUtil.setMockContext();
        try {
            SaRequestForMock req = (SaRequestForMock) cn.dev33.satoken.context.SaHolder.getRequest();
            // Basic base64("AK-auth:")
            req.headerMap.put("Authorization", "Basic " + SaBase64Util.encode("AK-auth:"));
            Assertions.assertEquals("AK-auth", t.readApiKeyValue(req));
        } finally {
            SaTokenContextMockUtil.clearContext();
        }
    }

    /** 应该从 Authorization 头读取 ApiKey（不带冒号） */
    @Test
    public void readApiKeyValue_fromAuthorization_withoutColon() {
        SaApiKeyTemplate t = new SaApiKeyTemplate();
        SaTokenContextMockUtil.setMockContext();
        try {
            SaRequestForMock req = (SaRequestForMock) cn.dev33.satoken.context.SaHolder.getRequest();
            req.headerMap.put("Authorization", "Basic " + SaBase64Util.encode("AK-auth2"));
            Assertions.assertEquals("AK-auth2", t.readApiKeyValue(req));
        } finally {
            SaTokenContextMockUtil.clearContext();
        }
    }

    /** 三处都没有 ApiKey 时应该返回 null */
    @Test
    public void readApiKeyValue_none_returnsNull() {
        SaApiKeyTemplate t = new SaApiKeyTemplate();
        SaTokenContextMockUtil.setMockContext();
        try {
            Assertions.assertNull(t.readApiKeyValue(cn.dev33.satoken.context.SaHolder.getRequest()));
        } finally {
            SaTokenContextMockUtil.clearContext();
        }
    }

    /** currentApiKey：请求中带有效 ApiKey 时应该返回 ApiKeyModel */
    @Test
    public void currentApiKey_valid() {
        SaApiKeyTemplate t = new SaApiKeyTemplate();
        t.saveApiKey(buildAk("AK-cur", 10001));
        SaTokenContextMockUtil.setMockContext();
        ((SaRequestForMock) cn.dev33.satoken.context.SaHolder.getRequest()).parameterMap.put("apikey", "AK-cur");
        try {
            Assertions.assertEquals("AK-cur", t.currentApiKey().getApiKey());
        } finally {
            SaTokenContextMockUtil.clearContext();
        }
    }

    /** currentApiKey：请求中无 ApiKey 时必须抛异常 */
    @Test
    public void currentApiKey_invalid_throws() {
        SaTokenContextMockUtil.setMockContext();
        try {
            Assertions.assertThrows(ApiKeyException.class, () -> new SaApiKeyTemplate().currentApiKey());
        } finally {
            SaTokenContextMockUtil.clearContext();
        }
    }
}
