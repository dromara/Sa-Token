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
package cn.dev33.satoken.aop;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaIgnore;
import cn.dev33.satoken.annotation.handler.SaAnnotationHandlerInterface;
import cn.dev33.satoken.annotation.handler.SaCheckLoginHandler;
import cn.dev33.satoken.annotation.handler.SaIgnoreHandler;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.AnnotatedElement;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * {@link SaAopPointcutAdvisorBeanRegister} 切入表达式计算测试
 *
 * @author click33
 * @since 1.46.0
 */
public class SaAopPointcutAdvisorBeanRegisterTest {

    /** 空 class 列表时切入表达式应该是空串 */
    @Test
    public void calcClassListExpression_empty_returnsBlank() {
        Assertions.assertEquals("", SaAopPointcutAdvisorBeanRegister.calcClassListExpression(Collections.emptyList()));
    }

    /** 单个注解 class 时应该同时拼 @within 和 @annotation */
    @Test
    public void calcClassListExpression_singleClass() {
        String expr = SaAopPointcutAdvisorBeanRegister.calcClassListExpression(Collections.singletonList(SaCheckLogin.class));
        Assertions.assertEquals(
                "@within(cn.dev33.satoken.annotation.SaCheckLogin) || @annotation(cn.dev33.satoken.annotation.SaCheckLogin)",
                expr);
    }

    /** 多个注解 class 时应该用 || 拼起来 */
    @Test
    public void calcClassListExpression_multipleClasses_joinedByOr() {
        String expr = SaAopPointcutAdvisorBeanRegister.calcClassListExpression(Arrays.asList(SaCheckLogin.class, SaIgnore.class));
        Assertions.assertTrue(expr.contains("@within(cn.dev33.satoken.annotation.SaCheckLogin)"));
        Assertions.assertTrue(expr.contains("@annotation(cn.dev33.satoken.annotation.SaIgnore)"));
        Assertions.assertTrue(expr.contains(" || "));
    }

    /** calcExpression 传 null 时应该只包含框架内置注解 */
    @Test
    public void calcExpression_nullAppend_usesBuiltinHandlers() {
        String expr = SaAopPointcutAdvisorBeanRegister.calcExpression(null);
        Assertions.assertTrue(expr.contains(SaCheckLogin.class.getName()));
        Assertions.assertFalse(expr.contains(SaIgnore.class.getName()));
        Assertions.assertFalse(expr.contains(ExtraCheck.class.getName()));
    }

    /** calcExpression 追加已内置的 handler 时不应该把同一注解再拼一遍 */
    @Test
    public void calcExpression_duplicateBuiltinHandler_notRepeated() {
        String expr = SaAopPointcutAdvisorBeanRegister.calcExpression(Collections.singletonList(new SaCheckLoginHandler()));
        int withinCount = countOf(expr, "@within(" + SaCheckLogin.class.getName() + ")");
        Assertions.assertEquals(1, withinCount);
    }

    /** calcExpression 追加未内置的 handler 时应该写进表达式，重复追加同一注解只保留一次 */
    @Test
    public void calcExpression_appendExtraHandler_once() {
        SaIgnoreHandler first = new SaIgnoreHandler();
        SaIgnoreHandler second = new SaIgnoreHandler();
        String expr = SaAopPointcutAdvisorBeanRegister.calcExpression(Arrays.asList(first, second, extraHandler()));
        Assertions.assertEquals(1, countOf(expr, "@annotation(" + SaIgnore.class.getName() + ")"));
        Assertions.assertEquals(1, countOf(expr, "@annotation(" + ExtraCheck.class.getName() + ")"));
    }

    /** 空追加列表时应该和 null 一样只走内置 handler */
    @Test
    public void calcExpression_emptyAppend_usesBuiltinHandlers() {
        String expr = SaAopPointcutAdvisorBeanRegister.calcExpression(Collections.emptyList());
        Assertions.assertTrue(expr.contains(SaCheckLogin.class.getName()));
        Assertions.assertFalse(expr.contains(ExtraCheck.class.getName()));
    }

    /** 默认构造 Advisor 与 Register 应该能 new 出来 */
    @Test
    public void constructors_canNew() {
        Assertions.assertNotNull(new SaAroundAnnotationPointcutAdvisor());
        Assertions.assertNotNull(new SaAopPointcutAdvisorBeanRegister());
    }

    private static int countOf(String text, String token) {
        int count = 0;
        for (int from = 0; (from = text.indexOf(token, from)) >= 0; from += token.length()) {
            count++;
        }
        return count;
    }

    private static SaAnnotationHandlerInterface<ExtraCheck> extraHandler() {
        return new SaAnnotationHandlerInterface<ExtraCheck>() {
            @Override
            public Class<ExtraCheck> getHandlerAnnotationClass() {
                return ExtraCheck.class;
            }
            @Override
            public void checkMethod(ExtraCheck at, AnnotatedElement element) {
            }
        };
    }

    /** 仅用于测试追加自定义注解处理器 */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    private @interface ExtraCheck {
    }

}
