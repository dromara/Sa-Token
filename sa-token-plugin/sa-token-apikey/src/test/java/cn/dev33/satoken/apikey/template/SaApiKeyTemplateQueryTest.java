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
import cn.dev33.satoken.apikey.error.SaApiKeyErrorCode;
import cn.dev33.satoken.apikey.exception.ApiKeyException;
import cn.dev33.satoken.apikey.exception.ApiKeyScopeException;
import cn.dev33.satoken.apikey.loader.SaApiKeyDataLoader;
import cn.dev33.satoken.apikey.model.ApiKeyModel;
import cn.dev33.satoken.dao.SaTokenDao;
import cn.dev33.satoken.test.SaTokenTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * {@link SaApiKeyTemplate} 构造、查询、scope、loginId 校验测试
 *
 * @author click33
 * @since 1.46.0
 */
@SaTokenTest
public class SaApiKeyTemplateQueryTest {

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

    /** 造一个字段齐全的 ApiKeyModel */
    private ApiKeyModel buildAk(String apiKey, Object loginId, long expiresTime, boolean valid, String... scopes) {
        ApiKeyModel ak = new ApiKeyModel()
                .setApiKey(apiKey).setLoginId(loginId).setCreateTime(System.currentTimeMillis())
                .setExpiresTime(expiresTime).setIsValid(valid);
        for (String s : scopes) {
            ak.addScope(s);
        }
        return ak;
    }

    /** 默认构造应该使用 namespace=apikey */
    @Test
    public void constructor_defaultNamespace() {
        Assertions.assertEquals(SaApiKeyTemplate.DEFAULT_NAMESPACE, new SaApiKeyTemplate().namespace);
    }

    /** 带 namespace 的构造函数应该把 namespace 存起来 */
    @Test
    public void constructor_withNamespace() {
        Assertions.assertEquals("my-ns", new SaApiKeyTemplate("my-ns").namespace);
    }

    /** namespace 为空时必须抛 ApiKeyException */
    @Test
    public void constructor_emptyNamespace_throws() {
        Assertions.assertThrows(ApiKeyException.class, () -> new SaApiKeyTemplate(""));
        Assertions.assertThrows(ApiKeyException.class, () -> new SaApiKeyTemplate(null));
    }

    /** getApiKey：apiKey 为 null 时应该返回 null */
    @Test
    public void getApiKey_null_returnsNull() {
        Assertions.assertNull(new SaApiKeyTemplate().getApiKey(null));
    }

    /** getApiKey：缓存命中时应该直接返回 */
    @Test
    public void getApiKey_cacheHit() {
        SaApiKeyTemplate t = new SaApiKeyTemplate();
        t.saveApiKey(buildAk("AK-1", 10001, SaTokenDao.NEVER_EXPIRE, true));
        ApiKeyModel ak = t.getApiKey("AK-1");
        Assertions.assertNotNull(ak);
        Assertions.assertEquals("AK-1", ak.getApiKey());
    }

    /** getApiKey：缓存未命中时应该走 database 并回写缓存 */
    @Test
    public void getApiKey_cacheMiss_fallsBackToDatabase() {
        SaApiKeyTemplate t = new SaApiKeyTemplate();
        ApiKeyModel ak = buildAk("AK-db", 10002, SaTokenDao.NEVER_EXPIRE, true);
        SaApiKeyManager.setSaApiKeyDataLoader(new SaApiKeyDataLoader() {
            @Override
            public ApiKeyModel getApiKeyModelFromDatabase(String namespace, String apiKey) {
                return "AK-db".equals(apiKey) ? ak : null;
            }
        });
        Assertions.assertNull(t.getApiKeyModelFromCache("AK-db"));
        ApiKeyModel result = t.getApiKey("AK-db");
        Assertions.assertNotNull(result);
        Assertions.assertEquals("AK-db", result.getApiKey());
        Assertions.assertNotNull(t.getApiKeyModelFromCache("AK-db"));
    }

