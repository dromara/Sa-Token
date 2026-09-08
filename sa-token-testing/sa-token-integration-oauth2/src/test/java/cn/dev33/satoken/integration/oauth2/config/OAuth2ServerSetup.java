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
package cn.dev33.satoken.integration.oauth2.config;

import cn.dev33.satoken.oauth2.SaOAuth2Manager;
import cn.dev33.satoken.oauth2.config.SaOAuth2ServerConfig;
import cn.dev33.satoken.oauth2.consts.GrantType;
import cn.dev33.satoken.oauth2.data.model.loader.SaClientModel;
import cn.dev33.satoken.oauth2.strategy.SaOAuth2Strategy;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaResult;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Configuration;

/**
 * 挂默认 client、登录函数和视图，贴近 Demo 的真实接线。
 */
@Configuration
public class OAuth2ServerSetup implements ApplicationRunner {

	/** 端口起来后再装 client 和策略 */
	@Override
	public void run(ApplicationArguments args) {
		SaOAuth2Manager.setStpLogic(StpUtil.stpLogic);
		SaOAuth2ServerConfig cfg = SaOAuth2Manager.getServerConfig();

		SaClientModel auto = new SaClientModel()
				.setClientId("1001")
				.setClientSecret("aaaa-bbbb-cccc-dddd-eeee")
				.addAllowRedirectUris("http://oauth-client.com/callback", "http://oauth-client.com/*")
				.addContractScopes("openid", "unionid", "userid", "oidc", "userinfo")
				.addAllowGrantTypes(
						GrantType.authorization_code,
						GrantType.implicit,
						GrantType.refresh_token,
						GrantType.password,
						GrantType.client_credentials)
				.setIsAutoConfirm(true);
		cfg.addClient(auto);

		SaClientModel confirm = new SaClientModel()
				.setClientId("1002")
				.setClientSecret("ffff-gggg-hhhh-iiii-jjjj")
				.addAllowRedirectUris("http://oauth-client.com/callback")
				.addContractScopes("userinfo")
				.addAllowGrantTypes(GrantType.authorization_code, GrantType.implicit)
				.setIsAutoConfirm(false);
		cfg.addClient(confirm);

		SaClientModel confirmOnly = new SaClientModel()
				.setClientId("1003")
				.setClientSecret("kkkk-llll-mmmm-nnnn-oooo")
				.addAllowRedirectUris("http://oauth-client.com/callback")
				.addContractScopes("userinfo")
				.addAllowGrantTypes(GrantType.authorization_code)
				.setIsAutoConfirm(false);
		cfg.addClient(confirmOnly);

		SaOAuth2Strategy.instance.notLoginView = () -> "OAUTH2-LOGIN-VIEW";
		SaOAuth2Strategy.instance.confirmView = (clientId, scopes) -> "OAUTH2-CONFIRM-VIEW";
		SaOAuth2Strategy.instance.doLoginHandle = (name, pwd) -> {
			if ("sa".equals(name) && "123456".equals(pwd)) {
				StpUtil.login(10001);
				return SaResult.ok("登录成功");
			}
			return SaResult.error("登录失败");
		};
	}

}
