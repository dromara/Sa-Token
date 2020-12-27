package cn.dev33.satoken.annotation;

/**
 * 指定注解鉴权的验证模式 
 * @author kong
 *
 */
public enum SaMode {

	/**
	 * 必须具有所有的选项 
	 */
	AND,

	/**
	 * 只需具有其中一个选项 
	 */
	OR
	
}
