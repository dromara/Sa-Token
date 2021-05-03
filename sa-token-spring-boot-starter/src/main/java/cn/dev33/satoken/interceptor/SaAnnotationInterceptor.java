package cn.dev33.satoken.interceptor;

import java.lang.reflect.Method;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.dev33.satoken.SaManager;
import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaCheckRole;
import cn.dev33.satoken.exception.NotLoginException;
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
		Class<?> cutClass = method.getDeclaringClass();
		Map<String, StpLogic> stpLogicMap = SaManager.stpLogicMap;

		// ----------- 验证登录
		SaCheckLogin checkLogin = null;
		if(method.isAnnotationPresent(SaCheckLogin.class)) { // 方法注解的优先级高于类注解
			checkLogin = method.getAnnotation(SaCheckLogin.class);
		} else if(cutClass.isAnnotationPresent(SaCheckLogin.class)) {
			checkLogin = cutClass.getAnnotation(SaCheckLogin.class);
		}
		if (checkLogin != null) {
			String loginKey = checkLogin.key();
			if (stpLogicMap.containsKey(loginKey)) {
				StpLogic stpLogic = stpLogicMap.get(loginKey);
				stpLogic.checkLogin();
			} else {
				throw NotLoginException.newInstance(loginKey, NotLoginException.DEFAULT_MESSAGE);
			}
		}

		// ----------- 验证角色
		SaCheckRole saCheckRole = null;
		if (method.isAnnotationPresent(SaCheckRole.class)) { // 方法注解的优先级高于类注解
			saCheckRole = method.getAnnotation(SaCheckRole.class);
		} else if (cutClass.isAnnotationPresent(SaCheckRole.class)) {
			saCheckRole = cutClass.getAnnotation(SaCheckRole.class);
		}
		if (saCheckRole != null) {
			String loginKey = saCheckRole.key();
			if (stpLogicMap.containsKey(loginKey)) {
				StpLogic stpLogic = stpLogicMap.get(loginKey);
				stpLogic.checkHasRoles(saCheckRole.value(), saCheckRole.mode());
			} else {
				throw NotLoginException.newInstance(loginKey, NotLoginException.DEFAULT_MESSAGE);
			}
		}

		// ----------- 验证权限
		SaCheckPermission saCheckPermission = null;
		if (method.isAnnotationPresent(SaCheckPermission.class)) { // 方法注解的优先级高于类注解
			saCheckPermission = method.getAnnotation(SaCheckPermission.class);
		} else if (cutClass.isAnnotationPresent(SaCheckPermission.class)){
			saCheckPermission = cutClass.getAnnotation(SaCheckPermission.class);
		}
		if (saCheckPermission != null) {
			String loginKey = saCheckPermission.key();
			if (stpLogicMap.containsKey(loginKey)) {
				StpLogic stpLogic = stpLogicMap.get(loginKey);
				stpLogic.checkHasPermissions(saCheckPermission.value(), saCheckPermission.mode());
			} else {
				throw NotLoginException.newInstance(loginKey, NotLoginException.DEFAULT_MESSAGE);
			}
		}

		// 通过验证
		return true;
	}

}
