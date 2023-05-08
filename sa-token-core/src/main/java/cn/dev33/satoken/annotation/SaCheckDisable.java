package cn.dev33.satoken.annotation;

import cn.dev33.satoken.util.SaTokenConsts;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 服务禁用校验：判断当前账号是否被禁用了指定服务，如果被禁用，会抛出异常，没有被禁用才能进入方法。
 *
 * <p> 可标注在方法、类上（效果等同于标注在此类的所有方法上）
 *
 * @author videomonster
 * @since <= 1.34.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD, ElementType.TYPE })
public @interface SaCheckDisable {

    /**
     * 多账号体系下所属的账号体系标识，非多账号体系无需关注此值
     *
     * @return /
     */
    String type() default "";
    
    /**
     * 服务标识 （具体你要校验是否禁用的服务名称）
     * 
     * @return /
     */
    String[] value() default { SaTokenConsts.DEFAULT_DISABLE_SERVICE };

    /**
     * 封禁等级（如果当前账号的被封禁等级 ≥ 此值，请求就无法进入方法）
     * 
     * @return / 
     */
    int level() default SaTokenConsts.DEFAULT_DISABLE_LEVEL;
    
}
