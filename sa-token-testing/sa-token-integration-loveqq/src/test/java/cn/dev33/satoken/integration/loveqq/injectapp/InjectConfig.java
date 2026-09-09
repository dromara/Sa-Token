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
package cn.dev33.satoken.integration.loveqq.injectapp;

import cn.dev33.satoken.annotation.handler.SaCheckLoginHandler;
import cn.dev33.satoken.apikey.loader.SaApiKeyDataLoaderDefaultImpl;
import cn.dev33.satoken.apikey.template.SaApiKeyTemplate;
import cn.dev33.satoken.dao.SaTokenDaoDefaultImpl;
import cn.dev33.satoken.fun.strategy.SaCorsHandleFunction;
import cn.dev33.satoken.http.SaHttpTemplateDefaultImpl;
import cn.dev33.satoken.httpauth.basic.SaHttpBasicTemplate;
import cn.dev33.satoken.httpauth.digest.SaHttpDigestTemplate;
import cn.dev33.satoken.json.SaJsonTemplateDefaultImpl;
import cn.dev33.satoken.listener.SaTokenListenerForSimple;
import cn.dev33.satoken.log.SaLogForConsole;
import cn.dev33.satoken.oauth2.dao.SaOAuth2Dao;
import cn.dev33.satoken.oauth2.data.convert.SaOAuth2DataConverterDefaultImpl;
import cn.dev33.satoken.oauth2.data.generate.SaOAuth2DataGenerateDefaultImpl;
import cn.dev33.satoken.oauth2.data.loader.SaOAuth2DataLoaderDefaultImpl;
import cn.dev33.satoken.oauth2.data.model.AccessTokenModel;
import cn.dev33.satoken.oauth2.data.model.ClientTokenModel;
import cn.dev33.satoken.oauth2.data.resolver.SaOAuth2DataResolverDefaultImpl;
import cn.dev33.satoken.oauth2.granttype.handler.SaOAuth2GrantTypeHandlerInterface;
import cn.dev33.satoken.oauth2.processor.SaOAuth2ServerProcessor;
import cn.dev33.satoken.oauth2.scope.handler.SaOAuth2ScopeHandlerInterface;
import cn.dev33.satoken.oauth2.template.SaOAuth2Template;
import cn.dev33.satoken.plugin.SaTokenPlugin;
import cn.dev33.satoken.plugin.SaTokenPluginHolder;
import cn.dev33.satoken.same.SaSameTemplate;
import cn.dev33.satoken.secure.totp.SaTotpTemplate;
import cn.dev33.satoken.serializer.impl.SaSerializerTemplateForJson;
import cn.dev33.satoken.sign.template.SaSignTemplate;
import cn.dev33.satoken.sso.template.SaSsoClientTemplate;
import cn.dev33.satoken.sso.template.SaSsoServerTemplate;
import cn.dev33.satoken.stp.StpInterfaceDefaultImpl;
import cn.dev33.satoken.stp.StpLogic;
import cn.dev33.satoken.strategy.hooks.SaFirewallCheckHook;
import cn.dev33.satoken.temp.SaTempTemplate;
import com.kfyty.loveqq.framework.core.autoconfig.annotation.Bean;
import com.kfyty.loveqq.framework.core.autoconfig.annotation.Configuration;
import com.kfyty.loveqq.framework.core.support.AntPathMatcher;
import com.kfyty.loveqq.framework.core.support.PatternMatcher;

import java.util.List;

/**
 * 向容器塞一套自定义组件，验证插件 *BeanInject 生产路径。
 */
@Configuration
public class InjectConfig {

	@Bean
	public SaLogForConsole saLog() {
		return new SaLogForConsole();
	}

	@Bean
	public SaTokenPluginHolder pluginHolder() {
		return new SaTokenPluginHolder();
	}

	/** 方法名避开自动配置里的 saTokenDaoForRedisson */
	@Bean
	public SaTokenDaoDefaultImpl saTokenDao() {
		return new SaTokenDaoDefaultImpl();
	}

	@Bean
	public StpInterfaceDefaultImpl stpInterface() {
		return new StpInterfaceDefaultImpl();
	}

	@Bean
	public SaTokenListenerForSimple listener() {
		return new SaTokenListenerForSimple();
	}

	@Bean
	public SaCheckLoginHandler checkLoginHandler() {
		return new SaCheckLoginHandler();
	}

