package cn.dev33.satoken.annotation;

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
		HandlerMethod  method = (HandlerMethod ) handler;
		
		// 验证登录 
		if(method.hasMethodAnnotation(SaCheckLogin.class) || method.getBeanType().isAnnotationPresent(SaCheckLogin.class)) {
			stpLogic.checkLogin();
		}
		
		// 获取权限注解 
		SaCheckPermission scp = method.getMethodAnnotation(SaCheckPermission.class);
		if(scp == null) {
			scp = method.getBeanType().getAnnotation(SaCheckPermission.class);
		}
		if(scp == null) {
			return true;
		}
		
		// 开始验证权限 
		Object[] codeArray = concatAbc(scp.value(), scp.valueInt(), scp.valueLong());
		if(scp.isAnd()) {
			stpLogic.checkPermissionAnd(codeArray);		// 必须全部都有 
		} else {
			stpLogic.checkPermissionOr(codeArray);		// 有一个就行了  
		}
			
		return true;
	}
	


	
	
	/**
	 * 合并三个数组 
	 * @param a .
	 * @param b .
	 * @param c .
	 * @return .
	 */
	private Object[] concatAbc(String[] a, int[] b, long[] c) {
		// 循环赋值  
		Object[] d = new Object[a.length + b.length + c.length];
		for (int i = 0; i < a.length; i++) {
			d[i] = a[i];
		}
		for (int i = 0; i < b.length; i++) {
			d[a.length + i] = b[i];
		}
		for (int i = 0; i < c.length; i++) {
			d[a.length + b.length + i] = c[i];
		}
		return d;
	}

	
	
	
}
