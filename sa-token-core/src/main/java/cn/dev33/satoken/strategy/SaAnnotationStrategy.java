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
package cn.dev33.satoken.strategy;

import cn.dev33.satoken.annotation.*;
import cn.dev33.satoken.annotation.handler.*;
import cn.dev33.satoken.fun.strategy.SaCheckELRootMapExtendFunction;
import cn.dev33.satoken.fun.strategy.SaCheckMethodAnnotationFunction;
import cn.dev33.satoken.fun.strategy.SaGetAnnotationFunction;
import cn.dev33.satoken.fun.strategy.SaIsAnnotationPresentFunction;
import cn.dev33.satoken.listener.SaTokenEventCenter;

import java.lang.annotation.Annotation;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Sa-Token 注解鉴权相关策略
 *
 * @author click33
 * @since 1.39.0
 */
public final class SaAnnotationStrategy {

	private SaAnnotationStrategy() {
		registerDefaultAnnotationHandler();
	}

	/**
	 * 全局单例引用
	 */
	public static final SaAnnotationStrategy instance = new SaAnnotationStrategy();


	// ----------------------- 所有策略

	/**
	 * 注解处理器集合
	 */
	public Map<Class<?>, SaAnnotationHandlerInterface<?>> annotationHandlerMap = new LinkedHashMap<>();

	/**
	 * 注册所有默认的注解处理器
	 */
	public void registerDefaultAnnotationHandler() {
		annotationHandlerMap.put(SaIgnore.class, new SaIgnoreHandler());
		annotationHandlerMap.put(SaCheckLogin.class, new SaCheckLoginHandler());
		annotationHandlerMap.put(SaCheckRole.class, new SaCheckRoleHandler());
		annotationHandlerMap.put(SaCheckPermission.class, new SaCheckPermissionHandler());
		annotationHandlerMap.put(SaCheckSafe.class, new SaCheckSafeHandler());
		annotationHandlerMap.put(SaCheckDisable.class, new SaCheckDisableHandler());
		annotationHandlerMap.put(SaCheckHttpBasic.class, new SaCheckHttpBasicHandler());
		annotationHandlerMap.put(SaCheckHttpDigest.class, new SaCheckHttpDigestHandler());
		annotationHandlerMap.put(SaCheckOr.class, new SaCheckOrHandler());
		annotationHandlerMap.put(SaCheckSign.class, new SaCheckSignHandler());
		annotationHandlerMap.put(SaCheckApiKey.class, new SaCheckApiKeyHandler());
	}

	/**
	 * 注册一个注解处理器
	 */
	public void registerAnnotationHandler(SaAnnotationHandlerInterface<?> handler) {
		annotationHandlerMap.put(handler.getHandlerAnnotationClass(), handler);
		SaTokenEventCenter.doRegisterAnnotationHandler(handler);
	}

	/**
	 * 注册一个注解处理器，到首位
	 */
	public void registerAnnotationHandlerToFirst(SaAnnotationHandlerInterface<?> handler) {
		Map<Class<?>, SaAnnotationHandlerInterface<?>> newMap = new LinkedHashMap<>();
		newMap.put(handler.getHandlerAnnotationClass(), handler);
		newMap.putAll(annotationHandlerMap);
		this.annotationHandlerMap = newMap;
		SaTokenEventCenter.doRegisterAnnotationHandler(handler);
	}

	/**
	 * 移除一个注解处理器
	 */
	public void removeAnnotationHandler(Class<?> cls) {
		annotationHandlerMap.remove(cls);
	}

	/**
	 * 对一个 [Method] 对象进行注解校验 （注解鉴权内部实现）
	 */
	@SuppressWarnings("unchecked")
	public SaCheckMethodAnnotationFunction checkMethodAnnotation = (method) -> {
		// 遍历所有的注解处理器，检查此 method 是否具有这些指定的注解
		for (Map.Entry<Class<?>, SaAnnotationHandlerInterface<?>> entry: annotationHandlerMap.entrySet()) {

			// 先校验 Method 所属 Class 上的注解
			Annotation classTakeAnnotation = instance.getAnnotation.apply(method.getDeclaringClass(), (Class<Annotation>)entry.getKey());
			if(classTakeAnnotation != null) {
				entry.getValue().check(classTakeAnnotation, method);
			}

			// 再校验 Method 上的注解
			Annotation methodTakeAnnotation = instance.getAnnotation.apply(method, (Class<Annotation>)entry.getKey());
			if(methodTakeAnnotation != null) {
				entry.getValue().check(methodTakeAnnotation, method);
			}
		}
	};

	/**
	 * 从元素上获取注解
	 */
	public SaGetAnnotationFunction getAnnotation = (element, annotationClass)->{
		// 默认使用jdk的注解处理器
		return element.getAnnotation(annotationClass);
	};

	/**
	 * 判断一个 Method 或其所属 Class 是否包含指定注解
	 */
	public SaIsAnnotationPresentFunction isAnnotationPresent = (method, annotationClass) -> {
		return instance.getAnnotation.apply(method, annotationClass) != null ||
				instance.getAnnotation.apply(method.getDeclaringClass(), annotationClass) != null;
	};

	/**
	 * SaCheckELRootMap 扩展函数
	 */
	public SaCheckELRootMapExtendFunction checkELRootMapExtendFunction = rootMap -> {
		// 默认不做任何处理
	};



}
