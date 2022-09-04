package cn.dev33.satoken.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 校验当前用户是否可用
 *
 * <p> 可标注在函数、类上（效果等同于标注在此类的所有方法上）
 *
 * @author videomonster
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD, ElementType.TYPE })
public @interface SaCheckEnable {

    /**
     * 多账号体系下所属的账号体系标识
     * @return see note
     */
    String type() default "";
}
