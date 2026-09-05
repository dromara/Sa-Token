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
package cn.dev33.satoken.temp.jwt;

import cn.dev33.satoken.SaManager;
import cn.dev33.satoken.dao.SaTokenDao;
import cn.dev33.satoken.exception.ApiDisabledException;
import cn.dev33.satoken.exception.SaTokenException;
import cn.dev33.satoken.test.SaTokenTest;
import cn.dev33.satoken.temp.jwt.error.SaTempJwtErrorCode;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * {@link SaTempTemplateForJwt} 模板方法测试
 *
 * @author click33
 * @since 1.46.0
 */
@SaTokenTest
public class SaTempTemplateForJwtTest {

    /** 测试秘钥 */
    public static final String JWT_SECRET_KEY = "SaTempJwtTest-Secret-Key-0123456789";

    private SaTempTemplateForJwt tempTemplate;

    @BeforeEach
    public void beforeEach() {
        // @SaTokenTest 已把全局状态复位为默认配置，这里补上 jwt 秘钥
        SaManager.getConfig().setJwtSecretKey(JWT_SECRET_KEY);
        tempTemplate = new SaTempTemplateForJwt();
    }

    /** createToken 生成的 token 交给 parseToken 应该能还原原值 */
    @Test
    public void createToken_parseToken_roundtrip() {
        String token = tempTemplate.createToken("group-1014", 200);
        Assertions.assertEquals("group-1014", tempTemplate.parseToken(token));
    }

    /** parseToken 指定类型时应该能转换出对应类型 */
    @Test
    public void parseToken_typeConvert() {
        String token = tempTemplate.createToken(10001, 200);
        Assertions.assertEquals(10001, tempTemplate.parseToken(token, Integer.class));
    }

    /** parseToken 携带裁剪前缀时应该能取出前缀后的值 */
    @Test
    public void parseToken_cutPrefix() {
        String token = tempTemplate.createToken("group-1014", 200);
        Assertions.assertEquals(1014L, (long) tempTemplate.parseToken(token, "group-", Long.class));
    }

    /** 值不符合裁剪前缀时 parseToken 应该返回 null */
    @Test
    public void parseToken_cutPrefixMismatch_returnNull() {
        String token = tempTemplate.createToken("user-1014", 200);
        Assertions.assertNull(tempTemplate.parseToken(token, "group-", Long.class));
    }

    /** getTimeout 应该返回剩余有效期（允许几秒误差） */
    @Test
    public void getTimeout_validToken() {
        String token = tempTemplate.createToken("group-1014", 200);
        long timeout = tempTemplate.getTimeout(token);
        Assertions.assertTrue(timeout > 195 && timeout <= 200);
    }

    /** timeout=-1 时应该代表永不过期，且能正常解析、getTimeout 返回 NEVER_EXPIRE */
    @Test
    public void createToken_neverExpire() {
        String token = tempTemplate.createToken("group-1014", SaTokenDao.NEVER_EXPIRE);
        Assertions.assertEquals("group-1014", tempTemplate.parseToken(token));
        Assertions.assertEquals(SaTokenDao.NEVER_EXPIRE, tempTemplate.getTimeout(token));
    }

    /** isRecordIndex=true 时 jwt 模式也应该不往 Dao 写任何索引记录 */
    @Test
    public void createToken_isRecordIndex_ignored() {
        String token = tempTemplate.createToken("group-1014", 200, true);
        Assertions.assertNull(SaManager.getSaTokenDao().getObject("satoken:temp-token:" + token));
    }

    /** jwt 模式调用 deleteToken 时必须抛出 ApiDisabledException，且错误码为 30302 */
    @Test
    public void deleteToken_disabled() {
        String token = tempTemplate.createToken("group-1014", 200);
        ApiDisabledException e = Assertions.assertThrows(ApiDisabledException.class,
                () -> tempTemplate.deleteToken(token));
        Assertions.assertEquals(SaTempJwtErrorCode.CODE_30302, e.getCode());
    }

    /** jwt 模式调用 getTempTokenList 时必须抛出 ApiDisabledException，且错误码为 30304 */
    @Test
    public void getTempTokenList_disabled() {
        ApiDisabledException e = Assertions.assertThrows(ApiDisabledException.class,
                () -> tempTemplate.getTempTokenList("group-1014"));
        Assertions.assertEquals(SaTempJwtErrorCode.CODE_30304, e.getCode());
    }

    /** 正确配置秘钥后 getJwtSecretKey 应该返回配置的值 */
    @Test
    public void getJwtSecretKey_configured() {
        Assertions.assertEquals(JWT_SECRET_KEY, tempTemplate.getJwtSecretKey());
    }

    /** 秘钥未配置（null 或空串）时必须抛出 SaTokenException，且错误码为 30301 */
    @Test
    public void getJwtSecretKey_empty() {
        SaManager.getConfig().setJwtSecretKey(null);
        SaTokenException e1 = Assertions.assertThrows(SaTokenException.class, () -> tempTemplate.getJwtSecretKey());
        Assertions.assertEquals(SaTempJwtErrorCode.CODE_30301, e1.getCode());

        SaManager.getConfig().setJwtSecretKey("");
        SaTokenException e2 = Assertions.assertThrows(SaTokenException.class, () -> tempTemplate.getJwtSecretKey());
        Assertions.assertEquals(SaTempJwtErrorCode.CODE_30301, e2.getCode());
    }

}
