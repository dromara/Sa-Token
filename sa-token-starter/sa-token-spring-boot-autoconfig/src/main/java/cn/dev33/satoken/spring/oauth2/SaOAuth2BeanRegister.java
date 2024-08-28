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

import cn.dev33.satoken.annotation.handler.SaAnnotationHandlerInterface;
import cn.dev33.satoken.oauth2.SaOAuth2Manager;
import cn.dev33.satoken.oauth2.annotation.SaCheckAccessToken;
import cn.dev33.satoken.oauth2.annotation.SaCheckClientIdSecret;
import cn.dev33.satoken.oauth2.annotation.SaCheckClientToken;
import cn.dev33.satoken.oauth2.annotation.handler.SaCheckAccessTokenHandler;
import cn.dev33.satoken.oauth2.annotation.handler.SaCheckClientIdSecretHandler;
import cn.dev33.satoken.oauth2.annotation.handler.SaCheckClientTokenHandler;
import cn.dev33.satoken.oauth2.config.SaOAuth2ServerConfig;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;

/**
 * 注册 Sa-Token-OAuth2 所需要的Bean
 *
 * @author click33
 * @since 1.34.0
 */
@ConditionalOnClass(SaOAuth2Manager.class)
public class SaOAuth2BeanRegister {

	/**
	 * 获取 OAuth2 配置 Bean
	 *
	 * @return 配置对象 
	 */
	@Bean
	@ConfigurationProperties(prefix = "sa-token.oauth2-server")
	public SaOAuth2ServerConfig getSaOAuth2Config() {
		return new SaOAuth2ServerConfig();
	}

	// 自定义注解处理器
	@Bean
	public SaAnnotationHandlerInterface<SaCheckAccessToken> getSaCheckAccessTokenHandler() {
		return new SaCheckAccessTokenHandler();
	}
	@Bean
	public SaAnnotationHandlerInterface<SaCheckClientToken> getSaCheckClientTokenHandler() {
		return new SaCheckClientTokenHandler();
	}
	@Bean
	public SaAnnotationHandlerInterface<SaCheckClientIdSecret> getSaCheckClientIdSecretHandler() {
		return new SaCheckClientIdSecretHandler();
	}

	/*
	 // 这种写法有问题，当项目还有自定义的注解处理器时，项目中的自定义注解处理器将会覆盖掉此处 List 中的注解处理器
//	@Bean
//	public List<SaAnnotationHandlerInterface<?>> getXxx() {
//		return Arrays.asList(
//				new SaCheckAccessTokenHandler(),
//				new SaCheckClientTokenHandler(),
//				new SaCheckClientSecretHandler()
//		);
//	}

	 */

}
