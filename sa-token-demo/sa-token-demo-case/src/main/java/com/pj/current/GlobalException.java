package com.pj.current;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import cn.dev33.satoken.exception.DisableServiceException;
import cn.dev33.satoken.exception.NotBasicAuthException;
import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.exception.NotPermissionException;
import cn.dev33.satoken.exception.NotRoleException;
import cn.dev33.satoken.exception.NotSafeException;
import cn.dev33.satoken.util.SaResult;

/**
 * 全局异常处理 
 */
@RestControllerAdvice
public class GlobalException {

	// 拦截：未登录异常
	@ExceptionHandler(NotLoginException.class)
	public SaResult handlerException(NotLoginException e) {

		// 打印堆栈，以供调试
		e.printStackTrace(); 

		// 返回给前端
		return SaResult.error(e.getMessage());
	}

	// 拦截：缺少权限异常
	@ExceptionHandler(NotPermissionException.class)
	public SaResult handlerException(NotPermissionException e) {
		e.printStackTrace(); 
		return SaResult.error("缺少权限：" + e.getPermission());
	}

	// 拦截：缺少角色异常
	@ExceptionHandler(NotRoleException.class)
	public SaResult handlerException(NotRoleException e) {
		e.printStackTrace(); 
		return SaResult.error("缺少角色：" + e.getRole());
	}

	// 拦截：二级认证校验失败异常
	@ExceptionHandler(NotSafeException.class)
	public SaResult handlerException(NotSafeException e) {
		e.printStackTrace(); 
		return SaResult.error("二级认证校验失败");
	}

	// 拦截：服务封禁异常 
	@ExceptionHandler(DisableServiceException.class)
	public SaResult handlerException(DisableServiceException e) {
		e.printStackTrace(); 
		return SaResult.error("当前账号 " + e.getService() + " 服务已被封禁 (level=" + e.getLevel() + ")：" + e.getDisableTime() + "秒后解封");
	}

	// 拦截：Http Basic 校验失败异常 
	@ExceptionHandler(NotBasicAuthException.class)
	public SaResult handlerException(NotBasicAuthException e) {
		e.printStackTrace(); 
		return SaResult.error(e.getMessage());
	}

	// 拦截：其它所有异常
	@ExceptionHandler(Exception.class)
	public SaResult handlerException(Exception e) {
		e.printStackTrace(); 
		return SaResult.error(e.getMessage());
	}
	
}
