package cn.dev33.satoken.annotation;

/**
 * 注解鉴权的验证模式 
 * @author kong
 *
 */
public enum SaMode {

	/**
	 * 必须具有所有的元素 
	 */
	AND,

	/**
	 * 只需具有其中一个元素
	 */
	OR
	
}
