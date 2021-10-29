package cn.dev33.satoken.context.second;

/**
 * Sa-Token 二级Context - 创建器  
 * 
 * @author kong
 *
 */
@FunctionalInterface
public interface SaTokenSecondContextCreator {
	
	/**
	 * 创建一个二级 Context 
	 * @return / 
	 */
	public SaTokenSecondContext create();
	
}
