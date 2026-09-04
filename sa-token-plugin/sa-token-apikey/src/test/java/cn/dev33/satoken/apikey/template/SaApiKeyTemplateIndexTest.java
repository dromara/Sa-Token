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

import java.util.List;

/**
 * {@link SaApiKeyTemplate} 索引相关方法测试（开启索引记录）
 *
 * @author click33
 * @since 1.46.0
 */
@SaTokenTest
public class SaApiKeyTemplateIndexTest {

    private SaApiKeyConfig backupConfig;
    private SaApiKeyDataLoader backupLoader;
    private SaApiKeyTemplate backupTemplate;

    @BeforeEach
    public void backup() {
        backupConfig = SaApiKeyManager.getConfig();
        backupLoader = SaApiKeyManager.getSaApiKeyDataLoader();
        backupTemplate = SaApiKeyManager.getSaApiKeyTemplate();
        // 开启索引记录
        SaApiKeyManager.setConfig(new SaApiKeyConfig().setIsRecordIndex(true));
    }

    @AfterEach
    public void restore() {
        SaApiKeyManager.setConfig(backupConfig);
        SaApiKeyManager.setSaApiKeyDataLoader(backupLoader);
        SaApiKeyManager.setSaApiKeyTemplate(backupTemplate);
    }

    private ApiKeyModel buildAk(String apiKey, Object loginId, long expiresTime) {
        return new ApiKeyModel()
                .setApiKey(apiKey).setLoginId(loginId).setCreateTime(System.currentTimeMillis())
                .setExpiresTime(expiresTime).setIsValid(true);
    }

    /** saveApiKey 开启索引时应该记录到 RawSession */
    @Test
    public void saveApiKey_recordsIndex() {
        SaApiKeyTemplate t = new SaApiKeyTemplate();
        t.saveApiKey(buildAk("AK-idx1", 10001, SaTokenDao.NEVER_EXPIRE));
        List<ApiKeyModel> list = t.getApiKeyList(10001);
        Assertions.assertEquals(1, list.size());
        Assertions.assertEquals("AK-idx1", list.get(0).getApiKey());
    }

    /** getApiKeyList：未开启索引时应该返回空列表 */
    @Test
    public void getApiKeyList_indexDisabled_returnsEmpty() {
        SaApiKeyManager.setConfig(new SaApiKeyConfig().setIsRecordIndex(false));
        SaApiKeyTemplate t = new SaApiKeyTemplate();
        Assertions.assertTrue(t.getApiKeyList(10001).isEmpty());
    }

    /** getApiKeyList：session 不存在时应该返回空列表 */
    @Test
    public void getApiKeyList_noSession_returnsEmpty() {
        Assertions.assertTrue(new SaApiKeyTemplate().getApiKeyList(99999).isEmpty());
    }

    /** getApiKeyList：应该过滤掉已过期的 ApiKey */
    @Test
    public void getApiKeyList_filtersExpired() {
        SaApiKeyTemplate t = new SaApiKeyTemplate();
        t.saveApiKey(buildAk("AK-valid", 10002, SaTokenDao.NEVER_EXPIRE));
        t.saveApiKey(buildAk("AK-expired", 10002, System.currentTimeMillis() - 1000));
        List<ApiKeyModel> list = t.getApiKeyList(10002);
        Assertions.assertEquals(1, list.size());
        Assertions.assertEquals("AK-valid", list.get(0).getApiKey());
    }

    /** deleteApiKeyByLoginId：未开启索引时应该打 warn 并返回 */
    @Test
    public void deleteApiKeyByLoginId_indexDisabled_returns() {
        SaApiKeyManager.setConfig(new SaApiKeyConfig().setIsRecordIndex(false));
        SaApiKeyTemplate t = new SaApiKeyTemplate();
        Assertions.assertDoesNotThrow(() -> t.deleteApiKeyByLoginId(10001));
    }

    /** deleteApiKeyByLoginId：session 不存在时应该直接返回 */
    @Test
    public void deleteApiKeyByLoginId_noSession_returns() {
        Assertions.assertDoesNotThrow(() -> new SaApiKeyTemplate().deleteApiKeyByLoginId(99999));
    }

    /** deleteApiKeyByLoginId：应该删除指定 loginId 的所有 ApiKey 及索引 */
    @Test
    public void deleteApiKeyByLoginId_removesAll() {
        SaApiKeyTemplate t = new SaApiKeyTemplate();
        t.saveApiKey(buildAk("AK-b1", 10003, SaTokenDao.NEVER_EXPIRE));
        t.saveApiKey(buildAk("AK-b2", 10003, SaTokenDao.NEVER_EXPIRE));
        Assertions.assertEquals(2, t.getApiKeyList(10003).size());
        t.deleteApiKeyByLoginId(10003);
        Assertions.assertTrue(t.getApiKeyList(10003).isEmpty());
        Assertions.assertNull(t.getApiKeyModelFromCache("AK-b1"));
        Assertions.assertNull(t.getApiKeyModelFromCache("AK-b2"));
    }

    /** deleteApiKey：开启索引且仅剩一个时应该删除整个 RawSession */
    @Test
    public void deleteApiKey_singleRemovesSession() {
        SaApiKeyTemplate t = new SaApiKeyTemplate();
        t.saveApiKey(buildAk("AK-single", 10004, SaTokenDao.NEVER_EXPIRE));
        Assertions.assertEquals(1, t.getApiKeyList(10004).size());
        t.deleteApiKey("AK-single");
        Assertions.assertTrue(t.getApiKeyList(10004).isEmpty());
    }

