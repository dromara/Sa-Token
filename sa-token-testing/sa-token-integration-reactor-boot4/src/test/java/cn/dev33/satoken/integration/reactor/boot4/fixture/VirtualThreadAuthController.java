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
package cn.dev33.satoken.integration.reactor.boot4.fixture;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.reactor.context.SaReactorHolder;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

/**
 * 虚拟线程上下文测试端点：登录走文档规范的 sync 包裹；注解鉴权端点使用阻塞式写法，
 * 用于复现注解检查线程与 ThreadLocal 上下文线程不一致的问题。
 */
@RestController
@RequestMapping("/vt/")
public class VirtualThreadAuthController {

	/** 按文档规范包一层 sync 完成登录，返回 token 供后续请求携带 */
	@GetMapping("/login")
	public Mono<SaResult> login() {
		return SaReactorHolder.sync(() -> {
			StpUtil.login(10001);
			return SaResult.ok().set("token", StpUtil.getTokenValue());
		});
	}

	/** 阻塞式端点 + 注解鉴权：注解检查与业务代码都直接调用 Sa-Token API */
	@SaCheckLogin
	@GetMapping("/annotated")
	public SaResult annotated() {
		return SaResult.ok()
				.set("loginId", StpUtil.getLoginIdAsString())
				.set("thread", Thread.currentThread().getName());
	}

}
