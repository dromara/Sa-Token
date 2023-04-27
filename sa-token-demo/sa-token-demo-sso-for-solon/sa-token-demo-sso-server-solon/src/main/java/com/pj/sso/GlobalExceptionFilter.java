package com.pj.sso;


import cn.dev33.satoken.util.SaResult;
import org.noear.solon.annotation.Component;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.Filter;
import org.noear.solon.core.handle.FilterChain;

/**
 * 全局异常处理 
 * @author kong
 *
 */
@Component
public class GlobalExceptionFilter implements Filter {

	@Override
	public void doFilter(Context ctx, FilterChain chain) throws Throwable {
		try {
			chain.doFilter(ctx);
		} catch (Exception e) {
			e.printStackTrace();

			ctx.render(SaResult.error(e.getMessage()));
		}
	}
}
