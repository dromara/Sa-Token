package cn.dev33.satoken.context.dubbo.model;

import org.apache.dubbo.rpc.RpcContext;

import cn.dev33.satoken.context.model.SaResponse;

/**
 * Response for Servlet
 * @author click33
 *
 */
public class SaResponseForDubbo implements SaResponse {

	/**
	 * 底层Request对象 
	 */
	protected RpcContext rpcContext;
	
	/**
	 * 实例化
	 * @param rpcContext rpcContext对象 
	 */
	public SaResponseForDubbo(RpcContext rpcContext) {
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
	 * 设置响应状态码 
	 */
	@Override
	public SaResponse setStatus(int sc) {
		// 不回传 status 状态 
		return this;
	}
	
	/**
	 * 在响应头里写入一个值 
	 */
	@Override
	public SaResponse setHeader(String name, String value) {
		// 不回传 header响应头 
		return this;
	}

	/**
	 * 在响应头里添加一个值 
	 * @param name 名字
	 * @param value 值 
	 * @return 对象自身 
	 */
	public SaResponse addHeader(String name, String value) {
		// 不回传 header响应头 
		return this;
	}
	
	/**
	 * 重定向 
	 */
	@Override
	public Object redirect(String url) {
		// 不回传 重定向 动作 
		return null;
	}

	
}
