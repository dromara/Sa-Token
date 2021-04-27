package cn.dev33.satoken.interceptor;

import java.lang.reflect.Method;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.dev33.satoken.SaManager;
import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaCheckRole;
import cn.dev33.satoken.exception.UnrecognizedLoginKeyException;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import cn.dev33.satoken.stp.StpLogic;
import cn.dev33.satoken.stp.StpUtil;

/**
 * 注解式鉴权 - 拦截器
 * 
 * @author kong
 */
public class SaAnnotationInterceptor implements HandlerInterceptor {

	/**
	 * 在进行注解鉴权时使用的 StpLogic 对象
	 */
	public StpLogic stpLogic = null;

	/**
	 * @return 在进行注解鉴权时使用的 StpLogic 对象 
	 */
	public StpLogic getStpLogic() {
		if (stpLogic == null) {
			stpLogic = StpUtil.stpLogic;
		}
		return stpLogic;
	}

	/**
	 * @param stpLogic 在进行注解鉴权时使用的 StpLogic 对象 
	 * @return 拦截器自身
	 */
	public SaAnnotationInterceptor setStpLogic(StpLogic stpLogic) {
		this.stpLogic = stpLogic;
		return this;
	}

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
		if (handler instanceof HandlerMethod == false) {
			return true;
		}
		Method method = ((HandlerMethod) handler).getMethod();

		// 进行验证
		Map<String, StpLogic> stpLogicMap = SaManager.stpLogicMap;

		// ----------- 验证登录
		if(method.isAnnotationPresent(SaCheckLogin.class) || method.getDeclaringClass().isAnnotationPresent(SaCheckLogin.class)) {
			SaCheckLogin checkLogin = method.getAnnotation(SaCheckLogin.class);
			if(checkLogin.loginKeys().length == 0) {
				getStpLogic().checkLogin();
			} else {
				for(String loginKey : checkLogin.loginKeys()) {
					if (stpLogicMap.containsKey(loginKey)) {
						StpLogic stpLogic = stpLogicMap.get(loginKey);
						stpLogic.checkLogin();
					} else {
						throw new UnrecognizedLoginKeyException(loginKey);
					}
				}
			}
		}

		// ----------- 验证角色
		// 验证方法上的
		SaCheckRole scr = method.getAnnotation(SaCheckRole.class);
		if(scr != null) {
			if (scr.loginKeys().length == 0) {
				String[] roleArray = scr.value();
				getStpLogic().checkHasRoles(roleArray, scr.mode());
			} else {
				for(String loginKey : scr.loginKeys()) {
					if (stpLogicMap.containsKey(loginKey)) {
						StpLogic stpLogic = stpLogicMap.get(loginKey);
						String[] roleArray = scr.value();
						stpLogic.checkHasRoles(roleArray, scr.mode());
					} else {
						throw new UnrecognizedLoginKeyException(loginKey);
					}
				}
			}
		}
		// 验证类上的
		scr = method.getDeclaringClass().getAnnotation(SaCheckRole.class);
		if(scr != null) {
			if (scr.loginKeys().length == 0) {
				String[] roleArray = scr.value();
				getStpLogic().checkHasRoles(roleArray, scr.mode());
			} else {
				for(String loginKey : scr.loginKeys()) {
					if (stpLogicMap.containsKey(loginKey)) {
						StpLogic stpLogic = stpLogicMap.get(loginKey);
						String[] roleArray = scr.value();
						stpLogic.checkHasRoles(roleArray, scr.mode());
					} else {
						throw new UnrecognizedLoginKeyException(loginKey);
					}
				}
			}
		}

		// ----------- 验证权限
		// 验证方法上的
		SaCheckPermission scp = method.getAnnotation(SaCheckPermission.class);
		if(scp != null) {
			if (scr.loginKeys().length == 0) {
				String[] permissionArray = scp.value();
				getStpLogic().checkHasPermissions(permissionArray, scp.mode());
			} else {
				for(String loginKey : scr.loginKeys()) {
					if (stpLogicMap.containsKey(loginKey)) {
						StpLogic stpLogic = stpLogicMap.get(loginKey);
						String[] permissionArray = scp.value();
						stpLogic.checkHasPermissions(permissionArray, scp.mode());
					} else {
						throw new UnrecognizedLoginKeyException(loginKey);
					}
				}
			}
		}
		// 验证类上的
		scp = method.getDeclaringClass().getAnnotation(SaCheckPermission.class);
		if(scp != null) {
			if (scr.loginKeys().length == 0) {
				String[] permissionArray = scp.value();
				getStpLogic().checkHasPermissions(permissionArray, scp.mode());
			} else {
				for(String loginKey : scr.loginKeys()) {
					if (stpLogicMap.containsKey(loginKey)) {
						StpLogic stpLogic = stpLogicMap.get(loginKey);
						String[] permissionArray = scp.value();
						stpLogic.checkHasPermissions(permissionArray, scp.mode());
					} else {
						throw new UnrecognizedLoginKeyException(loginKey);
					}
				}
			}
		}

		// 通过验证
		return true;
	}

}
