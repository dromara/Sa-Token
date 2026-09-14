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

import cn.dev33.satoken.stp.StpLogic;
import cn.dev33.satoken.test.SaTokenTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Sa-Token Freemarker 标签模板模型测试：12 个标签的注册与断言逻辑
 *
 * @author click33
 * @since 1.46.0
 */
@SaTokenTest
public class SaTokenTemplateModelTest {

    /** 默认构造应该使用默认属性名 value 且使用 StpUtil 的全局 StpLogic */
    @Test
    public void defaultConstructor_registersAllTags() throws Exception {
        SaTokenTemplateModel model = new SaTokenTemplateModel();
        Assertions.assertSame(cn.dev33.satoken.stp.StpUtil.stpLogic, model.stpLogic);
        assertTagNamesRegistered(model);
    }

    /** 单参构造应该注入自定义 StpLogic */
    @Test
    public void stpLogicConstructor_injectsCustomLogic() throws Exception {
        StpLogic custom = new StpLogic("custom-fm");
        SaTokenTemplateModel model = new SaTokenTemplateModel(custom);
        Assertions.assertSame(custom, model.stpLogic);
        assertTagNamesRegistered(model);
    }

    /** 双参构造应该使用自定义属性名 */
    @Test
    public void attrNameConstructor_usesCustomAttrName() throws Exception {
        StpLogic custom = new StpLogic("custom-fm-attr");
        SaTokenTemplateModel model = new SaTokenTemplateModel("name", custom);

        SaTokenTemplateDirectiveModel directive =
                (SaTokenTemplateDirectiveModel) model.get("hasRole");
        Assertions.assertEquals("name", directive.attrName);
    }

    /** toArray 应该把逗号分隔字符串转为数组，且容忍空格与空串 */
    @Test
    public void toArray_splitsAndTrims() {
        SaTokenTemplateModel model = new SaTokenTemplateModel(new StpLogic("fm-toarray"));
        Assertions.assertArrayEquals(new String[]{"admin", "user"},
                model.toArray(" admin , user "));
        Assertions.assertArrayEquals(new String[0], model.toArray(null));
        Assertions.assertArrayEquals(new String[0], model.toArray(""));
    }

    /** 12 个标签名必须全部注册到位 */
    private void assertTagNamesRegistered(SaTokenTemplateModel model) throws Exception {
        String[] tagNames = {
                "login", "notLogin",
                "hasRole", "hasRoleAnd", "hasRoleOr", "notRole", "lackRole",
                "hasPermission", "hasPermissionAnd", "hasPermissionOr", "notPermission", "lackPermission"
        };
        for (String name : tagNames) {
            Object directive = model.get(name);
            Assertions.assertNotNull(directive, "标签未注册: " + name);
            Assertions.assertTrue(directive instanceof SaTokenTemplateDirectiveModel,
                    "标签类型不对: " + name);
        }
    }
}
