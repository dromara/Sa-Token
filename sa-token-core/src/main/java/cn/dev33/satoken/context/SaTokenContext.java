package cn.dev33.satoken.context;

import cn.dev33.satoken.context.model.SaRequest;
import cn.dev33.satoken.context.model.SaStorage;
import cn.dev33.satoken.context.model.SaResponse;

/**
 * Sa-Token 上下文处理器
 * @author kong
 *
 */
public interface SaTokenContext {

	/**
	 * 获取当前请求的 [Request] 对象
	 * 
	 * @return see note 
	 */
	public SaRequest getRequest();

	/**
	 * 获取当前请求的 [Response] 对象
	 * 
	 * @return see note 
	 */
	public SaResponse getResponse();

	/**
	 * 获取当前请求的 [存储器] 对象 
	 * 
	 * @return see note 
	 */
	public SaStorage getStorage();

	/**
	 * 校验指定路由匹配符是否可以匹配成功指定路径 
	 * 
	 * @param pattern 路由匹配符 
	 * @param path 需要匹配的路径 
	 * @return see note 
	 */
	public boolean matchPath(String pattern, String path);

	/**
	 * 此上下文是否有效 
	 * @return / 
	 */
	public default boolean isValid() {
		return false;
	}
	
}
