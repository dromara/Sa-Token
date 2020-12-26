package cn.dev33.satoken.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 标注一个路由方法，当前会话必须具有指定权限才可以通过 
 * <p> 可标注在类上，其效果等同于标注在此类的所有方法上 
 * @author kong
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD,ElementType.TYPE})
public @interface SaCheckPermission {

	/**
	 * 需要验证的权限码
	 * @return 需要验证的权限码
	 */
	String [] value() default {};

	/**
	 * 需要验证的权限码 (int类型)
	 * @return 需要验证的权限码 (int类型)
	 */
	int [] valueInt() default {};

	/**
	 * 需要验证的权限码 (long类型) 
	 * @return 需要验证的权限码 (long类型) 
	 */
	long [] valueLong() default {};
	
	/**
	 * 是否属于and型验证，true=必须全部具有，false=只要具有一个就可以通过 
	 * @return 是否属于and型验证
	 */
	boolean isAnd() default true;
	
}
