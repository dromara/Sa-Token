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
package cn.dev33.satoken.integration.reactor.boot2.login;

import cn.dev33.satoken.integration.reactor.boot2.support.AbstractWebFluxIntegrationTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Map;

/**
 * 登录 / 注销 / Token 信息 HTTP 集成测试。
 */
public class LoginIntegrationTest extends AbstractWebFluxIntegrationTest {

    /** 登录后 isLogin、isLogin2、tokenInfo 应该正常，注销后 isLogin 应为 false */
    @Test
    public void loginLogoutFlow_shouldWork() {
        String token = getBody("/acc/getToken?id=10001");
        Assertions.assertNotNull(token);

        Map<String, Object> isLogin = getAsMapWithToken("/acc/isLogin", token);
        Assertions.assertEquals(200, isLogin.get("code"));
        Assertions.assertEquals(Boolean.TRUE, isLogin.get("data"));

        // SaReactorSyncHolder 手动上下文形式也应该能读到登录态
        Map<String, Object> isLogin2 = getAsMapWithToken("/acc/isLogin2", token);
        Assertions.assertEquals(Boolean.TRUE, isLogin2.get("data"));

        Map<String, Object> tokenInfo = getAsMapWithToken("/acc/tokenInfo", token);
        Assertions.assertEquals(200, tokenInfo.get("code"));
        @SuppressWarnings("unchecked")
        Map<String, Object> data = (Map<String, Object>) tokenInfo.get("data");
        Assertions.assertEquals("satoken", data.get("tokenName"));
        Assertions.assertEquals(token, data.get("tokenValue"));

        getBodyWithToken("/acc/logout", token);
        Assertions.assertEquals(Boolean.FALSE, getAsMapWithToken("/acc/isLogin", token).get("data"));
    }

    /** 未登录时 isLogin 应该返回 false */
    @Test
    public void isLogin_withoutToken_false() {
        Assertions.assertEquals(Boolean.FALSE, getAsMap("/acc/isLogin").get("data"));
    }

    /** doLogin 应该返回登录成功结果与 token */
    @Test
    public void doLogin_ok() {
        Map<String, Object> result = getAsMap("/acc/doLogin?id=10001");
        Assertions.assertEquals(200, result.get("code"));
        Assertions.assertNotNull(result.get("token"));
    }

}
