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
package cn.dev33.satoken.freemarker.dialect;

import cn.dev33.satoken.SaManager;
import cn.dev33.satoken.context.mock.SaTokenContextMockUtil;
import cn.dev33.satoken.stp.StpInterface;
import cn.dev33.satoken.stp.StpLogic;
import cn.dev33.satoken.test.SaTokenTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Sa-Token Freemarker 标签模型集成渲染测试：12 个标签在生产接法（shared variable + sa. 前缀）下的显隐行为
 *
 * @author click33
 * @since 1.46.0
 */
@SaTokenTest
public class SaTokenTemplateModelRenderTest {

    /** login / notLogin 标签：登录与否决定显隐 */
    @Test
    public void loginAndNotLoginTag_byLoginState() {
        Assertions.assertEquals("WELCOME", render("<@sa.login>WELCOME</@sa.login>", true, null, null));
        Assertions.assertEquals("", render("<@sa.login>WELCOME</@sa.login>", false, null, null));
        Assertions.assertEquals("PLEASE-LOGIN", render("<@sa.notLogin>PLEASE-LOGIN</@sa.notLogin>", false, null, null));
        Assertions.assertEquals("", render("<@sa.notLogin>PLEASE-LOGIN</@sa.notLogin>", true, null, null));
    }

    /** hasRole / notRole / lackRole 标签：有无角色决定显隐 */
    @Test
    public void roleTags_byRoleState() {
        Assertions.assertEquals("ADMIN", render("<@sa.hasRole value=\"admin\">ADMIN</@sa.hasRole>", true, Arrays.asList("admin"), null));
        Assertions.assertEquals("", render("<@sa.hasRole value=\"admin\">ADMIN</@sa.hasRole>", true, Arrays.asList("user"), null));

        Assertions.assertEquals("NOT-ADMIN", render("<@sa.notRole value=\"admin\">NOT-ADMIN</@sa.notRole>", true, Arrays.asList("user"), null));
        Assertions.assertEquals("", render("<@sa.notRole value=\"admin\">NOT-ADMIN</@sa.notRole>", true, Arrays.asList("admin"), null));

        Assertions.assertEquals("LACK-ADMIN", render("<@sa.lackRole value=\"admin\">LACK-ADMIN</@sa.lackRole>", true, Arrays.asList("user"), null));
        Assertions.assertEquals("", render("<@sa.lackRole value=\"admin\">LACK-ADMIN</@sa.lackRole>", true, Arrays.asList("admin"), null));
    }

    /** hasRoleAnd / hasRoleOr 标签：多角色全部/其一满足才显示 */
    @Test
    public void roleAndOrTags_multiRoles() {
        String andTpl = "<@sa.hasRoleAnd value=\"admin,ops\">AND</@sa.hasRoleAnd>";
        Assertions.assertEquals("AND", render(andTpl, true, Arrays.asList("admin", "ops"), null));
        Assertions.assertEquals("", render(andTpl, true, Arrays.asList("admin"), null));

        String orTpl = "<@sa.hasRoleOr value=\"admin,ops\">OR</@sa.hasRoleOr>";
        Assertions.assertEquals("OR", render(orTpl, true, Arrays.asList("ops"), null));
        Assertions.assertEquals("", render(orTpl, true, Arrays.asList("user"), null));
    }

    /** hasPermission / notPermission / lackPermission 标签：有无权限决定显隐 */
    @Test
    public void permissionTags_byPermissionState() {
        Assertions.assertEquals("ADD", render("<@sa.hasPermission value=\"user:add\">ADD</@sa.hasPermission>", true, null, Arrays.asList("user:add")));
        Assertions.assertEquals("", render("<@sa.hasPermission value=\"user:add\">ADD</@sa.hasPermission>", true, null, Arrays.asList("user:view")));

        Assertions.assertEquals("NOT-ADD", render("<@sa.notPermission value=\"user:add\">NOT-ADD</@sa.notPermission>", true, null, Arrays.asList("user:view")));
        Assertions.assertEquals("", render("<@sa.notPermission value=\"user:add\">NOT-ADD</@sa.notPermission>", true, null, Arrays.asList("user:add")));

        Assertions.assertEquals("LACK-ADD", render("<@sa.lackPermission value=\"user:add\">LACK-ADD</@sa.lackPermission>", true, null, Arrays.asList("user:view")));
        Assertions.assertEquals("", render("<@sa.lackPermission value=\"user:add\">LACK-ADD</@sa.lackPermission>", true, null, Arrays.asList("user:add")));
    }

