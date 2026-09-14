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
package cn.dev33.satoken.thymeleaf.dialect;

import cn.dev33.satoken.SaManager;
import cn.dev33.satoken.context.mock.SaTokenContextMockUtil;
import cn.dev33.satoken.stp.StpInterface;
import cn.dev33.satoken.stp.StpLogic;
import cn.dev33.satoken.test.SaTokenTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templateresolver.StringTemplateResolver;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * Sa-Token Thymeleaf 标签方言测试：方言注册、标签渲染显隐与 toArray 转换
 *
 * @author click33
 * @since 1.46.0
 */
@SaTokenTest
public class SaTokenDialectTest {

    /** 默认构造应该使用 "sa" 前缀且底层是 StpUtil 的全局 StpLogic */
    @Test
    public void defaultConstructor_usesSaPrefixAndGlobalStpLogic() {
        SaTokenDialect dialect = new SaTokenDialect();
        Assertions.assertSame(cn.dev33.satoken.stp.StpUtil.stpLogic, dialect.stpLogic);
        Assertions.assertEquals("sa", dialect.getName());
    }

    /** 自定义构造应该使用传入的名称、优先级与 StpLogic */
    @Test
    public void customConstructor_keepsNameAndPrecedence() {
        StpLogic custom = new StpLogic("custom-thymeleaf");
        SaTokenDialect dialect = new SaTokenDialect("satoken", 2000, custom);

        Assertions.assertSame(custom, dialect.stpLogic);
        Assertions.assertEquals("satoken", dialect.getName());
        Assertions.assertEquals(2000, dialect.getDialectProcessorPrecedence());
    }

    /** getProcessors 应该注册 13 个处理器（12 个鉴权标签 + 1 个命名空间处理器） */
    @Test
    public void getProcessors_registers13Processors() {
        SaTokenDialect dialect = new SaTokenDialect("sa", 1000, new StpLogic("thymeleaf-processors"));
        Set<org.thymeleaf.processor.IProcessor> processors = dialect.getProcessors("sa");

        Assertions.assertEquals(13, processors.size());
    }

    /** hasRole 标签：拥有角色时元素保留，没有角色时元素整个被移除 */
    @Test
    public void hasRoleTag_rendersOrRemoves() {
        String html = "<div sa:hasRole=\"admin\">ADMIN-AREA</div>";
        Assertions.assertEquals("<div>ADMIN-AREA</div>", render(html, true, Arrays.asList("admin"), null));
        Assertions.assertEquals("", render(html, true, Arrays.asList("user"), null));
    }

    /** hasRoleAnd 标签：要求多个角色全部满足才显示 */
    @Test
    public void hasRoleAndTag_requiresAllRoles() {
        String html = "<div sa:hasRoleAnd=\"admin,ops\">ALL-AREA</div>";
        Assertions.assertEquals("<div>ALL-AREA</div>", render(html, true, Arrays.asList("admin", "ops"), null));
        Assertions.assertEquals("", render(html, true, Arrays.asList("admin"), null));
    }

    /** hasRoleOr 标签：多个角色满足其一即显示 */
    @Test
    public void hasRoleOrTag_requiresAnyRole() {
        String html = "<div sa:hasRoleOr=\"admin,ops\">ANY-AREA</div>";
        Assertions.assertEquals("<div>ANY-AREA</div>", render(html, true, Arrays.asList("ops"), null));
        Assertions.assertEquals("", render(html, true, Arrays.asList("user"), null));
    }

    /** hasPermission 标签：拥有权限时元素保留，没有时移除 */
    @Test
    public void hasPermissionTag_rendersOrRemoves() {
        String html = "<div sa:hasPermission=\"user:add\">ADD-BTN</div>";
        Assertions.assertEquals("<div>ADD-BTN</div>", render(html, true, null, Arrays.asList("user:add")));
        Assertions.assertEquals("", render(html, true, null, Arrays.asList("user:view")));
    }

    /** notRole / lackRole 标签：没有指定角色时才显示 */
    @Test
    public void notRoleTag_visibleWhenLackRole() {
        String html = "<div sa:notRole=\"admin\">NOT-ADMIN</div>";
        Assertions.assertEquals("<div>NOT-ADMIN</div>", render(html, true, Arrays.asList("user"), null));
        Assertions.assertEquals("", render(html, true, Arrays.asList("admin"), null));
    }

