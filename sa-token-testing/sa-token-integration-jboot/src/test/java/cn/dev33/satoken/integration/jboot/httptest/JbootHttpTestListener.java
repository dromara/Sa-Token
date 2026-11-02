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
package cn.dev33.satoken.integration.jboot.httptest;

import cn.dev33.satoken.SaManager;
import cn.dev33.satoken.config.SaTokenConfig;
import cn.dev33.satoken.jboot.SaAnnotationInterceptor;
import cn.dev33.satoken.jboot.SaTokenContextForJboot;
import com.jfinal.config.Interceptors;
import io.jboot.core.listener.JbootAppListener;

/**
 * 集成测用的 JBoot 启动监听：挂上下文、注解拦截器，不起 Redis。
 */
public class JbootHttpTestListener implements JbootAppListener {

	/** 启动前把 JBoot 版上下文和角色数据挂到 SaManager */
	@Override
	public void onInit() {
		SaTokenConfig config = new SaTokenConfig();
		config.setIsPrint(false);
		config.setIsLog(false);
		SaManager.setConfig(config);
		SaManager.setSaTokenContext(new SaTokenContextForJboot());
		SaManager.setStpInterface(new JbootStpInterface());
	}

	/** 开启注解方式权限验证 */
	@Override
	public void onInterceptorConfig(Interceptors interceptors) {
		interceptors.add(new SaAnnotationInterceptor());
	}
}
