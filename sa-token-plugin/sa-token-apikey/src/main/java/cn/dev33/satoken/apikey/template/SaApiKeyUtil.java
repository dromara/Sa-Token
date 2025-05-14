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
package cn.dev33.satoken.apikey.template;

import cn.dev33.satoken.apikey.SaApiKeyManager;
import cn.dev33.satoken.apikey.model.ApiKeyModel;
import cn.dev33.satoken.context.model.SaRequest;
import cn.dev33.satoken.session.SaSession;

import java.util.List;

/**
 * API Key 操作工具类
 *
 * @author click33
 * @since 1.42.0
 */
public class SaApiKeyUtil {

	/**
	 * 获取 ApiKeyModel，无效的 ApiKey 会返回 null
	 * @param apiKey /
	 * @return /
	 */
	public static ApiKeyModel getApiKey(String apiKey) {
		return SaApiKeyManager.getSaApiKeyTemplate().getApiKey(apiKey);
	}

	/**
	 * 校验 ApiKey，成功返回 ApiKeyModel，失败则抛出异常
	 * @param apiKey /
	 * @return /
	 */
	public static ApiKeyModel checkApiKey(String apiKey) {
		return SaApiKeyManager.getSaApiKeyTemplate().checkApiKey(apiKey);
	}

	/**
	 * 持久化：ApiKeyModel
	 * @param ak /
	 */
	public static void saveApiKey(ApiKeyModel ak) {
		SaApiKeyManager.getSaApiKeyTemplate().saveApiKey(ak);
	}

	/**
	 * 获取 ApiKey 所代表的 LoginId
	 * @param apiKey ApiKey
	 * @return LoginId
	 */
	public static Object getLoginIdByApiKey(String apiKey) {
		return SaApiKeyManager.getSaApiKeyTemplate().getLoginIdByApiKey(apiKey);
	}

	/**
	 * 删除 ApiKey
	 * @param apiKey ApiKey
	 */
	public static void deleteApiKey(String apiKey) {
		SaApiKeyManager.getSaApiKeyTemplate().deleteApiKey(apiKey);
	}

	/**
	 * 删除指定 loginId 的所有 ApiKey
	 * @param loginId /
	 */
	public static void deleteApiKeyByLoginId(Object loginId) {
		SaApiKeyManager.getSaApiKeyTemplate().deleteApiKeyByLoginId(loginId);
	}

	// ------- 创建

	/**
	 * 创建一个 ApiKeyModel 对象
	 *
	 * @return /
	 */
	public static ApiKeyModel createApiKeyModel() {
		return SaApiKeyManager.getSaApiKeyTemplate().createApiKeyModel();
	}

	/**
	 * 创建一个 ApiKeyModel 对象
	 *
	 * @return /
	 */
	public static ApiKeyModel createApiKeyModel(Object loginId) {
		return SaApiKeyManager.getSaApiKeyTemplate().createApiKeyModel(loginId);
	}


	// ------------------- Scope

	/**
	 * 判断：指定 ApiKey 是否具有指定 Scope 列表 (AND 模式，需要全部具备)，返回 true 或 false
	 * @param apiKey ApiKey
	 * @param scopes 需要校验的权限列表
	 */
	public static boolean hasApiKeyScope(String apiKey, String... scopes) {
		return SaApiKeyManager.getSaApiKeyTemplate().hasApiKeyScope(apiKey, scopes);
	}

	/**
	 * 校验：指定 ApiKey 是否具有指定 Scope 列表 (AND 模式，需要全部具备)，如果不具备则抛出异常
	 * @param apiKey ApiKey
	 * @param scopes 需要校验的权限列表
	 */
	public static void checkApiKeyScope(String apiKey, String... scopes) {
		SaApiKeyManager.getSaApiKeyTemplate().checkApiKeyScope(apiKey, scopes);
	}

	/**
	 * 判断：指定 ApiKey 是否具有指定 Scope 列表 (OR 模式，具备其一即可)，返回 true 或 false
	 * @param apiKey ApiKey
	 * @param scopes 需要校验的权限列表
	 */
	public static boolean hasApiKeyScopeOr(String apiKey, String... scopes) {
		return SaApiKeyManager.getSaApiKeyTemplate().hasApiKeyScopeOr(apiKey, scopes);
	}

	/**
	 * 校验：指定 ApiKey 是否具有指定 Scope 列表 (OR 模式，具备其一即可)，如果不具备则抛出异常
	 * @param apiKey ApiKey
	 * @param scopes 需要校验的权限列表
	 */
	public static void checkApiKeyScopeOr(String apiKey, String... scopes) {
		SaApiKeyManager.getSaApiKeyTemplate().checkApiKeyScopeOr(apiKey, scopes);
	}

	/**
	 * 判断：指定 ApiKey 是否属于指定 LoginId，返回 true 或 false
	 * @param apiKey /
	 * @param loginId /
	 */
	public static boolean isApiKeyLoginId(String apiKey, Object loginId) {
		return SaApiKeyManager.getSaApiKeyTemplate().isApiKeyLoginId(apiKey, loginId);
	}

	/**
	 * 校验：指定 ApiKey 是否属于指定 LoginId，如果不是则抛出异常
	 *
	 * @param apiKey /
	 * @param loginId /
	 */
	public static void checkApiKeyLoginId(String apiKey, Object loginId) {
		SaApiKeyManager.getSaApiKeyTemplate().checkApiKeyLoginId(apiKey, loginId);
	}


	// ------------------- 请求查询

	/**
	 * 数据读取：从请求对象中读取 ApiKey，获取不到返回 null
	 */
	public static String readApiKeyValue(SaRequest request) {
		return SaApiKeyManager.getSaApiKeyTemplate().readApiKeyValue(request);
	}

	/**
	 * 数据读取：从请求对象中读取 ApiKey，并查询到 ApiKeyModel 信息
	 */
	public static ApiKeyModel currentApiKey() {
		return SaApiKeyManager.getSaApiKeyTemplate().currentApiKey();
	}


	// ------------------- 索引操作

	/**
	 * 调整指定 SaSession 的 TTL 值，以保证最小化内存占用
	 * @param loginId /
	 * @param session 可填写 null，代表使用 loginId 现场查询
	 */
	public static void adjustIndex(Object loginId, SaSession session) {
		SaApiKeyManager.getSaApiKeyTemplate().adjustIndex(loginId, session);
	}

	/**
	 * 获取指定 loginId 的 ApiKey 列表记录
	 * @param loginId /
	 * @return /
	 */
	public static List<ApiKeyModel> getApiKeyList(Object loginId) {
		return SaApiKeyManager.getSaApiKeyTemplate().getApiKeyList(loginId);
	}

}
