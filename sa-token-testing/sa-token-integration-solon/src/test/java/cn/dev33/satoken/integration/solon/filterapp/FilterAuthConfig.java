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
package cn.dev33.satoken.integration.solon.filterapp;

import cn.dev33.satoken.context.SaHolder;
import cn.dev33.satoken.exception.BackResultException;
import cn.dev33.satoken.exception.FirewallCheckException;
import cn.dev33.satoken.fun.strategy.SaCorsHandleFunction;
import cn.dev33.satoken.solon.integration.SaTokenFilter;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.strategy.hooks.SaFirewallCheckHook;
import org.noear.solon.annotation.Bean;
import org.noear.solon.annotation.Configuration;
import org.noear.solon.core.handle.Filter;

/**
 * 注册 SaTokenFilter：拦截业务接口，登录页和公开页放行。
 */
@Configuration
public class FilterAuthConfig {

	/** 注册鉴权 Filter：公开页放行，/back 走 BackResult 写回 */
	@Bean
	public Filter saTokenFilter() {
		return new SaTokenFilter()
				.addInclude("/**")
				.addExclude("/login", "/open", "/fwd")
				.setAuth(r -> {
					if ("/back".equals(SaHolder.getRequest().getRequestPath())) {
						throw new BackResultException("back-ok");
					}
					StpUtil.checkLogin();
				})
				.setError(e -> e.getMessage());
	}

	/** 指定路径抛 BackResultException，用来打 CORS Filter 的写回分支 */
	@Bean
	public SaCorsHandleFunction corsHandle() {
		return (req, res, sto) -> {
			if ("/cors-back".equals(req.getRequestPath())) {
				throw new BackResultException("cors-back");
			}
		};
	}

	/** 指定路径抛防火墙异常，用来打 Firewall Filter 的写回分支 */
	@Bean
	public SaFirewallCheckHook testFirewallHook() {
		return (req, res, ext) -> {
			String path = req.getRequestPath();
			if ("/fw-back".equals(path)) {
				throw new BackResultException("fw-back");
			}
			if ("/fw-fail".equals(path)) {
				throw new FirewallCheckException("fw-fail");
			}
		};
	}

}
