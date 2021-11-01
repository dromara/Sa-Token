package cn.dev33.satoken.context.dubbo;

import cn.dev33.satoken.context.second.SaTokenSecondContext;
import cn.dev33.satoken.context.second.SaTokenSecondContextCreator;

/**
 * Sa-Token 二级Context - 创建器 [Dubbo版] 
 * 
 * @author kong
 *
 */
public class SaTokenSecondContextCreatorForDubbo implements SaTokenSecondContextCreator {

	@Override
	public SaTokenSecondContext create() {
		return new SaTokenSecondContextForDubbo();
	}

}
