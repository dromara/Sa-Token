package cn.dev33.satoken.session;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

import cn.dev33.satoken.SaManager;
import cn.dev33.satoken.fun.SaRetFunction;
import cn.dev33.satoken.util.SaFoxUtil;

/**
 * Session Model
 *
 * @author kong
 *
 */
public class SaSession implements Serializable {

	private static final long serialVersionUID = 1L;

	/** 此Session的id */
	private String id;

	/** 此Session的创建时间 */
	private long createTime;

	/** 此Session的所有挂载数据 */
	private final Map<String, Object> dataMap = new ConcurrentHashMap<>();

	// ----------------------- 构建相关

	/**
	 * 构建一个Session对象
	 */
	public SaSession() {
		this(null);
	}

	/**
	 * 构建一个Session对象
	 * @param id Session的id
	 */
	public SaSession(String id) {
		this.id = id;
		this.createTime = System.currentTimeMillis();
 		// $$ 通知监听器 
 		SaManager.getSaTokenListener().doCreateSession(id);
	}

	/**
	 * 获取此Session的id
	 * @return 此会话的id
	 */
	public String getId() {
		return id;
	}

	/**
	 * 写入此Session的id
	 * @param id SessionId
	 * @return 对象自身
	 */
	public SaSession setId(String id) {
		this.id = id;
		return this;
	}

	/**
	 * 返回当前会话创建时间
	 * @return 时间戳
	 */
	public long getCreateTime() {
		return createTime;
	}

	/**
	 * 写入此Session的创建时间
	 * @param createTime 时间戳
	 * @return 对象自身
	 */
	public SaSession setCreateTime(long createTime) {
		this.createTime = createTime;
		return this;
	}


	// ----------------------- TokenSign相关

	/**
	 * 此Session绑定的token签名列表
	 */
	private final List<TokenSign> tokenSignList = new Vector<>();

	/**
	 * 返回token签名列表的拷贝副本
	 *
	 * @return token签名列表
	 */
	public List<TokenSign> getTokenSignList() {
		return new Vector<>(tokenSignList);
	}

	/**
	 * 查找一个token签名
	 *
	 * @param tokenValue token值
	 * @return 查找到的tokenSign
	 */
	public TokenSign getTokenSign(String tokenValue) {
		for (TokenSign tokenSign : getTokenSignList()) {
			if (tokenSign.getValue().equals(tokenValue)) {
				return tokenSign;
			}
		}
		return null;
	}

	/**
	 * 添加一个token签名
	 *
	 * @param tokenSign token签名
	 */
	public void addTokenSign(TokenSign tokenSign) {
		// 如果已经存在于列表中，则无需再次添加
		for (TokenSign tokenSign2 : getTokenSignList()) {
			if (tokenSign2.getValue().equals(tokenSign.getValue())) {
				return;
			}
		}
		// 添加并更新
		tokenSignList.add(tokenSign);
		update();
	}

	/**
	 * 移除一个token签名
	 *
	 * @param tokenValue token名称
	 */
	public void removeTokenSign(String tokenValue) {
		TokenSign tokenSign = getTokenSign(tokenValue);
		if (tokenSignList.remove(tokenSign)) {
			update();
		}
	}

	// ----------------------- 存取值

	/**
	 * 写入一个值
	 *
	 * @param key   名称
	 * @param value 值
	 */
	public void setAttribute(String key, Object value) {
		dataMap.put(key, value);
		update();
	}

	/**
	 * 取出一个值
	 *
	 * @param key 名称
	 * @return 值
	 */
	public Object getAttribute(String key) {
		return dataMap.get(key);
	}

	/**
	 * 取值，并指定取不到值时的默认值
	 *
	 * @param key          名称
	 * @param defaultValue 取不到值的时候返回的默认值
	 * @return value
	 */
	public Object getAttribute(String key, Object defaultValue) {
		Object value = getAttribute(key);
		if (value != null) {
			return value;
		}
		return defaultValue;
	}

	/**
	 * 移除一个值
	 *
	 * @param key 要移除的值的名字
	 */
	public void removeAttribute(String key) {
		dataMap.remove(key);
		update();
	}

	/**
	 * 清空所有值
	 */
	public void clearAttribute() {
		dataMap.clear();
		update();
	}

	/**
	 * 是否含有指定key
	 *
	 * @param key 是否含有指定值
	 * @return 是否含有
	 */
	public boolean containsAttribute(String key) {
		return dataMap.containsKey(key);
	}

	/**
	 * 返回当前session会话所有key
	 *
	 * @return 所有值的key列表
	 */
	public Set<String> attributeKeys() {
		return dataMap.keySet();
	}

	/**
	 * 获取数据挂载集合（如果更新map里的值，请调用session.update()方法避免产生脏数据 ）
	 *
	 * @return 返回底层储存值的map对象
	 */
	public Map<String, Object> getDataMap() {
		return dataMap;
	}

	/**
	 * 写入数据集合 (不改变底层对象，只将此dataMap所有数据进行替换) 
	 * @param dataMap 数据集合 
	 */
	public void refreshDataMap(Map<String, Object> dataMap) {
		this.dataMap.clear();
		this.dataMap.putAll(dataMap);
		this.update();
	}

	
	// ----------------------- 一些操作

	/**
	 * 更新Session（从持久库更新刷新一下）
	 */
	public void update() {
		SaManager.getSaTokenDao().updateSession(this);
	}

