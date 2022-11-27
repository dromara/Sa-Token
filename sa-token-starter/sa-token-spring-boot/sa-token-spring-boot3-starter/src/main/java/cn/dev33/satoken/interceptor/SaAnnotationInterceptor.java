package cn.dev33.satoken.interceptor;

import java.lang.reflect.Method;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import cn.dev33.satoken.strategy.SaStrategy;

/**
 * Sa-Token 注解式鉴权 - 拦截器
 * <h2> [ 当前拦截器写法已过期，可能将在以后的版本删除，推荐升级为 SaInterceptor ] </h2>
 * 
 * @author kong
 */
@Deprecated
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

		// 获取处理 Method 
		if (handler instanceof HandlerMethod == false) {
			return true;
		}
		Method method = ((HandlerMethod) handler).getMethod();

		// 进行验证
		SaStrategy.me.checkMethodAnnotation.accept(method);
		
		// 通过验证
		return true;
	}

}
