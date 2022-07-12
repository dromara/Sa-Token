package cn.dev33.satoken.solon.integration;

import org.noear.solon.core.aspect.Interceptor;
import org.noear.solon.core.aspect.Invocation;

import cn.dev33.satoken.strategy.SaStrategy;

/**
 * @author noear
 * @since 1.4
 */
public class SaTokenAnnotationInterceptor implements Interceptor {
    
	public static final SaTokenAnnotationInterceptor INSTANCE = new SaTokenAnnotationInterceptor();

    @Override
    public Object doIntercept(Invocation inv) throws Throwable {
        // 注解鉴权 
    	SaStrategy.me.checkMethodAnnotation.accept(inv.method().getMethod());

        // 执行原有逻辑
        return inv.invoke();
    }
}
