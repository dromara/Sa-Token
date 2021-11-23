package cn.dev33.satoken.jfinal;

import cn.dev33.satoken.strategy.SaStrategy;
import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;

/**
 * 注解式鉴权 - 拦截器
 */
public class SaAnnotationInterceptor implements Interceptor {
    @Override
    public void intercept(Invocation invocation) {
        SaStrategy.me.checkMethodAnnotation.accept((invocation.getMethod()));
        invocation.invoke();
    }
}
