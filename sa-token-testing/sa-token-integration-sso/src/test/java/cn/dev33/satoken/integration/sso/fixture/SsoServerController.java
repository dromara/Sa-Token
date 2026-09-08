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

import cn.dev33.satoken.sso.processor.SaSsoServerProcessor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * SSO Server 端入口，和生产 Demo 一样走 dister。
 */
@RestController
public class SsoServerController {

	/** Server 所有 /sso/* 都交给处理器 */
	@RequestMapping("/sso/*")
	public Object ssoRequest() {
		return SaSsoServerProcessor.instance.dister();
	}

}
