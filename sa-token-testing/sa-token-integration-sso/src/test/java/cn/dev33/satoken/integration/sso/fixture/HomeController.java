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
import cn.dev33.satoken.sso.processor.SaSsoServerProcessor;
import cn.dev33.satoken.stp.StpLogic;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 两端首页：用来看各自 StpLogic 登没登录。
 */
@RestController
public class HomeController {

	/** Server 首页 */
	@RequestMapping("/server/home")
	public String serverHome() {
		StpLogic logic = SaSsoServerProcessor.instance.ssoServerTemplate.getStpLogicOrGlobal();
		return "server-login=" + logic.isLogin() + ",id=" + logic.getLoginIdDefaultNull();
	}

	/** Client 首页 */
	@RequestMapping("/client/home")
	public String clientHome() {
		StpLogic logic = SaSsoClientProcessor.instance.ssoClientTemplate.getStpLogicOrGlobal();
		return "client-login=" + logic.isLogin() + ",id=" + logic.getLoginIdDefaultNull();
	}

}
