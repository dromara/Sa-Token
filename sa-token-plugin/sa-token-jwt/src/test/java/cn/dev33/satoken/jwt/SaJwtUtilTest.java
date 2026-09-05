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
import cn.hutool.json.JSONObject;
import cn.hutool.jwt.JWT;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * {@link SaJwtUtil} 静态委托方法测试
 *
 * @author click33
 * @since 1.46.0
 */
public class SaJwtUtilTest {

    /** 测试秘钥 */
    public static final String KEY = "SaJwtUtilTest-Secret-Key-0123456789";

    /** 登录类型 */
    public static final String LOGIN_TYPE = "jwt-util-test";

    /** 备份原 saJwtTemplate，测完恢复，避免影响其它用例 */
    private SaJwtTemplate backupTemplate;

    @BeforeEach
    public void backup() {
        backupTemplate = SaJwtUtil.getSaJwtTemplate();
    }

    @AfterEach
    public void restore() {
        SaJwtUtil.setSaJwtTemplate(backupTemplate);
    }

    /** 默认构造函数应该能 new 出来 */
    @Test
    public void constructor_instantiable() {
        Assertions.assertNotNull(new SaJwtUtil());
    }

    /** getSaJwtTemplate / setSaJwtTemplate 读写应该一致 */
    @Test
    public void getAndSetSaJwtTemplate() {
        SaJwtTemplate custom = new SaJwtTemplate();
        SaJwtUtil.setSaJwtTemplate(custom);
        Assertions.assertSame(custom, SaJwtUtil.getSaJwtTemplate());
    }

    /** 常量字段应该和 SaJwtTemplate 中的定义保持一致 */
    @Test
    public void constants_sameAsTemplate() {
        Assertions.assertEquals(SaJwtTemplate.LOGIN_TYPE, SaJwtUtil.LOGIN_TYPE);
        Assertions.assertEquals(SaJwtTemplate.LOGIN_ID, SaJwtUtil.LOGIN_ID);
        Assertions.assertEquals(SaJwtTemplate.DEVICE_TYPE, SaJwtUtil.DEVICE_TYPE);
        Assertions.assertEquals(SaJwtTemplate.EFF, SaJwtUtil.EFF);
        Assertions.assertEquals(SaJwtTemplate.RN_STR, SaJwtUtil.RN_STR);
        Assertions.assertEquals(SaJwtTemplate.NEVER_EXPIRE, SaJwtUtil.NEVER_EXPIRE);
        Assertions.assertEquals(SaJwtTemplate.NOT_VALUE_EXPIRE, SaJwtUtil.NOT_VALUE_EXPIRE);
    }

    /** 简单方式委托创建的 token 应该能正常解析（简单模式无 eff，须用不校验有效期的 API） */
    @Test
    public void createToken_simple_delegate() {
        String token = SaJwtUtil.createToken(LOGIN_TYPE, 10001, null, KEY);
        Assertions.assertEquals(10001, SaJwtUtil.getPayloadsNotCheck(token, LOGIN_TYPE, KEY)
                .getInt(SaJwtUtil.LOGIN_ID));
    }

    /** 全参数方式委托创建的 token 应该能正常解析出设备类型 */
    @Test
    public void createToken_full_delegate() {
        String token = SaJwtUtil.createToken(LOGIN_TYPE, 10001, "pc", 200, null, KEY);
        Assertions.assertEquals("pc", SaJwtUtil.getPayloadsNotCheck(token, LOGIN_TYPE, KEY)
                .getStr(SaJwtUtil.DEVICE_TYPE));
    }

    /** Map 参数方式委托创建的 token 应该原样携带 Map 数据 */
    @Test
    public void createToken_mapWay_delegate() {
        Map<String, Object> map = new HashMap<>();
        map.put("name", "zhang");
        String token = SaJwtUtil.createToken(map, KEY);
        Assertions.assertEquals("zhang", SaJwtUtil.parseToken(token, null, KEY, false).getPayloads().getStr("name"));
    }

    /** generateToken 委托生成的 token 应该能通过签名校验 */
    @Test
    public void generateToken_delegate() {
        JWT jwt = JWT.create().setPayload(SaJwtUtil.LOGIN_TYPE, LOGIN_TYPE);
        String token = SaJwtUtil.generateToken(jwt, KEY);
        Assertions.assertEquals(LOGIN_TYPE, SaJwtUtil.getPayloadsNotCheck(token, LOGIN_TYPE, KEY)
                .getStr(SaJwtUtil.LOGIN_TYPE));
    }

    /** parseToken 委托：校验有效期开启时过期 token 应该抛异常 */
    @Test
    public void parseToken_delegate_checkTimeout() {
        Map<String, Object> payloads = new HashMap<>();
        payloads.put(SaJwtUtil.LOGIN_TYPE, LOGIN_TYPE);
        payloads.put(SaJwtUtil.EFF, System.currentTimeMillis() - 60 * 1000);
        String token = SaJwtUtil.generateToken(JWT.create().addPayloads(payloads), KEY);

        Assertions.assertThrows(Exception.class, () -> SaJwtUtil.parseToken(token, LOGIN_TYPE, KEY, true));
        Assertions.assertNotNull(SaJwtUtil.parseToken(token, LOGIN_TYPE, KEY, false));
    }

    /** getLoginIdOrNull 委托：异常 token 应该兜底返回 null */
    @Test
    public void getLoginIdOrNull_delegate() {
        Assertions.assertNull(SaJwtUtil.getLoginIdOrNull("invalid-token", LOGIN_TYPE, KEY));
    }

    /** getTimeout 委托：应该能正确返回剩余有效期 */
    @Test
    public void getTimeout_delegate() {
        String token = SaJwtUtil.createToken(LOGIN_TYPE, 10001, "pc", 200, null, KEY);
        long timeout = SaJwtUtil.getTimeout(token, LOGIN_TYPE, KEY);
        Assertions.assertTrue(timeout > 195 && timeout <= 200);
        Assertions.assertEquals(SaTokenDao.NOT_VALUE_EXPIRE,
                SaJwtUtil.getTimeout("invalid-token", LOGIN_TYPE, KEY));
    }

}
