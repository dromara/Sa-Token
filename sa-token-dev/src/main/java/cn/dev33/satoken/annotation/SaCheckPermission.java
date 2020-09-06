package cn.dev33.satoken.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 标注一个路由方法，当前会话必须具有指定权限才可以通过 
 * @author kong
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD,ElementType.TYPE})
public @interface SaCheckPermission {

	/**
	 * 权限码数组 ，String类型 
	 * @return .
	 */
	String [] value() default {};

	/**
	 * 权限码数组 ，int类型 
	 * @return .
	 */
	int [] valueInt() default {};

	/**
	 * 权限码数组 ，long类型 
	 * @return .
	 */
	long [] valueLong() default {};
	
	/**
	 * 是否属于and型验证 ，true=必须全部具有，false=只要具有一个就可以通过 
	 * @return .
	 */
	boolean isAnd() default true;
	
}
