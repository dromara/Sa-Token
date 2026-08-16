/*
 * Copyright 2020-2099 sa-token.com
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
package cn.dev33.satoken.spring;

import cn.dev33.satoken.filter.SaFirewallCheckFilterForJakartaServlet;
import cn.dev33.satoken.filter.SaTokenContextFilterForJakartaServlet;
import cn.dev33.satoken.filter.SaTokenCorsFilterForJakartaServlet;
import cn.dev33.satoken.servlet.model.SaRequestForServlet;
import cn.dev33.satoken.servlet.model.SaResponseForServlet;
import cn.dev33.satoken.servlet.model.SaStorageForServlet;
import cn.dev33.satoken.spring.pathmatch.SaPathPatternParserUtil;
import cn.dev33.satoken.strategy.SaStrategy;
import cn.dev33.satoken.util.SaTokenConsts;
import jakarta.servlet.DispatcherType;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;

import java.util.EnumSet;

/**
 * 注册 Sa-Token 框架所需要的 Bean
 * 
 * @author click33
 * @since 1.34.0
 */
public class SaTokenContextRegister {

	public SaTokenContextRegister() {
		// 重写路由匹配算法
		SaStrategy.instance.routeMatcher = (pattern, path) -> {
			return SaPathPatternParserUtil.match(pattern, path);
		};
		// 重写 SaRequest 创建策略
		SaStrategy.instance.createSaRequest = source -> new SaRequestForServlet((HttpServletRequest) source);
		// 重写 SaResponse 创建策略
		SaStrategy.instance.createSaResponse = source -> new SaResponseForServlet((HttpServletResponse) source);
		// 重写 SaStorage 创建策略
		SaStrategy.instance.createSaStorage = source -> new SaStorageForServlet((HttpServletRequest) source);
	}

	/**
	 * 上下文过滤器
	 *
	 * @return /
	 */
	@Bean
	public FilterRegistrationBean<SaTokenContextFilterForJakartaServlet> saTokenContextFilterForServlet() {
		FilterRegistrationBean<SaTokenContextFilterForJakartaServlet> bean = new FilterRegistrationBean<>(new SaTokenContextFilterForJakartaServlet());
		bean.addUrlPatterns("/*");
		bean.setOrder(SaTokenConsts.SA_TOKEN_CONTEXT_FILTER_ORDER);
		bean.setAsyncSupported(true);
		bean.setDispatcherTypes(EnumSet.of(DispatcherType.ASYNC, DispatcherType.REQUEST));
		return bean;
	}

	/**
	 * CORS 跨域策略过滤器
	 *
	 * @return /
	 */
	@Bean
	public SaTokenCorsFilterForJakartaServlet saTokenCorsFilterForJakartaServlet() {
		return new SaTokenCorsFilterForJakartaServlet();
	}

	/**
	 * 防火墙过滤器
	 *
	 * @return /
	 */
	@Bean
	public SaFirewallCheckFilterForJakartaServlet saFirewallCheckFilterForJakartaServlet() {
		return new SaFirewallCheckFilterForJakartaServlet();
	}

}
