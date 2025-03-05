package com.pj.current;

import cn.dev33.satoken.util.SaResult;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 全局异常处理 
 */
@RestControllerAdvice
public class GlobalException {

	@ExceptionHandler
	public SaResult handlerException(Exception e, HttpServletRequest request, HttpServletResponse response) {
		e.printStackTrace();
		return SaResult.error(e.getMessage());
	}
	
}
