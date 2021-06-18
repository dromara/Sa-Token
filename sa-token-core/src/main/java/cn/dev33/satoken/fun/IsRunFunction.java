package cn.dev33.satoken.fun;

/**
 * 根据Boolean变量，决定是否执行一个函数
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
	 * 当 isRun == true 时执行此函数
	 * @param function 函数
	 * @return 对象自身
	 */
	public IsRunFunction exe(SaFunction function) {
		if (isRun) {
			function.run();
		}
		return this;
	}

	/**
	 * 当 isRun == false 时执行此函数
	 * @param function 函数
	 * @return 对象自身
	 */
	public IsRunFunction noExe(SaFunction function) {
		if (!isRun) {
			function.run();
		}
		return this;
	}

}
