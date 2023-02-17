package cn.dev33.satoken.context.dubbo3.model;

import cn.dev33.satoken.context.model.SaRequest;
import org.apache.dubbo.rpc.RpcContext;

/**
 * Request for Dubbo3
 * 
 * @author kong
 *
 */
public class SaRequestForDubbo3 implements SaRequest {

	/**
	 * 底层对象 
	 */
	protected RpcContext rpcContext;
	
	/**
	 * 实例化
	 * @param rpcContext rpcContext对象 
	 */
	public SaRequestForDubbo3(RpcContext rpcContext) {
		this.rpcContext = rpcContext;
	}
	
	/**
	 * 获取底层源对象 
	 */
	@Override
	public Object getSource() {
		return rpcContext;
	}

	/**
	 * 在 [请求体] 里获取一个值 
	 */
	@Override
	public String getParam(String name) {
		// 不传播 url 参数 
		return null;
	}

	/**
	 * 在 [请求头] 里获取一个值 
	 */
	@Override
	public String getHeader(String name) {
		// 不传播 header 参数 
		return null;
	}

	/**
	 * 在 [Cookie作用域] 里获取一个值 
	 */
	@Override
	public String getCookieValue(String name) {
		// 不传播 cookie 参数 
		return null;
	}

	/**
	 * 返回当前请求path (不包括上下文名称)  
	 */
	@Override
	public String getRequestPath() {
		// 不传播 requestPath 
		return null;
	}

	/**
	 * 返回当前请求的url，例：http://xxx.com/test
	 * @return see note
	 */
	public String getUrl() {
		// 不传播 url 
		return null;
	}
	
	/**
	 * 返回当前请求的类型 
	 */
	@Override
	public String getMethod() {
		// 不传播 method 
		return null;
	}

	/**
	 * 转发请求 
	 */
	@Override
	public Object forward(String path) {
		// 不传播 forward 动作 
		return null;
	}

}
