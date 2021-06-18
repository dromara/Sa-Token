package cn.dev33.satoken.fun;

/**
 * 设定一个函数，并返回一个值，方便在Lambda表达式下的函数式编程
 * @author kong
 *
 */
@FunctionalInterface
public interface SaRetFunction {
	
	/**
	 * 执行的方法 
	 * @return 返回值 
	 */
	public Object run();
	
}
