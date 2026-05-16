package cn.dev33.satoken.apikey.template;

import cn.dev33.satoken.SaManager;
import cn.dev33.satoken.apikey.SaApiKeyManager;
import cn.dev33.satoken.apikey.exception.ApiKeyException;
import cn.dev33.satoken.apikey.loader.SaApiKeyDataLoader;
import cn.dev33.satoken.apikey.model.ApiKeyModel;
import cn.dev33.satoken.dao.SaTokenDao;
import cn.dev33.satoken.error.SaErrorCode;
import cn.dev33.satoken.secure.SaSecureUtil;
import cn.dev33.satoken.util.SaFoxUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Sa-Token API Key 操作模板类
 *
 * @author click33
 * @since 1.41.0
 */
public class SaApiKeyTemplate {

	public static final String API_KEY_SAVE_KEY = "apikey:";
	public static final String API_KEY_INDEX_SAVE_KEY = "apikey-index:";

	public SaTokenDao getSaTokenDao() {
		return SaManager.getSaTokenDao();
	}

	public String splicingApiKeySaveKey(String apiKey) {
		return SaManager.getConfig().getTokenName() + ":" + API_KEY_SAVE_KEY + apiKey;
	}

	public String splicingApiKeyIndexSaveKey(Object loginId) {
		return SaManager.getConfig().getTokenName() + ":" + API_KEY_INDEX_SAVE_KEY + loginId;
	}

	public ApiKeyModel createApiKeyModel(Object loginId) {
		return SaApiKeyManager.getDataLoader().createApiKeyModel(loginId);
	}

	public ApiKeyModel getApiKey(String apiKey) {
		return getApiKey(apiKey, !SaApiKeyManager.getDataLoader().getIsAlwaysFromDataLoader());
	}

	public ApiKeyModel getApiKey(String apiKey, boolean fromCache) {
		if (SaFoxUtil.isEmpty(apiKey)) {
			return null;
		}

		if (fromCache) {
			ApiKeyModel akModel = (ApiKeyModel) getSaTokenDao().getObject(splicingApiKeySaveKey(apiKey));
			if (akModel != null) {
				return akModel;
			}
		}

		SaApiKeyDataLoader dataLoader = SaApiKeyManager.getDataLoader();
		ApiKeyModel akModel = dataLoader.getApiKeyFromDatabase(apiKey);
		if (akModel == null) {
			return null;
		}

		if (dataLoader.getIsCacheable(akModel)) {
			saveApiKey(akModel);
		}

		return akModel;
	}

	public ApiKeyModel checkApiKey(String apiKey) {
		ApiKeyModel akModel = getApiKey(apiKey);
		if (akModel == null) {
			throw new ApiKeyException("无效 API Key:" + apiKey).setCode(SaErrorCode.CODE_12301);
		}
		if (!akModel.getIsValid()) {
			throw new ApiKeyException("API Key 已被禁用:" + apiKey).setCode(SaErrorCode.CODE_12302);
		}
		if (akModel.getExpiresTime() != -1 && akModel.getExpiresTime() < System.currentTimeMillis()) {
			throw new ApiKeyException("API Key 已过期:" + apiKey).setCode(SaErrorCode.CODE_12303);
		}
		return akModel;
	}

	public void saveApiKey(ApiKeyModel akModel) {
		if (akModel == null || SaFoxUtil.isEmpty(akModel.getApiKey())) {
			throw new ApiKeyException("API Key 不能为空").setCode(SaErrorCode.CODE_12311);
		}
		long timeout = akModel.getExpiresTime() == -1 ? -1
				: (akModel.getExpiresTime() - System.currentTimeMillis()) / 1000;
		getSaTokenDao().setObject(splicingApiKeySaveKey(akModel.getApiKey()), akModel, timeout);
		adddApiKeyIndex(akModel);
	}

	public void deleteApiKey(String apiKey) {
		ApiKeyModel akModel = getApiKey(apiKey);
		if (akModel == null) {
			return;
		}
		getSaTokenDao().deleteObject(splicingApiKeySaveKey(apiKey));
		deleteApiKeyIndex(akModel);
	}

	@SuppressWarnings("unchecked")
	public List<String> getApiKeyList(Object loginId) {
		List<String> list = (List<String>) getSaTokenDao().getObject(splicingApiKeyIndexSaveKey(loginId));
		return list == null ? new ArrayList<>() : list;
	}

	public void adddApiKeyIndex(ApiKeyModel akModel) {
		List<String> list = getApiKeyList(akModel.getLoginId());
		if (!list.contains(akModel.getApiKey())) {
			list.add(akModel.getApiKey());
			getSaTokenDao().setObject(splicingApiKeyIndexSaveKey(akModel.getLoginId()), list, -1);
		}
	}

	public void deleteApiKeyIndex(ApiKeyModel akModel) {
		List<String> list = getApiKeyList(akModel.getLoginId());
		if (list.contains(akModel.getApiKey())) {
			list.remove(akModel.getApiKey());
			getSaTokenDao().setObject(splicingApiKeyIndexSaveKey(akModel.getLoginId()), list, -1);
		}
	}

	public String randomApiKeyValue() {
		return SaSecureUtil.md5(SaFoxUtil.getRandomString(32));
	}

}
