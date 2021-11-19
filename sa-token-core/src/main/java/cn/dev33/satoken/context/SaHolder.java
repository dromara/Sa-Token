package cn.dev33.satoken.context;

import cn.dev33.satoken.SaManager;
import cn.dev33.satoken.context.model.SaRequest;
import cn.dev33.satoken.context.model.SaResponse;
import cn.dev33.satoken.context.model.SaStorage;

/**
 * Sa-Token 上下文持有类 
 * @author kong
 *
 */
public class SaHolder {
	
	/**
	 * 获取当前请求的 SaTokenContext
	 * 
	 * @return see note 
	 */
	public static SaTokenContext getContext() {
		return SaManager.getSaTokenContextOrSecond();
	}

	/**
	 * 获取当前请求的 [Request] 对象
	 * 
	 * @return see note 
	 */
	public static SaRequest getRequest() {
		return SaManager.getSaTokenContextOrSecond().getRequest();
	}

	/**
	 * 获取当前请求的 [Response] 对象
	 * 
	 * @return see note 
	 */
	public static SaResponse getResponse() {
		return SaManager.getSaTokenContextOrSecond().getResponse();
	}

	/**
	 * 获取当前请求的 [存储器] 对象 
	 * 
	 * @return see note 
	 */
	public static SaStorage getStorage() {
		return SaManager.getSaTokenContextOrSecond().getStorage();
	}

}
