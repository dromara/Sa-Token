package cn.dev33.satoken.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.HandlerInterceptor;

import cn.dev33.satoken.exception.BackResultException;
import cn.dev33.satoken.exception.StopMatchException;
import cn.dev33.satoken.router.SaRouteFunction;
import cn.dev33.satoken.servlet.model.SaRequestForServlet;
import cn.dev33.satoken.servlet.model.SaResponseForServlet;
import cn.dev33.satoken.stp.StpUtil;

/**
 * Sa-Token 拦截式鉴权 - 拦截器 
 * <h2> [ 当前拦截器写法已过期，可能将在以后的版本删除，推荐升级为 SaInterceptor ] </h2>
 * 
 * @author kong
 */
@Deprecated
public class SaRouteInterceptor implements HandlerInterceptor {

	/**
	 * 每次进入拦截器的[执行函数]，默认为登录校验 
	 */
	public SaRouteFunction function = (req, res, handler) -> StpUtil.checkLogin();

	/**
	 * 创建一个路由拦截器
	 */
	public SaRouteInterceptor() {
	}

	/**
	 * 创建, 并指定[执行函数]
	 * @param function [执行函数]
	 */
	public SaRouteInterceptor(SaRouteFunction function) {
		this.function = function;
	}

	/**
	 * 静态方法快速构建一个 
	 * @param function 自定义模式下的执行函数
	 * @return sa路由拦截器 
	 */
	public static SaRouteInterceptor newInstance(SaRouteFunction function) {
		return new SaRouteInterceptor(function);
	}
	
	
	// ----------------- 验证方法 ----------------- 

	/**
	 * 每次请求之前触发的方法 
	 */
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		
		try {
			function.run(new SaRequestForServlet(request), new SaResponseForServlet(response), handler);
		} catch (StopMatchException e) {
			// 停止匹配，进入Controller 
		} catch (BackResultException e) {
			// 停止匹配，向前端输出结果 
			if(response.getContentType() == null) {
				response.setContentType("text/plain; charset=utf-8"); 
			}
			response.getWriter().print(e.getMessage());
			return false;
		}
		
		// 通过验证 
		return true;
	}

}
