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
package cn.dev33.satoken.integration.dubbo3.fixture;

import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaResult;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Consumer HTTP：先有 Servlet 上下文，再走 Dubbo，Filter 才能下传 token。
 */
@RestController
public class ConsumerController {

	@DubboReference(check = false, injvm = false, url = "dubbo://127.0.0.1:${test.dubbo.port}")
	private DemoService demoService;

	/** Consumer 登录后再 RPC，看 Provider 能不能接到会话 */
	@RequestMapping("/consumer-login-then-rpc")
	public SaResult consumerLoginThenRpc() {
		StpUtil.login(10001);
		return SaResult.data(demoService.snapshot("after-consumer-login"));
	}

	/** Provider 登录后看 Consumer 能不能接到回传 token */
	@RequestMapping("/provider-login-then-check")
	public SaResult providerLoginThenCheck() {
		demoService.doLogin(10002);
		return SaResult.ok()
				.set("consumerLogin", StpUtil.isLogin())
				.set("consumerLoginId", StpUtil.getLoginIdDefaultNull())
				.set("consumerToken", StpUtil.getTokenValue());
	}

	/** 没登录直接 RPC，Provider 也应该是没登录 */
	@RequestMapping("/rpc-without-login")
	public SaResult rpcWithoutLogin() {
		return SaResult.data(demoService.snapshot("no-login"));
	}

	/** Consumer 登录后 RPC，自己这边会话还在 */
	@RequestMapping("/consumer-still-login")
	public SaResult consumerStillLogin() {
		StpUtil.login(10003);
		demoService.snapshot("provider-side");
		return SaResult.ok()
				.set("consumerLogin", StpUtil.isLogin())
				.set("consumerLoginId", StpUtil.getLoginIdDefaultNull());
	}

}
