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
import cn.dev33.satoken.plugin.SaTokenPlugin;
import cn.dev33.satoken.plugin.SaTokenPluginHolder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * SaTokenPluginHolder SPI 加载与 init 路径测试
 *
 * @author click33
 * @since 1.46.0
 */
public class SaTokenPluginHolderInitTest {

	@BeforeEach
	void resetPlugins() {
		TestSaTokenPlugin.reset();
	}

	/** loaderPlugins 应从 classpath 加载 SPI 插件 */
	@Test
	void loaderPlugins_loadsSpiFromClasspath() {
		SaTokenPluginHolder holder = new SaTokenPluginHolder();
		holder.loaderPlugins();
		Assertions.assertTrue(holder.isInstalledPlugin(TestSaTokenPlugin.class));
		Assertions.assertTrue(TestSaTokenPlugin.installed);
	}

	/** init 应设置 isLoader 且重复调用具有幂等性 */
	@Test
	void init_setsLoaderFlagAndIsIdempotent() {
		SaTokenPluginHolder holder = new SaTokenPluginHolder();
		holder.init();
		Assertions.assertTrue(holder.isLoader);
		TestSaTokenPlugin.reset();
		holder.init();
		Assertions.assertFalse(TestSaTokenPlugin.installed);
	}

	/** installPlugin/destroyPlugin 应返回 holder 自身 */
	@Test
	void installAndDestroy_returnSelf() {
		SaTokenPluginHolder holder = new SaTokenPluginHolder();
		SaTokenPlugin plugin = new TestSaTokenPlugin();
		Assertions.assertSame(holder, holder.installPlugin(plugin));
		Assertions.assertSame(holder, holder.destroyPlugin(plugin));
	}

	/** spiDir 可配置时应从指定目录加载插件 */
	@Test
	void loaderPlugins_spiDirIsConfigurable() {
		SaTokenPluginHolder holder = new SaTokenPluginHolder();
		holder.spiDir = "satoken";
		holder.loaderPlugins();
		Assertions.assertTrue(holder.isInstalledPlugin(TestSaTokenPlugin.class));
	}

	/** installPlugin(Class) 应实例化并安装插件 */
	@Test
	void installPluginByClass_instantiatesPlugin() {
		SaTokenPluginHolder holder = new SaTokenPluginHolder();
		holder.installPlugin(TestSaTokenPlugin.class);
		Assertions.assertNotNull(holder.getPlugin(TestSaTokenPlugin.class));
	}

	/** 未安装插件时 getPlugin 应返回 null */
	@Test
	void getPlugin_returnsNullWhenMissing() {
		SaTokenPluginHolder holder = new SaTokenPluginHolder();
		Assertions.assertNull(holder.getPlugin(TestSaTokenPlugin.class));
		Assertions.assertFalse(holder.isInstalledPlugin(TestSaTokenPlugin.class));
	}

	/** 安装 null 插件时应抛出 SaTokenPluginException */
	@Test
	void installPlugin_rejectsNullPlugin() {
		SaTokenPluginHolder holder = new SaTokenPluginHolder();
		Assertions.assertThrows(SaTokenPluginException.class, () -> holder.installPlugin((SaTokenPlugin) null));
	}

	/** destroyPlugin 传入 null 或未安装插件时应抛出异常 */
	@Test
	void destroyPlugin_rejectsNullOrUninstalled() {
		SaTokenPluginHolder holder = new SaTokenPluginHolder();
		Assertions.assertThrows(SaTokenPluginException.class, () -> holder.destroyPlugin((SaTokenPlugin) null));
		Assertions.assertThrows(SaTokenPluginException.class,
				() -> holder.destroyPlugin(new TestSaTokenPlugin()));
	}

	/** 插件已安装时注册 onAfterInstall 应立即触发 */
	@Test
	void onAfterInstall_runsImmediatelyWhenAlreadyInstalled() {
		SaTokenPluginHolder holder = new SaTokenPluginHolder();
		holder.installPlugin(TestSaTokenPlugin.class);
		AtomicBoolean ran = new AtomicBoolean(false);
		holder.onAfterInstall(TestSaTokenPlugin.class, plugin -> ran.set(true));
		Assertions.assertTrue(ran.get());
	}

	/** 插件未安装时 onAfterInstall 应排队并在安装后触发 */
	@Test
	void onAfterInstall_queuesHookWhenPluginNotInstalled() {
		SaTokenPluginHolder holder = new SaTokenPluginHolder();
		AtomicBoolean ran = new AtomicBoolean(false);
		Assertions.assertSame(holder, holder.onAfterInstall(TestSaTokenPlugin.class, plugin -> ran.set(true)));
		Assertions.assertFalse(ran.get());
		holder.installPlugin(TestSaTokenPlugin.class);
		Assertions.assertTrue(ran.get());
	}

	/** 无默认构造函数的插件类安装时应抛出异常 */
	@Test
	void installPluginByClass_noDefaultConstructor_throws() {
		SaTokenPluginHolder holder = new SaTokenPluginHolder();
		Assertions.assertThrows(SaTokenPluginException.class,
				() -> holder.installPlugin(BadCtorSaTokenPlugin.class));
	}

	/** getPluginListCopy 应返回独立副本列表 */
	@Test
	void getPluginListCopy_returnsDetachedList() {
		SaTokenPluginHolder holder = new SaTokenPluginHolder();
		holder.installPlugin(new TestSaTokenPlugin());
		Assertions.assertEquals(1, holder.getPluginListCopy().size());
	}

	/** 重复安装同类型插件时应抛出异常 */
	@Test
	void installPlugin_rejectsDuplicateByClass() {
		SaTokenPluginHolder holder = new SaTokenPluginHolder();
		holder.installPlugin(TestSaTokenPlugin.class);
		Assertions.assertThrows(SaTokenPluginException.class,
				() -> holder.installPlugin(TestSaTokenPlugin.class));
	}

}