    /** checkApiKey：无效时必须抛 CODE_12301 */
    @Test
    public void checkApiKey_invalid_throws() {
        ApiKeyException ex = Assertions.assertThrows(ApiKeyException.class,
                () -> new SaApiKeyTemplate().checkApiKey("not-exist"));
        Assertions.assertEquals(SaApiKeyErrorCode.CODE_12301, ex.getCode());
        Assertions.assertEquals("not-exist", ex.getApiKey());
    }

    /** checkApiKey：已过期时必须抛 CODE_12302 */
    @Test
    public void checkApiKey_expired_throws() {
        SaApiKeyTemplate t = new SaApiKeyTemplate();
        // saveApiKey 过期的会删缓存，故直接用 dao 写入过期 ApiKey 绕过删除逻辑
        ApiKeyModel ak = buildAk("AK-exp", 1L, System.currentTimeMillis() - 1000, true);
        cn.dev33.satoken.SaManager.getSaTokenDao().setObject(t.splicingApiKeySaveKey("AK-exp"), ak, 60);
        Assertions.assertEquals(SaApiKeyErrorCode.CODE_12302,
                Assertions.assertThrows(ApiKeyException.class, () -> t.checkApiKey("AK-exp")).getCode());
    }

    /** checkApiKey：已禁用时必须抛 CODE_12303 */
    @Test
    public void checkApiKey_disabled_throws() {
        SaApiKeyTemplate t = new SaApiKeyTemplate();
        t.saveApiKey(buildAk("AK-dis", 1L, SaTokenDao.NEVER_EXPIRE, false));
        Assertions.assertEquals(SaApiKeyErrorCode.CODE_12303,
                Assertions.assertThrows(ApiKeyException.class, () -> t.checkApiKey("AK-dis")).getCode());
    }

    /** checkApiKey：有效时应该返回 ApiKeyModel */
    @Test
    public void checkApiKey_valid_returnsModel() {
        SaApiKeyTemplate t = new SaApiKeyTemplate();
        t.saveApiKey(buildAk("AK-ok", 10001, SaTokenDao.NEVER_EXPIRE, true, "read"));
        Assertions.assertEquals("AK-ok", t.checkApiKey("AK-ok").getApiKey());
    }

    /** getLoginIdByApiKey 应该返回 checkApiKey 拿到的 loginId */
    @Test
    public void getLoginIdByApiKey() {
        SaApiKeyTemplate t = new SaApiKeyTemplate();
        t.saveApiKey(buildAk("AK-lid", 20001, SaTokenDao.NEVER_EXPIRE, true));
        Assertions.assertEquals(20001, t.getLoginIdByApiKey("AK-lid"));
    }

    /** checkApiKeyScope(AND)：空 scopes 时应该直接通过 */
    @Test
    public void checkApiKeyScope_emptyScopes_passes() {
        SaApiKeyTemplate t = new SaApiKeyTemplate();
        t.saveApiKey(buildAk("AK-scope", 1L, SaTokenDao.NEVER_EXPIRE, true, "read"));
        Assertions.assertTrue(t.hasApiKeyScope("AK-scope"));
        Assertions.assertDoesNotThrow(() -> t.checkApiKeyScope("AK-scope"));
    }

    /** checkApiKeyScope(AND)：全部具备时应该通过，缺一个时必须抛 CODE_12311 */
    @Test
    public void checkApiKeyScope_andMode() {
        SaApiKeyTemplate t = new SaApiKeyTemplate();
        t.saveApiKey(buildAk("AK-and", 1L, SaTokenDao.NEVER_EXPIRE, true, "read", "write"));
        Assertions.assertTrue(t.hasApiKeyScope("AK-and", "read", "write"));
        Assertions.assertDoesNotThrow(() -> t.checkApiKeyScope("AK-and", "read", "write"));
        ApiKeyScopeException ex = Assertions.assertThrows(ApiKeyScopeException.class,
                () -> t.checkApiKeyScope("AK-and", "read", "admin"));
        Assertions.assertEquals(SaApiKeyErrorCode.CODE_12311, ex.getCode());
        Assertions.assertFalse(t.hasApiKeyScope("AK-and", "read", "admin"));
    }

