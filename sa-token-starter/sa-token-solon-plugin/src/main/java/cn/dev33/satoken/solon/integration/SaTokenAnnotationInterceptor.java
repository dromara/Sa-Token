package cn.dev33.satoken.solon.integration;

import cn.dev33.satoken.annotation.SaIgnore;
import cn.dev33.satoken.strategy.SaStrategy;
import org.noear.solon.core.aspect.Interceptor;
import org.noear.solon.core.aspect.Invocation;
import org.noear.solon.core.handle.Context;

import java.lang.reflect.Method;

/**
 * @author noear
 * @since 1.4
 * @deprecated 1.10，改用 SaTokenPathInterceptor
 */
@Deprecated
public class SaTokenAnnotationInterceptor implements Interceptor {

    public static final SaTokenAnnotationInterceptor INSTANCE = new SaTokenAnnotationInterceptor();

    @Override
    public Object doIntercept(Invocation inv) throws Throwable {
        // 如果此 Method 或其所属 Class 标注了 @SaIgnore，则忽略掉鉴权
        Context ctx = Context.current();

        if (ctx != null && "1".equals(ctx.attr("_SaTokenPathInterceptor"))) {
            // 执行原有逻辑
            return inv.invoke();
        } else {

            Method method = inv.method().getMethod();
            if (SaStrategy.me.isAnnotationPresent.apply(method, SaIgnore.class) == false) {
                SaStrategy.me.checkMethodAnnotation.accept(method);
            }

            // 执行原有逻辑
            return inv.invoke();
        }
    }
}