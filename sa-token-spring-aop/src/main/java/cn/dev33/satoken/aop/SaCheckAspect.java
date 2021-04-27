package cn.dev33.satoken.aop;

import cn.dev33.satoken.SaManager;
import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaCheckRole;
import cn.dev33.satoken.exception.NotLoginException;
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
				// StpUserUtil里面的StpLogic对象只有调用至少一次才会初始化,如果没有初始化SaManager.stpLogicMap里面是没有loginKey的
				// 还有一种可能是使用者写错了loginKey,这两种方式都会导致SaManager.stpLogicMap查不到loginKey
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

		try {
			// 执行原有逻辑
			Object obj = joinPoint.proceed();
			return obj;
		} catch (Throwable e) {
			throw e;
		}
	}

}
