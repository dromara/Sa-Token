package cn.dev33.satoken.spring;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import cn.dev33.satoken.error.SaSpringBootErrorCode;
import cn.dev33.satoken.exception.NotWebContextException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * SpringMVC 相关操作工具类，快速获取当前会话的 HttpServletRequest、HttpServletResponse 对象
 *
 * @author click33
 * @since <= 1.34.0
 */
public class SpringMVCUtil {
	
	private SpringMVCUtil() {
	}
	
	/**
	 * 获取当前会话的 request 
	 * @return request
	 */
	public static HttpServletRequest getRequest() {
		ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
		if(servletRequestAttributes == null) {
			throw new NotWebContextException("非 web 上下文无法获取 HttpServletRequest").setCode(SaSpringBootErrorCode.CODE_20101);
		}
		return servletRequestAttributes.getRequest();
	}
	
	/**
	 * 获取当前会话的 response
	 * @return response
	 */
	public static HttpServletResponse getResponse() {
		ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
		if(servletRequestAttributes == null) {
			throw new NotWebContextException("非 web 上下文无法获取 HttpServletRequest").setCode(SaSpringBootErrorCode.CODE_20101);
		}
		return servletRequestAttributes.getResponse();
	}

	/**
	 * 判断当前是否处于 Web 上下文中  
	 * @return request
	 */
	public static boolean isWeb() {
		return RequestContextHolder.getRequestAttributes() != null;
	}
	
}
