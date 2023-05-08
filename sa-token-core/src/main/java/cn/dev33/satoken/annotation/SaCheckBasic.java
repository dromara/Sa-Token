package cn.dev33.satoken.annotation;

import cn.dev33.satoken.basic.SaBasicTemplate;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Http Basic 认证校验：只有通过 Http Basic 认证后才能进入该方法，否则抛出异常。
 *
 * <p> 可标注在方法、类上（效果等同于标注在此类的所有方法上）
 *
 * @author click33
 * @since <= 1.34.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD, ElementType.TYPE })
public @interface SaCheckBasic {

    /**
     * 领域 
     * @return /
     */
    String realm() default SaBasicTemplate.DEFAULT_REALM;

    /**
     * 需要校验的账号密码，格式形如 sa:123456 
     * @return /
     */
    String account() default "";

}
