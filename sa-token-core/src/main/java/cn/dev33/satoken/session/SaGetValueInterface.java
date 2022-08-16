package cn.dev33.satoken.session;

import cn.dev33.satoken.fun.SaRetFunction;
import cn.dev33.satoken.util.SaFoxUtil;

/**
 * 对取值写值的一组方法封装 
 * 
 * @author kong
 * @since: 2022-8-16
 */
public interface SaGetValueInterface {

	// --------- 需要子类实现的方法 
	
	/**
	 * 取值 
	 * @param key key 
	 * @return 值 
	 */
	public abstract Object get(String key);
	
	/**
	 * 写值 
	 * @param key   名称
	 * @param value 值
	 * @return 对象自身
	 */
	public abstract SaGetValueInterface set(String key, Object value);

	
	// --------- 接口提供封装的方法 

	// 取值 
	
	/**
	 * 
	 * 取值 (指定默认值) 
	 * @param <T> 默认值的类型
	 * @param key key 
	 * @param defaultValue 取不到值时返回的默认值 
	 * @return 值 
	 */
	public default <T> T get(String key, T defaultValue) {
		return getValueByDefaultValue(get(key), defaultValue);
	}

	/**
	 * 
	 * 取值 (如果值为 null，则执行 fun 函数获取值，并把函数返回值写入缓存) 
	 * @param <T> 返回值的类型 
	 * @param key key 
	 * @param fun 值为null时执行的函数 
	 * @return 值 
	 */
	@SuppressWarnings("unchecked")
	public default <T> T get(String key, SaRetFunction fun) {
		Object value = get(key);
		if(value == null) {
			value = fun.run();
			set(key, value);
		}
		return (T) value;
	}
	
	/**
	 * 取值 (转String类型) 
	 * @param key key 
	 * @return 值 
	 */
	public default String getString(String key) {
		Object value = get(key);
		if(value == null) {
			return null;
		}
		return String.valueOf(value);
	}

	/**
	 * 取值 (转int类型) 
	 * @param key key 
	 * @return 值 
	 */
	public default int getInt(String key) {
		return getValueByDefaultValue(get(key), 0);
	}

	/**
	 * 取值 (转long类型) 
	 * @param key key 
	 * @return 值 
	 */
	public default long getLong(String key) {
		return getValueByDefaultValue(get(key), 0L);
	}

	/**
	 * 取值 (转double类型) 
	 * @param key key 
	 * @return 值 
	 */
	public default double getDouble(String key) {
		return getValueByDefaultValue(get(key), 0.0);
	}

	/**
	 * 取值 (转float类型) 
	 * @param key key 
	 * @return 值 
	 */
	public default float getFloat(String key) {
		return getValueByDefaultValue(get(key), 0.0f);
	}

	/**
	 * 取值 (指定转换类型)
	 * @param <T> 泛型
	 * @param key key 
	 * @param cs 指定转换类型 
	 * @return 值 
	 */
	public default <T> T getModel(String key, Class<T> cs) {
		return SaFoxUtil.getValueByType(get(key), cs);
	}

	/**
	 * 取值 (指定转换类型, 并指定值为Null时返回的默认值)
	 * @param <T> 泛型
	 * @param key key 
	 * @param cs 指定转换类型 
	 * @param defaultValue 值为Null时返回的默认值
	 * @return 值 
	 */
	@SuppressWarnings("unchecked")
	public default <T> T getModel(String key, Class<T> cs, Object defaultValue) {
		Object value = get(key);
		if(valueIsNull(value)) {
			return (T)defaultValue;
		}
		return SaFoxUtil.getValueByType(value, cs);
	}

	// 写值 & 其它 

	/**
	 * 写值 (只有在此 key 原本无值的情况下才会写入)
	 * @param key   名称
	 * @param value 值
	 * @return 对象自身
	 */
	public default SaGetValueInterface setByNull(String key, Object value) {
		if(has(key) == false) {
			set(key, value);
		}
		return this;
	}

	/**
	 * 是否含有某个key
	 * @param key has
	 * @return 是否含有
	 */
	public default boolean has(String key) {
		return !valueIsNull(get(key));
	}

	
	
	
	// --------- 内部工具方法 

	/**
	 * 判断一个值是否为null 
	 * @param value 指定值 
	 * @return 此value是否为null 
	 */
	public default boolean valueIsNull(Object value) {
		return value == null || value.equals("");
	}

	/**
	 * 根据默认值来获取值
	 * @param <T> 泛型
	 * @param value 值 
	 * @param defaultValue 默认值
	 * @return 转换后的值 
	 */
	@SuppressWarnings("unchecked")
	public default <T> T getValueByDefaultValue(Object value, T defaultValue) {
		
		// 如果 obj 为 null，则直接返回默认值 
		if(valueIsNull(value)) {
			return (T)defaultValue;
		}
		
		// 开始转换
		Class<T> cs = (Class<T>) defaultValue.getClass();
		return SaFoxUtil.getValueByType(value, cs);
	}
	
	
}
