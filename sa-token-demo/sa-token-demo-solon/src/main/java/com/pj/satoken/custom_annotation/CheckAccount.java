package com.pj.satoken.custom_annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 账号校验：在标注一个方法上时，要求前端必须提交相应的账号密码参数才能访问方法。
 *
 * @author click33
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD, ElementType.TYPE})
public @interface CheckAccount {

    /**
     * 需要校验的账号
     *
     * @return /
     */
    String name();

    /**
     * 需要校验的密码
     *
     * @return /
     */
    String pwd();


}
