package com.pj.test;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import com.pj.util.AjaxJson;

import cn.dev33.satoken.exception.DisableLoginException;
import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.exception.NotPermissionException;
import cn.dev33.satoken.exception.NotRoleException;

/**
 * 全局异常处理 
 */
@ControllerAdvice // 可指定包前缀，比如：(basePackages = "com.pj.admin")
public class GlobalException {

	// 全局异常拦截（拦截项目中的所有异常）
	@ResponseBody
	@ExceptionHandler
	public AjaxJson handlerException(Exception e)
			throws Exception {

		// 打印堆栈，以供调试
		System.out.println("全局异常---------------");
		e.printStackTrace(); 

		// 不同异常返回不同状态码 
		AjaxJson aj = null;
		if (e instanceof NotLoginException) {	// 如果是未登录异常
			NotLoginException ee = (NotLoginException) e;
			aj = AjaxJson.getNotLogin().setMsg(ee.getMessage());
		} else if(e instanceof NotRoleException) {		// 如果是角色异常
			NotRoleException ee = (NotRoleException) e;
			aj = AjaxJson.getNotJur("无此角色：" + ee.getRole());
		} else if(e instanceof NotPermissionException) {	// 如果是权限异常
			NotPermissionException ee = (NotPermissionException) e;
			aj = AjaxJson.getNotJur("无此权限：" + ee.getPermission());
		} else if(e instanceof DisableLoginException) {	// 如果是被封禁异常
			DisableLoginException ee = (DisableLoginException) e;
			aj = AjaxJson.getNotJur("账号被封禁：" + ee.getDisableTime() + "秒后解封");
		} else {	// 普通异常, 输出：500 + 异常信息
			aj = AjaxJson.getError(e.getMessage());
		}
		
		// 返回给前端
		return aj;
	}
	
}
