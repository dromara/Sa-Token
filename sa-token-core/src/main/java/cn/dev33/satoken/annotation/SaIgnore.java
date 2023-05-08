package cn.dev33.satoken.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 忽略认证：表示被修饰的方法或类无需进行注解认证和路由拦截认证。
 * 
 * <h3> 请注意：此注解的忽略效果只针对 SaInterceptor拦截器 和 AOP注解鉴权 生效，对自定义拦截器与过滤器不生效。 </h3>
 * 
 * @author click33
 * @since <= 1.34.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD, ElementType.TYPE })
public @interface SaIgnore {

}
