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
package cn.dev33.satoken.solon.oauth2;

import cn.dev33.satoken.oauth2.SaOAuth2Manager;
import cn.dev33.satoken.oauth2.config.SaOAuth2ServerConfig;
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
import cn.dev33.satoken.oauth2.strategy.SaOAuth2Strategy;
import cn.dev33.satoken.oauth2.template.SaOAuth2Template;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Collections;

/**
 * {@link SaOAuth2BeanInject} 注入方法测试
 */
public class SaOAuth2BeanInjectTest {

	/** OAuth2 各组件应该写进 Manager / Processor / Strategy */
	@Test
	public void injectMethods_shouldWriteIntoManagers() {
		SaOAuth2BeanInject inject = new SaOAuth2BeanInject();

		SaOAuth2ServerConfig config = new SaOAuth2ServerConfig();
		inject.setSaOAuth2Config(config);
		Assertions.assertSame(config, SaOAuth2Manager.getServerConfig());

		SaOAuth2Template template = new SaOAuth2Template();
		inject.setSaOAuth2Template(template);
		Assertions.assertSame(template, SaOAuth2Manager.getTemplate());

		SaOAuth2ServerProcessor processor = new SaOAuth2ServerProcessor();
		inject.setSaOAuth2Template(processor);
		Assertions.assertSame(processor, SaOAuth2ServerProcessor.instance);

		SaOAuth2DataLoaderDefaultImpl loader = new SaOAuth2DataLoaderDefaultImpl();
		inject.setSaOAuth2DataLoader(loader);
		Assertions.assertSame(loader, SaOAuth2Manager.getDataLoader());

		SaOAuth2DataResolverDefaultImpl resolver = new SaOAuth2DataResolverDefaultImpl();
		inject.setSaOAuth2DataResolver(resolver);
		Assertions.assertSame(resolver, SaOAuth2Manager.getDataResolver());

		SaOAuth2DataConverterDefaultImpl converter = new SaOAuth2DataConverterDefaultImpl();
		inject.setSaOAuth2DataConverter(converter);
		Assertions.assertSame(converter, SaOAuth2Manager.getDataConverter());

		SaOAuth2DataGenerateDefaultImpl generate = new SaOAuth2DataGenerateDefaultImpl();
		inject.setSaOAuth2DataGenerate(generate);
		Assertions.assertSame(generate, SaOAuth2Manager.getDataGenerate());

		SaOAuth2Dao dao = new SaOAuth2Dao();
		inject.setSaOAuth2Dao(dao);
		Assertions.assertSame(dao, SaOAuth2Manager.getDao());

		SaOAuth2ScopeHandlerInterface scopeHandler = new SaOAuth2ScopeHandlerInterface() {
			@Override
			public String getHandlerScope() {
				return "solon-test-scope";
			}

			@Override
			public void workAccessToken(AccessTokenModel at) {
			}

			@Override
			public void workClientToken(ClientTokenModel ct) {
			}
		};
		inject.setSaOAuth2ScopeHandler(Collections.singletonList(scopeHandler));
		Assertions.assertSame(scopeHandler, SaOAuth2Strategy.instance.scopeHandlerMap.get("solon-test-scope"));

		SaOAuth2GrantTypeHandlerInterface grantHandler = new SaOAuth2GrantTypeHandlerInterface() {
			@Override
			public String getHandlerGrantType() {
				return "solon_test_grant";
			}

			@Override
			public AccessTokenModel getAccessToken(cn.dev33.satoken.context.model.SaRequest req, String clientId,
					java.util.List<String> scopes) {
				return null;
			}
		};
		inject.setSaOAuth2GrantTypeHandlerInterface(Collections.singletonList(grantHandler));
		Assertions.assertSame(grantHandler, SaOAuth2Strategy.instance.grantTypeHandlerMap.get("solon_test_grant"));
	}

}
