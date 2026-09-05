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
import cn.dev33.satoken.dao.SaTokenDao;
import cn.dev33.satoken.exception.ApiDisabledException;
import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.exception.SaTokenException;
import cn.dev33.satoken.jwt.error.SaJwtErrorCode;
import cn.dev33.satoken.jwt.exception.SaJwtException;
import cn.dev33.satoken.session.SaSession;
import cn.dev33.satoken.stp.SaTokenInfo;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.stp.parameter.SaLoginParameter;
import cn.dev33.satoken.test.SaTokenTest;
import cn.dev33.satoken.util.SaTokenConsts;
import cn.hutool.json.JSONObject;
import cn.hutool.jwt.JWT;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * {@link StpLogicJwtForMixin} 混入模式测试
 *
 * @author click33
 * @since 1.46.0
 */
@SaTokenTest
public class StpLogicJwtForMixinTest {

    /** 测试秘钥 */
    public static final String KEY = "StpLogicJwtForMixinTest-Secret-Key-0123456789";

    /** 登录类型 */
    public static final String LOGIN_TYPE = "jwt-mixin-test";

    private StpLogicJwtForMixin stpLogic;

    @BeforeEach
    public void beforeEach() {
        // @SaTokenTest 已把全局状态复位为默认配置，这里补上 jwt 秘钥
        SaManager.getConfig().setJwtSecretKey(KEY);
        stpLogic = new StpLogicJwtForMixin(LOGIN_TYPE);
    }

    /** 造一个已过期、但 loginType 匹配的 token */
    private String buildExpiredToken() {
        Map<String, Object> payloads = new HashMap<>();
        payloads.put(SaJwtUtil.LOGIN_TYPE, LOGIN_TYPE);
        payloads.put(SaJwtUtil.LOGIN_ID, 10001);
        payloads.put(SaJwtUtil.EFF, System.currentTimeMillis() - 60 * 1000);
        return SaJwtUtil.generateToken(JWT.create().addPayloads(payloads), KEY);
    }

    /** 正确配置秘钥后 jwtSecretKey 应该返回配置的值 */
    @Test
    public void jwtSecretKey_configured() {
        Assertions.assertEquals(KEY, stpLogic.jwtSecretKey());
    }

    /** 秘钥未配置时必须抛出 SaJwtException，且错误码为 30205 */
    @Test
    public void jwtSecretKey_missing_throw() {
        SaManager.getConfig().setJwtSecretKey(null);
        SaJwtException e = Assertions.assertThrows(SaJwtException.class, () -> stpLogic.jwtSecretKey());
        Assertions.assertEquals(SaJwtErrorCode.CODE_30205, e.getCode());
    }

    /** createTokenValue 创建的 token 应该携带 loginId、deviceType 和 eff 有效期 */
    @Test
    public void createTokenValue_roundtrip() {
        String token = stpLogic.createTokenValue(10001, "pc", 200, null);

        JSONObject payloads = SaJwtUtil.getPayloadsNotCheck(token, LOGIN_TYPE, KEY);
        Assertions.assertEquals(10001, payloads.getInt(SaJwtUtil.LOGIN_ID));
        Assertions.assertEquals("pc", payloads.getStr(SaJwtUtil.DEVICE_TYPE));
        Assertions.assertTrue(payloads.getLong(SaJwtUtil.EFF) > System.currentTimeMillis() + 195 * 1000);
    }

    /** 登录状态下 getTokenInfo 应该返回完整 token 信息 */
    @Test
    public void getTokenInfo_loggedIn() {
        String token = stpLogic.createTokenValue(10001, "pc", 200, null);

        SaTokenContextMockUtil.setMockContext(() -> {
            SaRequestForMock request = (SaRequestForMock) SaHolder.getRequest();
            request.headerMap.put("satoken", token);

            SaTokenInfo info = stpLogic.getTokenInfo();
            Assertions.assertTrue(info.isLogin);
            Assertions.assertEquals(token, info.tokenValue);
            Assertions.assertEquals("10001", info.loginId);
            Assertions.assertTrue(info.tokenTimeout > 195 && info.tokenTimeout <= 200);
            Assertions.assertEquals(SaTokenDao.NOT_VALUE_EXPIRE, info.sessionTimeout);
            // 混入模式未重写 getLoginDeviceType，走 core 的会话查询，无会话数据时返回 null
            Assertions.assertNull(info.loginDeviceType);
        });
    }

    /** getLoginIdNotHandle 对有效 token 应该返回账号id字符串 */
    @Test
    public void getLoginIdNotHandle_valid() {
        String token = stpLogic.createTokenValue(10001, "pc", 200, null);
        Assertions.assertEquals("10001", stpLogic.getLoginIdNotHandle(token));
    }

    /** getLoginIdNotHandle 对过期 token 应该返回 TOKEN_TIMEOUT 标记值 */
    @Test
    public void getLoginIdNotHandle_expiredToken() {
        Assertions.assertEquals(NotLoginException.TOKEN_TIMEOUT, stpLogic.getLoginIdNotHandle(buildExpiredToken()));
    }

