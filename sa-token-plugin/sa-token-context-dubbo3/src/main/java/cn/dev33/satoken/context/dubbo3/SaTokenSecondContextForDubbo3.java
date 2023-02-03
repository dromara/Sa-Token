package cn.dev33.satoken.context.dubbo3;

import cn.dev33.satoken.context.dubbo3.model.SaRequestForDubbo3;
import cn.dev33.satoken.context.dubbo3.model.SaResponseForDubbo3;
import cn.dev33.satoken.context.dubbo3.model.SaStorageForDubbo3;
import cn.dev33.satoken.context.model.SaRequest;
import cn.dev33.satoken.context.model.SaResponse;
import cn.dev33.satoken.context.model.SaStorage;
import cn.dev33.satoken.context.second.SaTokenSecondContext;
import cn.dev33.satoken.exception.ApiDisabledException;
import org.apache.dubbo.rpc.RpcContext;

/**
 * Sa-Token 上下文 [Dubbo3版本]
 * 
 * @author kong
 *
 */
public class SaTokenSecondContextForDubbo3 implements SaTokenSecondContext {

	@Override
	public SaRequest getRequest() {
		return new SaRequestForDubbo3(RpcContext.getServiceContext());
	}

	@Override
	public SaResponse getResponse() {
		return new SaResponseForDubbo3(RpcContext.getServiceContext());
	}

	@Override
	public SaStorage getStorage() {
		return new SaStorageForDubbo3(RpcContext.getServiceContext());
	}

	@Override
	public boolean matchPath(String pattern, String path) {
		throw new ApiDisabledException();
	}

	@Override
	public boolean isValid() {
		return RpcContext.getServiceContext() != null;
	}
	
}
