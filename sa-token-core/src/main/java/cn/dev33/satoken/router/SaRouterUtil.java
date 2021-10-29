package cn.dev33.satoken.router;

import java.util.Arrays;
import java.util.List;

import cn.dev33.satoken.SaManager;
import cn.dev33.satoken.context.SaHolder;
import cn.dev33.satoken.exception.StopMatchException;
import cn.dev33.satoken.fun.IsRunFunction;
import cn.dev33.satoken.fun.SaFunction;

/**
 * <h1> 本类设计已过时，未来版本可能移除此类，请及时更换为 SaRouter ，使用方式保持不变 </h1>
 * 对路由匹配符相关操作的封装工具类 
 * @author kong
 *
 */
@Deprecated
public class SaRouterUtil {

	// -------------------- 路由匹配相关 -------------------- 
	
	/**
	 * 校验指定路由匹配符是否可以匹配成功指定路径  
	 * @param pattern 路由匹配符 
	 * @param path 需要匹配的路径 
	 * @return 是否匹配成功 
	 */
	public static boolean isMatch(String pattern, String path) {
		return SaManager.getSaTokenContextOrSecond().matchPath(pattern, path);
	}

	/**
	 * 校验指定路由匹配符是否可以匹配成功指定路径   
	 * @param patterns 路由匹配符 
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
		return isMatch(pattern, SaHolder.getRequest().getRequestPath());
	}

	/**
	 * 校验指定路由匹配符是否可以匹配成功当前URI 
	 * @param patterns 路由匹配符 
	 * @return 是否匹配成功 
	 */
	public static boolean isMatchCurrURI(List<String> patterns) {
		return isMatch(patterns, SaHolder.getRequest().getRequestPath());
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
		boolean matchResult = isMatch(Arrays.asList(patterns), SaHolder.getRequest().getRequestPath());
		return new IsRunFunction(matchResult);
	}

	
	// -------------------- 其它操作 -------------------- 
	
	/**
	 * 停止匹配，跳出函数 (在多个匹配链中一次性跳出Auth函数) 
	 */
	public static void stop() {
		throw new StopMatchException();
	}
	
	
	
	
}
