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
package cn.dev33.satoken.apikey;

import cn.dev33.satoken.SaManager;
import cn.dev33.satoken.apikey.model.ApiKeyModel;
import cn.dev33.satoken.config.SaTokenConfig;
import cn.dev33.satoken.context.SaHolder;
import cn.dev33.satoken.context.model.SaRequest;
import cn.dev33.satoken.dao.SaTokenDao;
import cn.dev33.satoken.error.SaErrorCode;
import cn.dev33.satoken.exception.ApiKeyException;
import cn.dev33.satoken.exception.ApiKeyScopeException;
import cn.dev33.satoken.httpauth.basic.SaHttpBasicUtil;
import cn.dev33.satoken.session.SaSession;
import cn.dev33.satoken.session.SaSessionRawUtil;
import cn.dev33.satoken.strategy.SaStrategy;
import cn.dev33.satoken.util.SaFoxUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * API Key 操作类
 *
 * @author click33
 * @since 1.42.0
 */
public class SaApiKeyTemplate {

	/**
	 * ApiKey 的 raw-session 类型
	 */
	public static final String SESSION_TYPE = "apikey";

	/**
	 * 在 raw-session 中的保存索引列表使用的 key
	 */
	public static final String API_KEY_LIST = "__HD_API_KEY_LIST";

	/**
	 * 网络传输时的参数名称 (字母全小写)
	 */
	public static final String API_KEY_PARAMETER_NAME = "apikey";

	// ------------------- ApiKey

	/**
	 * 根据 apiKey 从 Cache 获取 ApiKeyModel 信息
	 * @param apiKey /
	 * @return /
	 */
	public ApiKeyModel getApiKeyModelFromCache(String apiKey) {
		return getSaTokenDao().getObject(splicingApiKeySaveKey(apiKey), ApiKeyModel.class);
	}

	/**
	 * 根据 apiKey 从 Database 获取 ApiKeyModel 信息
	 * @param apiKey /
	 * @return /
	 */
	public ApiKeyModel getApiKeyModelFromDatabase(String apiKey) {
		return SaManager.getSaApiKeyDataLoader().getApiKeyModelFromDatabase(apiKey);
	}

	/**
	 * 获取 ApiKeyModel，无效的 ApiKey 会返回 null
	 * @param apiKey /
	 * @return /
	 */
	public ApiKeyModel getApiKey(String apiKey) {
		if(apiKey == null) {
			return null;
		}
		// 先从缓存中获取，缓存中找不到就尝试从数据库获取
		ApiKeyModel apiKeyModel = getApiKeyModelFromCache(apiKey);
		if(apiKeyModel == null) {
			apiKeyModel = getApiKeyModelFromDatabase(apiKey);
			saveApiKey(apiKeyModel);
		}
		return apiKeyModel;
	}

	/**
	 * 校验 ApiKey，成功返回 ApiKeyModel，失败则抛出异常
	 * @param apiKey /
	 * @return /
	 */
	public ApiKeyModel checkApiKey(String apiKey) {
		ApiKeyModel ak = getApiKey(apiKey);
		if(ak == null) {
			throw new ApiKeyException("无效 API Key: " + apiKey).setApiKey(apiKey).setCode(SaErrorCode.CODE_12301);
		}
		if(ak.timeExpired()) {
			throw new ApiKeyException("API Key 已过期: " + apiKey).setApiKey(apiKey).setCode(SaErrorCode.CODE_12302);
		}
		if(! ak.getIsValid()) {
			throw new ApiKeyException("API Key 已被禁用: " + apiKey).setApiKey(apiKey).setCode(SaErrorCode.CODE_12303);
		}
		return ak;
	}

	/**
	 * 持久化：ApiKeyModel
	 * @param ak /
	 */
	public void saveApiKey(ApiKeyModel ak) {
		if(ak == null) {
			return;
		}
		// 数据自检
		ak.checkByCanSaved();

		// 保存 ApiKeyModel
		String saveKey = splicingApiKeySaveKey(ak.getApiKey());
		if(ak.timeExpired()) {
			getSaTokenDao().deleteObject(saveKey);
		} else {
			getSaTokenDao().setObject(saveKey, ak, ak.expiresIn());
		}

		// 调整索引
		if (SaManager.getSaApiKeyDataLoader().getIsRecordIndex()) {
			// 记录索引
			SaSession session = SaSessionRawUtil.getSessionById(SESSION_TYPE, ak.getLoginId());
			ArrayList<String> apiKeyList = session.get(API_KEY_LIST, ArrayList::new);
			if(! apiKeyList.contains(ak.getApiKey())) {
				apiKeyList.add(ak.getApiKey());
				session.set(API_KEY_LIST, apiKeyList);
			}

			// 调整 ttl
			adjustIndex(ak.getLoginId(), session);
		}

	}

