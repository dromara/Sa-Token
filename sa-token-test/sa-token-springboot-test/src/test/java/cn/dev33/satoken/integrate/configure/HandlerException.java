package cn.dev33.satoken.integrate.configure;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import cn.dev33.satoken.exception.IdTokenInvalidException;
import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.exception.NotPermissionException;
import cn.dev33.satoken.exception.NotRoleException;
import cn.dev33.satoken.exception.NotSafeException;
import cn.dev33.satoken.util.SaResult;

/**
 * 全局异常处理 
 * @author kong
 *
 */
@RestControllerAdvice
public class HandlerException {

	// 未登录异常，code=401
	@ExceptionHandler(NotLoginException.class)
	public SaResult handlerNotLoginException(NotLoginException e) {
		return SaResult.error().setCode(401);
	}

	// 缺少角色异常，code=402
	@ExceptionHandler(NotRoleException.class)
	public SaResult handlerNotRoleException(NotRoleException e) {
		return SaResult.error().setCode(402);
	}

	// 缺少权限异常，code=403
	@ExceptionHandler(NotPermissionException.class)
	public SaResult handlerNotPermissionException(NotPermissionException e) {
		return SaResult.error().setCode(403);
	}

	// 二级认证失败，code=901
	@ExceptionHandler(NotSafeException.class)
	public SaResult handlerNotSafeException(NotSafeException e) {
		return SaResult.error().setCode(901);
	}

	// id-token 校验失败，code=902
	@ExceptionHandler(IdTokenInvalidException.class)
	public SaResult handlerIdTokenInvalidException(IdTokenInvalidException e) {
		return SaResult.error().setCode(902);
	}
	
}
