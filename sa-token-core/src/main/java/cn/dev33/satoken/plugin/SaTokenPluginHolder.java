/*
 * Copyright 2020-2099 sa-token.cc
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
package cn.dev33.satoken.plugin;

import cn.dev33.satoken.SaManager;
import cn.dev33.satoken.exception.SaTokenPluginException;
import cn.dev33.satoken.fun.hooks.SaTokenPluginHookFunction;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;

/**
 * Sa-Token 插件管理器，管理所有插件的加载与卸载
 *
 * @author click33
 * @since 1.41.0
 */
public class SaTokenPluginHolder {

	/**
	 * 默认实例，非单例模式，可替换
	 */
	public static SaTokenPluginHolder instance = new SaTokenPluginHolder();


	// ------------------- 插件初始化相关 -------------------

	/**
	 * 是否已经加载过插件
	 */
	public boolean isLoader = false;

	/**
	 * 初始化加载所有插件（多次调用只会执行一次）
	 */
	public synchronized void init() {
		if(isLoader) {
			return;
		}
		loaderPlugins();
		isLoader = true;
	}

	/**
	 * 根据 SPI 机制加载所有插件
	 */
	public synchronized void loaderPlugins() {
		SaManager.getLog().info("SPI 插件加载开始 ...");
		ServiceLoader<SaTokenPlugin> plugins = ServiceLoader.load(SaTokenPlugin.class);
		for (SaTokenPlugin plugin : plugins) {
			installPlugin(plugin);
		}
		SaManager.getLog().info("SPI 插件加载结束 ...");
	}


	// ------------------- 插件管理 -------------------

	/**
	 * 所有插件的集合
	 */
	private final List<SaTokenPlugin> pluginList = new ArrayList<>();

	/**
	 * 获取插件集合副本 (拷贝插件集合，而非每个插件实例)
	 * @return /
	 */
	public synchronized List<SaTokenPlugin> getPluginListCopy() {
		return new ArrayList<>(pluginList);
	}

