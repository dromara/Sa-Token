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
package cn.dev33.satoken.spring.oauth2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;

import cn.dev33.satoken.oauth2.SaOAuth2Manager;
import cn.dev33.satoken.oauth2.config.SaOAuth2Config;
import cn.dev33.satoken.oauth2.logic.SaOAuth2Template;
import cn.dev33.satoken.oauth2.logic.SaOAuth2Util;

/**
 * 注入 Sa-Token-OAuth2 所需要的组件
 * 
 * @author click33
 * @since 1.34.0
 */
@ConditionalOnClass(SaOAuth2Manager.class)
public class SaOAuth2BeanInject {

	/**
	 * 注入 OAuth2 配置对象
	 * 
	 * @param saOAuth2Config 配置对象 
	 */
	@Autowired(required = false)
	public void setSaOAuth2Config(SaOAuth2Config saOAuth2Config) {
		SaOAuth2Manager.setConfig(saOAuth2Config);
	}

	/**
	 * 注入 OAuth2 模板代码类
	 * 
	 * @param saOAuth2Template 模板代码类
	 */
	@Autowired(required = false)
	public void setSaOAuth2Interface(SaOAuth2Template saOAuth2Template) {
		SaOAuth2Util.saOAuth2Template = saOAuth2Template;
	}
	
}
