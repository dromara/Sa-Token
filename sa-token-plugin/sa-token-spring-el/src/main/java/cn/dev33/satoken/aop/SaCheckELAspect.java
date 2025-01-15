/*
 * Copyright 2020-2099 sa-token.cc
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
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.strategy.SaAnnotationStrategy;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.context.expression.BeanFactoryResolver;
import org.springframework.context.expression.MapAccessor;
import org.springframework.context.expression.MethodBasedEvaluationContext;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.util.ObjectUtils;

import java.lang.reflect.Method;

/**
 * Sa-Token 注解鉴权 EL 表达式 AOP 切入 (用于处理 @SaCheckEL 注解)
 *
 * @author click33
 * @since 1.40.0
 */
@Aspect
public class SaCheckELAspect implements BeanFactoryAware {

    /**
     * 表达式解析器 (用于解析 EL 表达式)
     */
    private final ExpressionParser parser = new SpelExpressionParser();

    /**
     * 参数名发现器 (用于获取方法参数名)
     */
    private final ParameterNameDiscoverer pnd = new DefaultParameterNameDiscoverer();

    /**
     * Spring Bean 工厂 (用于解析 Spring 容器中的 Bean 对象)
     */
    private BeanFactory beanFactory;

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }

    /**
     * 前置通知 (所有被 SaCheckEL 注解修饰的方法或类)
     *
     * @param joinPoint /
     */
    @Before("@within(cn.dev33.satoken.annotation.SaCheckEL) || @annotation(cn.dev33.satoken.annotation.SaCheckEL)")
    public void atBefore(JoinPoint joinPoint) {

        // 获取方法签名与参数列表
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        Object[] args = joinPoint.getArgs();

        // 如果标注了 @SaIgnore 注解，则跳过，代表不进行校验
        if(SaAnnotationStrategy.instance.isAnnotationPresent.apply(method, SaIgnore.class)) {
            return;
        }

        // 1、根数据对象构建
        //      构建校验上下文根数据对象
        SaCheckELRootMap rootMap = new SaCheckELRootMap(method, extractArgs(method, args), joinPoint.getTarget() );

        //      添加 this 指针指向注解函数所在类，使之可以在表达式中通过 this.xx 访问类的属性和方法 (与Target一致，此处只是为了更加语义化)
        rootMap.put(SaCheckELRootMap.KEY_THIS, joinPoint.getTarget());

        //      添加全局默认的 StpLogic 对象，使之可以在表达式中通过 stp.checkLogin() 方式调用校验方法
        rootMap.put(SaCheckELRootMap.KEY_STP, StpUtil.getStpLogic());

        //      添加 JoinPoint 对象，使开发者在扩展时可以根据 JoinPoint 对象获取更多信息
        rootMap.put(SaCheckELRootMap.KEY_JOIN_POINT, joinPoint);

        //      执行开发者自定义的增强策略
        SaAnnotationStrategy.instance.checkELRootMapExtendFunction.accept(rootMap);

        // 2、表达式解析方案构建
        //      创建表达式解析上下文
        MethodBasedEvaluationContext context = new MethodBasedEvaluationContext(rootMap, method, args, pnd);

        //      添加属性访问器，使之可以解析 Map 对象的属性作为根上下文
        context.addPropertyAccessor(new MapAccessor());

        //      设置 Bean 解析器，使之可以在表达式中引用 Spring 容器管理的所有 Bean 对象
        context.setBeanResolver(new BeanFactoryResolver(beanFactory));

        // 3、开始校验
        //      先校验 Method 所属 Class 上的注解表达式
        SaCheckEL ofClass = (SaCheckEL) SaAnnotationStrategy.instance.getAnnotation.apply(method.getDeclaringClass(), SaCheckEL.class);
        if (ofClass != null) {
            parser.parseExpression(ofClass.value()).getValue(context);
        }

        //      再校验 Method 上的注解表达式
        SaCheckEL ofMethod =  (SaCheckEL) SaAnnotationStrategy.instance.getAnnotation.apply(method, SaCheckEL.class);
        if (ofMethod != null) {
            parser.parseExpression(ofMethod.value()).getValue(context);
        }

    }

    /**
     * 如果是可变长参数，则展开并返回，否则原样返回
     *
     * @param method /
     * @param args /
     * @return /
     */
    private Object[] extractArgs(Method method, Object[] args) {
        if (!method.isVarArgs()) {
            return args;
        } else {
            Object[] varArgs = ObjectUtils.toObjectArray(args[args.length - 1]);
            Object[] combinedArgs = new Object[args.length - 1 + varArgs.length];
            System.arraycopy(args, 0, combinedArgs, 0, args.length - 1);
            System.arraycopy(varArgs, 0, combinedArgs, args.length - 1, varArgs.length);
            return combinedArgs;
        }
    }
}
