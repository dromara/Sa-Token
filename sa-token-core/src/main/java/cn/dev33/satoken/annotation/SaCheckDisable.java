package cn.dev33.satoken.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import cn.dev33.satoken.util.SaTokenConsts;

/**
 * 服务禁用校验：在没有被禁用指定服务的情况下才可以进入方法 
 *
 * <p> 可标注在函数、类上（效果等同于标注在此类的所有方法上）
 *
 * @author videomonster
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD, ElementType.TYPE })
public @interface SaCheckDisable {

    /**
     * 多账号体系下所属的账号体系标识
     * @return see note
     */
    String type() default "";
    
    /**
     * 服务标识 （要校验是否禁用的服务名称）
     * 
     * @return see note
     */
    String[] value() default { SaTokenConsts.DEFAULT_DISABLE_SERVICE };

    /**
     * 封禁等级（只有 封禁等级 ≥ 此值 才会抛出异常）
     * 
     * @return / 
     */
    int level() default SaTokenConsts.DEFAULT_DISABLE_LEVEL;
    
}
