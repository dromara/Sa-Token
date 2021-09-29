package cn.dev33.satoken.context;

import cn.dev33.satoken.context.model.SaRequest;
import cn.dev33.satoken.context.model.SaResponse;
import cn.dev33.satoken.context.model.SaStorage;

/**
 * Sa-Token 上下文处理器 [ThreadLocal版本] 
 * 
 * <p>
 * 	使用 [ThreadLocal版本] 上下文处理器需要在全局过滤器或者拦截器内率先调用 
 * 	SaTokenContextForThreadLocalStorage.setBox(req,res, sto) 初始化上下文 
 * </p>
 * 
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
