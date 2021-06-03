package cn.dev33.satoken.solon.integration;

import cn.dev33.satoken.SaManager;
import org.noear.solon.core.aspect.Interceptor;
import org.noear.solon.core.aspect.Invocation;

/**
 * @author noear
 * @since 1.4
 */
public class SaTokenMethodInterceptor implements Interceptor {
    
	public static final SaTokenMethodInterceptor INSTANCE = new SaTokenMethodInterceptor();

    @Override
    public Object doIntercept(Invocation inv) throws Throwable {
        // 注解鉴权
        SaManager.getSaTokenAction().checkMethodAnnotation(inv.method().getMethod());

        // 执行原有逻辑
        return inv.invoke();
    }
}
