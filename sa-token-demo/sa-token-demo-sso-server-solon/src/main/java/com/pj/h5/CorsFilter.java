package com.pj.h5;

import org.noear.solon.annotation.Component;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.Filter;
import org.noear.solon.core.handle.FilterChain;


/**
 * 跨域过滤器 
 * @author kong 
 */
@Component(index = -200)
public class CorsFilter implements Filter {

	static final String OPTIONS = "OPTIONS";

	@Override
	public void doFilter(Context ctx, FilterChain chain) throws Throwable {
		// 允许指定域访问跨域资源
		ctx.headerSet("Access-Control-Allow-Origin", "*");
		// 允许所有请求方式
		ctx.headerSet("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE");
		// 有效时间
		ctx.headerSet("Access-Control-Max-Age", "3600");
		// 允许的header参数
		ctx.headerSet("Access-Control-Allow-Headers", "x-requested-with,satoken");

		// 如果是预检请求，直接返回
		if (OPTIONS.equals(ctx.method())) {
			System.out.println("=======================浏览器发来了OPTIONS预检请求==========");
			ctx.output("");
			return;
		}

		// System.out.println("*********************************过滤器被使用**************************");
		chain.doFilter(ctx);
	}
}
