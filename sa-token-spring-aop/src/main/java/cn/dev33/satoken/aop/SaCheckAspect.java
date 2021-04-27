package cn.dev33.satoken.aop;

import cn.dev33.satoken.SaManager;
import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaCheckRole;
import cn.dev33.satoken.exception.UnrecognizedLoginKeyException;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import cn.dev33.satoken.stp.StpLogic;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaTokenConsts;

import java.lang.reflect.Method;
import java.util.Map;

/**
 * sa-token 基于 Spring Aop 的注解鉴权
 * 
 * @author kong
 */
@Aspect
@Component
@Order(SaTokenConsts.ASSEMBLY_ORDER)
public class SaCheckAspect {
	
	/**
	 * 构建
	 */
	public SaCheckAspect() {
	}

	/**
	 * 获取本切面使用的StpLogic 
	 */
	public StpLogic getStpLogic() {
		return StpUtil.stpLogic;
	}

	/**
	 * 定义AOP签名 (切入所有使用sa-token鉴权注解的方法)
	 */
	public static final String POINTCUT_SIGN = "@within(cn.dev33.satoken.annotation.SaCheckLogin) || @annotation(cn.dev33.satoken.annotation.SaCheckLogin) || "
			+ "@within(cn.dev33.satoken.annotation.SaCheckRole) || @annotation(cn.dev33.satoken.annotation.SaCheckRole) || "
			+ "@within(cn.dev33.satoken.annotation.SaCheckPermission) || @annotation(cn.dev33.satoken.annotation.SaCheckPermission)";

	/**
	 * 声明AOP签名
	 */
	@Pointcut(POINTCUT_SIGN)
	public void pointcut() {
	}

	/**
	 * 环绕切入
	 * 
	 * @param joinPoint 切面对象
	 * @return 底层方法执行后的返回值
	 * @throws Throwable 底层方法抛出的异常
	 */
	@Around("pointcut()")
	public Object around(ProceedingJoinPoint joinPoint) throws Throwable {

		// 注解鉴权
		MethodSignature signature = (MethodSignature) joinPoint.getSignature();
		Method method = signature.getMethod();
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

		try {
			// 执行原有逻辑
			Object obj = joinPoint.proceed();
			return obj;
		} catch (Throwable e) {
			throw e;
		}
	}

}
