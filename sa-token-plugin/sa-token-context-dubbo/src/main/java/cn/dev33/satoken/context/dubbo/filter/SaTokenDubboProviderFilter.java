package cn.dev33.satoken.context.dubbo.filter;

import org.apache.dubbo.common.constants.CommonConstants;
import org.apache.dubbo.common.extension.Activate;
import org.apache.dubbo.rpc.Filter;
import org.apache.dubbo.rpc.Invocation;
import org.apache.dubbo.rpc.Invoker;
import org.apache.dubbo.rpc.Result;
import org.apache.dubbo.rpc.RpcException;

import cn.dev33.satoken.SaManager;
import cn.dev33.satoken.id.SaIdUtil;

/**
 * 
 * Sa-Token 整合 Dubbo Provider端过滤器 
 * 
 * @author kong
 *
 */
@Activate(group = {CommonConstants.PROVIDER}, order = -30000)
public class SaTokenDubboProviderFilter implements Filter {

	@Override
	public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
		
		// RPC 调用鉴权 
		if(SaManager.getConfig().getCheckIdToken()) {
			String idToken = invocation.getAttachment(SaIdUtil.ID_TOKEN);
			// dubbo部分协议会将参数变为小写，详细参考：https://gitee.com/dromara/sa-token/issues/I4WXQG
			if(idToken == null) {
				idToken = invocation.getAttachment(SaIdUtil.ID_TOKEN.toLowerCase());
			}
			SaIdUtil.checkToken(idToken);
		}
		
		// 开始调用
		return invoker.invoke(invocation);
	}

}
