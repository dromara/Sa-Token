package cn.dev33.satoken.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 登录校验：标注在一个方法上，当前会话必须已经登录才能进入该方法 
 * <p> 可标注在类上，其效果等同于标注在此类的所有方法上 
 * @author kong
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD, ElementType.TYPE })
public @interface SaCheckLogin {

    /**
     * 多账号体系下所属的账号体系标识 
     * @return see note 
     */
    String key() default "";

}
