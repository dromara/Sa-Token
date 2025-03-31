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

	// 全局异常拦截（拦截项目中的所有异常）
	@ExceptionHandler
	public SaResult handlerException(Exception e, HttpServletRequest request, HttpServletResponse response) {

		// 打印堆栈，以供调试
		System.out.println("全局异常---------------");
		e.printStackTrace();

		// 返回给前端
		return SaResult.error(e.getMessage());
	}

}
