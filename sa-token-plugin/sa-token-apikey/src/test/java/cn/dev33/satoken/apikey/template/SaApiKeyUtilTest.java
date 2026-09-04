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
import cn.dev33.satoken.apikey.loader.SaApiKeyDataLoader;
import cn.dev33.satoken.apikey.model.ApiKeyModel;
import cn.dev33.satoken.context.mock.SaRequestForMock;
import cn.dev33.satoken.context.mock.SaTokenContextMockUtil;
import cn.dev33.satoken.dao.SaTokenDao;
import cn.dev33.satoken.test.SaTokenTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * {@link SaApiKeyUtil} 静态委托方法测试
 *
 * @author click33
 * @since 1.46.0
 */
@SaTokenTest
public class SaApiKeyUtilTest {

    private SaApiKeyConfig backupConfig;
    private SaApiKeyDataLoader backupLoader;
    private SaApiKeyTemplate backupTemplate;
    private SaApiKeyTemplate template;

    @BeforeEach
    public void setup() {
        backupConfig = SaApiKeyManager.getConfig();
        backupLoader = SaApiKeyManager.getSaApiKeyDataLoader();
        backupTemplate = SaApiKeyManager.getSaApiKeyTemplate();
        SaApiKeyManager.setConfig(new SaApiKeyConfig().setIsRecordIndex(false));
        template = new SaApiKeyTemplate();
        SaApiKeyManager.setSaApiKeyTemplate(template);
    }

    @AfterEach
    public void restore() {
        SaApiKeyManager.setConfig(backupConfig);
        SaApiKeyManager.setSaApiKeyDataLoader(backupLoader);
        SaApiKeyManager.setSaApiKeyTemplate(backupTemplate);
    }

    private ApiKeyModel buildAk(String apiKey, Object loginId, String... scopes) {
        ApiKeyModel ak = new ApiKeyModel()
                .setApiKey(apiKey).setLoginId(loginId).setCreateTime(System.currentTimeMillis())
                .setExpiresTime(SaTokenDao.NEVER_EXPIRE).setIsValid(true);
        for (String s : scopes) {
            ak.addScope(s);
        }
        return ak;
    }

    /** getApiKey/checkApiKey/saveApiKey/getLoginIdByApiKey/deleteApiKey 应该委托给全局 template */
    @Test
    public void crud_delegateToGlobalTemplate() {
        SaApiKeyUtil.saveApiKey(buildAk("AK-u1", 10001));
        Assertions.assertEquals("AK-u1", SaApiKeyUtil.getApiKey("AK-u1").getApiKey());
        Assertions.assertEquals("AK-u1", SaApiKeyUtil.checkApiKey("AK-u1").getApiKey());
        Assertions.assertEquals(10001, SaApiKeyUtil.getLoginIdByApiKey("AK-u1"));
        SaApiKeyUtil.deleteApiKey("AK-u1");
        Assertions.assertNull(SaApiKeyUtil.getApiKey("AK-u1"));
    }

    /** createApiKeyModel 应该委托给全局 template */
    @Test
    public void create_delegateToGlobalTemplate() {
        ApiKeyModel ak = SaApiKeyUtil.createApiKeyModel();
        Assertions.assertTrue(ak.getApiKey().startsWith("AK-"));
        ApiKeyModel ak2 = SaApiKeyUtil.createApiKeyModel(20001);
        Assertions.assertEquals(20001, ak2.getLoginId());
    }

    /** scope 应该委托给全局 template */
    @Test
    public void scope_delegateToGlobalTemplate() {
        SaApiKeyUtil.saveApiKey(buildAk("AK-u2", 1L, "read"));
        Assertions.assertTrue(SaApiKeyUtil.hasApiKeyScope("AK-u2", "read"));
        Assertions.assertDoesNotThrow(() -> SaApiKeyUtil.checkApiKeyScope("AK-u2", "read"));
        Assertions.assertTrue(SaApiKeyUtil.hasApiKeyScopeOr("AK-u2", "read", "write"));
        Assertions.assertDoesNotThrow(() -> SaApiKeyUtil.checkApiKeyScopeOr("AK-u2", "read", "write"));
    }

    /** isApiKeyLoginId/checkApiKeyLoginId 应该委托给全局 template */
    @Test
    public void loginId_delegateToGlobalTemplate() {
        SaApiKeyUtil.saveApiKey(buildAk("AK-u3", 10001));
        Assertions.assertTrue(SaApiKeyUtil.isApiKeyLoginId("AK-u3", 10001));
        Assertions.assertDoesNotThrow(() -> SaApiKeyUtil.checkApiKeyLoginId("AK-u3", 10001));
    }

    /** readApiKeyValue 应该委托给全局 template */
    @Test
    public void readApiKeyValue_delegateToGlobalTemplate() {
        Map<String, String> params = new LinkedHashMap<>();
        params.put("apikey", "AK-u4");
        SaTokenContextMockUtil.setMockContext();
        ((SaRequestForMock) cn.dev33.satoken.context.SaHolder.getRequest()).parameterMap = params;
        try {
            Assertions.assertEquals("AK-u4", SaApiKeyUtil.readApiKeyValue(cn.dev33.satoken.context.SaHolder.getRequest()));
        } finally {
            SaTokenContextMockUtil.clearContext();
        }
    }

    /** currentApiKey 应该委托给全局 template */
    @Test
    public void currentApiKey_delegateToGlobalTemplate() {
        SaApiKeyUtil.saveApiKey(buildAk("AK-u5", 10001));
        Map<String, String> params = new LinkedHashMap<>();
        params.put("apikey", "AK-u5");
        SaTokenContextMockUtil.setMockContext();
        ((SaRequestForMock) cn.dev33.satoken.context.SaHolder.getRequest()).parameterMap = params;
        try {
            Assertions.assertEquals("AK-u5", SaApiKeyUtil.currentApiKey().getApiKey());
        } finally {
            SaTokenContextMockUtil.clearContext();
        }
    }

    /** deleteApiKeyByLoginId 应该委托给全局 template（未开启索引时不抛异常） */
    @Test
    public void deleteByLoginId_delegateToGlobalTemplate() {
        Assertions.assertDoesNotThrow(() -> SaApiKeyUtil.deleteApiKeyByLoginId(99999));
    }

    /** getApiKeyList/adjustIndex 应该委托给全局 template（未开启索引时返回空） */
    @Test
    public void index_delegateToGlobalTemplate() {
        Assertions.assertTrue(SaApiKeyUtil.getApiKeyList(99999).isEmpty());
        Assertions.assertDoesNotThrow(() -> SaApiKeyUtil.adjustIndex(99999, null));
    }
}
