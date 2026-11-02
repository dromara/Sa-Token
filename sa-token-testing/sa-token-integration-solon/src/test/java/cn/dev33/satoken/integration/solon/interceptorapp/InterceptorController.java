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
package cn.dev33.satoken.integration.solon.interceptorapp;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaIgnore;
import cn.dev33.satoken.stp.StpUtil;
import org.noear.solon.annotation.Controller;
import org.noear.solon.annotation.Mapping;
import org.noear.solon.core.handle.Context;

/**
 * Interceptor 集成测用的登录和鉴权接口。
 */
@Controller
public class InterceptorController {

	/** 登录并回写 token */
	@Mapping("/login")
	public void login(Context ctx) {
		StpUtil.login(10001);
		ctx.output(StpUtil.getTokenValue());
	}

	/** 已登录才能看的业务接口 */
	@Mapping("/user")
	public void user(Context ctx) {
		ctx.output(String.valueOf(StpUtil.getLoginId()));
	}

	/** 公开接口 */
	@Mapping("/open")
	public void open(Context ctx) {
		ctx.output("open");
	}

	/** 方法上的登录注解 */
	@SaCheckLogin
	@Mapping("/anno")
	public void anno(Context ctx) {
		ctx.output("anno-ok");
	}

	/** @SaIgnore 应该跳过注解鉴权 */
	@SaIgnore
	@Mapping("/ignored")
	public void ignored(Context ctx) {
		ctx.output("ignored");
	}

}
