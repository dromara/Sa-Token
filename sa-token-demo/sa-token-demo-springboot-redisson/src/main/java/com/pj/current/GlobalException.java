package com.pj.current;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.pj.util.AjaxJson;

import cn.dev33.satoken.exception.DisableServiceException;
import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.exception.NotPermissionException;
import cn.dev33.satoken.exception.NotRoleException;

/**
 * 全局异常处理 
 */
@RestControllerAdvice
public class GlobalException {

	// 全局异常拦截（拦截项目中的所有异常）
	@ExceptionHandler
	public AjaxJson handlerException(Exception e, HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		// 打印堆栈，以供调试
		System.out.println("全局异常---------------");
		e.printStackTrace(); 

		// 不同异常返回不同状态码 
		AjaxJson aj = null;
		if (e instanceof NotLoginException) {	// 如果是未登录异常
			NotLoginException ee = (NotLoginException) e;
			aj = AjaxJson.getNotLogin().setMsg(ee.getMessage());
		} 
		else if(e instanceof NotRoleException) {		// 如果是角色异常
			NotRoleException ee = (NotRoleException) e;
			aj = AjaxJson.getNotJur("无此角色：" + ee.getRole());
		} 
		else if(e instanceof NotPermissionException) {	// 如果是权限异常
			NotPermissionException ee = (NotPermissionException) e;
			aj = AjaxJson.getNotJur("无此权限：" + ee.getPermission());
		} 
		else if(e instanceof DisableServiceException) {	// 如果是被封禁异常
			DisableServiceException ee = (DisableServiceException) e;
			aj = AjaxJson.getNotJur("当前账号 " + ee.getService() + " 服务已被封禁 (level=" + ee.getLevel() + ")：" + ee.getDisableTime() + "秒后解封");
		} 
		else {	// 普通异常, 输出：500 + 异常信息 
			aj = AjaxJson.getError(e.getMessage());
		}
		
		// 返回给前端
		return aj;
	}
	
}
