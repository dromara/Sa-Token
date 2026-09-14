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

import freemarker.template.Configuration;
import freemarker.template.Template;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Sa-Token Freemarker 标签指令模型测试：真实模板渲染下断言函数真假时内容的显示与隐藏
 *
 * @author click33
 * @since 1.46.0
 */
public class SaTokenTemplateDirectiveModelTest {

    /** 断言函数返回 true 时标签体内容应该出现在渲染结果里 */
    @Test
    public void execute_funTrue_rendersBody() throws Exception {
        AtomicReference<String> received = new AtomicReference<>();
        SaTokenTemplateDirectiveModel model = new SaTokenTemplateDirectiveModel("value", value -> {
            received.set(value);
            return true;
        });

        String result = render(model, "admin");

        Assertions.assertEquals("admin", received.get());
        Assertions.assertEquals("BODY", result);
    }

    /** 断言函数返回 false 时标签体内容不应该出现在渲染结果里 */
    @Test
    public void execute_funFalse_skipsBody() throws Exception {
        SaTokenTemplateDirectiveModel model = new SaTokenTemplateDirectiveModel("value", value -> false);

        String result = render(model, "admin");

        Assertions.assertEquals("", result);
    }

    /** 标签没写 value 属性时断言函数应该收到 null */
    @Test
    public void execute_missingAttr_passesNull() throws Exception {
        AtomicReference<String> received = new AtomicReference<>("sentinel");
        SaTokenTemplateDirectiveModel model = new SaTokenTemplateDirectiveModel("value", value -> {
            received.set(value);
            return true;
        });

        // 模板里不写 value 属性，直接调用标签
        String result = renderRaw(model, "<@tag>BODY</@tag>");

        Assertions.assertNull(received.get());
        Assertions.assertEquals("BODY", result);
    }

    /** 自定义属性名生效：模板里用 name 属性传值时断言函数应该收到 */
    @Test
    public void execute_customAttrName() throws Exception {
        AtomicReference<String> received = new AtomicReference<>();
        SaTokenTemplateDirectiveModel model = new SaTokenTemplateDirectiveModel("name", value -> {
            received.set(value);
            return true;
        });

        String result = renderRaw(model, "<@tag name=\"super-admin\">BODY</@tag>");

        Assertions.assertEquals("super-admin", received.get());
        Assertions.assertEquals("BODY", result);
    }

    // ------------------------- 渲染辅助 -------------------------

    /** 用 value 属性渲染标签：断言函数收到的值就是模板里写的属性值 */
    private String render(SaTokenTemplateDirectiveModel model, String attrValue) throws Exception {
        return renderRaw(model, "<@tag value=\"" + attrValue + "\">BODY</@tag>");
    }

    /** 真实 Freemarker 渲染：dataModel 里的 tag 变量即指令模型 */
    private String renderRaw(SaTokenTemplateDirectiveModel model, String templateStr) throws Exception {
        Configuration cfg = new Configuration(Configuration.VERSION_2_3_34);
        Template template = new Template("test", new StringReader(templateStr), cfg);

        Map<String, Object> root = new HashMap<>();
        root.put("tag", model);

        StringWriter out = new StringWriter();
        template.process(root, out);
        return out.toString();
    }
}
