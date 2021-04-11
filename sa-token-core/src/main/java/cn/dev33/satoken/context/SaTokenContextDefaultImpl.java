package cn.dev33.satoken.context;

import cn.dev33.satoken.context.model.SaRequest;
import cn.dev33.satoken.context.model.SaStorage;
import cn.dev33.satoken.context.model.SaResponse;
import cn.dev33.satoken.exception.SaTokenException;

/**
 * Sa-Token 上下文处理器 [默认实现类] 
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
