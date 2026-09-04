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
import cn.dev33.satoken.dao.SaTokenDao;
import cn.dev33.satoken.test.SaTokenTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * {@link SaApiKeyTemplate} 保存、删除、创建测试（关闭索引记录）
 *
 * @author click33
 * @since 1.46.0
 */
@SaTokenTest
public class SaApiKeyTemplateWriteTest {

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

    private ApiKeyModel buildAk(String apiKey, Object loginId, long expiresTime, boolean valid) {
        return new ApiKeyModel()
                .setApiKey(apiKey).setLoginId(loginId).setCreateTime(System.currentTimeMillis())
                .setExpiresTime(expiresTime).setIsValid(valid);
    }

    /** saveApiKey：传 null 时应该直接返回，不抛异常 */
    @Test
    public void saveApiKey_null_noThrow() {
        Assertions.assertDoesNotThrow(() -> new SaApiKeyTemplate().saveApiKey(null));
    }

    /** saveApiKey：已过期的 ApiKey 应该被删缓存而不是保存 */
    @Test
    public void saveApiKey_expired_deletesCache() {
        SaApiKeyTemplate t = new SaApiKeyTemplate();
        t.saveApiKey(buildAk("AK-del", 1L, SaTokenDao.NEVER_EXPIRE, true));
        Assertions.assertNotNull(t.getApiKeyModelFromCache("AK-del"));
        t.saveApiKey(buildAk("AK-del", 1L, System.currentTimeMillis() - 1000, true));
        Assertions.assertNull(t.getApiKeyModelFromCache("AK-del"));
    }

    /** saveApiKey：有效 ApiKey 应该写入缓存 */
    @Test
    public void saveApiKey_valid_writesCache() {
        SaApiKeyTemplate t = new SaApiKeyTemplate();
        t.saveApiKey(buildAk("AK-save", 1L, SaTokenDao.NEVER_EXPIRE, true));
        Assertions.assertNotNull(t.getApiKeyModelFromCache("AK-save"));
    }

    /** deleteApiKey：缓存无记录时应该直接返回 */
    @Test
    public void deleteApiKey_noCache_returns() {
        Assertions.assertDoesNotThrow(() -> new SaApiKeyTemplate().deleteApiKey("not-exist"));
    }

    /** deleteApiKey：缓存有记录时应该删除缓存 */
    @Test
    public void deleteApiKey_removesCache() {
        SaApiKeyTemplate t = new SaApiKeyTemplate();
        t.saveApiKey(buildAk("AK-rm", 1L, SaTokenDao.NEVER_EXPIRE, true));
        Assertions.assertNotNull(t.getApiKeyModelFromCache("AK-rm"));
        t.deleteApiKey("AK-rm");
        Assertions.assertNull(t.getApiKeyModelFromCache("AK-rm"));
    }

    /** createApiKeyModel 应该生成带前缀的 ApiKey，且不与已有的冲突 */
    @Test
    public void createApiKeyModel_generatesUnique() {
        SaApiKeyTemplate t = new SaApiKeyTemplate();
        ApiKeyModel ak = t.createApiKeyModel();
        Assertions.assertTrue(ak.getApiKey().startsWith("AK-"));
        Assertions.assertNull(t.getApiKey(ak.getApiKey()));
    }

    /** createApiKeyModel(loginId) 应该设置 loginId、isValid、expiresTime */
    @Test
    public void createApiKeyModel_withLoginId() {
        SaApiKeyManager.setConfig(new SaApiKeyConfig().setPrefix("BK-").setTimeout(100L).setIsRecordIndex(false));
        ApiKeyModel ak = new SaApiKeyTemplate().createApiKeyModel(30001);
        Assertions.assertTrue(ak.getApiKey().startsWith("BK-"));
        Assertions.assertEquals(30001, ak.getLoginId());
        Assertions.assertTrue(ak.getIsValid());
        Assertions.assertTrue(ak.getExpiresTime() > System.currentTimeMillis());
    }

    /** createApiKeyModel(loginId) timeout=-1 时 expiresTime 应该是 NEVER_EXPIRE */
    @Test
    public void createApiKeyModel_neverExpire() {
        SaApiKeyManager.setConfig(new SaApiKeyConfig().setTimeout(SaTokenDao.NEVER_EXPIRE).setIsRecordIndex(false));
        Assertions.assertEquals(SaTokenDao.NEVER_EXPIRE,
                new SaApiKeyTemplate().createApiKeyModel(1L).getExpiresTime());
    }

    /** randomApiKeyValue 应该带前缀，且长度是 prefix+36 */
    @Test
    public void randomApiKeyValue() {
        SaApiKeyManager.setConfig(new SaApiKeyConfig().setPrefix("X-").setIsRecordIndex(false));
        String v = new SaApiKeyTemplate().randomApiKeyValue();
        Assertions.assertTrue(v.startsWith("X-"));
        Assertions.assertEquals(2 + 36, v.length());
    }
}
