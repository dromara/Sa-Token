/*
 * Copyright 2020-2099 sa-token.com
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
package cn.dev33.satoken.apikey.model;

import cn.dev33.satoken.apikey.error.SaApiKeyErrorCode;
import cn.dev33.satoken.apikey.exception.ApiKeyException;
import cn.dev33.satoken.dao.SaTokenDao;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * {@link ApiKeyModel} 字段读写、扩展数据操作、自检与有效期判断测试
 *
 * @author click33
 * @since 1.46.0
 */
public class ApiKeyModelTest {

    /** 构造函数默认应该把 createTime 设成当前时间，isValid 设成 true */
    @Test
    public void constructor_defaults() {
        ApiKeyModel ak = new ApiKeyModel();
        Assertions.assertTrue(ak.getCreateTime() > 0);
        Assertions.assertTrue(ak.getIsValid());
        Assertions.assertTrue(ak.getScopes().isEmpty());
    }

    /** getter/setter 链式调用读写应该一致 */
    @Test
    public void getterSetter_chainReadWrite() {
        ApiKeyModel ak = new ApiKeyModel()
                .setTitle("t").setIntro("i").setApiKey("AK-1")
                .setLoginId(10001).setCreateTime(1000L).setExpiresTime(2000L).setIsValid(false);
        Assertions.assertEquals("t", ak.getTitle());
        Assertions.assertEquals("i", ak.getIntro());
        Assertions.assertEquals("AK-1", ak.getApiKey());
        Assertions.assertEquals(10001, ak.getLoginId());
        Assertions.assertEquals(1000L, ak.getCreateTime());
        Assertions.assertEquals(2000L, ak.getExpiresTime());
        Assertions.assertFalse(ak.getIsValid());

        List<String> scopes = Arrays.asList("a", "b");
        ak.setScopes(scopes);
        Assertions.assertSame(scopes, ak.getScopes());

        java.util.Map<String, Object> extra = new LinkedHashMap<>();
        ak.setExtraData(extra);
        Assertions.assertSame(extra, ak.getExtraData());
    }

    /** addScope 应该追加 scope，列表是 null 时应该自动初始化 */
    @Test
    public void addScope_appendsAndInitsNullList() {
        ApiKeyModel ak = new ApiKeyModel();
        ak.setScopes(null);
        ak.addScope("a", "b");
        Assertions.assertEquals(Arrays.asList("a", "b"), ak.getScopes());

        ak.addScope("c");
        Assertions.assertEquals(Arrays.asList("a", "b", "c"), ak.getScopes());
    }

    /** addExtra/getExtra/removeExtra 遇到 null extraData 时应该安全不抛异常 */
    @Test
    public void extraData_nullSafe() {
        ApiKeyModel ak = new ApiKeyModel();
        Assertions.assertNull(ak.getExtra("k"));
        Assertions.assertNull(ak.removeExtra("k"));

        ak.addExtra("k1", "v1");
        Assertions.assertEquals("v1", ak.getExtra("k1"));
        Assertions.assertEquals("v1", ak.removeExtra("k1"));
        Assertions.assertNull(ak.getExtra("k1"));
    }

    /** checkByCanSaved 时 apiKey 为空必须抛 CODE_12304 */
    @Test
    public void checkByCanSaved_emptyApiKey_throws() {
        ApiKeyModel ak = new ApiKeyModel().setLoginId(1L).setCreateTime(1L).setExpiresTime(2L).setIsValid(true);
        ApiKeyException ex = Assertions.assertThrows(ApiKeyException.class, ak::checkByCanSaved);
        Assertions.assertEquals(SaApiKeyErrorCode.CODE_12304, ex.getCode());
    }

    /** checkByCanSaved 时 loginId 为 null 必须抛 CODE_12304 */
    @Test
    public void checkByCanSaved_nullLoginId_throws() {
        ApiKeyModel ak = new ApiKeyModel().setApiKey("AK").setCreateTime(1L).setExpiresTime(2L).setIsValid(true);
        Assertions.assertThrows(ApiKeyException.class, ak::checkByCanSaved);
    }

    /** checkByCanSaved 时 createTime 为 0 必须抛 CODE_12304 */
    @Test
    public void checkByCanSaved_zeroCreateTime_throws() {
        ApiKeyModel ak = new ApiKeyModel().setApiKey("AK").setLoginId(1L).setExpiresTime(2L).setIsValid(true);
        ak.setCreateTime(0L);
        Assertions.assertThrows(ApiKeyException.class, ak::checkByCanSaved);
    }

    /** checkByCanSaved 时 expiresTime 为 0 必须抛 CODE_12304 */
    @Test
    public void checkByCanSaved_zeroExpiresTime_throws() {
        ApiKeyModel ak = new ApiKeyModel().setApiKey("AK").setLoginId(1L).setCreateTime(1L).setIsValid(true);
        ak.setExpiresTime(0L);
        Assertions.assertThrows(ApiKeyException.class, ak::checkByCanSaved);
    }

    /** checkByCanSaved 时 isValid 为 null 必须抛 CODE_12304 */
    @Test
    public void checkByCanSaved_nullIsValid_throws() {
        ApiKeyModel ak = new ApiKeyModel().setApiKey("AK").setLoginId(1L).setCreateTime(1L).setExpiresTime(2L);
        ak.setIsValid(null);
        Assertions.assertThrows(ApiKeyException.class, ak::checkByCanSaved);
    }

    /** checkByCanSaved 时字段齐全应该不抛异常 */
    @Test
    public void checkByCanSaved_allFieldsValid_noThrow() {
        ApiKeyModel ak = new ApiKeyModel().setApiKey("AK").setLoginId(1L).setCreateTime(1L).setExpiresTime(2L).setIsValid(true);
        Assertions.assertDoesNotThrow(ak::checkByCanSaved);
    }

    /** expiresIn 永不过期时应该返回 NEVER_EXPIRE */
    @Test
    public void expiresIn_neverExpire() {
        ApiKeyModel ak = new ApiKeyModel().setExpiresTime(SaTokenDao.NEVER_EXPIRE);
        Assertions.assertEquals(SaTokenDao.NEVER_EXPIRE, ak.expiresIn());
    }

    /** expiresIn 未来到期时应该返回正秒数，已过期时应该返回 -2 */
    @Test
    public void expiresIn_futureAndExpired() {
        ApiKeyModel ak = new ApiKeyModel().setExpiresTime(System.currentTimeMillis() + 100_000L);
        Assertions.assertTrue(ak.expiresIn() > 0);

        ak.setExpiresTime(System.currentTimeMillis() - 1000L);
        Assertions.assertEquals(-2, ak.expiresIn());
    }

    /** timeExpired 永不过期时应该返回 false，过去时间应该返回 true，未来时间应该返回 false */
    @Test
    public void timeExpired_branches() {
        ApiKeyModel ak = new ApiKeyModel().setExpiresTime(SaTokenDao.NEVER_EXPIRE);
        Assertions.assertFalse(ak.timeExpired());

        ak.setExpiresTime(System.currentTimeMillis() - 1L);
        Assertions.assertTrue(ak.timeExpired());

        ak.setExpiresTime(System.currentTimeMillis() + 100_000L);
        Assertions.assertFalse(ak.timeExpired());
    }

    /** toString 应该包含关键字段 */
    @Test
    public void toString_containsKeyFields() {
        String str = new ApiKeyModel().setApiKey("AK-1").setLoginId(1L).toString();
        Assertions.assertTrue(str.contains("AK-1"));
        Assertions.assertTrue(str.contains("loginId=1"));
    }
}
