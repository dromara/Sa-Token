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
package cn.dev33.satoken.apikey.support;

import cn.dev33.satoken.apikey.SaApiKeyManager;
import cn.dev33.satoken.apikey.config.SaApiKeyConfig;
import cn.dev33.satoken.apikey.loader.SaApiKeyDataLoader;
import cn.dev33.satoken.apikey.template.SaApiKeyTemplate;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

/**
 * ApiKey 插件单测现场：快照 / 恢复 {@link SaApiKeyManager} 静态态。
 */
public final class ApiKeyTestSupport {

	private ApiKeyTestSupport() {
	}

	/** 捕获当前 ApiKey Manager 静态态，供测试结束后 {@link Snapshot#restore()} */
	public static Snapshot capture() {
		return new Snapshot(
				SaApiKeyManager.getConfig(),
				SaApiKeyManager.getSaApiKeyDataLoader(),
				SaApiKeyManager.getSaApiKeyTemplate()
		);
	}

	/** 每个用例开始前拍快照，结束后把 ApiKey 静态态写回去 */
	public static final class ResetExtension implements BeforeEachCallback, AfterEachCallback {

		private Snapshot snapshot;

		/** 用例开始前先记下当前 ApiKey Manager */
		@Override
		public void beforeEach(ExtensionContext context) {
			snapshot = capture();
		}

		/** 用例结束后把 ApiKey 静态态恢复成用例开始前的样子 */
		@Override
		public void afterEach(ExtensionContext context) {
			if (snapshot != null) {
				snapshot.restore();
				snapshot = null;
			}
		}
	}

	/** ApiKey Manager 配置 / loader / template 的引用快照 */
	public static final class Snapshot {

		private final SaApiKeyConfig config;
		private final SaApiKeyDataLoader loader;
		private final SaApiKeyTemplate template;

		private Snapshot(SaApiKeyConfig config, SaApiKeyDataLoader loader, SaApiKeyTemplate template) {
			this.config = config;
			this.loader = loader;
			this.template = template;
		}

		/** 把捕获时的 ApiKey Manager 写回去 */
		public void restore() {
			SaApiKeyManager.setConfig(config);
			SaApiKeyManager.setSaApiKeyDataLoader(loader);
			SaApiKeyManager.setSaApiKeyTemplate(template);
		}
	}

}
