package cn.dev33.satoken.interceptor;

import java.util.Arrays;
import java.util.List;

import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;

import cn.dev33.satoken.SaTokenManager;
import cn.dev33.satoken.autowired.SaTokenSpringAutowired;
import cn.dev33.satoken.fun.IsRunFunction;
import cn.dev33.satoken.fun.SaFunction;

/**
 * 对路由匹配符相关操作的封装工具类 
 * @author kong
 *
 */
public class SaRouterUtil {

	/**
	 * 在进行路由匹配时所使用的的 PathMatcher 对象 
	 */
	private static PathMatcher pathMatcher;

	/**
	 * @return 在进行路由匹配时所使用的的 PathMatcher 对象
	 */
	public static PathMatcher getPathMatcher() {
		if(pathMatcher == null) {
			pathMatcher = SaTokenSpringAutowired.pathMatcher;
			if(pathMatcher == null) {
				pathMatcher = new AntPathMatcher();
			}
		}
		return pathMatcher;
	}

	/**
	 * @param pathMatcher 写入: 在进行路由匹配时所使用的的 PathMatcher 对象
	 */
	public static void setPathMatcher(PathMatcher pathMatcher) {
		SaRouterUtil.pathMatcher = pathMatcher;
	}
	
	
	// -------------------- 路由匹配相关 -------------------- 
	
	/**
	 * 校验指定路由匹配符是否可以匹配成功指定路径  
	 * @param pattern 路由匹配符 
	 * @param path 需要匹配的路径 
	 * @return 是否匹配成功 
	 */
	public static boolean isMatch(String pattern, String path) {
		if(getPathMatcher().match(pattern, path)) {
			return true;
		}
		return false;
	}

	/**
	 * 校验指定路由匹配符是否可以匹配成功指定路径   
	 * @param pattern 路由匹配符 
	 * @param path 需要匹配的路径集合 
	 * @return 是否匹配成功 
	 */
	public static boolean isMatch(List<String> patterns, String path) {
		for (String pattern : patterns) {
			if(isMatch(pattern, path)) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 校验指定路由匹配符是否可以匹配成功当前URI
	 * @param pattern 路由匹配符 
	 * @return 是否匹配成功 
	 */
	public static boolean isMatchCurrURI(String pattern) {
		return isMatch(pattern, SaTokenManager.getSaTokenServlet().getRequest().getRequestURI());
	}

	/**
	 * 校验指定路由匹配符是否可以匹配成功当前URI 
	 * @param pattern 路由匹配符 
	 * @return 是否匹配成功 
	 */
	public static boolean isMatchCurrURI(List<String> patterns) {
		return isMatch(patterns, SaTokenManager.getSaTokenServlet().getRequest().getRequestURI());
	}
	

	// -------------------- 执行相关 -------------------- 
	
	/**
	 * 使用路由匹配符与当前URI执行匹配，如果匹配成功则执行验证函数 
	 * @param pattern 路由匹配符
	 * @param function 要执行的方法 
	 */
	public static void match(String pattern, SaFunction function) {
		if(isMatchCurrURI(pattern)) {
			function.run();
		}
	}

	/**
	 * 使用路由匹配符与当前URI执行匹配 (并指定排除匹配符)，如果匹配成功则执行验证函数 
	 * @param pattern 路由匹配符 
	 * @param excludePattern 要排除的路由匹配符 
	 * @param function 要执行的方法 
	 */
	public static void match(String pattern, String excludePattern, SaFunction function) {
		if(isMatchCurrURI(pattern)) {
			if(isMatchCurrURI(excludePattern) == false) {
				function.run();
			}
		}
	}

	/**
	 * 使用路由匹配符集合与当前URI执行匹配，如果匹配成功则执行验证函数 
	 * @param patterns 路由匹配符集合
	 * @param function 要执行的方法 
	 */
	public static void match(List<String> patterns, SaFunction function) {
		if(isMatchCurrURI(patterns)) {
			function.run();
		}
	}

	/**
	 * 使用路由匹配符集合与当前URI执行匹配 (并指定排除匹配符)，如果匹配成功则执行验证函数 
	 * @param patterns 路由匹配符集合
	 * @param excludePatterns 要排除的路由匹配符集合
	 * @param function 要执行的方法 
	 */
	public static void match(List<String> patterns, List<String> excludePatterns, SaFunction function) {
		if(isMatchCurrURI(patterns)) {
			if(isMatchCurrURI(excludePatterns) == false) {
				function.run();
			}
		}
	}
	
	
	/**
	 * 使用路由匹配符集合与当前URI执行匹配，如果匹配成功则执行验证函数 
	 * @param patterns 路由匹配符集合
	 * @return 匹配结果包装对象 
	 */
	public static IsRunFunction match(String... patterns) {
		boolean matchResult = isMatch(Arrays.asList(patterns), SaTokenManager.getSaTokenServlet().getRequest().getRequestURI());
		return new IsRunFunction(matchResult);
	}
	
	
	
	
	
}
