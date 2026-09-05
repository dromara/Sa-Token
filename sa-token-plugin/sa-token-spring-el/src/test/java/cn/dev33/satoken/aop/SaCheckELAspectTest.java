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

import cn.dev33.satoken.annotation.SaCheckEL;
import cn.dev33.satoken.annotation.SaIgnore;
import cn.dev33.satoken.exception.SaTokenException;
import cn.dev33.satoken.strategy.SaAnnotationStrategy;
import cn.dev33.satoken.test.SaTokenTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.aop.aspectj.annotation.AspectJProxyFactory;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * {@link SaCheckELAspect} 切面机制测试：通过 AspectJProxyFactory 真实织入切面验证校验行为
 *
 * <p> 官方文档的 stp 用法示例见 {@link SaCheckELAspectDocTest }
 *
 * @author click33
 * @since 1.46.0
 */
@SaTokenTest
public class SaCheckELAspectTest {

    private SaCheckELAspect aspect;

    @BeforeEach
    public void beforeEach() {
        aspect = new SaCheckELAspect();
        // 注册一个测试 Bean，供表达式通过 @beanName 语法引用
        DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();
        beanFactory.registerSingleton("testBean", new TestBean());
        aspect.setBeanFactory(beanFactory);
    }

    @AfterEach
    public void afterEach() {
        // 恢复默认的 rootMap 扩展函数，避免影响其它用例
        SaAnnotationStrategy.instance.checkELRootMapExtendFunction = rootMap -> {
        };
    }

    /** 将目标对象与切面一起织入，返回代理对象 */
    private <T> T proxy(T target) {
        AspectJProxyFactory factory = new AspectJProxyFactory(target);
        factory.addAspect(aspect);
        return factory.getProxy();
    }

    /** 方法级表达式校验通过时应该正常放行 */
    @Test
    public void methodLevel_pass() {
        MethodLevelService proxy = proxy(new MethodLevelService());
        Assertions.assertDoesNotThrow(() -> proxy.withArg("hello"));
    }

    /** 方法级表达式校验不通过时应该抛出 SaTokenException */
    @Test
    public void methodLevel_fail() {
        MethodLevelService proxy = proxy(new MethodLevelService());
        SaTokenException e = Assertions.assertThrows(SaTokenException.class, () -> proxy.alwaysFail());
        Assertions.assertEquals("未通过 EL 表达式校验", e.getMessage());
    }

    /** 标注 @SaIgnore 的方法应该跳过校验，即使表达式必然不通过 */
    @Test
    public void saIgnore_skipCheck() {
        ClassLevelService proxy = proxy(new ClassLevelService());
        Assertions.assertDoesNotThrow(() -> proxy.ignoredMethod("hello"));
    }

    /** 类级表达式校验通过时应该正常放行 */
    @Test
    public void classLevel_pass() {
        ClassLevelService proxy = proxy(new ClassLevelService());
        Assertions.assertDoesNotThrow(() -> proxy.normalMethod());
    }

    /** 类级表达式校验不通过时应该抛出异常 */
    @Test
    public void classLevel_fail() {
        ClassLevelFailService proxy = proxy(new ClassLevelFailService());
        SaTokenException e = Assertions.assertThrows(SaTokenException.class, () -> proxy.anyMethod());
        Assertions.assertEquals("类级校验未通过", e.getMessage());
    }

    /** 类级与方法级注解同时存在时应该都被校验，方法级不通过时抛出方法级的异常信息 */
    @Test
    public void classAndMethod_bothEvaluated() {
        ClassLevelService proxy = proxy(new ClassLevelService());
        // 类级通过，方法级 args[0] == null 不通过
        SaTokenException e = Assertions.assertThrows(SaTokenException.class, () -> proxy.methodWithArgs(null));
        Assertions.assertEquals("方法级校验未通过", e.getMessage());
        // 两者都通过
        Assertions.assertDoesNotThrow(() -> proxy.methodWithArgs("hello"));
    }

    /** 可变长参数应该被展开后计入 args */
    @Test
    public void varargs_expanded() {
        MethodLevelService proxy = proxy(new MethodLevelService());
        Assertions.assertDoesNotThrow(() -> proxy.varargsMethod("a", "b", "c"));
        Assertions.assertThrows(SaTokenException.class, () -> proxy.varargsMethod("a"));
    }

    /** 表达式中应该能通过 @beanName 语法引用 Spring 容器中的 Bean */
    @Test
    public void beanReference() {
        MethodLevelService proxy = proxy(new MethodLevelService());
        Assertions.assertDoesNotThrow(() -> proxy.beanRef());
    }

    /** 开发者自定义的 rootMap 扩展函数应该被执行，且能拿到 method、target 等信息 */
    @Test
    public void extendFunction_invoked() {
        AtomicBoolean invoked = new AtomicBoolean(false);
        SaAnnotationStrategy.instance.checkELRootMapExtendFunction = rootMap -> {
            invoked.set(true);
            SaCheckELRootMap rm = (SaCheckELRootMap) rootMap;
            Assertions.assertNotNull(rm.getMethod());
            Assertions.assertNotNull(rm.getTarget());
            rm.put("extFlag", true);
        };
        MethodLevelService proxy = proxy(new MethodLevelService());
        Assertions.assertDoesNotThrow(() -> proxy.extendedCheck());
        Assertions.assertTrue(invoked.get());
    }

    /** 测试目标：方法级注解 */
    public static class MethodLevelService {

        /** 表达式：至少要有一个参数 */
        @SaCheckEL("args.length > 0")
        public void withArg(Object arg) {
        }

        /** 表达式：必然不通过 */
        @SaCheckEL("NEED(false, '未通过 EL 表达式校验')")
        public void alwaysFail() {
        }

        /** 表达式：可变长参数展开后应该有 3 个元素 */
        @SaCheckEL("NEED(args.length == 3, '可变长参数展开后数量不对')")
        public void varargsMethod(String first, String... others) {
        }

        /** 表达式：引用 Spring 容器中的 Bean */
        @SaCheckEL("@testBean.name == 'zhang'")
        public void beanRef() {
        }

        /** 表达式：依赖扩展函数放入的 extFlag */
        @SaCheckEL("extFlag == true")
        public void extendedCheck() {
        }
    }

    /** 测试目标：类级注解 */
    @SaCheckEL("target != null")
    public static class ClassLevelService {

        /** 无注解方法：只走类级校验 */
        public void normalMethod() {
        }

        /** 方法级注解：第一个参数必须非 null */
        @SaCheckEL("NEED(args[0] != null, '方法级校验未通过')")
        public void methodWithArgs(Object arg) {
        }

        /** 同时标注 @SaIgnore：应该整体跳过校验 */
        @SaCheckEL("NEED(false, '不应该被校验到')")
        @SaIgnore
        public void ignoredMethod(Object arg) {
        }
    }

    /** 测试目标：类级注解必然不通过 */
    @SaCheckEL("NEED(false, '类级校验未通过')")
    public static class ClassLevelFailService {

        /** 无注解方法：走类级校验 */
        public void anyMethod() {
        }
    }

    /** 测试用 Bean：供表达式 @testBean 引用 */
    public static class TestBean {

        public String getName() {
            return "zhang";
        }
    }

}
