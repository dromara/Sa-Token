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
 * SaTokenPluginHolder 插件管理测试
 *
 * @author click33
 * @since 1.46.0
 */
public class SaTokenPluginHolderTest {

	/** 每个用例开始前把插件状态复位 */
	@BeforeEach
	void resetPluginState() {
		TestSaTokenPlugin.reset();
		DestroyTrackingPlugin.reset();
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

	/** getPluginListCopy 返回副本，修改副本不影响已安装插件列表 */
	@Test
	void getPluginListCopy() {
		SaTokenPluginHolder holder = new SaTokenPluginHolder();
		holder.installPlugin(new TestSaTokenPlugin());
		Assertions.assertEquals(1, holder.getPluginListCopy().size());
		holder.getPluginListCopy().clear();
		Assertions.assertTrue(holder.isInstalledPlugin(TestSaTokenPlugin.class));
	}

	/** destroyPlugin 传入 null 或未安装插件时应抛出异常 */
	@Test
	void destroyPlugin_rejectsNullAndNotInstalled() {
		SaTokenPluginHolder holder = new SaTokenPluginHolder();
		Assertions.assertThrows(SaTokenPluginException.class, () -> holder.destroyPlugin((SaTokenPlugin) null));
		Assertions.assertThrows(SaTokenPluginException.class, () -> holder.destroyPlugin(TestSaTokenPlugin.class));
	}

	/** destroyPlugin 应调用插件的 destroy 方法 */
	@Test
	void destroyPlugin_invokesDestroy() {
		SaTokenPluginHolder holder = new SaTokenPluginHolder();
		holder.installPlugin(new DestroyTrackingPlugin());
		holder.destroyPlugin(DestroyTrackingPlugin.class);
		Assertions.assertTrue(DestroyTrackingPlugin.destroyed);
	}

	/** onInstall 钩子应替换默认 install 逻辑 */
	@Test
	void onInstallHook_replacesDefaultInstall() {
		SaTokenPluginHolder holder = new SaTokenPluginHolder();
		AtomicBoolean hookCalled = new AtomicBoolean(false);
		holder.onInstall(DestroyTrackingPlugin.class, plugin -> hookCalled.set(true));
		holder.installPlugin(new DestroyTrackingPlugin());
		Assertions.assertTrue(hookCalled.get());
		Assertions.assertFalse(DestroyTrackingPlugin.installed);
	}

	/** onBeforeInstall 钩子应在 install 之前执行 */
	@Test
	void onBeforeInstallHook_runsBeforeInstall() {
		SaTokenPluginHolder holder = new SaTokenPluginHolder();
		AtomicBoolean beforeCalled = new AtomicBoolean(false);
		holder.onBeforeInstall(DestroyTrackingPlugin.class, plugin -> beforeCalled.set(true));
		holder.installPlugin(new DestroyTrackingPlugin());
		Assertions.assertTrue(beforeCalled.get());
		Assertions.assertTrue(DestroyTrackingPlugin.installed);
	}

	/** 插件已安装后不允许重复注册 onInstall / onBeforeInstall 钩子 */
	@Test
	void onInstallHook_rejectsWhenAlreadyInstalled() {
		SaTokenPluginHolder holder = new SaTokenPluginHolder();
		holder.installPlugin(new DestroyTrackingPlugin());
		Assertions.assertThrows(SaTokenPluginException.class,
				() -> holder.onInstall(DestroyTrackingPlugin.class, plugin -> {}));
		Assertions.assertThrows(SaTokenPluginException.class,
				() -> holder.onBeforeInstall(DestroyTrackingPlugin.class, plugin -> {}));
	}

	/** destroy 钩子应按 before → destroy → after 顺序执行，且 onDestroy 可阻止默认 destroy */
	@Test
	void destroyHooks_runInOrder() {
		SaTokenPluginHolder holder = new SaTokenPluginHolder();
		AtomicBoolean destroyHookCalled = new AtomicBoolean(false);
		AtomicBoolean beforeDestroyCalled = new AtomicBoolean(false);
		AtomicBoolean afterDestroyCalled = new AtomicBoolean(false);
		holder.onBeforeDestroy(DestroyTrackingPlugin.class, plugin -> beforeDestroyCalled.set(true));
		holder.onDestroy(DestroyTrackingPlugin.class, plugin -> destroyHookCalled.set(true));
		holder.onAfterDestroy(DestroyTrackingPlugin.class, plugin -> afterDestroyCalled.set(true));
		holder.installPlugin(new DestroyTrackingPlugin());
		holder.destroyPlugin(DestroyTrackingPlugin.class);
		Assertions.assertTrue(beforeDestroyCalled.get());
		Assertions.assertTrue(destroyHookCalled.get());
		Assertions.assertTrue(afterDestroyCalled.get());
		Assertions.assertFalse(DestroyTrackingPlugin.destroyed);
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

	/** 用静态标记记录 install / destroy 是否被调用的插件夹具 */
	static class DestroyTrackingPlugin implements SaTokenPlugin {

		static volatile boolean installed;
		static volatile boolean destroyed;

		/** 把 install / destroy 标记清零 */
		static void reset() {
			installed = false;
			destroyed = false;
		}

		@Override
		public void install() {
			installed = true;
		}

		@Override
		public void destroy() {
			destroyed = true;
		}
	}

}
