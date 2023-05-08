package cn.dev33.satoken.context;

import cn.dev33.satoken.SaManager;
import cn.dev33.satoken.application.SaApplication;
import cn.dev33.satoken.context.model.SaRequest;
import cn.dev33.satoken.context.model.SaResponse;
import cn.dev33.satoken.context.model.SaStorage;

/**
 * Sa-Token 上下文持有类，你可以通过此类快速获取当前环境下的 SaRequest、SaResponse、SaStorage、SaApplication 对象。
 *
 * @author click33
 * @since <= 1.34.0
 */
public class SaHolder {
	
	/**
	 * 获取当前请求的 SaTokenContext 上下文对象
	 * @see SaTokenContext
	 * 
	 * @return /
	 */
	public static SaTokenContext getContext() {
		return SaManager.getSaTokenContextOrSecond();
	}

	/**
	 * 获取当前请求的 Request 包装对象
	 * @see SaRequest
	 * 
	 * @return /
	 */
	public static SaRequest getRequest() {
		return SaManager.getSaTokenContextOrSecond().getRequest();
	}

	/**
	 * 获取当前请求的 Response 包装对象
	 * @see SaResponse
	 * 
	 * @return /
	 */
	public static SaResponse getResponse() {
		return SaManager.getSaTokenContextOrSecond().getResponse();
	}

	/**
	 * 获取当前请求的 Storage 包装对象
	 * @see SaStorage
	 *
	 * @return /
	 */
	public static SaStorage getStorage() {
		return SaManager.getSaTokenContextOrSecond().getStorage();
	}

	/**
	 * 获取全局 SaApplication 对象
	 * @see SaApplication
	 * 
	 * @return /
	 */
	public static SaApplication getApplication() {
		return SaApplication.defaultInstance;
	}

}
