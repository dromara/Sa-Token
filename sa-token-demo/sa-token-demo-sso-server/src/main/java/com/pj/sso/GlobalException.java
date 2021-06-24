package com.pj.sso;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import com.pj.util.AjaxJson;

/**
 * 全局异常处理 
 */
@ControllerAdvice // 可指定包前缀，比如：(basePackages = "com.pj.admin")
public class GlobalException {

	// 全局异常拦截（拦截项目中的所有异常）
	@ResponseBody
	@ExceptionHandler
	public AjaxJson handlerException(Exception e, HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		// 打印堆栈，以供调试
		System.out.println("全局异常---------------");
		e.printStackTrace(); 

		// 不同异常返回不同状态码 
		AjaxJson aj = AjaxJson.getError(e.getMessage());
		
		// 返回给前端
		return aj;

		// 输出到客户端 
//		response.setContentType("application/json; charset=utf-8"); // http说明，我要返回JSON对象
//		response.getWriter().print(new ObjectMapper().writeValueAsString(aj));
	}
	
}
