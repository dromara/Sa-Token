package com.pj.current;

import cn.dev33.satoken.util.SaResult;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理 
 */
@RestControllerAdvice
public class GlobalException {

	// 全局异常拦截（拦截项目中的所有异常）
	@ExceptionHandler
	public SaResult handlerException(Exception e){
		e.printStackTrace();
		return SaResult.error(e.getMessage());
	}
	
}
