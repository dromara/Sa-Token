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
package cn.dev33.satoken.spring.sso;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;

import cn.dev33.satoken.config.SaSsoConfig;
import cn.dev33.satoken.sso.SaSsoManager;
import cn.dev33.satoken.sso.SaSsoProcessor;
import cn.dev33.satoken.sso.SaSsoTemplate;
import cn.dev33.satoken.sso.SaSsoUtil;

/**
 * 注入 Sa-Token-SSO 所需要的 Bean
 * 
 * @author click33
 * @since <= 1.34.0
 */
@ConditionalOnClass(SaSsoManager.class)
public class SaSsoBeanInject {

	/**
	 * 注入 Sa-Token-SSO 配置类
	 * 
	 * @param saSsoConfig 配置对象 
	 */
	@Autowired(required = false)
	public void setSaOAuth2Config(SaSsoConfig saSsoConfig) {
		SaSsoManager.setConfig(saSsoConfig);
	}

	/**
	 * 注入 SSO 模板代码类
	 * 
	 * @param ssoTemplate SaSsoTemplate 对象
	 */
	@Autowired(required = false)
	public void setSaSsoTemplate(SaSsoTemplate ssoTemplate) {
		SaSsoUtil.ssoTemplate = ssoTemplate;
		SaSsoProcessor.instance.ssoTemplate = ssoTemplate;
	}

}
