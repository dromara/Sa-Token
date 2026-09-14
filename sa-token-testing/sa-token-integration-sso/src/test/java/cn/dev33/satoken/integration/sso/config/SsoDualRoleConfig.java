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
package cn.dev33.satoken.integration.sso.config;

import cn.dev33.satoken.config.SaTokenConfig;
import cn.dev33.satoken.sso.config.SaSsoClientConfig;
import cn.dev33.satoken.sso.config.SaSsoClientModel;
import cn.dev33.satoken.sso.config.SaSsoServerConfig;
import cn.dev33.satoken.sso.name.ApiName;
import cn.dev33.satoken.sso.processor.SaSsoClientProcessor;
import cn.dev33.satoken.sso.processor.SaSsoServerProcessor;
import cn.dev33.satoken.sso.template.SaSsoClientTemplate;
import cn.dev33.satoken.sso.template.SaSsoServerTemplate;
import cn.dev33.satoken.stp.StpLogic;
import cn.dev33.satoken.util.SaResult;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.web.context.WebServerApplicationContext;
import org.springframework.context.annotation.Configuration;

/**
 * 同一进程双角色：Server / Client 各用一套 StpLogic，并把端口写进 allowUrl / serverUrl。
 */
@Configuration
public class SsoDualRoleConfig implements ApplicationRunner {

	private final WebServerApplicationContext webServerApplicationContext;
	private final SaSsoServerTemplate ssoServerTemplate;
	private final SaSsoClientTemplate ssoClientTemplate;
	private final SaSsoServerConfig ssoServerConfig;
	private final SaSsoClientConfig ssoClientConfig;

	/** 把模板、配置和内嵌端口接进来 */
	public SsoDualRoleConfig(WebServerApplicationContext webServerApplicationContext,
			SaSsoServerTemplate ssoServerTemplate, SaSsoClientTemplate ssoClientTemplate,
			SaSsoServerConfig ssoServerConfig, SaSsoClientConfig ssoClientConfig) {
		this.webServerApplicationContext = webServerApplicationContext;
		this.ssoServerTemplate = ssoServerTemplate;
		this.ssoClientTemplate = ssoClientTemplate;
		this.ssoServerConfig = ssoServerConfig;
		this.ssoClientConfig = ssoClientConfig;
	}

	/** 端口出来后再接线：两套会话、真实 HTTP 推送、Client 路由前缀 */
	@Override
	public void run(ApplicationArguments args) {
		int port = webServerApplicationContext.getWebServer().getPort();
		String origin = "http://127.0.0.1:" + port;

		StpLogic serverLogic = new StpLogic("sso-server");
		serverLogic.setConfig(new SaTokenConfig().setTokenName("satoken-server").setIsPrint(false));
		ssoServerTemplate.setStpLogic(serverLogic);

		StpLogic clientLogic = new StpLogic("sso-client");
		clientLogic.setConfig(new SaTokenConfig().setTokenName("satoken-client").setIsPrint(false));
		ssoClientTemplate.setStpLogic(clientLogic);
		ssoClientTemplate.apiName = new ApiName().addPrefix("/sso-client");

		ssoServerTemplate.strategy.asyncRun = fun -> fun.run();
		ssoServerTemplate.strategy.sendRequest = cn.dev33.satoken.integration.sso.support.SsoHttp::plainGet;
		ssoClientTemplate.strategy.sendRequest = cn.dev33.satoken.integration.sso.support.SsoHttp::plainGet;
		ssoServerTemplate.strategy.notLoginView = () -> "SSO-LOGIN-VIEW";
		ssoServerTemplate.strategy.doLoginHandle = (name, pwd) -> {
			if ("sa".equals(name) && "123456".equals(pwd)) {
				serverLogic.login(10001);
				return SaResult.ok("登录成功").setData(serverLogic.getTokenValue());
			}
			return SaResult.error("登录失败");
		};

		ssoClientConfig.setServerUrl(origin);
		ssoClientConfig.setCurrSsoLogin(origin + "/sso-client/sso/login");
		ssoClientConfig.setCurrSsoLogoutCall(origin + "/sso-client/sso/logoutCall");

		SaSsoClientModel client = ssoServerConfig.getClients().get("sso-client3");
		if (client == null) {
			client = new SaSsoClientModel().setClient("sso-client3").setIsPush(true).setIsSlo(true)
					.setSecretKey("sso-it-secret");
			ssoServerConfig.addClient(client);
		}
		client.setAllowUrl(origin + "/*");
		client.setServerUrl(origin + "/sso-client");
		client.setPushUrl("/sso/pushC");

		SaSsoServerProcessor.instance.ssoServerTemplate = ssoServerTemplate;
		SaSsoClientProcessor.instance.ssoClientTemplate = ssoClientTemplate;
	}

}
