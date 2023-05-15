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

import java.lang.reflect.Method;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import cn.dev33.satoken.annotation.SaIgnore;
import cn.dev33.satoken.strategy.SaStrategy;
import cn.dev33.satoken.util.SaTokenConsts;

/**
 * Sa-Token 基于 Spring Aop 的注解鉴权
 *
 * <p>
 *     注意：在打开 注解鉴权 时，AOP 模式与拦截器模式不可同时使用，否则会出现在 Controller 层重复鉴权两次的问题
 * </p>
 * 
 * @author click33
 * @since <= 1.34.0
 */
@Aspect
@Component
@Order(SaTokenConsts.ASSEMBLY_ORDER)
public class SaCheckAspect {
	
	/**
	 * 构建
	 */
	public SaCheckAspect() {
	}

	/**
	 * 定义AOP签名 (切入所有使用 Sa-Token 鉴权注解的方法)
	 */
	public static final String POINTCUT_SIGN = 
			"@within(cn.dev33.satoken.annotation.SaCheckLogin) || @annotation(cn.dev33.satoken.annotation.SaCheckLogin) || "
			+ "@within(cn.dev33.satoken.annotation.SaCheckRole) || @annotation(cn.dev33.satoken.annotation.SaCheckRole) || "
			+ "@within(cn.dev33.satoken.annotation.SaCheckPermission) || @annotation(cn.dev33.satoken.annotation.SaCheckPermission) || "
			+ "@within(cn.dev33.satoken.annotation.SaCheckSafe) || @annotation(cn.dev33.satoken.annotation.SaCheckSafe) || "
			+ "@within(cn.dev33.satoken.annotation.SaCheckDisable) || @annotation(cn.dev33.satoken.annotation.SaCheckDisable) || "
			+ "@within(cn.dev33.satoken.annotation.SaCheckBasic) || @annotation(cn.dev33.satoken.annotation.SaCheckBasic)";

	/**
	 * 声明AOP签名
	 */
	@Pointcut(POINTCUT_SIGN)
	public void pointcut() {
	}

	/**
	 * 环绕切入
	 * 
	 * @param joinPoint 切面对象
	 * @return 底层方法执行后的返回值
	 * @throws Throwable 底层方法抛出的异常
	 */
	@Around("pointcut()")
	public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
		
		// 获取对应的 Method 处理函数 
		MethodSignature signature = (MethodSignature) joinPoint.getSignature();
		Method method = signature.getMethod();
		
		// 如果此 Method 或其所属 Class 标注了 @SaIgnore，则忽略掉鉴权 
		if(SaStrategy.me.isAnnotationPresent.apply(method, SaIgnore.class)) {
			// ... 
		} else {
			// 注解鉴权 
			SaStrategy.me.checkMethodAnnotation.accept(method);
		}
		
		try {
			// 执行原有逻辑
			Object obj = joinPoint.proceed();
			return obj;
		} catch (Throwable e) {
			throw e;
		}
	}

}