	@Bean
	public SaTempTemplate saTempTemplate() {
		return new SaTempTemplate();
	}

	@Bean
	public SaSameTemplate saSameTemplate() {
		return new SaSameTemplate();
	}

	@Bean
	public SaHttpBasicTemplate saHttpBasicTemplate() {
		return new SaHttpBasicTemplate();
	}

	@Bean
	public SaHttpDigestTemplate saHttpDigestTemplate() {
		return new SaHttpDigestTemplate();
	}

	@Bean
	public SaJsonTemplateDefaultImpl saJsonTemplate() {
		return new SaJsonTemplateDefaultImpl();
	}

	@Bean
	public SaHttpTemplateDefaultImpl saHttpTemplate() {
		return new SaHttpTemplateDefaultImpl();
	}

	@Bean
	public SaSerializerTemplateForJson saSerializerTemplate() {
		return new SaSerializerTemplateForJson();
	}

	@Bean
	public SaTotpTemplate saTotpTemplate() {
		return new SaTotpTemplate();
	}

	@Bean
	public StpLogic stpLogic() {
		return new StpLogic("login");
	}

	@Bean
	public PatternMatcher pathMatcher() {
		return new AntPathMatcher();
	}

	@Bean
	public SaFirewallCheckHook firewallCheckHook() {
		return (req, res, ext) -> {};
	}

	@Bean
	public SaCorsHandleFunction corsHandle() {
		return (req, res, sto) -> {};
	}

	@Bean
	public SaTokenPlugin saTokenPlugin() {
		return new MarkerPlugin();
	}

	@Bean
	public SaSsoServerTemplate ssoServerTemplate() {
		return new SaSsoServerTemplate();
	}

	@Bean
	public SaSsoClientTemplate ssoClientTemplate() {
		return new SaSsoClientTemplate();
	}

	@Bean
	public SaOAuth2Template saOAuth2Template() {
		return new SaOAuth2Template();
	}

	@Bean
	public SaOAuth2ServerProcessor saOAuth2ServerProcessor() {
		return new SaOAuth2ServerProcessor();
	}

	@Bean
	public SaOAuth2DataLoaderDefaultImpl saOAuth2DataLoader() {
		return new SaOAuth2DataLoaderDefaultImpl();
	}

	@Bean
	public SaOAuth2DataResolverDefaultImpl saOAuth2DataResolver() {
		return new SaOAuth2DataResolverDefaultImpl();
	}

	@Bean
	public SaOAuth2DataConverterDefaultImpl saOAuth2DataConverter() {
		return new SaOAuth2DataConverterDefaultImpl();
	}

	@Bean
	public SaOAuth2DataGenerateDefaultImpl saOAuth2DataGenerate() {
		return new SaOAuth2DataGenerateDefaultImpl();
	}

	@Bean
	public SaOAuth2Dao saOAuth2Dao() {
		return new SaOAuth2Dao();
	}

	@Bean
	public SaOAuth2ScopeHandlerInterface saOAuth2ScopeHandler() {
		return new SaOAuth2ScopeHandlerInterface() {
			@Override
			public String getHandlerScope() {
				return "loveqq-inject-scope";
			}

			@Override
			public void workAccessToken(AccessTokenModel at) {
			}

			@Override
			public void workClientToken(ClientTokenModel ct) {
			}
		};
	}

	@Bean
	public SaOAuth2GrantTypeHandlerInterface saOAuth2GrantTypeHandler() {
		return new SaOAuth2GrantTypeHandlerInterface() {
			@Override
			public String getHandlerGrantType() {
				return "loveqq_inject_grant";
			}

			@Override
			public AccessTokenModel getAccessToken(cn.dev33.satoken.context.model.SaRequest req, String clientId,
					List<String> scopes) {
				return null;
			}
		};
	}

	@Bean
	public SaApiKeyTemplate saApiKeyTemplate() {
		return new SaApiKeyTemplate();
	}

	@Bean
	public SaApiKeyDataLoaderDefaultImpl saApiKeyDataLoader() {
		return new SaApiKeyDataLoaderDefaultImpl();
	}

	@Bean
	public SaSignTemplate saSignTemplate() {
		return new SaSignTemplate();
	}

	public static class MarkerPlugin implements SaTokenPlugin {
		@Override
		public void install() {
		}
	}

}
