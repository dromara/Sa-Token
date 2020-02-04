package cn.dev33.satoken.spring;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * 将此注解加到springboot启动类上，即可完成sa-token与springboot的集成  
 */
@Documented
@Target({java.lang.annotation.ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Configuration
@Import({SpringSaToken.class})
public @interface SaTokenSetup {
	
}