    /** deleteApiKey：开启索引且有多个时应该仅移除该 ApiKey */
    @Test
    public void deleteApiKey_multipleRemovesOnlyOne() {
        SaApiKeyTemplate t = new SaApiKeyTemplate();
        t.saveApiKey(buildAk("AK-m1", 10005, SaTokenDao.NEVER_EXPIRE));
        t.saveApiKey(buildAk("AK-m2", 10005, SaTokenDao.NEVER_EXPIRE));
        t.deleteApiKey("AK-m1");
        List<ApiKeyModel> list = t.getApiKeyList(10005);
        Assertions.assertEquals(1, list.size());
        Assertions.assertEquals("AK-m2", list.get(0).getApiKey());
    }

    /** adjustIndex：未开启索引时应该打 warn 并返回 */
    @Test
    public void adjustIndex_indexDisabled_returns() {
        SaApiKeyManager.setConfig(new SaApiKeyConfig().setIsRecordIndex(false));
        SaApiKeyTemplate t = new SaApiKeyTemplate();
        Assertions.assertDoesNotThrow(() -> t.adjustIndex(10001, null));
    }

    /** adjustIndex：session 不存在时应该直接返回 */
    @Test
    public void adjustIndex_noSession_returns() {
        Assertions.assertDoesNotThrow(() -> new SaApiKeyTemplate().adjustIndex(99999, null));
    }

    /** adjustIndex：清空过期 ApiKey 后应该删除空 session */
    @Test
    public void adjustIndex_allExpired_deletesSession() {
        SaApiKeyTemplate t = new SaApiKeyTemplate();
        // saveApiKey 过期的会先删缓存再触发 adjustIndex 清理索引，所以保存后索引即空
        t.saveApiKey(buildAk("AK-adj-exp", 10006, System.currentTimeMillis() - 1000));
        Assertions.assertTrue(t.getApiKeyList(10006).isEmpty());
        // 再次手动 adjustIndex 不抛异常
        Assertions.assertDoesNotThrow(() -> t.adjustIndex(10006, null));
    }

    /** adjustIndex：多个有效 ApiKey（含永不过期）时应该覆盖 TTL 计算分支 */
    @Test
    public void adjustIndex_multipleValid_adjustsTtl() {
        SaApiKeyTemplate t = new SaApiKeyTemplate();
        t.saveApiKey(buildAk("AK-ttl1", 10007, SaTokenDao.NEVER_EXPIRE));
        t.saveApiKey(buildAk("AK-ttl2", 10007, System.currentTimeMillis() + 200_000L));
        Assertions.assertEquals(2, t.getApiKeyList(10007).size());
        // 手动 adjustIndex 覆盖 TTL 比较分支
        Assertions.assertDoesNotThrow(() -> t.adjustIndex(10007, null));
        Assertions.assertEquals(2, t.getApiKeyList(10007).size());
    }

    /** deleteApiKey：开启索引但 session 不存在时应该直接返回 */
    @Test
    public void deleteApiKey_indexOn_sessionNull_returns() {
        SaApiKeyTemplate t = new SaApiKeyTemplate();
        // 先保存创建索引和缓存，再手动删 session，再 deleteApiKey 走 session==null 分支
        t.saveApiKey(buildAk("AK-sn", 10008, SaTokenDao.NEVER_EXPIRE));
        t.rawSessionDelegator.deleteSessionById(10008);
        Assertions.assertDoesNotThrow(() -> t.deleteApiKey("AK-sn"));
    }

    /** deleteApiKey：开启索引但 apiKeyList 不包含该 apiKey 时应该直接返回 */
    @Test
    public void deleteApiKey_indexOn_notInList_returns() {
        SaApiKeyTemplate t = new SaApiKeyTemplate();
        // 保存一个 ApiKey 创建 session，再保存另一个不同 loginId 的，然后删第一个但手动清空索引
        t.saveApiKey(buildAk("AK-ni1", 10009, SaTokenDao.NEVER_EXPIRE));
        // 手动从 session 移除索引中的 apiKey
        cn.dev33.satoken.session.SaSession session = t.rawSessionDelegator.getSessionById(10009, false);
        session.set(SaApiKeyTemplate.API_KEY_LIST, new java.util.ArrayList<>());
        Assertions.assertDoesNotThrow(() -> t.deleteApiKey("AK-ni1"));
    }

    /** saveApiKey：重复保存同一 apiKey 时不应该重复添加索引 */
    @Test
    public void saveApiKey_duplicate_notAddedAgain() {
        SaApiKeyTemplate t = new SaApiKeyTemplate();
        t.saveApiKey(buildAk("AK-dup", 10010, SaTokenDao.NEVER_EXPIRE));
        t.saveApiKey(buildAk("AK-dup", 10010, SaTokenDao.NEVER_EXPIRE));
        Assertions.assertEquals(1, t.getApiKeyList(10010).size());
    }

    /** getApiKeyList：索引中含缓存不存在的 apiKey 时应该被过滤掉 */
    @Test
    public void getApiKeyList_filtersNullCache() {
        SaApiKeyTemplate t = new SaApiKeyTemplate();
        t.saveApiKey(buildAk("AK-valid2", 10011, SaTokenDao.NEVER_EXPIRE));
        // 手动往索引加一个缓存中不存在的 apiKey
        cn.dev33.satoken.session.SaSession session = t.rawSessionDelegator.getSessionById(10011, false);
        java.util.List<String> list = session.getList(SaApiKeyTemplate.API_KEY_LIST, String.class, java.util.ArrayList::new);
        list.add("AK-not-in-cache");
        session.set(SaApiKeyTemplate.API_KEY_LIST, list);
        // 过滤掉缓存不存在的
        Assertions.assertEquals(1, t.getApiKeyList(10011).size());
    }
}