    /** hasPermissionAnd / hasPermissionOr 标签：多权限全部/其一满足才显示 */
    @Test
    public void permissionAndOrTags_multiPermissions() {
        String andTpl = "<@sa.hasPermissionAnd value=\"user:add,user:del\">AND</@sa.hasPermissionAnd>";
        Assertions.assertEquals("AND", render(andTpl, true, null, Arrays.asList("user:add", "user:del")));
        Assertions.assertEquals("", render(andTpl, true, null, Arrays.asList("user:add")));

        String orTpl = "<@sa.hasPermissionOr value=\"user:add,user:del\">OR</@sa.hasPermissionOr>";
        Assertions.assertEquals("OR", render(orTpl, true, null, Arrays.asList("user:del")));
        Assertions.assertEquals("", render(orTpl, true, null, Arrays.asList("user:view")));
    }

    /** 未登录时 hasRole 标签应该隐藏（hasRole 内部捕获 NotLoginException 返回 false） */
    @Test
    public void hasRoleTag_hiddenWhenNotLogin() {
        Assertions.assertEquals("", render("<@sa.hasRole value=\"admin\">ADMIN</@sa.hasRole>", false, null, null));
    }

    /** hasRole 用自定义属性名构造时，模板里用 name 属性传值也应该让内容显示出来 */
    @Test
    public void customAttrName_worksInTemplate() {
        String result = SaTokenContextMockUtil.setMockContext(() -> {
            SaManager.setStpInterface(stpInterface(Arrays.asList("admin"), null));
            StpLogic stpLogic = new StpLogic("fm-custom-attr");
            stpLogic.login(10001);

            SaTokenTemplateModel model = new SaTokenTemplateModel("name", stpLogic);

            freemarker.template.Configuration cfg = new freemarker.template.Configuration(
                    freemarker.template.Configuration.VERSION_2_3_34);
            cfg.setSharedVariable("sa", model);

            return renderTemplate("<@sa.hasRole name=\"admin\">ADMIN</@sa.hasRole>", cfg);
        });
        Assertions.assertEquals("ADMIN", result);
    }

    // ------------------------- 渲染辅助 -------------------------

    /** 造一个 StpInterface，角色与权限列表为空时返回空集合 */
    private StpInterface stpInterface(List<String> roles, List<String> permissions) {
        return new StpInterface() {
            @Override
            public List<String> getPermissionList(Object loginId, String loginType) {
                return permissions == null ? Collections.emptyList() : permissions;
            }
            @Override
            public List<String> getRoleList(Object loginId, String loginType) {
                return roles == null ? Collections.emptyList() : roles;
            }
        };
    }

    /** mock 登录态后用生产接法（shared variable）渲染模板，返回渲染结果 */
    private String render(String templateStr, boolean login, List<String> roles, List<String> permissions) {
        return SaTokenContextMockUtil.setMockContext(() -> {
            SaManager.setStpInterface(stpInterface(roles, permissions));

            StpLogic stpLogic = new StpLogic("fm-render");
            if (login) {
                stpLogic.login(10001);
            }

            freemarker.template.Configuration cfg = new freemarker.template.Configuration(
                    freemarker.template.Configuration.VERSION_2_3_34);
            cfg.setSharedVariable("sa", new SaTokenTemplateModel(stpLogic));

            return renderTemplate(templateStr, cfg);
        });
    }

    /** 用真实 Freemarker 引擎渲染模板，返回渲染结果 */
    private String renderTemplate(String templateStr, freemarker.template.Configuration cfg) {
        try {
            freemarker.template.Template template = new freemarker.template.Template("t",
                    new StringReader(templateStr), cfg);
            StringWriter out = new StringWriter();
            template.process(new HashMap<>(), out);
            return out.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
