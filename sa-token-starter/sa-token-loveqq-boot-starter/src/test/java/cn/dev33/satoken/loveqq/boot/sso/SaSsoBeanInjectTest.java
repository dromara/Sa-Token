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
package cn.dev33.satoken.loveqq.boot.sso;

import cn.dev33.satoken.sso.SaSsoManager;
import cn.dev33.satoken.sso.config.SaSsoClientConfig;
import cn.dev33.satoken.sso.config.SaSsoServerConfig;
import cn.dev33.satoken.sso.processor.SaSsoClientProcessor;
import cn.dev33.satoken.sso.processor.SaSsoServerProcessor;
import cn.dev33.satoken.sso.template.SaSsoClientTemplate;
import cn.dev33.satoken.sso.template.SaSsoServerTemplate;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * {@link SaSsoBeanInject} 注入方法测试
 */
public class SaSsoBeanInjectTest {

	/** Server/Client 配置和模板应该写进 Manager / Processor */
	@Test
	public void injectMethods_shouldWriteIntoManagers() {
		SaSsoBeanInject inject = new SaSsoBeanInject();

		SaSsoServerConfig serverConfig = new SaSsoServerConfig();
		inject.setSaSsoServerConfig(serverConfig);
		Assertions.assertSame(serverConfig, SaSsoManager.getServerConfig());

		SaSsoClientConfig clientConfig = new SaSsoClientConfig();
		inject.setSaSsoClientConfig(clientConfig);
		Assertions.assertSame(clientConfig, SaSsoManager.getClientConfig());

		SaSsoServerTemplate serverTemplate = new SaSsoServerTemplate();
		inject.setSaSsoServerTemplate(serverTemplate);
		Assertions.assertSame(serverTemplate, SaSsoServerProcessor.instance.ssoServerTemplate);

		SaSsoClientTemplate clientTemplate = new SaSsoClientTemplate();
		inject.setSaSsoClientTemplate(clientTemplate);
		Assertions.assertSame(clientTemplate, SaSsoClientProcessor.instance.ssoClientTemplate);
	}

}
