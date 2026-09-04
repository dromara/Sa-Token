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
package cn.dev33.satoken.core.plugin;

import cn.dev33.satoken.exception.SaTokenPluginException;
import cn.dev33.satoken.plugin.SaTokenPluginHolder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * SaTokenPluginHolder 插件管理测试
 *
 * @author click33
 * @since 1.46.0
 */
public class SaTokenPluginHolderTest {

	@BeforeEach
	void resetPluginState() {
		TestSaTokenPlugin.reset();
	}

	/** installPlugin 应安装插件并可通过 getPlugin 获取 */
	@Test
	void installPlugin() {
		SaTokenPluginHolder holder = new SaTokenPluginHolder();
		Assertions.assertFalse(holder.isInstalledPlugin(TestSaTokenPlugin.class));

		holder.installPlugin(new TestSaTokenPlugin());
		Assertions.assertTrue(holder.isInstalledPlugin(TestSaTokenPlugin.class));
		Assertions.assertTrue(TestSaTokenPlugin.installed);
		Assertions.assertSame(TestSaTokenPlugin.class, holder.getPlugin(TestSaTokenPlugin.class).getClass());
	}

	/** 重复安装同类型插件时应抛出异常 */
	@Test
	void installPlugin_rejectsDuplicate() {
		SaTokenPluginHolder holder = new SaTokenPluginHolder();
		holder.installPlugin(new TestSaTokenPlugin());
		Assertions.assertThrows(SaTokenPluginException.class, () -> holder.installPlugin(new TestSaTokenPlugin()));
	}

	/** init 应从 SPI 加载插件并设置 isLoader */
	@Test
	void init_loadsSpiPlugin() {
		SaTokenPluginHolder holder = new SaTokenPluginHolder();
		holder.init();
		Assertions.assertTrue(holder.isLoader);
		Assertions.assertTrue(holder.isInstalledPlugin(TestSaTokenPlugin.class));
		Assertions.assertTrue(TestSaTokenPlugin.installed);
	}

	/** 重复 init 不应再次加载 SPI 插件 */
	@Test
	void init_runsOnlyOnce() {
		SaTokenPluginHolder holder = new SaTokenPluginHolder();
		holder.init();
		TestSaTokenPlugin.reset();
		holder.init();
		Assertions.assertFalse(TestSaTokenPlugin.installed);
	}

}
