package cn.dev33.satoken.context;

import cn.dev33.satoken.context.model.SaRequest;
import cn.dev33.satoken.context.model.SaResponse;
import cn.dev33.satoken.context.model.SaStorage;

/**
 * Sa-Token 上下文处理器 [ ThreadLocal 版本 ]
 * 
 * <p>
 * 	使用 [ ThreadLocal 版本 ] 上下文处理器需要在全局过滤器或者拦截器内率先调用
 * 	SaTokenContextForThreadLocalStorage.setBox(req, res, sto) 初始化上下文
 * </p>
 *
 * <p> 一般情况下你不需要直接操作此类，因为框架的 starter 集成包里已经封装了完整的上下文操作 </p>
 *
 * @author click33
 * @since <= 1.34.0
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

	@Override
	public boolean isValid() {
		return SaTokenContextForThreadLocalStorage.getBox() != null;
	}

}
