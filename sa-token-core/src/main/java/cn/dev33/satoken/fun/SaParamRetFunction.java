package cn.dev33.satoken.fun;

/**
 * 单形参、有返回值的函数式接口，方便开发者进行 lambda 表达式风格调用
 *
 * @author click33
 * @since <= 1.34.0
 */
@FunctionalInterface
public interface SaParamRetFunction<T, R> {
	
	/**
	 * 执行的方法 
	 * @param param 传入的参数 
	 * @return 返回值 
	 */
	R run(T param);
	
}
