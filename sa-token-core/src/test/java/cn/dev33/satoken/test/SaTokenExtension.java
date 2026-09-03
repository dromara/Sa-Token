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
package cn.dev33.satoken.test;

import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

/**
 * 每个测试方法前后自动复位 {@link cn.dev33.satoken.SaManager}，避免单测间全局状态污染。
 */
public class SaTokenExtension implements BeforeEachCallback, AfterEachCallback {

	private SaTokenTestContext.Snapshot snapshot;

	@Override
	public void beforeEach(ExtensionContext context) {
		snapshot = SaTokenTestContext.capture();
		SaTokenTestContext.reset();
	}

	@Override
	public void afterEach(ExtensionContext context) {
		if (snapshot != null) {
			snapshot.restore();
			snapshot = null;
		}
	}

}
