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

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaIgnore;
import cn.dev33.satoken.loveqq.boot.context.SaReactorHolder;
import cn.dev33.satoken.stp.StpUtil;
import com.kfyty.loveqq.framework.web.core.annotation.GetMapping;
import com.kfyty.loveqq.framework.web.core.annotation.RestController;
import reactor.core.publisher.Mono;

/**
 * Interceptor 集成测用的登录和鉴权接口。
 */
@RestController
public class InterceptorController {

	/** 登录并回写 token */
	@GetMapping("/login")
	public Mono<String> login() {
		return SaReactorHolder.sync(() -> {
			StpUtil.login(10001);
			return StpUtil.getTokenValue();
		});
	}

	/** 已登录才能看的业务接口 */
	@GetMapping("/user")
	public Mono<String> user() {
		return SaReactorHolder.sync(() -> String.valueOf(StpUtil.getLoginId()));
	}

	/** 公开接口 */
	@GetMapping("/open")
	public String open() {
		return "open";
	}

	/** 方法上的登录注解 */
	@SaCheckLogin
	@GetMapping("/anno")
	public Mono<String> anno() {
		return Mono.just("anno-ok");
	}

	/** @SaIgnore 应该跳过注解鉴权 */
	@SaIgnore
	@GetMapping("/ignored")
	public String ignored() {
		return "ignored";
	}

	/** 给拦截器一个真实路由，用来打 BackResultException 写回 */
	@GetMapping("/back")
	public String back() {
		return "should-not-reach";
	}

}
