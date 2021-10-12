package cn.dev33.satoken.context;

import cn.dev33.satoken.context.model.SaRequest;
import cn.dev33.satoken.context.model.SaStorage;
import cn.dev33.satoken.context.model.SaResponse;
import cn.dev33.satoken.exception.SaTokenException;

/**
 * Sa-Token 上下文处理器 [默认实现类]
 * 
 * <p>  
 * 一般情况下框架会为你自动注入合适的上下文处理器，如果代码断点走到了此默认实现类，
 * 说明你引入的依赖有问题或者错误的调用了Sa-Token的API， 请在[在线开发文档 → 附录 → 常见问题排查] 中按照提示进行排查
 * </p>
 * 
 * @author kong
 *
 */
public class SaTokenContextDefaultImpl implements SaTokenContext {

	/**
	 * 默认的错误提示语 
	 */
	public static final String ERROR_MESSAGE = "未初始化任何有效上下文处理器";
	
	/**
	 * 获取当前请求的 [Request] 对象
	 */
	@Override
	public SaRequest getRequest() {
		throw new SaTokenException(ERROR_MESSAGE);
	}
	
	/**
	 * 获取当前请求的 [Response] 对象
	 */
	@Override
	public SaResponse getResponse() {
		throw new SaTokenException(ERROR_MESSAGE);
	}

	/**
	 * 获取当前请求的 [存储器] 对象 
	 */
	@Override
	public SaStorage getStorage() {
		throw new SaTokenException(ERROR_MESSAGE);
	}
	
	/**
	 * 校验指定路由匹配符是否可以匹配成功指定路径 
	 */
	@Override
	public boolean matchPath(String pattern, String path) {
		throw new SaTokenException(ERROR_MESSAGE);
	}

	

}
