package cn.dev33.satoken.spring;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * SpringMVC相关操作  
 * @author kong
 *
 */
public class SpringMVCUtil {
	
	/**
	 * 获取当前会话的 request 
	 * @return .
	 */
	public static HttpServletRequest getRequest() {
		ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();// 大善人SpringMVC提供的封装 
		if(servletRequestAttributes == null) {
			throw new RuntimeException("当前环境非JavaWeb");
		}
		return servletRequestAttributes.getRequest();
	}
	
	/**
	 * 获取当前会话的  response
	 * @return .
	 */
	public static HttpServletResponse getResponse() {
		ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();// 大善人SpringMVC提供的封装 
		if(servletRequestAttributes == null) {
			throw new RuntimeException("当前环境非JavaWeb");
		}
		return servletRequestAttributes.getResponse();
	}

	
	
	
}
