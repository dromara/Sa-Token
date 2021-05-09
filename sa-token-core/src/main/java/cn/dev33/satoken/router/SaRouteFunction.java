package cn.dev33.satoken.router;

import cn.dev33.satoken.context.model.SaRequest;
import cn.dev33.satoken.context.model.SaResponse;

/**
 * 执行验证方法的辅助类
 * 
 * @author kong
 *
 */
@FunctionalInterface
public interface SaRouteFunction {

	/**
	 * 执行验证的方法
	 * 
	 * @param request  Request包装对象
	 * @param response Response包装对象
	 * @param handler  处理对象
	 */
	public void run(SaRequest request, SaResponse response, Object handler);

}
