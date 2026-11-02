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
package cn.dev33.satoken.sign.support;

import cn.dev33.satoken.fun.SaParamRetFunction;
import cn.dev33.satoken.sign.SaSignManager;
import cn.dev33.satoken.sign.config.SaSignConfig;
import cn.dev33.satoken.sign.template.SaSignMany;
import cn.dev33.satoken.sign.template.SaSignTemplate;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Sign 插件单测现场：快照 / 恢复 {@link SaSignManager} 与 {@link SaSignMany} 静态态。
 */
public final class SignTestSupport {

	private SignTestSupport() {
	}

	/** 捕获当前 Sign Manager / SaSignMany 静态态，供测试结束后 {@link Snapshot#restore()} */
	public static Snapshot capture() {
		return new Snapshot(
				SaSignManager.getConfig(),
				new LinkedHashMap<>(SaSignManager.getSignMany()),
				SaSignManager.getSaSignTemplate(),
				SaSignMany.findSaSignConfigMethod
		);
	}

	/** 每个用例开始前拍快照，结束后把 Sign 静态态写回去 */
	public static final class ResetExtension implements BeforeEachCallback, AfterEachCallback {

		private Snapshot snapshot;

		/** 用例开始前先记下当前 Sign Manager / SaSignMany */
		@Override
		public void beforeEach(ExtensionContext context) {
			snapshot = capture();
		}

		/** 用例结束后把 Sign 静态态恢复成用例开始前的样子 */
		@Override
		public void afterEach(ExtensionContext context) {
			if (snapshot != null) {
				snapshot.restore();
				snapshot = null;
			}
		}
	}

	/** Sign Manager 与多实例查找函数的引用快照 */
	public static final class Snapshot {

		private final SaSignConfig config;
		private final Map<String, SaSignConfig> signMany;
		private final SaSignTemplate saSignTemplate;
		private final SaParamRetFunction<String, SaSignConfig> findSaSignConfigMethod;

		private Snapshot(SaSignConfig config, Map<String, SaSignConfig> signMany, SaSignTemplate saSignTemplate,
				SaParamRetFunction<String, SaSignConfig> findSaSignConfigMethod) {
			this.config = config;
			this.signMany = signMany;
			this.saSignTemplate = saSignTemplate;
			this.findSaSignConfigMethod = findSaSignConfigMethod;
		}

		/** 把捕获时的 Sign Manager / SaSignMany 写回去 */
		public void restore() {
			SaSignManager.setConfig(config);
			SaSignManager.setSignMany(new LinkedHashMap<>(signMany));
			SaSignManager.setSaSignTemplate(saSignTemplate);
			SaSignMany.findSaSignConfigMethod = findSaSignConfigMethod;
		}
	}

}
