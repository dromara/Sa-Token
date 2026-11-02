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
package cn.dev33.satoken.aop.boot;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.handler.SaAnnotationHandlerInterface;
import cn.dev33.satoken.aop.SaAopPointcutAdvisorBeanRegister;
import cn.dev33.satoken.aop.SaAroundAnnotationPointcutAdvisor;
import cn.dev33.satoken.context.mock.SaTokenContextMockUtil;
import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.exception.SaTokenException;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.strategy.SaAnnotationStrategy;
import cn.dev33.satoken.test.SaTokenTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * sa-token-spring-aop 自动装配后，Service 层注解鉴权应该被 Spring AOP 织入
 *
 * @author click33
 * @since 1.46.0
 */
@SaTokenTest
@SpringBootTest(classes = SaAopTestApplication.class, webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class SaAopAutoConfigurationSpringTest {

    @Autowired
    private SaAroundAnnotationPointcutAdvisor advisor;

    @Autowired
    private MethodLoginService methodLoginService;

    @Autowired
    private ClassLoginService classLoginService;

    @Autowired
    private ExtraCheckService extraCheckService;

    @Autowired
    private SaAnnotationHandlerInterface<ExtraCheck> extraCheckHandler;

    /** 自动装配应该注册 Advisor，并挂到静态字段上 */
    @Test
    public void autoConfig_registersAdvisor() {
        Assertions.assertNotNull(advisor);
        Assertions.assertSame(advisor, SaAopPointcutAdvisorBeanRegister.saAroundAnnoAdvisor);
        AspectJExpressionPointcut pointcut = (AspectJExpressionPointcut) advisor.getPointcut();
        String expr = pointcut.getExpression();
        Assertions.assertTrue(expr.contains(SaCheckLogin.class.getName()));
        Assertions.assertTrue(expr.contains(ExtraCheck.class.getName()));
    }

    /** 方法上的 @SaCheckLogin：未登录必须拦，登录后应该放行 */
    @Test
    public void methodLevel_checkLogin() {
        SaTokenContextMockUtil.setMockContext(() -> {
            Assertions.assertThrows(NotLoginException.class, () -> methodLoginService.needLogin());
            StpUtil.login(10001);
            Assertions.assertEquals("ok", methodLoginService.needLogin());
        });
    }

    /** 类上的 @SaCheckLogin 应该拦到这个类的方法（@within） */
    @Test
    public void classLevel_checkLogin() {
        SaTokenContextMockUtil.setMockContext(() -> {
            Assertions.assertThrows(NotLoginException.class, () -> classLoginService.any());
            StpUtil.login(10001);
            Assertions.assertEquals("class-ok", classLoginService.any());
        });
    }

    /** 类上有登录校验时，方法上的 @SaIgnore 应该跳过鉴权并执行原方法 */
    @Test
    public void saIgnore_skipsCheck() {
        SaTokenContextMockUtil.setMockContext(() -> {
            Assertions.assertEquals("ignored", classLoginService.ignored());
        });
    }

    /** Spring 容器里额外注册的注解处理器应该被织进切点，并在策略表注册后真正执行校验 */
    @Test
    public void extraHandler_isWoven() {
        SaAnnotationStrategy.instance.registerAnnotationHandler(extraCheckHandler);
        try {
            SaTokenException ex = Assertions.assertThrows(SaTokenException.class, () -> extraCheckService.hit());
            Assertions.assertEquals("extra-check", ex.getMessage());
        } finally {
            SaAnnotationStrategy.instance.removeAnnotationHandler(ExtraCheck.class);
        }
    }

}
