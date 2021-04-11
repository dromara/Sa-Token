package cn.dev33.satoken.filter;

import cn.dev33.satoken.exception.SaTokenException;

/**
 * sa-token全局过滤器-异常处理策略 [默认实现]
 * 
 * @author kong
 *
 */
public class SaFilterErrorStrategyDefaultImpl implements SaFilterErrorStrategy {

	/**
	 * 执行方法
	 */
	@Override
	public Object run(Throwable e) {
		throw new SaTokenException(e);
	}

}
