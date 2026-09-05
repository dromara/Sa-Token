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

import cn.dev33.satoken.SaManager;
import cn.dev33.satoken.context.SaHolder;
import cn.dev33.satoken.context.mock.SaRequestForMock;
import cn.dev33.satoken.context.mock.SaTokenContextMockUtil;
import cn.dev33.satoken.jwt.error.SaJwtErrorCode;
import cn.dev33.satoken.jwt.exception.SaJwtException;
import cn.dev33.satoken.test.SaTokenTest;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * {@link StpLogicJwtForSimple} 简单模式测试
 *
 * @author click33
 * @since 1.46.0
 */
@SaTokenTest
public class StpLogicJwtForSimpleTest {

    /** 测试秘钥 */
    public static final String KEY = "StpLogicJwtForSimpleTest-Secret-Key-0123456789";

    /** 登录类型 */
    public static final String LOGIN_TYPE = "jwt-simple-test";

    private StpLogicJwtForSimple stpLogic;

    @BeforeEach
    public void beforeEach() {
        // @SaTokenTest 已把全局状态复位为默认配置，这里补上 jwt 秘钥
        SaManager.getConfig().setJwtSecretKey(KEY);
        stpLogic = new StpLogicJwtForSimple(LOGIN_TYPE);
    }

    /** 正确配置秘钥后 jwtSecretKey 应该返回配置的值 */
    @Test
    public void jwtSecretKey_configured() {
        Assertions.assertEquals(KEY, stpLogic.jwtSecretKey());
    }

    /** 秘钥未配置（null 或空串）时必须抛出 SaJwtException，且错误码为 30205 */
    @Test
    public void jwtSecretKey_missing_throw() {
        SaManager.getConfig().setJwtSecretKey(null);
        SaJwtException e1 = Assertions.assertThrows(SaJwtException.class, () -> stpLogic.jwtSecretKey());
        Assertions.assertEquals(SaJwtErrorCode.CODE_30205, e1.getCode());

        SaManager.getConfig().setJwtSecretKey("");
        Assertions.assertThrows(SaJwtException.class, () -> stpLogic.jwtSecretKey());
    }

    /** createTokenValue 创建的 token 应该携带 loginId 和 extraData，且不携带 eff 字段（简单模式不管理有效期） */
    @Test
    public void createTokenValue_roundtrip() {
        Map<String, Object> extraData = new HashMap<>();
        extraData.put("company", "zhang-company");

        String token = stpLogic.createTokenValue(10001, "pc", 200, extraData);

        JSONObject payloads = SaJwtUtil.getPayloadsNotCheck(token, LOGIN_TYPE, KEY);
        Assertions.assertEquals(10001, payloads.getInt(SaJwtUtil.LOGIN_ID));
        Assertions.assertEquals("zhang-company", payloads.getStr("company"));
        Assertions.assertNull(payloads.get(SaJwtUtil.EFF));
    }

    /** getExtra 指定 token 时应该能取出扩展数据 */
    @Test
    public void getExtra_byTokenValue() {
        Map<String, Object> extraData = new HashMap<>();
        extraData.put("company", "zhang-company");
        String token = stpLogic.createTokenValue(10001, "pc", 200, extraData);

        Assertions.assertEquals("zhang-company", stpLogic.getExtra(token, "company"));
    }

    /** getExtra 从当前请求上下文中取 token 时应该也能取出扩展数据 */
    @Test
    public void getExtra_currentToken() {
        Map<String, Object> extraData = new HashMap<>();
        extraData.put("company", "zhang-company");
        String token = stpLogic.createTokenValue(10001, "pc", 200, extraData);

        SaTokenContextMockUtil.setMockContext(() -> {
            SaRequestForMock request = (SaRequestForMock) SaHolder.getRequest();
            request.headerMap.put("satoken", token);
            Assertions.assertEquals("zhang-company", stpLogic.getExtra("company"));
        });
    }

    /** 无参构造函数应该默认使用 StpUtil 的 loginType */
    @Test
    public void constructor_defaultLoginType() {
        StpLogicJwtForSimple logic = new StpLogicJwtForSimple();
        Assertions.assertEquals(StpUtil.TYPE, logic.getLoginType());
    }

    /** jwt-simple 模式即使配置了 is-share 也必须返回不支持复用旧 Token */
    @Test
    public void isSupportShareToken_false() {
        Assertions.assertFalse(stpLogic.isSupportShareToken());
    }

    /** jwt-simple 模式应该支持 extra 扩展参数 */
    @Test
    public void isSupportExtra_true() {
        Assertions.assertTrue(stpLogic.isSupportExtra());
    }

}
