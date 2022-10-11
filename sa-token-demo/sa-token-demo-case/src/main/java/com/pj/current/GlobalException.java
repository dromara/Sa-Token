package com.pj.current;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import cn.dev33.satoken.util.SaResult;

/**
 * 全局异常处理 
 */
@RestControllerAdvice
public class GlobalException {

	// 全局异常拦截
	@ExceptionHandler
	public SaResult handlerException(Exception e) {

		// 打印堆栈，以供调试
		e.printStackTrace(); 

		// 返回给前端
		return SaResult.error(e.getMessage());
	}
	
}
