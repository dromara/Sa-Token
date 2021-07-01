package com.pj.satoken.at;

import java.lang.reflect.AnnotatedElement;

import org.springframework.core.annotation.AnnotatedElementUtils;

import cn.dev33.satoken.SaManager;
import cn.dev33.satoken.action.SaTokenActionDefaultImpl;
import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaCheckRole;
import cn.dev33.satoken.annotation.SaCheckSafe;

/**
 * 继承Sa-Token行为Bean默认实现, 重写部分逻辑 
 */
//@Component
public class MySaTokenAction extends SaTokenActionDefaultImpl {

	/**
	 * 重写Sa-Token的注解处理器，加强注解合并功能 
	 * @param target see note 
	 */
	@Override
	protected void validateAnnotation(AnnotatedElement target) {
		
		// 校验 @SaCheckLogin 注解 
		if(AnnotatedElementUtils.isAnnotated(target, SaCheckLogin.class)) {
			SaCheckLogin at = AnnotatedElementUtils.getMergedAnnotation(target, SaCheckLogin.class);
			SaManager.getStpLogic(at.type()).checkByAnnotation(at);
		}

		// 校验 @SaCheckRole 注解 
		if(AnnotatedElementUtils.isAnnotated(target, SaCheckRole.class)) {
			SaCheckRole at = AnnotatedElementUtils.getMergedAnnotation(target, SaCheckRole.class);
			SaManager.getStpLogic(at.type()).checkByAnnotation(at);
		}

		// 校验 @SaCheckPermission 注解
		if(AnnotatedElementUtils.isAnnotated(target, SaCheckPermission.class)) {
			SaCheckPermission at = AnnotatedElementUtils.getMergedAnnotation(target, SaCheckPermission.class);
			SaManager.getStpLogic(at.type()).checkByAnnotation(at);
		}

		// 校验 @SaCheckSafe 注解
		if(AnnotatedElementUtils.isAnnotated(target, SaCheckSafe.class)) {
			SaCheckSafe at = AnnotatedElementUtils.getMergedAnnotation(target, SaCheckSafe.class);
			SaManager.getStpLogic(null).checkByAnnotation(at);
		}
	}
	
}