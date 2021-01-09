package cn.dev33.satoken.interceptor;

import java.lang.reflect.Method;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import cn.dev33.satoken.stp.StpLogic;
import cn.dev33.satoken.stp.StpUtil;

/**
 *  注解式鉴权 - 拦截器 
 * @author kong
 */
public class SaCheckInterceptor implements HandlerInterceptor {


	/**
	 * 底层的 StpLogic 对象  
	 */
	public StpLogic stpLogic = null;
	
	/**
	 * 创建，并指定一个默认的 StpLogic 
	 */
	public  SaCheckInterceptor() {
		this(StpUtil.stpLogic);
	}
	
	/**
	 * 创建，并指定一个的 StpLogic
	 * @param stpLogic 指定的StpLogic
	 */
	public  SaCheckInterceptor(StpLogic stpLogic) {
		this.stpLogic = stpLogic;
	}
	

	/**
	 * 每次请求之前触发 
	 */
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		
		// 获取处理method
		if (handler instanceof HandlerMethod == false) {
			return true;
		}
		Method method = ((HandlerMethod) handler).getMethod();
		
		// 进行验证 
		stpLogic.checkMethodAnnotation(method);
		
		// 通过验证 
		return true;
	}

	
	
	
}
