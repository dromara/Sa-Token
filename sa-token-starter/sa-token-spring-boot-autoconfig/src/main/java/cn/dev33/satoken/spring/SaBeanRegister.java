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
package cn.dev33.satoken.spring;

import cn.dev33.satoken.config.SaTokenConfig;
import cn.dev33.satoken.json.SaJsonTemplate;
import cn.dev33.satoken.json.SaJsonTemplateDefaultImpl;
import cn.dev33.satoken.spring.context.path.ApplicationContextPathLoading;
import cn.dev33.satoken.spring.json.SaJsonTemplateForJackson;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;

/**
 * 注册Sa-Token所需要的Bean 
 * <p> Bean 的注册与注入应该分开在两个文件中，否则在某些场景下会造成循环依赖 
 * @author click33
 *
 */
public class SaBeanRegister {

	/**
	 * 获取配置Bean
	 * 
	 * @return 配置对象
	 */
	@Bean
	@ConfigurationProperties(prefix = "sa-token")
	public SaTokenConfig getSaTokenConfig() {
		return new SaTokenConfig();
	}
	
	/**
	 * 获取 json 转换器 Bean (Jackson版)
	 * 
	 * @return json 转换器 Bean (Jackson版)
	 */
	@Bean
	public SaJsonTemplate getSaJsonTemplateForJackson() {
		try {
			// 部分开发者会在 springboot 项目中排除 jackson 依赖，所以这里做一个判断：
			// 	1、如果项目中存在 jackson 依赖，则使用 jackson 的 json 转换器
			// 	2、如果项目中不存在 jackson 依赖，则使用默认的 json 转换器
			// 	to：防止因为 jackson 依赖问题导致项目无法启动
			Class.forName("com.fasterxml.jackson.databind.ObjectMapper");
			return new SaJsonTemplateForJackson();
		} catch (ClassNotFoundException e) {
			return new SaJsonTemplateDefaultImpl();
		}
	}

	/**
	 * 应用上下文路径加载器
	 * @return /
	 */
	@Bean
	public ApplicationContextPathLoading getApplicationContextPathLoading() {
		return new ApplicationContextPathLoading();
	}

}