    /** checkApiKeyScopeOr(OR)：具备其一时应该通过，都不具备时必须抛 CODE_12311 */
    @Test
    public void checkApiKeyScopeOr_orMode() {
        SaApiKeyTemplate t = new SaApiKeyTemplate();
        t.saveApiKey(buildAk("AK-or", 1L, SaTokenDao.NEVER_EXPIRE, true, "read"));
        Assertions.assertTrue(t.hasApiKeyScopeOr("AK-or", "read", "write"));
        Assertions.assertDoesNotThrow(() -> t.checkApiKeyScopeOr("AK-or", "read", "write"));
        Assertions.assertEquals(SaApiKeyErrorCode.CODE_12311,
                Assertions.assertThrows(ApiKeyScopeException.class,
                        () -> t.checkApiKeyScopeOr("AK-or", "write", "admin")).getCode());
        Assertions.assertFalse(t.hasApiKeyScopeOr("AK-or", "write", "admin"));
    }

    /** checkApiKeyScopeOr(OR)：空 scopes 时应该直接通过 */
    @Test
    public void checkApiKeyScopeOr_emptyScopes_passes() {
        SaApiKeyTemplate t = new SaApiKeyTemplate();
        t.saveApiKey(buildAk("AK-or2", 1L, SaTokenDao.NEVER_EXPIRE, true, "read"));
        Assertions.assertDoesNotThrow(() -> t.checkApiKeyScopeOr("AK-or2"));
    }

    /** checkApiKeyLoginId：无效 ApiKey 时必须抛 CODE_12301 */
    @Test
    public void checkApiKeyLoginId_invalid_throws() {
        SaApiKeyTemplate t = new SaApiKeyTemplate();
        Assertions.assertFalse(t.isApiKeyLoginId("not-exist", 1L));
        Assertions.assertEquals(SaApiKeyErrorCode.CODE_12301,
                Assertions.assertThrows(ApiKeyException.class, () -> t.checkApiKeyLoginId("not-exist", 1L)).getCode());
    }

    /** checkApiKeyLoginId：不属于指定用户时必须抛 CODE_12312 */
    @Test
    public void checkApiKeyLoginId_notOwner_throws() {
        SaApiKeyTemplate t = new SaApiKeyTemplate();
        t.saveApiKey(buildAk("AK-owner", 10001, SaTokenDao.NEVER_EXPIRE, true));
        Assertions.assertFalse(t.isApiKeyLoginId("AK-owner", 10002));
        Assertions.assertEquals(SaApiKeyErrorCode.CODE_12312,
                Assertions.assertThrows(ApiKeyException.class, () -> t.checkApiKeyLoginId("AK-owner", 10002)).getCode());
    }

    /** checkApiKeyLoginId：属于指定用户时应该通过 */
    @Test
    public void checkApiKeyLoginId_owner_passes() {
        SaApiKeyTemplate t = new SaApiKeyTemplate();
        t.saveApiKey(buildAk("AK-owner2", 10001, SaTokenDao.NEVER_EXPIRE, true));
        Assertions.assertTrue(t.isApiKeyLoginId("AK-owner2", 10001));
        Assertions.assertDoesNotThrow(() -> t.checkApiKeyLoginId("AK-owner2", 10001));
    }

    /** splicingApiKeySaveKey 应该拼成 {tokenName}:{namespace}:{apiKey} 这样的格式 */
    @Test
    public void splicingApiKeySaveKey_format() {
        Assertions.assertEquals("satoken:apikey:AK-1", new SaApiKeyTemplate().splicingApiKeySaveKey("AK-1"));
    }
}