    /** lackRole 标签：没有指定角色时才显示 */
    @Test
    public void lackRoleTag_visibleWhenLackRole() {
        String html = "<div sa:lackRole=\"admin\">LACK-ADMIN</div>";
        Assertions.assertEquals("<div>LACK-ADMIN</div>", render(html, true, Arrays.asList("user"), null));
        Assertions.assertEquals("", render(html, true, Arrays.asList("admin"), null));
    }

    /** notPermission / lackPermission 标签：没有指定权限时才显示 */
    @Test
    public void notPermissionTag_visibleWhenLackPermission() {
        String html = "<div sa:notPermission=\"user:add\">NOT-ADD</div>";
        Assertions.assertEquals("<div>NOT-ADD</div>", render(html, true, null, Arrays.asList("user:view")));
        Assertions.assertEquals("", render(html, true, null, Arrays.asList("user:add")));

        String html2 = "<div sa:lackPermission=\"user:add\">LACK-ADD</div>";
        Assertions.assertEquals("<div>LACK-ADD</div>", render(html2, true, null, Arrays.asList("user:view")));
        Assertions.assertEquals("", render(html2, true, null, Arrays.asList("user:add")));
    }

    /** hasPermissionAnd / hasPermissionOr 标签：多权限全部/其一满足才显示 */
    @Test
    public void hasPermissionAndOrTag_multiPermissions() {
        String andHtml = "<div sa:hasPermissionAnd=\"user:add,user:del\">AND-BTN</div>";
        Assertions.assertEquals("<div>AND-BTN</div>", render(andHtml, true, null, Arrays.asList("user:add", "user:del")));
        Assertions.assertEquals("", render(andHtml, true, null, Arrays.asList("user:add")));

        String orHtml = "<div sa:hasPermissionOr=\"user:add,user:del\">OR-BTN</div>";
        Assertions.assertEquals("<div>OR-BTN</div>", render(orHtml, true, null, Arrays.asList("user:del")));
        Assertions.assertEquals("", render(orHtml, true, null, Arrays.asList("user:view")));
    }

    /** login / notLogin 标签：登录与否决定显隐 */
    @Test
    public void loginTag_byLoginState() {
        Assertions.assertEquals("<div>WELCOME</div>", render("<div sa:login=\"\">WELCOME</div>", true, null, null));
        Assertions.assertEquals("", render("<div sa:login=\"\">WELCOME</div>", false, null, null));
        Assertions.assertEquals("<div>PLEASE-LOGIN</div>", render("<div sa:notLogin=\"\">PLEASE-LOGIN</div>", false, null, null));
        Assertions.assertEquals("", render("<div sa:notLogin=\"\">PLEASE-LOGIN</div>", true, null, null));
    }

    /** 未登录时 hasRole 等标签应该全部隐藏（hasRole 内部捕获 NotLoginException 返回 false） */
    @Test
    public void hasRoleTag_hiddenWhenNotLogin() {
        String html = "<div sa:hasRole=\"admin\">ADMIN-AREA</div>";
        Assertions.assertEquals("", render(html, false, null, null));
    }

    /** toArray 应该把逗号分隔字符串转为数组，容忍空格与空串 */
    @Test
    public void toArray_splitsAndTrims() {
        SaTokenDialect dialect = new SaTokenDialect("sa", 1000, new StpLogic("thymeleaf-toarray"));
        Assertions.assertArrayEquals(new String[]{"admin", "user"},
                dialect.toArray(" admin , user "));
        Assertions.assertArrayEquals(new String[0], dialect.toArray(null));
        Assertions.assertArrayEquals(new String[0], dialect.toArray(""));
    }

    // ------------------------- 渲染辅助 -------------------------

    /**
     * 渲染带 sa: 前缀标签的模板：先配置角色/权限数据源，再按需登录，最后用真实 Thymeleaf 引擎渲染
     */
    private String render(String html, boolean login, List<String> roles, List<String> permissions) {
        return SaTokenContextMockUtil.setMockContext(() -> {
            SaManager.setStpInterface(new StpInterface() {
                @Override
                public List<String> getPermissionList(Object loginId, String loginType) {
                    return permissions == null ? Collections.emptyList() : permissions;
                }
                @Override
                public List<String> getRoleList(Object loginId, String loginType) {
                    return roles == null ? Collections.emptyList() : roles;
                }
            });

            StpLogic stpLogic = new StpLogic("thymeleaf-render");
            if (login) {
                stpLogic.login(10001);
            }

            TemplateEngine engine = new TemplateEngine();
            engine.setTemplateResolver(new StringTemplateResolver());
            engine.addDialect(new SaTokenDialect("sa", 1000, stpLogic));

            return engine.process(html, new Context()).trim();
        });
    }
}
