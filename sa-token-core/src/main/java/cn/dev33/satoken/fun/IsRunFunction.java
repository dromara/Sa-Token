package cn.dev33.satoken.fun;

/**
 * 根据boolean变量，决定是否执行一个函数
 * 
 * @author kong
 *
 */
public class IsRunFunction {

	/**
	 * 变量 
	 */
	public final Boolean isRun;

	/**
	 * 设定一个变量，如果为true，则执行exe函数
	 * 
	 * @param isRun 变量
	 */
	public IsRunFunction(boolean isRun) {
		this.isRun = isRun;
	}

	/**
	 * 根据变量决定是否执行此函数
	 * 
	 * @param function 函数
	 */
	public void exe(SaFunction function) {
		if (isRun) {
			function.run();
		}
	}

}
