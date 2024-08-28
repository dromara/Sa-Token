package com.pj.satoken.merge_annotation;

import cn.dev33.satoken.annotation.SaCheckSafe;
import cn.dev33.satoken.util.SaTokenConsts;
import com.pj.satoken.StpUserUtil;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 二级认证校验(User版)：客户端必须完成二级认证之后，才能进入该方法，否则将被抛出异常。
 *
 * <p> 可标注在方法、类上（效果等同于标注在此类的所有方法上）。
 *
 * @author click33
 */
@SaCheckSafe(type = StpUserUtil.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD, ElementType.TYPE })
public @interface SaUserCheckSafe {

	/**
	 * 要校验的服务
	 *
	 * @return /
	 */
	String value() default SaTokenConsts.DEFAULT_SAFE_AUTH_SERVICE;

}
