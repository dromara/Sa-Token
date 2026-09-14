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
package cn.dev33.satoken.integration.loveqq.filterapp;

import cn.dev33.satoken.context.SaHolder;
import cn.dev33.satoken.loveqq.boot.context.SaReactorHolder;
import cn.dev33.satoken.stp.StpUtil;
import com.kfyty.loveqq.framework.web.core.annotation.GetMapping;
import com.kfyty.loveqq.framework.web.core.annotation.RestController;
import reactor.core.publisher.Mono;

/**
 * Filter 集成测用的登录、鉴权和转发接口。
 */
@RestController
public class FilterController {

	/** 登录并回写 token，走 SaReactorHolder.sync 覆盖响应式上下文 */
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

	/** 公开接口，过滤器 exclude */
	@GetMapping("/open")
	public String open() {
		return "open";
	}

	/** 走 SaRequest.forward，覆盖 LoveQQ 适配的转发/重定向 */
	@GetMapping("/fwd")
	public Object fwd() {
		return SaHolder.getRequest().forward("/open");
	}

}
