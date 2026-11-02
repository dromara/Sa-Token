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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templateresolver.StringTemplateResolver;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Sa-Token Thymeleaf 标签处理器测试：断言函数收到的属性值与真假时的渲染行为
 *
 * @author click33
 * @since 1.46.0
 */
public class SaTokenTagProcessorTest {

    /** 断言函数应该收到模板里写的属性值 */
    @Test
    public void funReceivesAttributeValue() {
        AtomicReference<String> received = new AtomicReference<>();
        SaTokenTagProcessor processor = new SaTokenTagProcessor("sa", "check", value -> {
            received.set(value);
            return true;
        });

        render("<div sa:check=\"user:add\">OK</div>", processor);

        Assertions.assertEquals("user:add", received.get());
    }

    /** 断言函数返回 true 时元素保留 */
    @Test
    public void funTrue_keepsElement() {
        Assertions.assertEquals("<div>OK</div>", render(
                "<div sa:check=\"x\">OK</div>",
                new SaTokenTagProcessor("sa", "check", value -> true)));
    }

    /** 断言函数返回 false 时元素整个被移除 */
    @Test
    public void funFalse_removesElement() {
        Assertions.assertEquals("", render(
                "<div sa:check=\"x\">OK</div>",
                new SaTokenTagProcessor("sa", "check", value -> false)));
    }

    // ------------------------- 渲染辅助 -------------------------

    /** 用真实 Thymeleaf 引擎渲染一个带自定义处理器的模板 */
    private String render(String html, SaTokenTagProcessor processor) {
        TemplateEngine engine = new TemplateEngine();
        engine.setTemplateResolver(new StringTemplateResolver());
        engine.addDialect(new SingleProcessorDialect(processor));
        return engine.process(html, new Context()).trim();
    }

    /** 只挂一个处理器的最小方言，专供本测试类渲染用 */
    static class SingleProcessorDialect extends org.thymeleaf.dialect.AbstractProcessorDialect {

        private final SaTokenTagProcessor processor;

        SingleProcessorDialect(SaTokenTagProcessor processor) {
            super("single", "sa", 1000);
            this.processor = processor;
        }

        @Override
        public java.util.Set<org.thymeleaf.processor.IProcessor> getProcessors(String prefix) {
            return java.util.Collections.singleton(processor);
        }
    }
}
