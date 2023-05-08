package cn.dev33.satoken.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 登录认证校验：只有登录之后才能进入该方法。
 *
 * <p> 可标注在方法、类上（效果等同于标注在此类的所有方法上）
 *
 * @author kong
 * @since <= 1.34.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD, ElementType.TYPE })
public @interface SaCheckLogin {

    /**
     * 多账号体系下所属的账号体系标识，非多账号体系无需关注此值
     *
     * @return /
     */
    String type() default "";

}
