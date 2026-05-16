/*
 * Copyright 2020-2099 sa-token.cc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cn.dev33.satoken.apikey;

import cn.dev33.satoken.apikey.model.ApiKeyModel;
import cn.dev33.satoken.apikey.template.SaApiKeyTemplate;

import java.util.List;

/**
 * Sa-Token API Key 工具类
 *
 * @author click33
 * @since 1.41.0
 */
public class SaApiKeyUtil {

	private SaApiKeyUtil() {
	}

	/**
	 * 底层使用的 API Key 操作模板对象
	 */
	public static SaApiKeyTemplate apiKeyTemplate = new SaApiKeyTemplate();

	/**
	 * 校验 ApiKey, 返回其代表的 LoginId, 如果校验失败则抛出异常
	 *
	 * @param apiKey /
	 * @return /
	 */
	public static Object checkApiKey(String apiKey) {
		return apiKeyTemplate.checkApiKey(apiKey);
	}

	/**
	 * 校验 ApiKey 是否具有指定 Scope, 如果校验失败则抛出异常
	 *
	 * @param apiKey /
	 * @param scopes /
	 */
	public static void checkApiKeyScope(String apiKey, String... scopes) {
		apiKeyTemplate.checkApiKeyScope(apiKey, scopes);
	}

	/**
	 * 校验 ApiKey 是否具有指定 Scope (指定多个,只要具有其中一个就通过校验), 如果校验失败则抛出异常
	 *
	 * @param apiKey /
	 * @param scopes /
	 */
	public static void checkApiKeyScopeOr(String apiKey, String... scopes) {
		apiKeyTemplate.checkApiKeyScopeOr(apiKey, scopes);
	}

	/**
	 * 判断一个 ApiKey 是否有效
	 *
	 * @param apiKey /
	 * @return /
	 */
	public static boolean isValid(String apiKey) {
		return apiKeyTemplate.isValid(apiKey);
	}

	/**
	 * 判断一个 ApiKey 是否拥有指定 Scope
	 *
	 * @param apiKey /
	 * @param scope /
	 * @return /
	 */
	public static boolean hasApiKeyScope(String apiKey, String scope) {
		return apiKeyTemplate.hasApiKeyScope(apiKey, scope);
	}

	/**
	 * 获取一个 ApiKey 的 Scope 列表
	 *
	 * @param apiKey /
	 * @return /
	 */
	public static List<String> getApiKeyScopeList(String apiKey) {
		return apiKeyTemplate.getApiKeyScopeList(apiKey);
	}

	/**
	 * 获取 ApiKey 信息, 如果 ApiKey 无效则返回 null
	 *
	 * @param apiKey /
	 * @return /
	 */
	public static ApiKeyModel getApiKey(String apiKey) {
		return apiKeyTemplate.getApiKey(apiKey);
	}

	/**
	 * 获取 ApiKey 信息, 如果 ApiKey 无效则返回 null
	 *
	 * @param apiKey /
	 * @param noCache 是否绕过缓存,直接从数据加载器读取
	 * @return /
	 */
	public static ApiKeyModel getApiKey(String apiKey, boolean noCache) {
		return apiKeyTemplate.getApiKey(apiKey, noCache);
	}

	/**
	 * 直接从数据加载器获取 ApiKey 信息(不走缓存), 如果 ApiKey 无效则返回 null
	 *
	 * @param apiKey /
	 * @return /
	 */
	public static ApiKeyModel getApiKeyFromDataLoader(String apiKey) {
		return apiKeyTemplate.getApiKeyFromDataLoader(apiKey);
	}

	/**
	 * 创建一个 ApiKey (只是返回模型,不会保存到数据源中)
	 *
	 * @param loginId /
	 * @return /
	 */
	public static ApiKeyModel createApiKeyModel(Object loginId) {
		return apiKeyTemplate.createApiKeyModel(loginId);
	}

	/**
	 * 保存一个 ApiKey 到数据源
	 *
	 * @param apiKeyModel /
	 */
	public static void saveApiKey(ApiKeyModel apiKeyModel) {
		apiKeyTemplate.saveApiKey(apiKeyModel);
	}

	/**
	 * 修改一个 ApiKey 的信息
	 *
	 * @param apiKeyModel /
	 */
	public static void editApiKey(ApiKeyModel apiKeyModel) {
		apiKeyTemplate.editApiKey(apiKeyModel);
	}

	/**
	 * 删除一个 ApiKey
	 *
	 * @param apiKey /
	 */
	public static void deleteApiKey(String apiKey) {
		apiKeyTemplate.deleteApiKey(apiKey);
	}

	/**
	 * 调整 ApiKey 的剩余有效时间, 单位:秒
	 *
	 * @param apiKey /
	 * @param expiresTime 单位:秒, -1 代表永久有效
	 */
	public static void adjustApiKeyExpireTime(String apiKey, long expiresTime) {
		apiKeyTemplate.adjustApiKeyExpireTime(apiKey, expiresTime);
	}

	/**
	 * 获取指定账号的 ApiKey 列表记录
	 *
	 * @param loginId /
	 * @return /
	 */
	public static List<ApiKeyModel> getApiKeyList(Object loginId) {
		return apiKeyTemplate.getApiKeyList(loginId);
	}

	/**
	 * 删除指定账号下的所有 ApiKey
	 *
	 * @param loginId /
	 */
	public static void deleteApiKeyByLoginId(Object loginId) {
		apiKeyTemplate.deleteApiKeyByLoginId(loginId);
	}

}
