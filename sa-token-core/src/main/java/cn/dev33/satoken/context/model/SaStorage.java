package cn.dev33.satoken.context.model;

import cn.dev33.satoken.application.SaSetValueInterface;

/**
 * Storage Model，请求作用域的读取值对象。
 *
 * <p> 在一次请求范围内: 存值、取值。数据在请求结束后失效。
 * 
 * @author click33
 * @since <= 1.34.0
 */
public interface SaStorage extends SaSetValueInterface {

	/**
	 * 获取底层被包装的源对象
	 * @return /
	 */
	public Object getSource();

	// ---- 实现接口存取值方法 

	/** 取值 */
	@Override
	public Object get(String key);

	/** 写值 */
	@Override
	public SaStorage set(String key, Object value);

	/** 删值 */
	@Override
	public SaStorage delete(String key);

}
