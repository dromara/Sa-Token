package cn.dev33.satoken.context.dubbo;

import org.apache.dubbo.rpc.RpcContext;

import cn.dev33.satoken.context.dubbo.model.SaRequestForDubbo;
import cn.dev33.satoken.context.dubbo.model.SaResponseForDubbo;
import cn.dev33.satoken.context.dubbo.model.SaStorageForDubbo;
import cn.dev33.satoken.context.model.SaRequest;
import cn.dev33.satoken.context.model.SaResponse;
import cn.dev33.satoken.context.model.SaStorage;
import cn.dev33.satoken.context.second.SaTokenSecondContext;
import cn.dev33.satoken.exception.ApiDisabledException;

/**
 * Sa-Token 上下文 [Dubbo版本] 
 * 
 * @author click33
 *
 */
public class SaTokenSecondContextForDubbo implements SaTokenSecondContext {

	@Override
	public SaRequest getRequest() {
		return new SaRequestForDubbo(RpcContext.getContext());
	}

	@Override
	public SaResponse getResponse() {
		return new SaResponseForDubbo(RpcContext.getContext());
	}

	@Override
	public SaStorage getStorage() {
		return new SaStorageForDubbo(RpcContext.getContext());
	}

	@Override
	public boolean matchPath(String pattern, String path) {
		throw new ApiDisabledException();
	}

	@Override
	public boolean isValid() {
		return RpcContext.getContext() != null;
	}
	
}
