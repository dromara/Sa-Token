package com.pj.test;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.exception.NotPermissionException;

/**
 * 全局异常处理 
 */
@RestControllerAdvice // 可指定包前缀，比如：(basePackages = "com.pj.admin")
public class GlobalException {

	// 在每个控制器之前触发的操作
	@ModelAttribute
	public void get(HttpServletRequest request) throws IOException {

	}

	// 全局异常拦截（拦截项目中的所有异常）
	@ExceptionHandler
	public AjaxJson handlerException(Exception e, HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		// 打印堆栈，以供调试
		e.printStackTrace(); 

		// 不同异常返回不同状态码 
		AjaxJson aj = null;
		if (e instanceof NotLoginException) {	// 如果是未登录异常
			aj = AjaxJson.getNotLogin();
			// 判断具体是什么类型 
			NotLoginException ee = (NotLoginException) e;
			if(ee.getType() == NotLoginException.NOT_TOKEN) {
				aj.setMsg("未提供token");
			}
			if(ee.getType() == NotLoginException.INVALID_TOKEN) {
				aj.setMsg("token无效");
			}
			if(ee.getType() == NotLoginException.BE_REPLACED) {
				aj.setMsg("token已被顶下线");
			}
			if(ee.getType() == NotLoginException.TOKEN_TIMEOUT) {
				aj.setMsg("token已过期");
			}
		} else if(e instanceof NotPermissionException) {	// 如果是权限异常
			NotPermissionException ee = (NotPermissionException) e;
			aj = AjaxJson.getNotJur("无此权限：" + ee.getCode());
		} else {	// 普通异常, 输出：500 + 异常信息
			aj = AjaxJson.getError(e.getMessage());
		}
		
		// 返回给前端
		return aj;

		// 输出到客户端 
//		response.setContentType("application/json; charset=utf-8"); // http说明，我要返回JSON对象
//		response.getWriter().print(new ObjectMapper().writeValueAsString(aj));
	}

}
