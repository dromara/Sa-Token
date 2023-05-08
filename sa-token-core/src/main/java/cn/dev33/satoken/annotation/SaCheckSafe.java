package cn.dev33.satoken.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import cn.dev33.satoken.util.SaTokenConsts;

/**
 * 二级认证校验：客户端必须完成二级认证之后，才能进入该方法，否则将被抛出异常。
 * 
 * <p> 可标注在方法、类上（效果等同于标注在此类的所有方法上）。
 *
 * @author click33
 * @since <= 1.34.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD, ElementType.TYPE })
public @interface SaCheckSafe {

	/**
	 * 多账号体系下所属的账号体系标识，非多账号体系无需关注此值
	 *
	 * @return /
	 */
	String type() default "";

	/**
	 * 要校验的服务
	 *
	 * @return /
	 */
	String value() default SaTokenConsts.DEFAULT_SAFE_AUTH_SERVICE;
	
}
