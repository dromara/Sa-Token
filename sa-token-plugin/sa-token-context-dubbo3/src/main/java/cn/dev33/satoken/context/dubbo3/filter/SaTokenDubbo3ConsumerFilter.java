package cn.dev33.satoken.context.dubbo3.filter;

import cn.dev33.satoken.SaManager;
import cn.dev33.satoken.context.SaTokenContextDefaultImpl;
import cn.dev33.satoken.same.SaSameUtil;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaTokenConsts;
import org.apache.dubbo.common.constants.CommonConstants;
import org.apache.dubbo.common.extension.Activate;
import org.apache.dubbo.rpc.*;

/**
 * Sa-Token 整合 Dubbo3 Consumer 端（调用端）过滤器
 *
 * @author click33
 * @since <= 1.34.0
 */
@Activate(group = {CommonConstants.CONSUMER}, order = -30000)
public class SaTokenDubbo3ConsumerFilter implements Filter {

	@Override
	public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
		
		// 追加 Same-Token 参数 
		if(SaManager.getConfig().getCheckSameToken()) {
			RpcContext.getServiceContext().setAttachment(SaSameUtil.SAME_TOKEN,SaSameUtil.getToken());
		}
		
		// 1. 调用前，向下传递会话Token
		if(SaManager.getSaTokenContextOrSecond() != SaTokenContextDefaultImpl.defaultContext) {
			RpcContext.getServiceContext().setAttachment(SaTokenConsts.JUST_CREATED, StpUtil.getTokenValueNotCut());
		}

		// 2. 开始调用 
		Result invoke = invoker.invoke(invocation);
		
		// 3. 调用后，解析回传的Token值 
		StpUtil.setTokenValue(invoke.getAttachment(SaTokenConsts.JUST_CREATED_NOT_PREFIX));
		
		// note 
		return invoke;
	}

}
