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
import cn.dev33.satoken.fun.strategy.SaAutoRenewFunction;
import cn.dev33.satoken.fun.strategy.SaCorsHandleFunction;
import cn.dev33.satoken.fun.strategy.SaCreateSaRequestFunction;
import cn.dev33.satoken.fun.strategy.SaCreateSaResponseFunction;
import cn.dev33.satoken.fun.strategy.SaCreateSaStorageFunction;
import cn.dev33.satoken.fun.strategy.SaCreateSessionFunction;
import cn.dev33.satoken.fun.strategy.SaCreateStpLogicFunction;
import cn.dev33.satoken.fun.strategy.SaCreateTokenFunction;
import cn.dev33.satoken.fun.strategy.SaGenerateUniqueTokenFunction;
import cn.dev33.satoken.fun.strategy.SaGetSaTokenConfigFunction;
import cn.dev33.satoken.fun.strategy.SaHasElementFunction;
import cn.dev33.satoken.fun.strategy.SaRouteMatchFunction;
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
import cn.dev33.satoken.session.SaSession;
import cn.dev33.satoken.stp.StpInterface;
import cn.dev33.satoken.stp.StpInterfaceDefaultImpl;
import cn.dev33.satoken.stp.StpLogic;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.strategy.SaStrategy;
import cn.dev33.satoken.temp.SaTempTemplate;

import java.util.HashMap;
import java.util.Map;

/**
 * 单测 / 集成测中 {@link SaManager}、{@link SaStrategy} 全局状态快照与复位。
 *
 * @author click33
 * @since 1.46.0
 */
public final class SaTokenTestContext {

	/** 类加载时记下的策略默认引用，供 {@link #reset()} 写回 */
	private static final StrategySnapshot DEFAULT_STRATEGY = StrategySnapshot.capture();

	private SaTokenTestContext() {
	}

	/**
	 * 将 SaManager 重置为干净的默认状态（独立 DAO、默认配置、默认模板、空 StpLogic 集合），
	 * 并把 {@link SaStrategy#instance} 写回类加载时的默认策略引用。
	 */
	public static void reset() {
		// 先写回策略：setConfig / 打日志会走 SaManager.getConfig()，可能读 getSaTokenConfig
		DEFAULT_STRATEGY.restore();
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
	 * 捕获当前 SaManager / SaStrategy 全局状态，供测试结束后 {@link Snapshot#restore()}。
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
				new HashMap<>(SaManager.stpLogicMap),
				StrategySnapshot.capture()
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
		private final StrategySnapshot strategySnapshot;

		private Snapshot(SaTokenConfig config, SaTokenDao saTokenDao, StpInterface stpInterface,
				SaTokenContext saTokenContext, SaTempTemplate saTempTemplate, SaJsonTemplate saJsonTemplate,
				SaHttpTemplate saHttpTemplate, SaSerializerTemplate saSerializerTemplate,
				SaSameTemplate saSameTemplate, SaLog log, SaTotpTemplate totpTemplate,
				Map<String, StpLogic> stpLogicMap, StrategySnapshot strategySnapshot) {
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
			this.strategySnapshot = strategySnapshot;
		}

		/** 把捕获时的 SaManager / SaStrategy 全局状态写回去 */
		public void restore() {
			strategySnapshot.restore();
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

	/** {@link SaStrategy#instance} 全部可变策略字段的引用快照 */
	private static final class StrategySnapshot {

		private final SaCreateTokenFunction createToken;
		private final SaCreateSessionFunction createSession;
		private final Class<? extends SaSession> sessionClassType;
		private final SaHasElementFunction hasElement;
		private final SaGenerateUniqueTokenFunction generateUniqueToken;
		private final SaAutoRenewFunction autoRenew;
		private final SaCreateStpLogicFunction createStpLogic;
		private final SaRouteMatchFunction routeMatcher;
		private final SaCreateSaRequestFunction createSaRequest;
		private final SaCreateSaResponseFunction createSaResponse;
		private final SaCreateSaStorageFunction createSaStorage;
		private final SaCorsHandleFunction corsHandle;
		private final SaGetSaTokenConfigFunction getSaTokenConfig;

		private StrategySnapshot(SaCreateTokenFunction createToken, SaCreateSessionFunction createSession,
				Class<? extends SaSession> sessionClassType, SaHasElementFunction hasElement,
				SaGenerateUniqueTokenFunction generateUniqueToken, SaAutoRenewFunction autoRenew,
				SaCreateStpLogicFunction createStpLogic, SaRouteMatchFunction routeMatcher,
				SaCreateSaRequestFunction createSaRequest, SaCreateSaResponseFunction createSaResponse,
				SaCreateSaStorageFunction createSaStorage, SaCorsHandleFunction corsHandle,
				SaGetSaTokenConfigFunction getSaTokenConfig) {
			this.createToken = createToken;
			this.createSession = createSession;
			this.sessionClassType = sessionClassType;
			this.hasElement = hasElement;
			this.generateUniqueToken = generateUniqueToken;
			this.autoRenew = autoRenew;
			this.createStpLogic = createStpLogic;
			this.routeMatcher = routeMatcher;
			this.createSaRequest = createSaRequest;
			this.createSaResponse = createSaResponse;
			this.createSaStorage = createSaStorage;
			this.corsHandle = corsHandle;
			this.getSaTokenConfig = getSaTokenConfig;
		}

		private static StrategySnapshot capture() {
			SaStrategy s = SaStrategy.instance;
			return new StrategySnapshot(
					s.createToken,
					s.createSession,
					s.sessionClassType,
					s.hasElement,
					s.generateUniqueToken,
					s.autoRenew,
					s.createStpLogic,
					s.routeMatcher,
					s.createSaRequest,
					s.createSaResponse,
					s.createSaStorage,
					s.corsHandle,
					s.getSaTokenConfig
			);
		}

		private void restore() {
			SaStrategy s = SaStrategy.instance;
			s.createToken = createToken;
			s.createSession = createSession;
			s.sessionClassType = sessionClassType;
			s.hasElement = hasElement;
			s.generateUniqueToken = generateUniqueToken;
			s.autoRenew = autoRenew;
			s.createStpLogic = createStpLogic;
			s.routeMatcher = routeMatcher;
			s.createSaRequest = createSaRequest;
			s.createSaResponse = createSaResponse;
			s.createSaStorage = createSaStorage;
			s.corsHandle = corsHandle;
			s.getSaTokenConfig = getSaTokenConfig;
		}
	}

}
