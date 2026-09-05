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
package cn.dev33.satoken.jwt;

import cn.dev33.satoken.dao.SaTokenDao;
import cn.dev33.satoken.jwt.error.SaJwtErrorCode;
import cn.dev33.satoken.jwt.exception.SaJwtException;
import cn.hutool.json.JSONObject;
import cn.hutool.jwt.JWT;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * {@link SaJwtTemplate} 核心方法测试（创建、解析、超时判断）
 *
 * @author click33
 * @since 1.46.0
 */
public class SaJwtTemplateTest {

    /** 测试秘钥 */
    public static final String KEY = "SaJwtTemplateTest-Secret-Key-0123456789";

    /** 登录类型 */
    public static final String LOGIN_TYPE = "jwt-template-test";

    private SaJwtTemplate template;

    @BeforeEach
    public void beforeEach() {
        template = new SaJwtTemplate();
    }

    /** 按指定载荷造一个 token（测试里用于构造过期、缺字段等特殊场景） */
    private String buildToken(Map<String, Object> payloads, String keyt) {
        return template.generateToken(JWT.create().addPayloads(payloads), keyt);
    }

    /** 造一个已过期的 token */
    private String buildExpiredToken(String keyt) {
        Map<String, Object> payloads = new HashMap<>();
        payloads.put(SaJwtTemplate.LOGIN_TYPE, LOGIN_TYPE);
        payloads.put(SaJwtTemplate.LOGIN_ID, 10001);
        payloads.put(SaJwtTemplate.EFF, System.currentTimeMillis() - 60 * 1000);
        return buildToken(payloads, keyt);
    }

    // ------ 创建

    /** 简单方式创建的 token 应该能解析出 loginType、loginId，且带有随机字符串 */
    @Test
    public void createToken_simple_roundtrip() {
        String token = template.createToken(LOGIN_TYPE, 10001, null, KEY);

        JSONObject payloads = template.parseToken(token, LOGIN_TYPE, KEY, false).getPayloads();
        Assertions.assertEquals(LOGIN_TYPE, payloads.getStr(SaJwtTemplate.LOGIN_TYPE));
        Assertions.assertEquals(10001, payloads.getInt(SaJwtTemplate.LOGIN_ID));
        Assertions.assertEquals(32, payloads.getStr(SaJwtTemplate.RN_STR).length());
    }

    /** 同参数两次创建的 token 应该不相同（随机字符串生效） */
    @Test
    public void createToken_random() {
        String token1 = template.createToken(LOGIN_TYPE, 10001, null, KEY);
        String token2 = template.createToken(LOGIN_TYPE, 10001, null, KEY);
        Assertions.assertNotEquals(token1, token2);
    }

    /** 全参数方式创建的 token 应该携带 deviceType、eff 和 extraData 数据 */
    @Test
    public void createToken_full_roundtrip() {
        Map<String, Object> extraData = new HashMap<>();
        extraData.put("company", "zhang-company");

        String token = template.createToken(LOGIN_TYPE, 10001, "pc", 200, extraData, KEY);

        JSONObject payloads = template.parseToken(token, LOGIN_TYPE, KEY, false).getPayloads();
        Assertions.assertEquals("pc", payloads.getStr(SaJwtTemplate.DEVICE_TYPE));
        Assertions.assertEquals("zhang-company", payloads.getStr("company"));
        // eff 应该是 13 位毫秒时间戳，约等于当前时间 + 200 秒
        Long eff = payloads.getLong(SaJwtTemplate.EFF);
        Assertions.assertTrue(eff > System.currentTimeMillis() + 195 * 1000);
        Assertions.assertTrue(eff <= System.currentTimeMillis() + 200 * 1000);
    }

    /** 全参数方式 timeout=-1 时 eff 应该被记为 -1（永不过期） */
    @Test
    public void createToken_full_neverExpire() {
        String token = template.createToken(LOGIN_TYPE, 10001, "pc", SaJwtTemplate.NEVER_EXPIRE, null, KEY);

        JSONObject payloads = template.parseToken(token, LOGIN_TYPE, KEY, false).getPayloads();
        Assertions.assertEquals(SaJwtTemplate.NEVER_EXPIRE, payloads.getLong(SaJwtTemplate.EFF));
    }

    /** extraData 包含保留字段（loginId / eff）时必须抛出 SaJwtException，且错误码为 30207 */
    @Test
    public void createToken_extraDataReservedKey_throw() {
        Map<String, Object> extraData1 = new HashMap<>();
        extraData1.put(SaJwtTemplate.LOGIN_ID, "hack");
        SaJwtException e1 = Assertions.assertThrows(SaJwtException.class,
                () -> template.createToken(LOGIN_TYPE, 10001, extraData1, KEY));
        Assertions.assertEquals(SaJwtErrorCode.CODE_30207, e1.getCode());

        Map<String, Object> extraData2 = new HashMap<>();
        extraData2.put(SaJwtTemplate.EFF, 1L);
        SaJwtException e2 = Assertions.assertThrows(SaJwtException.class,
                () -> template.createToken(LOGIN_TYPE, 10001, "pc", 200, extraData2, KEY));
        Assertions.assertEquals(SaJwtErrorCode.CODE_30207, e2.getCode());
    }

