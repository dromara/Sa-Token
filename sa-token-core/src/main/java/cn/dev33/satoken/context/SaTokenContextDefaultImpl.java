package cn.dev33.satoken.context;

import cn.dev33.satoken.context.model.SaRequest;
import cn.dev33.satoken.context.model.SaResponse;
import cn.dev33.satoken.exception.SaTokenException;

/**
 * Sa-Token 与底层容器交互接口 [默认实现类] 
 * @author kong
 *
 */
public class SaTokenContextDefaultImpl implements SaTokenContext {

	/**
	 * 获取当前请求的Request对象
	 */
	@Override
	public SaRequest getRequest() {
		throw new SaTokenException("未初始化任何有效容器");
	}

	/**
	 * 获取当前请求的Response对象
	 */
	@Override
	public SaResponse getResponse() {
		throw new SaTokenException("未初始化任何有效容器");
	}

	/**
	 * 校验指定路由匹配符是否可以匹配成功指定路径 
	 */
	@Override
	public boolean matchPath(String pattern, String path) {
		throw new SaTokenException("未初始化任何有效容器");
	}

}