    /** getLoginIdNotHandle 对签名无效的 token 应该返回 null */
    @Test
    public void getLoginIdNotHandle_invalidSign() {
        String token = SaJwtUtil.createToken(LOGIN_TYPE, 10001, "pc", 200, null, KEY);
        Assertions.assertNull(stpLogic.getLoginIdNotHandle(token.replace("a", "b")));
    }

    /** logout 应该清除 just-created 标记并清除 Cookie */
    @Test
    public void logout_withToken() {
        String token = stpLogic.createTokenValue(10001, "pc", 200, null);

        SaTokenContextMockUtil.setMockContext(() -> {
            SaRequestForMock request = (SaRequestForMock) SaHolder.getRequest();
            request.headerMap.put("satoken", token);
            SaHolder.getStorage().set(stpLogic.splicingKeyJustCreatedSave(), "1");

            stpLogic.logout();

            Assertions.assertNull(SaHolder.getStorage().get(stpLogic.splicingKeyJustCreatedSave()));
        });
    }

    /** 关闭 Cookie 模式后 logout 应该跳过 Cookie 清理 */
    @Test
    public void logout_cookieDisabled() {
        SaManager.getConfig().setIsReadCookie(false);
        String token = stpLogic.createTokenValue(10001, "pc", 200, null);

        SaTokenContextMockUtil.setMockContext(() -> {
            SaRequestForMock request = (SaRequestForMock) SaHolder.getRequest();
            request.headerMap.put("satoken", token);
            Assertions.assertDoesNotThrow(() -> stpLogic.logout());
        });
    }

    /** 无参构造函数应该默认使用 StpUtil 的 loginType */
    @Test
    public void constructor_defaultLoginType() {
        StpLogicJwtForMixin logic = new StpLogicJwtForMixin();
        Assertions.assertEquals(StpUtil.TYPE, logic.getLoginType());
    }

    /** 注销相关的三个 API 在混入模式下必须全部禁用 */
    @Test
    public void logout_relatedApis_disabled() {
        Assertions.assertThrows(ApiDisabledException.class,
                () -> stpLogic._logoutByTokenValue("token", null));
        Assertions.assertThrows(ApiDisabledException.class,
                () -> stpLogic._logout(10001, null));
        Assertions.assertThrows(ApiDisabledException.class,
                () -> stpLogic.replaced(10001, "pc"));
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

    /** Token-Id 映射相关的三个方法在混入模式下应该不做任何动作、也不抛异常 */
    @Test
    public void tokenToIdMapping_noAction() {
        Assertions.assertDoesNotThrow(() -> stpLogic.deleteTokenToIdMapping("token"));
        Assertions.assertDoesNotThrow(() -> stpLogic.updateTokenToIdMapping("token", 10001));
        Assertions.assertDoesNotThrow(() -> stpLogic.saveTokenToIdMapping("token", 10001, 200));
    }

    /** 混入模式的 getTokenTimeout 应该返回 token 剩余有效期 */
    @Test
    public void getTokenTimeout_valid() {
        String token = stpLogic.createTokenValue(10001, "pc", 200, null);
        long timeout = stpLogic.getTokenTimeout(token);
        Assertions.assertTrue(timeout > 195 && timeout <= 200);
    }

    /** getTokenSessionByToken 传入空 token 时必须抛出 SaTokenException */
    @Test
    public void getTokenSessionByToken_emptyToken_throw() {
        Assertions.assertThrows(SaTokenException.class, () -> stpLogic.getTokenSessionByToken(null, true));
        Assertions.assertThrows(SaTokenException.class, () -> stpLogic.getTokenSessionByToken("", true));
    }

    /** getTokenSessionByToken 应该能创建 Token-Session，且类型、登录类型、token 值正确 */
    @Test
    public void getTokenSessionByToken_create() {
        String token = stpLogic.createTokenValue(10001, "pc", 200, null);

        SaSession session = stpLogic.getTokenSessionByToken(token, true);
        Assertions.assertEquals(SaTokenConsts.SESSION_TYPE__TOKEN, session.getType());
        Assertions.assertEquals(LOGIN_TYPE, session.getLoginType());
        Assertions.assertEquals(token, session.getToken());

        // 已创建过的情况下 isCreate=false 也应该能取到同一个 session
        Assertions.assertSame(session, stpLogic.getTokenSessionByToken(token, false));
    }

    /** 混入模式下必须禁用按条件查询 Token */
    @Test
    public void searchTokenValue_disabled() {
        Assertions.assertThrows(ApiDisabledException.class,
                () -> stpLogic.searchTokenValue("keyword", 0, 10, false));
    }

    /** jwt-mixin 模式必须返回不支持复用旧 Token */
    @Test
    public void modeFlags() {
        Assertions.assertFalse(stpLogic.isSupportShareToken());
        Assertions.assertTrue(stpLogic.isSupportExtra());
        Assertions.assertEquals(-1, stpLogic.getConfigOfMaxTryTimes(new SaLoginParameter()));
    }

}
