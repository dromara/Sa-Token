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
 * SaTokenPluginHolder 补充测试
 *
 * @author click33
 * @since 1.46.0
 */
public class SaTokenPluginHolderExtendedTest {

	@BeforeEach
	void resetPlugins() {
		DestroyTrackingPlugin.reset();
		TestSaTokenPlugin.reset();
	}

	/** 通过 Class 安装插件，并校验安装状态 */
	@Test
	void installPluginByClass() {
		SaTokenPluginHolder holder = new SaTokenPluginHolder();
		holder.installPlugin(TestSaTokenPlugin.class);
		Assertions.assertTrue(holder.isInstalledPlugin(TestSaTokenPlugin.class));
		Assertions.assertTrue(TestSaTokenPlugin.installed);
	}

	/** 安装 null 插件时应抛出 SaTokenPluginException */
	@Test
	void installPlugin_rejectsNull() {
		SaTokenPluginHolder holder = new SaTokenPluginHolder();
		Assertions.assertThrows(SaTokenPluginException.class, () -> holder.installPlugin((SaTokenPlugin) null));
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

	/** 未安装插件时 getPlugin 应返回 null */
	@Test
	void getPlugin_returnsNullWhenMissing() {
		SaTokenPluginHolder holder = new SaTokenPluginHolder();
		Assertions.assertNull(holder.getPlugin(TestSaTokenPlugin.class));
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

	/** 插件已安装时注册 onAfterInstall 应立即触发回调 */
	@Test
	void onAfterInstallHook_runsImmediatelyIfAlreadyInstalled() {
		SaTokenPluginHolder holder = new SaTokenPluginHolder();
		holder.installPlugin(new DestroyTrackingPlugin());
		AtomicBoolean afterCalled = new AtomicBoolean(false);
		holder.onAfterInstall(DestroyTrackingPlugin.class, plugin -> afterCalled.set(true));
		Assertions.assertTrue(afterCalled.get());
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

	static class DestroyTrackingPlugin implements SaTokenPlugin {

		static volatile boolean installed;
		static volatile boolean destroyed;

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
