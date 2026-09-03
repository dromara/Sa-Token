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

import cn.dev33.satoken.SaManager;
import cn.dev33.satoken.config.SaTokenConfig;
import cn.dev33.satoken.config.SaTokenConfigFactory;
import cn.dev33.satoken.dao.SaTokenDao;
import cn.dev33.satoken.dao.SaTokenDaoDefaultImpl;
import cn.dev33.satoken.stp.StpInterface;
import cn.dev33.satoken.stp.StpInterfaceDefaultImpl;
import cn.dev33.satoken.stp.StpLogic;
import cn.dev33.satoken.stp.StpUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * 单测 / 集成测中 {@link SaManager} 全局状态快照与复位。
 */
public final class SaTokenTestContext {

	private SaTokenTestContext() {
	}

	/**
	 * 将 SaManager 重置为干净的默认状态（独立 DAO、默认配置、空 StpLogic 集合）。
	 */
	public static void reset() {
		SaTokenConfig config = SaTokenConfigFactory.createConfig();
		config.setIsPrint(false);
		config.setIsLog(false);
		SaManager.setConfig(config);
		SaManager.setSaTokenDao(new SaTokenDaoDefaultImpl());
		SaManager.stpLogicMap.clear();
		SaManager.setStpInterface(new StpInterfaceDefaultImpl());
		StpUtil.getLoginType();
	}

	/**
	 * 捕获当前 SaManager 全局状态，供测试结束后 {@link Snapshot#restore()}。
	 */
	public static Snapshot capture() {
		return new Snapshot(
				SaManager.config,
				SaManager.getSaTokenDao(),
				SaManager.getStpInterface(),
				new HashMap<>(SaManager.stpLogicMap)
		);
	}

	public static final class Snapshot {

		private final SaTokenConfig config;
		private final SaTokenDao saTokenDao;
		private final StpInterface stpInterface;
		private final Map<String, StpLogic> stpLogicMap;

		private Snapshot(SaTokenConfig config, SaTokenDao saTokenDao, StpInterface stpInterface,
				Map<String, StpLogic> stpLogicMap) {
			this.config = config;
			this.saTokenDao = saTokenDao;
			this.stpInterface = stpInterface;
			this.stpLogicMap = stpLogicMap;
		}

		public void restore() {
			if (config != null) {
				SaManager.setConfig(config);
			}
			if (saTokenDao != null) {
				SaManager.setSaTokenDao(saTokenDao);
			}
			if (stpInterface != null) {
				SaManager.setStpInterface(stpInterface);
			}
			SaManager.stpLogicMap.clear();
			SaManager.stpLogicMap.putAll(stpLogicMap);
		}
	}

}
