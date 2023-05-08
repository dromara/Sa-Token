package cn.dev33.satoken.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 角色认证校验：必须具有指定角色标识才能进入该方法。
 *
 * <p> 可标注在方法、类上（效果等同于标注在此类的所有方法上）
 *
 * @author click33
 * @since <= 1.34.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD,ElementType.TYPE})
public @interface SaCheckRole {

	/**
	 * 多账号体系下所属的账号体系标识，非多账号体系无需关注此值
	 *
	 * @return /
	 */
	String type() default "";

	/**
	 * 需要校验的角色标识 [ 数组 ]
	 *
	 * @return /
	 */
	String [] value() default {};

	/**
	 * 验证模式：AND | OR，默认AND
	 *
	 * @return /
	 */
	SaMode mode() default SaMode.AND;

}
