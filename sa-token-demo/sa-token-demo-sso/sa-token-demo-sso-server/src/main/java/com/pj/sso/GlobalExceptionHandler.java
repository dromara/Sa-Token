package com.pj.sso;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import cn.dev33.satoken.util.SaResult;

/**
 * 全局异常处理 
 * @author click33
 *
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

	// 全局异常拦截 
	@ExceptionHandler
	public SaResult handlerException(Exception e) {
		e.printStackTrace(); 
		return SaResult.error(e.getMessage());
	}
	
}
