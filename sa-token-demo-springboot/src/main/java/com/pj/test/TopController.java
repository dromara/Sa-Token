package com.pj.test;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.fasterxml.jackson.databind.ObjectMapper;

import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.exception.NotPermissionException;

/**
 * 加强版控制器
 */
@ControllerAdvice // 可指定包前缀，比如：(basePackages = "com.zyd.blog.controller.admin")
public class TopController {

	// 在每个控制器之前触发的操作
	@ModelAttribute
	public void get(HttpServletRequest request) throws IOException {

	}

	// 全局异常拦截（拦截项目中的所有异常）
	@ExceptionHandler
	public void handlerException(Exception e, HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		e.printStackTrace(); // 打印堆栈，以供调试

		response.setContentType("application/json; charset=utf-8"); // http说明，我要返回JSON对象

		// 如果是未登录异常
		if (e instanceof NotLoginException) {
			String jsonStr = new ObjectMapper().writeValueAsString(AjaxJson.getNotLogin());
			response.getWriter().print(jsonStr);
			return;
		}
		// 如果是权限异常
		if (e instanceof NotPermissionException) {
			NotPermissionException ee = (NotPermissionException) e;
			String jsonStr = new ObjectMapper().writeValueAsString(AjaxJson.getNotJur("无此权限：" + ee.getCode()));
			response.getWriter().print(jsonStr);
			return;
		}

		// 普通异常输出：500 + 异常信息
		response.getWriter().print(new ObjectMapper().writeValueAsString(AjaxJson.getError(e.getMessage())));

	}

}
