package cn.dev33.satoken.context.dubbo3.model;

import cn.dev33.satoken.context.model.SaStorage;
import cn.dev33.satoken.util.SaTokenConsts;
import org.apache.dubbo.rpc.RpcContext;

/**
 * Storage for Servlet 
 * @author click33
 *
 */
public class SaStorageForDubbo3 implements SaStorage {

	/**
	 * 底层对象 
	 */
	protected RpcContext rpcContext;
	
	/**
	 * 实例化
	 * @param rpcContext rpcContext对象 
	 */
	public SaStorageForDubbo3(RpcContext rpcContext) {
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
	 * 在 [Request作用域] 里写入一个值 
	 */
	@Override
	public SaStorageForDubbo3 set(String key, Object value) {
		rpcContext.setObjectAttachment(key, value);
		// 如果是token写入，则回传到Consumer端  
		if(key.equals(SaTokenConsts.JUST_CREATED_NOT_PREFIX)) {
			RpcContext.getServerContext().setAttachment(key, value);
		}
		return this;
	}

	/**
	 * 在 [Request作用域] 里获取一个值 
	 */
	@Override
	public Object get(String key) {
		return rpcContext.getObjectAttachment(key);
	}

	/**
	 * 在 [Request作用域] 里删除一个值 
	 */
	@Override
	public SaStorageForDubbo3 delete(String key) {
		rpcContext.removeAttachment(key);
		return this;
	}

}
