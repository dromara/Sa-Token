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
public class SaAnnotationInterceptor implements HandlerInterceptor {


	/**
	 * 底层的 StpLogic 对象  
	 */
	public StpLogic stpLogic = null;
	
	/**
	 * @return 底层的 StpLogic 对象 
	 */
	public StpLogic getStpLogic() {
		if(stpLogic == null) {
			stpLogic = StpUtil.stpLogic;
		}
		return stpLogic;
	}

	/**
	 * @param stpLogic 底层的 StpLogic 对象 
	 * @return 拦截器自身
	 */
	public SaAnnotationInterceptor setStpLogic(StpLogic stpLogic) {
		this.stpLogic = stpLogic;
		return this;
	}

	
	/**
	 * 创建，并指定一个默认的 StpLogic 
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
		if (handler instanceof HandlerMethod == false) {
			return true;
		}
		Method method = ((HandlerMethod) handler).getMethod();
		
		// 进行验证 
		getStpLogic().checkMethodAnnotation(method);
		
		// 通过验证 
		return true;
	}

	
	
}
