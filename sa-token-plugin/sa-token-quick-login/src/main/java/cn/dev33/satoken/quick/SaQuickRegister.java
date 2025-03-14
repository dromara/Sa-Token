/*
 * Copyright 2020-2099 sa-token.cc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cn.dev33.satoken.quick;

import cn.dev33.satoken.context.SaHolder;
import cn.dev33.satoken.filter.SaServletFilter;
import cn.dev33.satoken.quick.config.SaQuickConfig;
import cn.dev33.satoken.router.SaRouter;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaFoxUtil;
import cn.dev33.satoken.util.SaTokenConsts;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

/**
 * Quick Login 相关 Bean 注册
 * 
 * @author click33
 * @since 1.30.0
 */
@Configuration
public class SaQuickRegister {

	/**
	 * 使用一个比较短的前缀，尽量提高 cmd 命令台启动时指定参数的便利性
	 */
	public static final String CONFIG_VERSION = "sa";

	/**
	 * 注册 Quick-Login 配置
	 * 
	 * @return see note
	 */
	@Bean
	@ConfigurationProperties(prefix = CONFIG_VERSION)
	public SaQuickConfig getSaQuickConfig() {
		return new SaQuickConfig();
	}


	/**
	 * 注册 Sa-Token 全局过滤器
	 *
	 * @return /
	 */
	@Bean
	@Order(SaTokenConsts.ASSEMBLY_ORDER - 1)
	SaServletFilter getSaServletFilterForQuickLogin() {
		return new SaServletFilter()

				// 拦截路由
				.addInclude("/**")

				// 排除掉登录相关接口，不需要鉴权的
				.addExclude("/favicon.ico", "/saLogin", "/doLogin", "/sa-res/**")

				// 认证函数: 每次请求执行
				.setAuth(obj -> {
					SaRouter
							.match(SaFoxUtil.convertStringToList(SaQuickManager.getConfig().getInclude()))
							.notMatch(SaFoxUtil.convertStringToList(SaQuickManager.getConfig().getExclude()))
							.check(r -> {
								// 未登录时直接转发到login.html页面
								if (SaQuickManager.getConfig().getAuth() && ! StpUtil.isLogin()) {
									SaHolder.getRequest().forward("/saLogin");
									SaRouter.back();
								}
							});
				}).

				// 异常处理函数：每次认证函数发生异常时执行此函数
				setError(e -> {
					return e.getMessage();
				});
	}

}
