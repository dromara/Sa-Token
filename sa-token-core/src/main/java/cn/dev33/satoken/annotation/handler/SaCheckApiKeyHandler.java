package cn.dev33.satoken.annotation.handler;

import cn.dev33.satoken.annotation.SaCheckApiKey;
import cn.dev33.satoken.apikey.SaApiKeyUtil;
import cn.dev33.satoken.apikey.model.ApiKeyModel;
import cn.dev33.satoken.context.SaHolder;
import cn.dev33.satoken.exception.ApiKeyException;

import java.lang.reflect.Method;

/**
 * 注解 SaCheckApiKey 的处理器
 *
 * @author click33
 */
public class SaCheckApiKeyHandler implements SaAnnotationHandlerInterface<SaCheckApiKey> {

	@Override
	public Class<SaCheckApiKey> getHandlerAnnotationClass() {
		return SaCheckApiKey.class;
	}

	@Override
	public void checkMethod(SaCheckApiKey at, Method method) {
		_checkMethod(at.value());
	}

	public static void _checkMethod(String... scopes) {
		String apiKey = SaApiKeyUtil.readApiKeyValue(SaHolder.getRequest());
		ApiKeyModel apiKeyModel = SaApiKeyUtil.getApiKey(apiKey);
		if (apiKeyModel == null) {
			throw new ApiKeyException("无效的 API Key:" + apiKey);
		}
		SaApiKeyUtil.checkApiKeyScope(apiKeyModel, scopes);
	}

}
