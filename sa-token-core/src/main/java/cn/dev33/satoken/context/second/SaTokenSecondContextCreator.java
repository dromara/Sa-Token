package cn.dev33.satoken.context.second;

/**
 * Sa-Token 二级Context - 创建器  
 * 
 * @author click33
 * @since <= 1.34.0
 */
@FunctionalInterface
public interface SaTokenSecondContextCreator {
	
	/**
	 * 创建一个二级 Context 处理器
	 * @return / 
	 */
	public SaTokenSecondContext create();
	
}
