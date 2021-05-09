package cn.dev33.satoken.fun;

/**
 * 设定一个函数，方便在Lambda表达式下的函数式编程
 * 
 * @author kong
 *
 */
@FunctionalInterface
public interface SaFunction {

	/**
	 * 执行的方法
	 */
	public void run();

}
