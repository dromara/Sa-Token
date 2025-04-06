package com.pj.test;

import cn.dev33.satoken.util.SaResult;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理 
 */
@RestControllerAdvice
public class GlobalException {

	@ExceptionHandler
	public SaResult handlerException(Exception e) {
		e.printStackTrace();
		return SaResult.error(e.getMessage());
	}
	
}
