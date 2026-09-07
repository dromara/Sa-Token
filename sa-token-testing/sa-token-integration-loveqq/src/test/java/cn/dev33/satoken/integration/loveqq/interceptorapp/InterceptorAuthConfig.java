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
package cn.dev33.satoken.integration.loveqq.interceptorapp;

import cn.dev33.satoken.context.SaHolder;
import cn.dev33.satoken.exception.BackResultException;
import cn.dev33.satoken.loveqq.boot.interceptor.SaInterceptor;
import cn.dev33.satoken.stp.StpUtil;
import com.kfyty.loveqq.framework.core.autoconfig.annotation.Bean;
import com.kfyty.loveqq.framework.core.autoconfig.annotation.Configuration;

/**
 * 注册 SaInterceptor：拦截业务接口，登录页放行。
 */
@Configuration
public class InterceptorAuthConfig {

	/** 注册鉴权拦截器：公开页放行，/back 走 BackResult 写回 */
	@Bean
	public SaInterceptor saInterceptor() {
		return new SaInterceptor(handler -> {
			if ("/back".equals(SaHolder.getRequest().getRequestPath())) {
				throw new BackResultException("back-ok");
			}
			StpUtil.checkLogin();
		}) {
			@Override
			public String[] excludes() {
				return new String[]{"/login", "/open"};
			}
		};
	}

}
