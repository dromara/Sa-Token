package cn.dev33.satoken.interceptor;

import java.lang.reflect.Method;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import cn.dev33.satoken.SaManager;

/**
 * 注解式鉴权 - 拦截器
 * 
 * @author kong
 */
public class SaAnnotationInterceptor implements HandlerInterceptor {

	/**
	 * 构建： 注解式鉴权 - 拦截器
	 */
	public SaAnnotationInterceptor() {
	}

	/**
	 * 每次请求之前触发的方法
	 */
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {

		// 获取处理method
		if (!(handler instanceof HandlerMethod)) {
			return true;
		}
		Method method = ((HandlerMethod) handler).getMethod();

		// 进行验证
		SaManager.getSaTokenAction().checkMethodAnnotation(method);
		
		// 通过验证
		return true;
	}

}
