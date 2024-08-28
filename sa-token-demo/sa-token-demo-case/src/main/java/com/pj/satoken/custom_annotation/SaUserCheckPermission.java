package com.pj.satoken.custom_annotation;

import cn.dev33.satoken.annotation.SaMode;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 权限认证(User版)：必须具有指定权限才能进入该方法 
 * <p> 可标注在函数、类上（效果等同于标注在此类的所有方法上）
 *
 * @author click33
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD, ElementType.TYPE})
public @interface SaUserCheckPermission {

	/**
	 * 需要校验的权限码
	 * @return 需要校验的权限码
	 */
	String [] value() default {};

	/**
	 * 验证模式：AND | OR，默认AND
	 * @return 验证模式
	 */
	SaMode mode() default SaMode.AND;

	/**
	 * 在权限校验不通过时的次要选择，两者只要其一校验成功即可通过校验
	 *
	 * <p>
	 * 	例1：@SaCheckPermission(value="user-add", orRole="admin")，
	 * 	代表本次请求只要具有 user-add权限 或 admin角色 其一即可通过校验。
	 * </p>
	 *
	 * <p>
	 * 	例2： orRole = {"admin", "manager", "staff"}，具有三个角色其一即可。 <br>
	 * 	例3： orRole = {"admin, manager, staff"}，必须三个角色同时具备。
	 * </p>
	 *
	 * @return /
	 */
	String[] orRole() default {};

}
