package cn.dev33.satoken.context.dubbo3;


import cn.dev33.satoken.context.second.SaTokenSecondContext;
import cn.dev33.satoken.context.second.SaTokenSecondContextCreator;

/**
 * Sa-Token 二级Context - 创建器 [Dubbo3版]
 * 
 * @author click33
 *
 */
public class SaTokenSecondContextCreatorForDubbo3 implements SaTokenSecondContextCreator {

	@Override
	public SaTokenSecondContext create() {
		return new SaTokenSecondContextForDubbo3();
	}

}
