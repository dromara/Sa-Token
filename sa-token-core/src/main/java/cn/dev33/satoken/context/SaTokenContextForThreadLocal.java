package cn.dev33.satoken.context;

import cn.dev33.satoken.context.model.SaRequest;
import cn.dev33.satoken.context.model.SaResponse;
import cn.dev33.satoken.context.model.SaStorage;

/**
 * 上下文环境 [ThreadLocal版本] 
 * @author kong
 *
 */
public class SaTokenContextForThreadLocal implements SaTokenContext {

	@Override
	public SaRequest getRequest() {
		return SaTokenContextForThreadLocalStorage.getRequest();
	}

	@Override
	public SaResponse getResponse() {
		return SaTokenContextForThreadLocalStorage.getResponse();
	}

	@Override
	public SaStorage getStorage() {
		return SaTokenContextForThreadLocalStorage.getStorage();
	}

	@Override
	public boolean matchPath(String pattern, String path) {
		return false;
	}

}
