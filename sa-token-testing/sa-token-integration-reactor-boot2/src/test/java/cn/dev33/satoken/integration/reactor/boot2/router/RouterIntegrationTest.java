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
package cn.dev33.satoken.integration.reactor.boot2.router;

import cn.dev33.satoken.integration.reactor.boot2.support.AbstractWebFluxIntegrationTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Map;

/**
 * 全局过滤器路由守卫集成测试。
 */
public class RouterIntegrationTest extends AbstractWebFluxIntegrationTest {

    /** 未登录访问 /guard/** 应该被全局过滤器拦截，错误策略返回 REFUSED 标记 */
    @Test
    public void guard_withoutLogin_refused() {
        String body = getBody("/guard/check");
        Assertions.assertTrue(body.startsWith("REFUSED:NotLoginException"),
                "实际响应：" + body);
    }

    /** 登录后访问 /guard/** 应该放行 */
    @Test
    public void guard_withLogin_pass() {
        String token = getBody("/acc/getToken?id=10001");
        Map<String, Object> result = getAsMapWithToken("/guard/check", token);
        Assertions.assertEquals(200, result.get("code"));
        Assertions.assertEquals("已通过路由守卫", result.get("msg"));
    }

    /** 有权限的账号访问需权限路由应该放行，无权限账号应该被拦截返回 403 */
    @Test
    public void guard_permission_check() {
        String token = getBody("/acc/getToken?id=10001");
        Map<String, Object> allowed = getAsMapWithToken("/guard/permission", token);
        Assertions.assertEquals(200, allowed.get("code"));
        Assertions.assertEquals("已通过权限校验", allowed.get("msg"));

        // 账号 20002 无 article:add 权限，应被全局异常处理拦截为 403
        String noPermToken = getBody("/acc/getToken?id=20002");
        Map<String, Object> denied = getAsMapWithToken("/guard/permission", noPermToken);
        Assertions.assertEquals(403, denied.get("code"));
    }

}
