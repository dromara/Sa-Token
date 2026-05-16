package cn.dev33.satoken.apikey.loader;

import cn.dev33.satoken.apikey.model.ApiKeyModel;

/**
 * Sa-Token ApiKey 数据加载器
 *
 * @author click33
 * @since 1.41.0
 */
public interface SaApiKeyDataLoader {

	/**
	 * 根据 apiKey 从数据源加载 ApiKeyModel 信息
	 *
	 * @param apiKey /
	 * @return /
	 */
	default ApiKeyModel getApiKeyModel(String apiKey) {
		return null;
	}

	/**
	 * 是否启用缓存模式：
	 * <p> true：读取时优先从缓存中查找，未命中再从数据加载器加载（read-through，默认行为） </p>
	 * <p> false：每次都从数据加载器中查找，缓存仅作为辅助 </p>
	 *
	 * @return /
	 */
	default boolean getIsCache() {
		return true;
	}

}
