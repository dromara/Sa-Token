package com.pj.controller;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import com.pj.utils.AjaxJson;

import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.exception.NotPermissionException;
import cn.dev33.satoken.exception.NotRoleException;

/**
 * 全局异常拦截 
 * <p> @ControllerAdvice 可指定包前缀，例如：(basePackages = "com.pj.controller.admin")
 * @author kong
 *
 */
@ControllerAdvice
public class ExceptionHandle {

	
	/** 全局异常拦截  */
	@ResponseBody
	@ExceptionHandler
	public AjaxJson handlerException(Exception e) {

		// 打印堆栈，以供调试
		e.printStackTrace(); 

    	// 记录日志信息
    	AjaxJson aj = null;
		
		// ------------- 判断异常类型，提供个性化提示信息 
		
    	// 如果是未登录异常 
		if(e instanceof NotLoginException){	
			aj = AjaxJson.getNotLogin();
		} 
		// 如果是角色异常
		else if(e instanceof NotRoleException) {	
			NotPermissionException ee = (NotPermissionException) e;
			aj = AjaxJson.getNotJur("无此角色：" + ee.getCode());
		} 
		// 如果是权限异常
		else if(e instanceof NotPermissionException) {	
			NotPermissionException ee = (NotPermissionException) e;
			aj = AjaxJson.getNotJur("无此权限：" + ee.getCode());
		} 
		// 普通异常输出：500 + 异常信息 
		else {
			aj = AjaxJson.getError(e.getMessage());
		}
		
		// 返回到前台 
		return aj;
	}

}
