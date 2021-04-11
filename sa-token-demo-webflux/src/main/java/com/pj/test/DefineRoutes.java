package com.pj.test;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import com.pj.util.AjaxJson;

import cn.dev33.satoken.stp.StpUtil;

@Configuration
public class DefineRoutes {

	/**
	 * 函数式编程，初始化路由表 
	 * @return 路由表 
	 */
	@Bean
	public RouterFunction<ServerResponse> getRoutes() {
		return RouterFunctions.route(RequestPredicates.GET("/fun"), req -> {
			// 测试打印 
			System.out.println("是否登录：" + StpUtil.isLogin());
			
			// 返回结果 
			AjaxJson aj = AjaxJson.getSuccessData(StpUtil.getTokenInfo());
			return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON_UTF8).syncBody(aj);
		});	
	}
	
}
