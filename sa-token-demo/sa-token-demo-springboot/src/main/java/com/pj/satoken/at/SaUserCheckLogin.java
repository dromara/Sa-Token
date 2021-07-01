package com.pj.satoken.at;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import cn.dev33.satoken.annotation.SaCheckLogin;

/**
 * 登录认证(User版)：只有登录之后才能进入该方法 
 * <p> 可标注在函数、类上（效果等同于标注在此类的所有方法上） 
 * @author kong
 *
 */
@SaCheckLogin(type = StpUserUtil.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD, ElementType.TYPE})
public @interface SaUserCheckLogin {
	
}
