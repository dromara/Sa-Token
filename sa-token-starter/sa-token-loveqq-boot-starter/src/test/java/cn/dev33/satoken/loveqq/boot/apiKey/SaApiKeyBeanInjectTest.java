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
package cn.dev33.satoken.loveqq.boot.apiKey;

import cn.dev33.satoken.apikey.SaApiKeyManager;
import cn.dev33.satoken.apikey.config.SaApiKeyConfig;
import cn.dev33.satoken.apikey.loader.SaApiKeyDataLoaderDefaultImpl;
import cn.dev33.satoken.apikey.template.SaApiKeyTemplate;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * {@link SaApiKeyBeanInject} 注入方法测试
 */
public class SaApiKeyBeanInjectTest {

	/** 配置、模板、数据加载器应该写进 SaApiKeyManager */
	@Test
	public void injectMethods_shouldWriteIntoManager() {
		SaApiKeyBeanInject inject = new SaApiKeyBeanInject();

		SaApiKeyConfig config = new SaApiKeyConfig();
		inject.setSaApiKeyConfig(config);
		Assertions.assertSame(config, SaApiKeyManager.getConfig());

		SaApiKeyTemplate template = new SaApiKeyTemplate();
		inject.setSaApiKeyTemplate(template);
		Assertions.assertSame(template, SaApiKeyManager.getSaApiKeyTemplate());

		SaApiKeyDataLoaderDefaultImpl loader = new SaApiKeyDataLoaderDefaultImpl();
		inject.setSaApiKeyDataLoader(loader);
		Assertions.assertSame(loader, SaApiKeyManager.getSaApiKeyDataLoader());
	}

}
