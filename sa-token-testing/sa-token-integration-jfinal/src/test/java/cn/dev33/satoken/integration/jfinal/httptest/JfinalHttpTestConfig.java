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
package cn.dev33.satoken.integration.jfinal.httptest;

import cn.dev33.satoken.SaManager;
import cn.dev33.satoken.config.SaTokenConfig;
import cn.dev33.satoken.jfinal.SaAnnotationInterceptor;
import cn.dev33.satoken.jfinal.SaTokenActionHandler;
import cn.dev33.satoken.jfinal.SaTokenContextForJfinal;
import com.jfinal.config.Constants;
import com.jfinal.config.Handlers;
import com.jfinal.config.Interceptors;
import com.jfinal.config.JFinalConfig;
import com.jfinal.config.Plugins;
import com.jfinal.config.Routes;
import com.jfinal.template.Engine;

/**
 * 集成测用的 JFinal 配置：挂上下文、注解拦截器、ActionHandler，不起 Redis。
 */
public class JfinalHttpTestConfig extends JFinalConfig {

	/** 启动前把 JFinal 版上下文和角色数据挂到 SaManager */
	public JfinalHttpTestConfig() {
		SaTokenConfig config = new SaTokenConfig();
		config.setIsPrint(false);
		config.setIsLog(false);
		SaManager.setConfig(config);
		SaManager.setSaTokenContext(new SaTokenContextForJfinal());
		SaManager.setStpInterface(new JfinalStpInterface());
	}

	@Override
	public void configConstant(Constants constants) {
		constants.setDevMode(false);
		constants.setEncoding("UTF-8");
	}

	@Override
	public void configRoute(Routes routes) {
		routes.add("/", JfinalHttpController.class);
	}

	@Override
	public void configEngine(Engine engine) {
	}

	@Override
	public void configPlugin(Plugins plugins) {
	}

	@Override
	public void configInterceptor(Interceptors interceptors) {
		interceptors.add(new SaAnnotationInterceptor());
	}

	@Override
	public void configHandler(Handlers handlers) {
		handlers.setActionHandler(new SaTokenActionHandler());
	}
}
