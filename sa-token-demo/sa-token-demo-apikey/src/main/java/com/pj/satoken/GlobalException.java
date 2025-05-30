package com.pj.satoken;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import cn.dev33.satoken.util.SaResult;

/**
 * 全局异常处理 
 */
@RestControllerAdvice
public class GlobalException {

	// 全局异常拦截（拦截项目中的所有异常）
	@ExceptionHandler
	public SaResult handlerException(Exception e, HttpServletRequest request, HttpServletResponse response) throws Exception {
		e.printStackTrace(); 
		return SaResult.error(e.getMessage());
	}
	
}