	/**
	 * 获取 ApiKey 所代表的 LoginId
	 * @param apiKey ApiKey
	 * @return LoginId
	 */
	public Object getLoginIdByApiKey(String apiKey) {
		return checkApiKey(apiKey).getLoginId();
	}

	/**
	 * 删除 ApiKey
	 * @param apiKey ApiKey
	 */
	public void deleteApiKey(String apiKey) {
		// 删 ApiKeyModel
		ApiKeyModel ak = getApiKeyModelFromCache(apiKey);
		if(ak == null) {
			return;
		}
		getSaTokenDao().deleteObject(splicingApiKeySaveKey(apiKey));

		// 删索引
		if(SaManager.getSaApiKeyDataLoader().getIsRecordIndex()) {
			// RawSession 中不存在，提前退出
			SaSession session = SaSessionRawUtil.getSessionById(SESSION_TYPE, ak.getLoginId(), false);
			if(session == null) {
				return;
			}
			// 索引无记录，提前退出
			ArrayList<String> apiKeyList = session.get(API_KEY_LIST, ArrayList::new);
			if(! apiKeyList.contains(apiKey)) {
				return;
			}

			// 如果只有一个 ApiKey，则整个 RawSession 删掉
			if (apiKeyList.size() == 1) {
				SaSessionRawUtil.deleteSessionById(SESSION_TYPE, ak.getLoginId());
			} else {
				// 否则移除此 ApiKey 并保存
				apiKeyList.remove(apiKey);
				session.set(API_KEY_LIST, apiKeyList);
			}
		}
	}

	/**
	 * 删除指定 loginId 的所有 ApiKey
	 * @param loginId /
	 */
	public void deleteApiKeyByLoginId(Object loginId) {
		// 先判断是否开启索引
		if(! SaManager.getSaApiKeyDataLoader().getIsRecordIndex()) {
			SaManager.getLog().warn("当前 API Key 模块未开启索引记录功能，无法执行 deleteApiKeyByLoginId 操作");
			return;
		}

		// RawSession 中不存在，提前退出
		SaSession session = SaSessionRawUtil.getSessionById(SESSION_TYPE, loginId, false);
		if(session == null) {
			return;
		}

		// 先删 ApiKeyModel
		ArrayList<String> apiKeyList = session.get(API_KEY_LIST, ArrayList::new);
		for (String apiKey : apiKeyList) {
			getSaTokenDao().deleteObject(splicingApiKeySaveKey(apiKey));
		}

		// 再删索引
		SaSessionRawUtil.deleteSessionById(SESSION_TYPE, loginId);
	}

	// ------- 创建

	/**
	 * 创建一个 ApiKeyModel 对象
	 *
	 * @return /
	 */
	public ApiKeyModel createApiKeyModel() {
		String apiKey = SaStrategy.instance.generateUniqueToken.execute(
				"API Key",
				SaManager.getConfig().getMaxTryTimes(),
				this::randomApiKeyValue,
				_apiKey -> getApiKey(_apiKey) == null
		);
		return new ApiKeyModel().setApiKey(apiKey);
	}

	/**
	 * 创建一个 ApiKeyModel 对象
	 *
	 * @return /
	 */
	public ApiKeyModel createApiKeyModel(Object loginId) {
		long timeout = SaManager.getConfig().getApiKey().getTimeout();
		long expiresTime = (timeout == SaTokenDao.NEVER_EXPIRE) ? SaTokenDao.NEVER_EXPIRE : System.currentTimeMillis() + timeout * 1000;
		return createApiKeyModel()
				.setLoginId(loginId)
				.setIsValid(true)
				.setExpiresTime(expiresTime)
				;
	}

	/**
	 * 随机一个 ApiKey 码
	 *
	 * @return /
	 */
	public String randomApiKeyValue() {
		return SaManager.getConfig().getApiKey().getPrefix() + SaFoxUtil.getRandomString(36);
	}


	// ------------------- 校验

	/**
	 * 判断：指定 ApiKey 是否具有指定 Scope 列表 (AND 模式，需要全部具备)，返回 true 或 false
	 * @param apiKey ApiKey
	 * @param scopes 需要校验的权限列表
	 */
	public boolean hasApiKeyScope(String apiKey, String... scopes) {
		try {
			checkApiKeyScope(apiKey, scopes);
			return true;
		} catch (ApiKeyException e) {
			return false;
		}
	}

