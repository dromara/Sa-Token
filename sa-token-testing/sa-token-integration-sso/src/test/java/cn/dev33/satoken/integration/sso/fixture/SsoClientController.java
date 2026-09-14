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
package cn.dev33.satoken.integration.sso.fixture;

import cn.dev33.satoken.sso.processor.SaSsoClientProcessor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * SSO Client 端入口，路由加了 /sso-client 前缀以免和 Server 撞车。
 */
@RestController
public class SsoClientController {

	/** Client 所有 /sso-client/sso/* 都交给处理器 */
	@RequestMapping("/sso-client/sso/*")
	public Object ssoRequest() {
		return SaSsoClientProcessor.instance.dister();
	}

}
