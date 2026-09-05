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
package cn.dev33.satoken.integration.boot2.auth;

import cn.dev33.satoken.integration.boot2.support.AbstractMockMvcIntegrationTest;
import cn.dev33.satoken.util.SaResult;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * 注解鉴权集成测试：{@code @SaCheckLogin} / Role / Permission / Safe / Disable / Ignore。
 */
public class AnnotationAuthIntegrationTest extends AbstractMockMvcIntegrationTest {

    /** 账号 10001 具备完整角色权限时，注解校验应该全部通过 */
    @Test
    public void annotationChecks_passForFullyAuthorizedUser() {
        SaResult login = request("/at/login?id=10001");
        String satoken = login.get("token", String.class);
        Assertions.assertNotNull(satoken);

        Assertions.assertEquals(200, request("/at/checkLogin?satoken=" + satoken).getCode());
        Assertions.assertEquals(200, request("/at/checkRole?satoken=" + satoken).getCode());
        Assertions.assertEquals(200, request("/at/checkPermission?satoken=" + satoken).getCode());
        Assertions.assertEquals(200, request("/at/checkPermission2?satoken=" + satoken).getCode());
        Assertions.assertEquals(200, request("/at/openSafe?satoken=" + satoken).getCode());
        Assertions.assertEquals(200, request("/at/checkSafe?satoken=" + satoken).getCode());
        Assertions.assertEquals(200, request("/at/checkDisable?satoken=" + satoken).getCode());
    }

    /** 权限不足或二级认证未通过时，应该返回对应业务错误码 */
    @Test
    public void annotationChecks_rejectUnauthorizedAccess() {
        String satoken = request("/at/login?id=10002").get("token", String.class);
        Assertions.assertNotNull(satoken);

        Assertions.assertEquals(401, request("/at/checkLogin").getCode());
        Assertions.assertEquals(402, request("/at/checkRole?satoken=" + satoken).getCode());
        Assertions.assertEquals(403, request("/at/checkPermission?satoken=" + satoken).getCode());
        Assertions.assertEquals(403, request("/at/checkPermission2?satoken=" + satoken).getCode());
        Assertions.assertEquals(901, request("/at/checkSafe?satoken=" + satoken).getCode());

        String satoken10042 = request("/at/login?id=10042").get("token", String.class);
        Assertions.assertEquals(200, request("/at/disable?id=10042").getCode());
        Assertions.assertEquals(904, request("/at/checkDisable?satoken=" + satoken10042).getCode());
        request("/at/untieDisable?id=10042");
        Assertions.assertEquals(200, request("/at/checkDisable?satoken=" + satoken10042).getCode());
    }

    /** {@code @SaIgnore} 应该让方法跳过类级登录校验 */
    @Test
    public void saIgnore_skipClassLevelLoginCheck() {
        Assertions.assertEquals(401, request("/ig/show1").getCode());
        Assertions.assertEquals(200, request("/ig/show2").getCode());
    }

}
