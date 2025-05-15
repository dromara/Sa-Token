/*
 * Copyright 2020-2099 sa-token.cc
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
package cn.dev33.satoken.solon.sso;

import cn.dev33.satoken.sso.SaSsoManager;
import cn.dev33.satoken.sso.config.SaSsoClientConfig;
import cn.dev33.satoken.sso.config.SaSsoServerConfig;
import cn.dev33.satoken.sso.processor.SaSsoClientProcessor;
import cn.dev33.satoken.sso.processor.SaSsoServerProcessor;
import cn.dev33.satoken.sso.template.SaSsoClientTemplate;
import cn.dev33.satoken.sso.template.SaSsoServerTemplate;
import org.noear.solon.annotation.Bean;
import org.noear.solon.annotation.Condition;
import org.noear.solon.annotation.Configuration;
import org.noear.solon.annotation.Inject;

/**
 * 注册 Sa-Token SSO 所需要的 Bean
 *
 * @author click33
 * @since 1.34.0
 */
@Condition(onClass=SaSsoManager.class)
@Configuration
public class SaSsoBeanRegister {

	/**
	 * 获取 SSO Server 端 配置对象
	 *
	 * @return 配置对象
	 */
	@Bean
	public SaSsoServerConfig getSaSsoServerConfig(@Inject(value = "${sa-token.sso-server}", required = false) SaSsoServerConfig serverConfig) {
		if (serverConfig == null) {
			return new SaSsoServerConfig();
		} else {
			return serverConfig;
		}
	}

	/**
	 * 获取 SSO Client 端 配置对象
	 *
	 * @return 配置对象
	 */
	@Bean
	public SaSsoClientConfig getSaSsoClientConfig(@Inject(value = "${sa-token.sso-client}", required = false) SaSsoClientConfig clientConfig) {
		if (clientConfig == null) {
			return new SaSsoClientConfig();
		} else {
			return clientConfig;
		}
	}

	/**
	 * 获取 SSO Server 端 SaSsoServerTemplate
	 *
	 * @return /
	 */
	@Bean
	@Condition(onMissingBean = SaSsoServerTemplate.class)
	public SaSsoServerTemplate getSaSsoServerTemplate() {
		return SaSsoServerProcessor.instance.ssoServerTemplate;
	}

	/**
	 * 获取 SSO Client 端 SaSsoClientTemplate
	 *
	 * @return /
	 */
	@Bean
	@Condition(onMissingBean = SaSsoClientTemplate.class)
	public SaSsoClientTemplate getSaSsoClientTemplate() {
		return SaSsoClientProcessor.instance.ssoClientTemplate;
	}

}