    /** extraData 为 null 或空 Map 时应该正常创建 token */
    @Test
    public void createToken_extraDataNullAndEmpty_pass() {
        Assertions.assertNotNull(template.createToken(LOGIN_TYPE, 10001, null, KEY));

        Map<String, Object> extraData = new HashMap<>();
        Assertions.assertNotNull(template.createToken(LOGIN_TYPE, 10001, extraData, KEY));
    }

    /** Map 参数方式创建的 token 应该原样携带 Map 中的数据 */
    @Test
    public void createToken_mapWay_roundtrip() {
        Map<String, Object> map = new HashMap<>();
        map.put("name", "zhang");
        map.put("age", 18);

        String token = template.createToken(map, KEY);

        JSONObject payloads = template.parseToken(token, null, KEY, false).getPayloads();
        Assertions.assertEquals("zhang", payloads.getStr("name"));
        Assertions.assertEquals(18, payloads.getInt("age"));
    }

    // ------ 解析

    /** 秘钥为 null 或空串时解析必须抛出 SaJwtException */
    @Test
    public void parseToken_emptyKeyt_throw() {
        String token = template.createToken(LOGIN_TYPE, 10001, null, KEY);

        Assertions.assertThrows(SaJwtException.class, () -> template.parseToken(token, LOGIN_TYPE, null, false));
        Assertions.assertThrows(SaJwtException.class, () -> template.parseToken(token, LOGIN_TYPE, "", false));
    }

    /** token 为 null 时解析必须抛出 SaJwtException */
    @Test
    public void parseToken_nullToken_throw() {
        Assertions.assertThrows(SaJwtException.class, () -> template.parseToken(null, LOGIN_TYPE, KEY, false));
    }

    /** 解析格式非法的 token 必须抛出 SaJwtException，且错误码为 30201 */
    @Test
    public void parseToken_malformedToken_throw30201() {
        SaJwtException e = Assertions.assertThrows(SaJwtException.class,
                () -> template.parseToken("abc.def.gh", LOGIN_TYPE, KEY, false));
        Assertions.assertEquals(SaJwtErrorCode.CODE_30201, e.getCode());
    }

    /** 用错误秘钥解析 token 必须抛出 SaJwtException，且错误码为 30202 */
    @Test
    public void parseToken_wrongKeyt_throw30202() {
        String token = template.createToken(LOGIN_TYPE, 10001, null, KEY);
        SaJwtException e = Assertions.assertThrows(SaJwtException.class,
                () -> template.parseToken(token, LOGIN_TYPE, KEY + "-wrong", false));
        Assertions.assertEquals(SaJwtErrorCode.CODE_30202, e.getCode());
    }

    /** loginType 不匹配时解析必须抛出 SaJwtException，且错误码为 30203 */
    @Test
    public void parseToken_wrongLoginType_throw30203() {
        String token = template.createToken(LOGIN_TYPE, 10001, null, KEY);
        SaJwtException e = Assertions.assertThrows(SaJwtException.class,
                () -> template.parseToken(token, "other-type", KEY, false));
        Assertions.assertEquals(SaJwtErrorCode.CODE_30203, e.getCode());
    }

    /** 已过期的 token 在校验有效期时必须抛出 SaJwtException，且错误码为 30204 */
    @Test
    public void parseToken_expired_throw30204() {
        String token = buildExpiredToken(KEY);
        SaJwtException e = Assertions.assertThrows(SaJwtException.class,
                () -> template.parseToken(token, LOGIN_TYPE, KEY, true));
        Assertions.assertEquals(SaJwtErrorCode.CODE_30204, e.getCode());
    }

    /** 不带 eff 字段的 token 在校验有效期时应该按已过期处理，错误码为 30204 */
    @Test
    public void parseToken_missingEff_throw30204() {
        String token = template.createToken(LOGIN_TYPE, 10001, null, KEY);
        SaJwtException e = Assertions.assertThrows(SaJwtException.class,
                () -> template.parseToken(token, LOGIN_TYPE, KEY, true));
        Assertions.assertEquals(SaJwtErrorCode.CODE_30204, e.getCode());
    }

    /** isCheckTimeout=false 时不校验有效期，过期 token 也应该能正常解析 */
    @Test
    public void parseToken_notCheckTimeout_pass() {
        String token = buildExpiredToken(KEY);
        JWT jwt = template.parseToken(token, LOGIN_TYPE, KEY, false);
        Assertions.assertNotNull(jwt.getPayloads());
    }