	/**
	 * 校验：指定 ApiKey 是否具有指定 Scope 列表 (AND 模式，需要全部具备)，如果不具备则抛出异常
	 * @param apiKey ApiKey
	 * @param scopes 需要校验的权限列表
	 */
	public void checkApiKeyScope(String apiKey, String... scopes) {
		ApiKeyModel ak = checkApiKey(apiKey);
		if(SaFoxUtil.isEmptyArray(scopes)) {
			return;
		}
		for (String scope : scopes) {
			if(! ak.getScopes().contains(scope)) {
				throw new ApiKeyScopeException("该 API Key 不具备 Scope：" + scope)
						.setApiKey(apiKey)
						.setScope(scope)
						.setCode(SaErrorCode.CODE_12311);
			}
		}
	}

	/**
	 * 判断：指定 ApiKey 是否具有指定 Scope 列表 (OR 模式，具备其一即可)，返回 true 或 false
	 * @param apiKey ApiKey
	 * @param scopes 需要校验的权限列表
	 */
	public boolean hasApiKeyScopeOr(String apiKey, String... scopes) {
		try {
			checkApiKeyScopeOr(apiKey, scopes);
			return true;
		} catch (ApiKeyException e) {
			return false;
		}
	}

	/**
	 * 校验：指定 ApiKey 是否具有指定 Scope 列表 (OR 模式，具备其一即可)，如果不具备则抛出异常
	 * @param apiKey ApiKey
	 * @param scopes 需要校验的权限列表
	 */
	public void checkApiKeyScopeOr(String apiKey, String... scopes) {
		ApiKeyModel ak = checkApiKey(apiKey);
		if(SaFoxUtil.isEmptyArray(scopes)) {
			return;
		}
		for (String scope : scopes) {
			if(ak.getScopes().contains(scope)) {
				return;
			}
		}
		throw new ApiKeyScopeException("该 API Key 不具备 Scope：" + scopes[0])
				.setApiKey(apiKey)
				.setScope(scopes[0])
				.setCode(SaErrorCode.CODE_12311);
	}

	/**
	 * 判断：指定 ApiKey 是否属于指定 LoginId，返回 true 或 false
	 * @param apiKey /
	 * @param loginId /
	 */
	public boolean isApiKeyLoginId(String apiKey, Object loginId) {
		try {
			checkApiKeyLoginId(apiKey, loginId);
			return true;
		} catch (ApiKeyException e) {
			return false;
		}
	}

	/**
	 * 校验：指定 ApiKey 是否属于指定 LoginId，如果不是则抛出异常
	 *
	 * @param apiKey /
	 * @param loginId /
	 */
	public void checkApiKeyLoginId(String apiKey, Object loginId) {
		ApiKeyModel ak = getApiKey(apiKey);
		if(ak == null) {
			throw new ApiKeyException("无效 API Key: " + apiKey).setApiKey(apiKey).setCode(SaErrorCode.CODE_12301);
		}
		if (SaFoxUtil.notEquals(String.valueOf(ak.getLoginId()), String.valueOf(loginId))) {
			throw new ApiKeyException("该 API Key 不属于用户: " + loginId)
					.setApiKey(apiKey)
					.setCode(SaErrorCode.CODE_12312);
		}
	}


	// ------------------- 索引操作

	/**
	 * 调整指定 SaSession 的 TTL 值，以保证最小化内存占用
	 * @param loginId /
	 * @param session 可填写 null，代表使用 loginId 现场查询
	 */
	public void adjustIndex(Object loginId, SaSession session) {
		// 先判断是否开启索引
		if(! SaManager.getSaApiKeyDataLoader().getIsRecordIndex()) {
			SaManager.getLog().warn("当前 API Key 模块未开启索引记录功能，无法执行 adjustIndex 操作");
			return;
		}

		// 未提供则现场查询
		if(session == null) {
			session = SaSessionRawUtil.getSessionById(SESSION_TYPE, loginId, false);
			if(session == null) {
				return;
			}
		}

		// 重新整理索引列表
		ArrayList<String> apiKeyList = session.get(API_KEY_LIST, ArrayList::new);
		ArrayList<String> apiKeyNewList = new ArrayList<>();
		ArrayList<ApiKeyModel> apiKeyModelList = new ArrayList<>();
		for (String apikey : apiKeyList) {
			ApiKeyModel ak = getApiKeyModelFromCache(apikey);
			if(ak == null || ak.timeExpired()) {
				continue;
			}
			apiKeyNewList.add(apikey);
			apiKeyModelList.add(ak);
		}
		session.set(API_KEY_LIST, apiKeyNewList);

		// 调整 SaSession TTL
		long maxTtl = 0;
		for (ApiKeyModel ak : apiKeyModelList) {
			long ttl = ak.expiresIn();
			if(ttl == SaTokenDao.NEVER_EXPIRE) {
				maxTtl = SaTokenDao.NEVER_EXPIRE;
				break;
			}
			if(ttl > maxTtl) {
				maxTtl = ttl;
			}
		}
		if(maxTtl != 0) {
			session.updateTimeout(maxTtl);
		}
	}

