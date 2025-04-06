package com.pj.test;

import cn.dev33.satoken.reactor.context.SaReactorSyncHolder;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaResult;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class DefineRoutes {

	/**
	 * 函数式编程，初始化路由表 
	 * @return 路由表 
	 */
	@SuppressWarnings("deprecation")
	@Bean
	public RouterFunction<ServerResponse> getRoutes() {
		return RouterFunctions.route(RequestPredicates.GET("/fun"), req -> {
			return SaReactorSyncHolder.setContext(req.exchange(), () -> {
				System.out.println("是否登录：" + StpUtil.isLogin());
				SaResult res = SaResult.data(StpUtil.getTokenInfo());
				return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON_UTF8).syncBody(res);
			});
		});	
	}
	
}