	/**
	 * 判断是否已经安装了指定插件
	 *
	 * @param pluginClass 插件类型
	 * @return /
	 */
	public synchronized<T extends SaTokenPlugin> boolean isInstalledPlugin(Class<T> pluginClass) {
		for (SaTokenPlugin plugin : pluginList) {
			if (plugin.getClass().equals(pluginClass)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 获取指定类型的插件
	 * @param pluginClass /
	 * @return /
	 * @param <T> /
	 */
	public synchronized<T extends SaTokenPlugin> T getPlugin(Class<T> pluginClass) {
		for (SaTokenPlugin plugin : pluginList) {
			if (plugin.getClass().equals(pluginClass)) {
				return (T) plugin;
			}
		}
		return null;
	}

	/**
	 * 消费指定集合的钩子函数
	 * @param pluginClass /
	 * @param hooks /
	 * @param <T> /
	 */
	protected synchronized <T extends SaTokenPlugin> void _consumeHooks(List<SaTokenPluginHookModel<? extends SaTokenPlugin>> hooks, Class<T> pluginClass) {
		for (int i = 0; i < hooks.size(); i++) {
			SaTokenPluginHookModel<? extends SaTokenPlugin> model = hooks.get(i);
			if(model.listenerClass.equals(pluginClass)) {
				model.executeFunction.execute(getPlugin(pluginClass));
				hooks.remove(i);
				i--;
			}
		}
	}


	// ------------------- 插件 Install -------------------

	/**
	 * 安装指定插件
	 * @param plugin /
	 */
	public synchronized void installPlugin(SaTokenPlugin plugin) {

		// 插件为空，拒绝安装
		if (plugin == null) {
			throw new SaTokenPluginException("插件不可为空");
		}

		// 插件已经被安装过了，拒绝再次安装
		if (isInstalledPlugin(plugin.getClass())) {
			throw new SaTokenPluginException("插件 [ " + plugin.getClass().getCanonicalName() + " ] 已安装，不可重复安装");
		}

		// 执行该插件的 install 前置钩子
		_consumeHooks(beforeInstallHooks, plugin.getClass());

		// 插件安装
		plugin.install();

		// 执行该插件的 install 后置钩子
		_consumeHooks(afterInstallHooks, plugin.getClass());

		// 添加到插件集合
		pluginList.add(plugin);
	}

	/**
	 * 安装指定插件，根据插件类型
	 * @param pluginClass /
	 */
	public synchronized<T extends SaTokenPlugin> void installPlugin(Class<T> pluginClass) {
		try {
			T plugin = pluginClass.getDeclaredConstructor().newInstance();
			installPlugin(plugin);
		} catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
			throw new SaTokenPluginException(e);
		}
	}

	/**
	 * 插件 [ Install 前置钩子 ] 集合
	 */
	private final List<SaTokenPluginHookModel<? extends SaTokenPlugin>> beforeInstallHooks = new ArrayList<>();

	/**
	 * 插件 [ Install 后置钩子 ] 集合
	 */
	private final List<SaTokenPluginHookModel<? extends SaTokenPlugin>> afterInstallHooks = new ArrayList<>();

	/**
	 * 注册指定插件的 [ Install 前置钩子 ]，同插件支持多次注册，如果插件已经安装完毕，则抛出异常
	 * @param listenerClass /
	 * @param executeFunction /
	 * @param <T> /
	 */
	public synchronized<T extends SaTokenPlugin> void onBeforeInstall(Class<T> listenerClass, SaTokenPluginHookFunction<T> executeFunction) {
		// 如果指定的插件已经安装完毕，则不再允许注册前置钩子函数
		if(isInstalledPlugin(listenerClass)) {
			throw new SaTokenPluginException("插件 [ " + listenerClass.getCanonicalName() + " ] 已安装完毕，不允许再注册前置钩子函数");
		}

		// 堆积到钩子函数集合
		beforeInstallHooks.add(new SaTokenPluginHookModel<T>(listenerClass, executeFunction));
	}

	/**
	 * 注册指定插件的 [ Install 后置钩子 ]，同插件支持多次注册，如果插件已经安装完毕，则立即执行该钩子函数
	 * @param listenerClass /
	 * @param executeFunction /
	 * @param <T> /
	 */
	public synchronized<T extends SaTokenPlugin> void onAfterInstall(Class<T> listenerClass, SaTokenPluginHookFunction<T> executeFunction) {
		// 如果指定的插件已经安装完毕，则立即执行该钩子函数
		if(isInstalledPlugin(listenerClass)) {
			executeFunction.execute(getPlugin(listenerClass));
			return;
		}

		// 堆积到钩子函数集合
		afterInstallHooks.add(new SaTokenPluginHookModel<T>(listenerClass, executeFunction));
	}


	// ------------------- 插件 Destroy -------------------

	/**
	 * 卸载指定插件
	 * @param plugin /
	 */
	public synchronized void destroyPlugin(SaTokenPlugin plugin) {

		// 插件为空，拒绝卸载
		if (plugin == null) {
			throw new SaTokenPluginException("插件不可为空");
		}

		// 插件未被安装，拒绝卸载
		if (!isInstalledPlugin(plugin.getClass())) {
			throw new SaTokenPluginException("插件 [ " + plugin.getClass().getCanonicalName() + " ] 未安装，无法卸载");
		}

		// 执行该插件的 destroy 前置钩子
		_consumeHooks(beforeDestroyHooks, plugin.getClass());

		// 插件卸载
		plugin.destroy();

		// 执行该插件的 destroy 后置钩子
		_consumeHooks(afterDestroyHooks, plugin.getClass());
	}

	/**
	 * 卸载指定插件，根据插件类型
	 * @param pluginClass /
	 */
	public synchronized<T extends SaTokenPlugin> void destroyPlugin(Class<T> pluginClass) {
		destroyPlugin(getPlugin(pluginClass));
	}

	/**
	 * 插件 [ Destroy 前置钩子 ] 集合
	 */
	private final List<SaTokenPluginHookModel<? extends SaTokenPlugin>> beforeDestroyHooks = new ArrayList<>();

	/**
	 * 插件 [ Destroy 后置钩子 ] 集合
	 */
	private final List<SaTokenPluginHookModel<? extends SaTokenPlugin>> afterDestroyHooks = new ArrayList<>();

	/**
	 * 注册指定插件的 [ Destroy 前置钩子 ]，同插件支持多次注册
	 * @param listenerClass /
	 * @param executeFunction /
	 * @param <T> /
	 */
	public synchronized<T extends SaTokenPlugin> void onBeforeDestroy(Class<T> listenerClass, SaTokenPluginHookFunction<T> executeFunction) {
		beforeDestroyHooks.add(new SaTokenPluginHookModel<T>(listenerClass, executeFunction));
	}

	/**
	 * 注册指定插件的 [ Destroy 后置钩子 ]，同插件支持多次注册
	 * @param listenerClass /
	 * @param executeFunction /
	 * @param <T> /
	 */
	public synchronized<T extends SaTokenPlugin> void onAfterDestroy(Class<T> listenerClass, SaTokenPluginHookFunction<T> executeFunction) {
		afterDestroyHooks.add(new SaTokenPluginHookModel<T>(listenerClass, executeFunction));
	}



}
