package cn.dev33.satoken.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 标注一个路由方法，当前会话必须具有指定角色标识才可以通过 
 * <p> 可标注在类上，其效果等同于标注在此类的所有方法上 
 * @author kong
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD,ElementType.TYPE})
public @interface SaCheckRole {

	/**
	 * 需要验证的角色标识
	 * @return 需要验证的权限码
	 */
	String [] value() default {};

	/**
	 * 指定验证模式是AND还是OR，默认AND
	 * @return 验证模式
	 */
	SaMode mode() default SaMode.AND;
	
}
