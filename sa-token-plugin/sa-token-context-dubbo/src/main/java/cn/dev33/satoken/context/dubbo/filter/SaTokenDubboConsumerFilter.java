package cn.dev33.satoken.context.dubbo.filter;

import org.apache.dubbo.common.constants.CommonConstants;
import org.apache.dubbo.common.extension.Activate;
import org.apache.dubbo.rpc.Filter;
import org.apache.dubbo.rpc.Invocation;
import org.apache.dubbo.rpc.Invoker;
import org.apache.dubbo.rpc.Result;
import org.apache.dubbo.rpc.RpcContext;
import org.apache.dubbo.rpc.RpcException;

import cn.dev33.satoken.SaManager;
import cn.dev33.satoken.id.SaIdUtil;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaTokenConsts;

/**
 * 
 * Sa-Token 整合 Dubbo Consumer端过滤器 
 * 
 * @author kong
 *
 */
@Activate(group = {CommonConstants.CONSUMER}, order = -10000)
public class SaTokenDubboConsumerFilter implements Filter {

	@Override
	public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
		
		// 追加 Id-Token 参数 
		if(SaManager.getConfig().getCheckIdToken()) {
			RpcContext.getContext().setAttachment(SaIdUtil.ID_TOKEN, SaIdUtil.getToken()); 
		}
		
		// 1. 调用前，向下传递会话Token 
		RpcContext.getContext().setAttachment(SaTokenConsts.JUST_CREATED, StpUtil.getTokenValueNotCut()); 
		
		// 2. 开始调用 
		Result invoke = invoker.invoke(invocation);
		
		// 3. 调用后，解析回传的Token值 
		StpUtil.setTokenValue(invoke.getAttachment(SaTokenConsts.JUST_CREATED_NOT_PREFIX));
		
		// note 
		return invoke;
	}

}