	/** 注销Session (从持久库删除) */
	public void logout() {
		SaManager.getSaTokenDao().deleteSession(this.id);
 		// $$ 通知监听器 
 		SaManager.getSaTokenListener().doLogoutSession(id);
	}

	/** 当Session上的tokenSign数量为零时，注销会话 */
	public void logoutByTokenSignCountToZero() {
		if (tokenSignList.size() == 0) {
			logout();
		}
	}

	/**
	 * 获取此Session的剩余存活时间 (单位: 秒) 
	 * @return 此Session的剩余存活时间 (单位: 秒)
	 */
	public long getTimeout() {
		return SaManager.getSaTokenDao().getSessionTimeout(this.id);
	}
	
	/**
	 * 修改此Session的剩余存活时间
	 * @param timeout 过期时间 (单位: 秒) 
	 */
	public void updateTimeout(long timeout) {
		SaManager.getSaTokenDao().updateSessionTimeout(this.id, timeout);
	}
	
	/**
	 * 修改此Session的最小剩余存活时间 (只有在Session的过期时间低于指定的minTimeout时才会进行修改)
	 * @param minTimeout 过期时间 (单位: 秒) 
	 */
	public void updateMinTimeout(long minTimeout) {
		if(getTimeout() < minTimeout) {
			SaManager.getSaTokenDao().updateSessionTimeout(this.id, minTimeout);
		}
	}

	/**
	 * 修改此Session的最大剩余存活时间 (只有在Session的过期时间高于指定的maxTimeout时才会进行修改)
	 * @param maxTimeout 过期时间 (单位: 秒) 
	 */
	public void updateMaxTimeout(long maxTimeout) {
		if(getTimeout() > maxTimeout) {
			SaManager.getSaTokenDao().updateSessionTimeout(this.id, maxTimeout);
		}
	}
	
	
	
	// ----------------------- 存取值 (类型转换) 

	// ---- 取值 
	/**
	 * 取值
	 * @param key key 
	 * @return 值 
	 */
	public Object get(String key) {
		return dataMap.get(key);
	}

	/**
	 * 
	 * 取值 (指定默认值) 
	 * @param <T> 默认值的类型
	 * @param key key 
	 * @param defaultValue 取不到值时返回的默认值 
	 * @return 值 
	 */
	public <T> T get(String key, T defaultValue) {
		return getValueByDefaultValue(get(key), defaultValue);
	}
	
	/**
	 * 
	 * 取值 (如果值为null，则执行fun函数获取值) 
	 * @param <T> 返回值的类型 
	 * @param key key 
	 * @param fun 值为null时执行的函数 
	 * @return 值 
	 */
	@SuppressWarnings("unchecked")
	public <T> T get(String key, SaRetFunction fun) {
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
	public String getString(String key) {
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
	public int getInt(String key) {
		return getValueByDefaultValue(get(key), 0);
	}

	/**
	 * 取值 (转long类型) 
	 * @param key key 
	 * @return 值 
	 */
	public long getLong(String key) {
		return getValueByDefaultValue(get(key), 0L);
	}

	/**
	 * 取值 (转double类型) 
	 * @param key key 
	 * @return 值 
	 */
	public double getDouble(String key) {
		return getValueByDefaultValue(get(key), 0.0);
	}

	/**
	 * 取值 (转float类型) 
	 * @param key key 
	 * @return 值 
	 */
	public float getFloat(String key) {
		return getValueByDefaultValue(get(key), 0.0f);
	}

	/**
	 * 取值 (指定转换类型)
	 * @param <T> 泛型
	 * @param key key 
	 * @param cs 指定转换类型 
	 * @return 值 
	 */
	public <T> T getModel(String key, Class<T> cs) {
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
	public <T> T getModel(String key, Class<T> cs, Object defaultValue) {
		Object value = get(key);
		if(valueIsNull(value)) {
			return (T)defaultValue;
		}
		return SaFoxUtil.getValueByType(value, cs);
	}

	// ---- 其他
	/**
	 * 写值
	 * @param key   名称
	 * @param value 值
	 * @return 对象自身
	 */
	public SaSession set(String key, Object value) {
		dataMap.put(key, value);
		update();
		return this;
	}

	/**
	 * 写值(只有在此key原本无值的时候才会写入)
	 * @param key   名称
	 * @param value 值
	 * @return 对象自身
	 */
	public SaSession setDefaultValue(String key, Object value) {
		if(has(key) == false) {
			dataMap.put(key, value);
			update();
		}
		return this;
	}

	/**
	 * 是否含有某个key
	 * @param key has
	 * @return 是否含有
	 */
	public boolean has(String key) {
		return !valueIsNull(get(key));
	}

	/**
	 * 删值
	 * @param key 要删除的key
	 * @return 对象自身
	 */
	public SaSession delete(String key) {
		dataMap.remove(key);
		update();
		return this;
	}
	
	
	// --------- 工具方法 

	/**
	 * 判断一个值是否为null 
	 * @param value 指定值 
	 * @return 此value是否为null 
	 */
	public boolean valueIsNull(Object value) {
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
	protected <T> T getValueByDefaultValue(Object value, T defaultValue) {
		
		// 如果 obj 为 null，则直接返回默认值 
		if(valueIsNull(value)) {
			return (T)defaultValue;
		}
		
		// 开始转换
		Class<T> cs = (Class<T>) defaultValue.getClass();
		return SaFoxUtil.getValueByType(value, cs);
	}
	
	
	
}
