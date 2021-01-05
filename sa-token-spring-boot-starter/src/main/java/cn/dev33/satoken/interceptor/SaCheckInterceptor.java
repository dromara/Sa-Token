package cn.dev33.satoken.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaCheckRole;
import cn.dev33.satoken.annotation.SaMode;
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
		
		// ----------- 验证登录 
		if(method.hasMethodAnnotation(SaCheckLogin.class) || method.getBeanType().isAnnotationPresent(SaCheckLogin.class)) {
			stpLogic.checkLogin();
		}
		
		// ----------- 验证角色
		// 验证方法上的 
		SaCheckRole scr = method.getMethodAnnotation(SaCheckRole.class);
		if(scr != null) { 
			String[] roleArray = scr.value();
			if(scr.mode() == SaMode.AND) {
				stpLogic.checkRoleAnd(roleArray);		// 必须全部都有 
			} else {
				stpLogic.checkRoleOr(roleArray);		// 有一个就行了  
			}
		}
		// 验证类上的 
		scr = method.getBeanType().getAnnotation(SaCheckRole.class);
		if(scr != null) { 
			String[] roleArray = scr.value();
			if(scr.mode() == SaMode.AND) {
				stpLogic.checkRoleAnd(roleArray);		// 必须全部都有 
			} else {
				stpLogic.checkRoleOr(roleArray);		// 有一个就行了  
			}
		}
		
		// ----------- 验证权限 
		// 验证方法上的 
		SaCheckPermission scp = method.getMethodAnnotation(SaCheckPermission.class);
		if(scp != null) { 
			String[] permissionArray = scp.value();
			if(scp.mode() == SaMode.AND) {
				stpLogic.checkPermissionAnd(permissionArray);		// 必须全部都有 
			} else {
				stpLogic.checkPermissionOr(permissionArray);		// 有一个就行了  
			}
		}
		// 验证类上的 
		scp = method.getBeanType().getAnnotation(SaCheckPermission.class);
		if(scp != null) { 
			String[] permissionArray = scp.value();
			if(scp.mode() == SaMode.AND) {
				stpLogic.checkPermissionAnd(permissionArray);		// 必须全部都有 
			} else {
				stpLogic.checkPermissionOr(permissionArray);		// 有一个就行了  
			}
		}
		
		// 通过验证 
		return true;
	}

	
	
	
}
