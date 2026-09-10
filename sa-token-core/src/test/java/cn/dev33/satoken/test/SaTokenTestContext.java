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
import cn.dev33.satoken.context.SaTokenContext;
import cn.dev33.satoken.context.SaTokenContextForThreadLocal;
import cn.dev33.satoken.context.SaTokenContextForThreadLocalStaff;
import cn.dev33.satoken.dao.SaTokenDao;
import cn.dev33.satoken.dao.SaTokenDaoDefaultImpl;
import cn.dev33.satoken.http.SaHttpTemplate;
import cn.dev33.satoken.http.SaHttpTemplateDefaultImpl;
import cn.dev33.satoken.json.SaJsonTemplate;
import cn.dev33.satoken.json.SaJsonTemplateDefaultImpl;
import cn.dev33.satoken.log.SaLog;
import cn.dev33.satoken.log.SaLogForConsole;
import cn.dev33.satoken.same.SaSameTemplate;
import cn.dev33.satoken.secure.totp.SaTotpTemplate;
import cn.dev33.satoken.serializer.SaSerializerTemplate;
import cn.dev33.satoken.serializer.impl.SaSerializerTemplateForJson;
import cn.dev33.satoken.stp.StpInterface;
import cn.dev33.satoken.stp.StpInterfaceDefaultImpl;
import cn.dev33.satoken.stp.StpLogic;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.temp.SaTempTemplate;

import java.util.HashMap;
import java.util.Map;

/**
 * 单测 / 集成测中 {@link SaManager} 全局状态快照与复位。
 *
 * @author click33
 * @since 1.46.0
 */
public final class SaTokenTestContext {

	private SaTokenTestContext() {
	}

	/**
	 * 将 SaManager 重置为干净的默认状态（独立 DAO、默认配置、默认模板、空 StpLogic 集合）。
	 */
	public static void reset() {
		SaTokenConfig config = SaTokenConfigFactory.createConfig();
		config.setIsPrint(false);
		config.setIsLog(false);
		SaManager.setConfig(config);
		SaManager.setSaTokenDao(new SaTokenDaoDefaultImpl());
		SaManager.stpLogicMap.clear();
		SaManager.setStpInterface(new StpInterfaceDefaultImpl());
		SaManager.setSaTokenContext(new SaTokenContextForThreadLocal());
		SaManager.setSaTempTemplate(new SaTempTemplate());
		SaManager.setSaJsonTemplate(new SaJsonTemplateDefaultImpl());
		SaManager.setSaHttpTemplate(new SaHttpTemplateDefaultImpl());
		SaManager.setSaSerializerTemplate(new SaSerializerTemplateForJson());
		SaManager.setSaSameTemplate(new SaSameTemplate());
		SaManager.setLog(new SaLogForConsole());
		SaManager.setSaTotpTemplate(new SaTotpTemplate());
		SaTokenContextForThreadLocalStaff.clearModelBox();
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
				SaManager.getSaTokenContext(),
				SaManager.getSaTempTemplate(),
				SaManager.getSaJsonTemplate(),
				SaManager.getSaHttpTemplate(),
				SaManager.getSaSerializerTemplate(),
				SaManager.getSaSameTemplate(),
				SaManager.getLog(),
				SaManager.getSaTotpTemplate(),
				new HashMap<>(SaManager.stpLogicMap)
		);
	}

	public static final class Snapshot {

		private final SaTokenConfig config;
		private final SaTokenDao saTokenDao;
		private final StpInterface stpInterface;
		private final SaTokenContext saTokenContext;
		private final SaTempTemplate saTempTemplate;
		private final SaJsonTemplate saJsonTemplate;
		private final SaHttpTemplate saHttpTemplate;
		private final SaSerializerTemplate saSerializerTemplate;
		private final SaSameTemplate saSameTemplate;
		private final SaLog log;
		private final SaTotpTemplate totpTemplate;
		private final Map<String, StpLogic> stpLogicMap;

		private Snapshot(SaTokenConfig config, SaTokenDao saTokenDao, StpInterface stpInterface,
				SaTokenContext saTokenContext, SaTempTemplate saTempTemplate, SaJsonTemplate saJsonTemplate,
				SaHttpTemplate saHttpTemplate, SaSerializerTemplate saSerializerTemplate,
				SaSameTemplate saSameTemplate, SaLog log, SaTotpTemplate totpTemplate,
				Map<String, StpLogic> stpLogicMap) {
			this.config = config;
			this.saTokenDao = saTokenDao;
			this.stpInterface = stpInterface;
			this.saTokenContext = saTokenContext;
			this.saTempTemplate = saTempTemplate;
			this.saJsonTemplate = saJsonTemplate;
			this.saHttpTemplate = saHttpTemplate;
			this.saSerializerTemplate = saSerializerTemplate;
			this.saSameTemplate = saSameTemplate;
			this.log = log;
			this.totpTemplate = totpTemplate;
			this.stpLogicMap = stpLogicMap;
		}

		/** 把捕获时的 SaManager 全局状态写回去 */
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
			if (saTokenContext != null) {
				SaManager.setSaTokenContext(saTokenContext);
			}
			if (saTempTemplate != null) {
				SaManager.setSaTempTemplate(saTempTemplate);
			}
			if (saJsonTemplate != null) {
				SaManager.setSaJsonTemplate(saJsonTemplate);
			}
			if (saHttpTemplate != null) {
				SaManager.setSaHttpTemplate(saHttpTemplate);
			}
			if (saSerializerTemplate != null) {
				SaManager.setSaSerializerTemplate(saSerializerTemplate);
			}
			if (saSameTemplate != null) {
				SaManager.setSaSameTemplate(saSameTemplate);
			}
			if (log != null) {
				SaManager.setLog(log);
			}
			if (totpTemplate != null) {
				SaManager.setSaTotpTemplate(totpTemplate);
			}
			SaManager.stpLogicMap.clear();
			SaManager.stpLogicMap.putAll(stpLogicMap);
		}
	}

}