    /** 永不过期（eff=-1）的 token 在校验有效期时也应该能正常通过 */
    @Test
    public void parseToken_neverExpire_pass() {
        String token = template.createToken(LOGIN_TYPE, 10001, "pc", SaJwtTemplate.NEVER_EXPIRE, null, KEY);
        JWT jwt = template.parseToken(token, LOGIN_TYPE, KEY, true);
        Assertions.assertNotNull(jwt.getPayloads());
    }

    // ------ 载荷 / 账号id

    /** getPayloads 应该能取出载荷，getPayloadsNotCheck 对过期 token 也应该能取出载荷 */
    @Test
    public void getPayloads_and_getPayloadsNotCheck() {
        String token = template.createToken(LOGIN_TYPE, 10001, "pc", 200, null, KEY);
        Assertions.assertNotNull(template.getPayloads(token, LOGIN_TYPE, KEY).get(SaJwtTemplate.LOGIN_ID));

        String expiredToken = buildExpiredToken(KEY);
        Assertions.assertNotNull(template.getPayloadsNotCheck(expiredToken, LOGIN_TYPE, KEY)
                .get(SaJwtTemplate.LOGIN_ID));
    }

    /** getLoginId 应该能取出账号id，getLoginIdOrNull 对异常 token 应该返回 null */
    @Test
    public void getLoginId_and_getLoginIdOrNull() {
        String token = template.createToken(LOGIN_TYPE, 10001, "pc", 200, null, KEY);
        Assertions.assertEquals("10001", String.valueOf(template.getLoginId(token, LOGIN_TYPE, KEY)));
        Assertions.assertEquals("10001", String.valueOf(template.getLoginIdOrNull(token, LOGIN_TYPE, KEY)));

        // 过期 token 解析会抛 SaJwtException，此时 OrNull 应该兜底返回 null
        String expiredToken = buildExpiredToken(KEY);
        Assertions.assertNull(template.getLoginIdOrNull(expiredToken, LOGIN_TYPE, KEY));
    }

    // ------ 剩余有效期

    /** token 为 null 时 getTimeout 应该返回 NOT_VALUE_EXPIRE */
    @Test
    public void getTimeout_nullToken() {
        Assertions.assertEquals(SaTokenDao.NOT_VALUE_EXPIRE, template.getTimeout(null, LOGIN_TYPE, KEY));
    }

    /** 格式非法的 token 调用 getTimeout 应该返回 NOT_VALUE_EXPIRE */
    @Test
    public void getTimeout_malformedToken() {
        Assertions.assertEquals(SaTokenDao.NOT_VALUE_EXPIRE, template.getTimeout("abc.def.gh", LOGIN_TYPE, KEY));
    }

    /** 秘钥不匹配时 getTimeout 应该返回 NOT_VALUE_EXPIRE */
    @Test
    public void getTimeout_wrongKeyt() {
        String token = template.createToken(LOGIN_TYPE, 10001, "pc", 200, null, KEY);
        Assertions.assertEquals(SaTokenDao.NOT_VALUE_EXPIRE,
                template.getTimeout(token, LOGIN_TYPE, KEY + "-wrong"));
    }

    /** loginType 不匹配时 getTimeout 应该返回 NOT_VALUE_EXPIRE */
    @Test
    public void getTimeout_wrongLoginType() {
        String token = template.createToken(LOGIN_TYPE, 10001, "pc", 200, null, KEY);
        Assertions.assertEquals(SaTokenDao.NOT_VALUE_EXPIRE,
                template.getTimeout(token, "other-type", KEY));
    }

    /** eff=-1 的 token 调用 getTimeout 应该返回 NEVER_EXPIRE */
    @Test
    public void getTimeout_neverExpire() {
        String token = template.createToken(LOGIN_TYPE, 10001, "pc", SaJwtTemplate.NEVER_EXPIRE, null, KEY);
        Assertions.assertEquals(SaTokenDao.NEVER_EXPIRE, template.getTimeout(token, LOGIN_TYPE, KEY));
    }

    /** 已过期的 token 调用 getTimeout 应该返回 NOT_VALUE_EXPIRE */
    @Test
    public void getTimeout_expired() {
        String token = buildExpiredToken(KEY);
        Assertions.assertEquals(SaTokenDao.NOT_VALUE_EXPIRE, template.getTimeout(token, LOGIN_TYPE, KEY));
    }

    /** token 无 eff 字段时 getTimeout 应该返回 NOT_VALUE_EXPIRE（而不是抛异常） */
    @Test
    public void getTimeout_missingEff() {
        String token = template.createToken(LOGIN_TYPE, 10001, null, KEY);
        Assertions.assertEquals(SaTokenDao.NOT_VALUE_EXPIRE, template.getTimeout(token, LOGIN_TYPE, KEY));
    }

    /** 有效期内的 token 调用 getTimeout 应该返回剩余秒数 */
    @Test
    public void getTimeout_validToken() {
        String token = template.createToken(LOGIN_TYPE, 10001, "pc", 200, null, KEY);
        long timeout = template.getTimeout(token, LOGIN_TYPE, KEY);
        Assertions.assertTrue(timeout > 195 && timeout <= 200);
    }

}