	/**
	 * 获取指定 loginId 的 ApiKey 列表记录
	 * @param loginId /
	 * @return /
	 */
	public List<ApiKeyModel> getApiKeyList(Object loginId) {
		// 先判断是否开启索引
		if(! SaManager.getSaApiKeyDataLoader().getIsRecordIndex()) {
			SaManager.getLog().warn("当前 API Key 模块未开启索引记录功能，无法执行 getApiKeyList 操作");
			return new ArrayList<>();
		}

		// 先查 RawSession
		List<ApiKeyModel> apiKeyModelList = new ArrayList<>();
		SaSession session = SaSessionRawUtil.getSessionById(SESSION_TYPE, loginId, false);
		if(session == null) {
			return apiKeyModelList;
		}

		// 从 RawSession 遍历查询
		ArrayList<String> apiKeyList = session.get(API_KEY_LIST, ArrayList::new);
		for (String apikey : apiKeyList) {
			ApiKeyModel ak = getApiKeyModelFromCache(apikey);
			if(ak == null || ak.timeExpired()) {
				continue;
			}
			apiKeyModelList.add(ak);
		}
		return apiKeyModelList;
	}


	// ------------------- 请求查询

	/**
	 * 数据读取：从请求对象中读取 ApiKey，获取不到返回 null
	 */
	public String readApiKeyValue(SaRequest request) {

		// 优先从请求参数中获取
		String apiKey = request.getParam(API_KEY_PARAMETER_NAME);
		if(SaFoxUtil.isNotEmpty(apiKey)) {
			return apiKey;
		}

		// 然后请求头
		apiKey = request.getHeader(API_KEY_PARAMETER_NAME);
		if(SaFoxUtil.isNotEmpty(apiKey)) {
			return apiKey;
		}

		// 最后从 Authorization 中获取
		apiKey = SaHttpBasicUtil.getAuthorizationValue();
		if(SaFoxUtil.isNotEmpty(apiKey)) {
			if(apiKey.endsWith(":")) {
				apiKey = apiKey.substring(0, apiKey.length() - 1);
			}
			return apiKey;
		}

		return null;
	}

	/**
	 * 数据读取：从请求对象中读取 ApiKey，并查询到 ApiKeyModel 信息
	 */
	public ApiKeyModel currentApiKey() {
		String readApiKeyValue = readApiKeyValue(SaHolder.getRequest());
		return checkApiKey(readApiKeyValue);
	}



	// ------------------- 拼接key

	/**
	 * 拼接key：ApiKey 持久化
	 * @param apiKey ApiKey
	 * @return key
	 */
	public String splicingApiKeySaveKey(String apiKey) {
		return getSaTokenConfig().getTokenName() + ":apikey:" + apiKey;
	}


	// -------- bean 对象代理

	/**
	 * 获取使用的 getSaTokenDao 实例
	 *
	 * @return /
	 */
	public SaTokenDao getSaTokenDao() {
		return SaManager.getSaTokenDao();
	}

	/**
	 * 获取使用的 SaTokenConfig 实例
	 *
	 * @return /
	 */
	public SaTokenConfig getSaTokenConfig() {
		return SaManager.getConfig();
	}

	/**
	 * 校验是否开启了索引记录功能，如果未开启则抛出异常
	 */
//	protected void checkOpenRecordIndex() {
//		if(! SaManager.getSaApiKeyDataLoader().getIsRecordIndex()) {
//			SaManager.getLog().warn("当前 API Key 模块未开启索引记录功能，无法执行此操作");
//			throw new ApiKeyException("当前 API Key 模块未开启索引记录功能，无法执行此操作").setCode(SaErrorCode.CODE_12305);
//		}
//	}

}
