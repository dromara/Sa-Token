package cn.dev33.satoken.session;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

import cn.dev33.satoken.SaManager;
import cn.dev33.satoken.application.SaSetValueInterface;
import cn.dev33.satoken.dao.SaTokenDao;
import cn.dev33.satoken.listener.SaTokenEventCenter;
import cn.dev33.satoken.util.SaFoxUtil;

/**
 * Session Model，会话作用域的读取值对象 
 * <p> 在一次会话范围内: 存值、取值
 *
 * @author kong
 *
 */
public class SaSession implements SaSetValueInterface, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 在 SaSession 上存储用户对象时建议使用的 key
	 */
	public static final String USER = "USER";

	/**
	 * 在 SaSession 上存储角色列表时建议使用的 key
	 */
	public static final String ROLE_LIST = "ROLE_LIST";

	/**
	 * 在 SaSession 上存储权限列表时建议使用的 key
	 */
	public static final String PERMISSION_LIST = "PERMISSION_LIST";

	/**
	 * 此 SaSession 的 id
	 */
	private String id;

	/**
	 * 此 SaSession 的 类型
	 */
	private String type;

	/**
	 * 所属 loginType
	 */
	private String loginType;

	/**
	 * 所属 loginId （当此 SaSession 属于 Account-Session 时，此值有效）
	 */
	private Object loginId;

	/**
	 * 所属 Token （当此 SaSession 属于 Token-Session 时，此值有效）
	 */
	private String token;

	/**
	 * 此 SaSession 的创建时间（13位时间戳）
	 */
	private long createTime;

	/**
	 * 所有挂载数据
	 */
	private final Map<String, Object> dataMap = new ConcurrentHashMap<>();


	// ----------------------- 构建相关

	/**
	 * 构建一个 Session 对象
	 */
	public SaSession() {
		/*
		 * 当 Session 从 Redis 中反序列化取出时，框架会误以为创建了新的Session，
		 * 因此此处不可以调用this(null); 避免监听器收到错误的通知 
		 */
		// this(null);
	}

	/**
	 * 构建一个 Session 对象
	 * @param id Session的id
	 */
	public SaSession(String id) {
		this.id = id;
		this.createTime = System.currentTimeMillis();
 		// $$ 发布事件
		SaTokenEventCenter.doCreateSession(id);
	}

	/**
	 * 获取：此 SaSession 的 id
	 * @return /
	 */
	public String getId() {
		return this.id;
	}

	/**
	 * 写入：此 SaSession 的 id
	 * @param id /
	 * @return 对象自身
	 */
	public SaSession setId(String id) {
		this.id = id;
		return this;
	}

	/**
	 * 获取：此 SaSession 的 类型
	 *
	 * @return /
	 */
	public String getType() {
		return this.type;
	}

	/**
	 * 设置：此 SaSession 的 类型
	 *
	 * @param type /
	 * @return 对象自身
	 */
	public SaSession setType(String type) {
		this.type = type;
		return this;
	}

	/**
	 * 获取：所属 loginType
	 * @return /
	 */
	public String getLoginType() {
		return this.loginType;
	}

	/**
	 * 设置：所属 loginType
	 * @param loginType /
	 * @return 对象自身
	 */
	public SaSession setLoginType(String loginType) {
		this.loginType = loginType;
		return this;
	}

	/**
	 * 获取：所属 loginId （当此 SaSession 属于 Account-Session 时，此值有效）
	 * @return /
	 */
	public Object getLoginId() {
		return this.loginId;
	}

	/**
	 * 设置：所属 loginId （当此 SaSession 属于 Account-Session 时，此值有效）
	 * @param loginId /
	 * @return 对象自身
	 */
	public SaSession setLoginId(Object loginId) {
		this.loginId = loginId;
		return this;
	}

	/**
	 * 获取：所属 Token （当此 SaSession 属于 Token-Session 时，此值有效）
	 * @return /
	 */
	public String getToken() {
		return this.token;
	}

	/**
	 * 设置：所属 Token （当此 SaSession 属于 Token-Session 时，此值有效）
	 * @param token /
	 * @return 对象自身
	 */
	public SaSession setToken(String token) {
		this.token = token;
		return this;
	}

	/**
	 * 返回：当前 SaSession 的创建时间（13位时间戳）
	 * @return /
	 */
	public long getCreateTime() {
		return this.createTime;
	}

	/**
	 * 写入：此 SaSession 的创建时间（13位时间戳）
	 * @param createTime /
	 * @return 对象自身
	 */
	public SaSession setCreateTime(long createTime) {
		this.createTime = createTime;
		return this;
	}


	// ----------------------- TokenSign 相关

	/**
	 * 此 Session 绑定的 Token 签名列表 
	 */
	private List<TokenSign> tokenSignList = new Vector<>();

	/**
	 * 写入此 Session 绑定的 Token 签名列表 
	 * @param tokenSignList Token 签名列表
	 */
	public void setTokenSignList(List<TokenSign> tokenSignList) {
		this.tokenSignList = tokenSignList;
	}

	/**
	 * 获取此 Session 绑定的 Token 签名列表 
	 *
	 * @return Token 签名列表
	 */
	public List<TokenSign> getTokenSignList() {
		return tokenSignList;
	}

	/**
	 * 获取 Token 签名列表 的拷贝副本
	 *
	 * @return token签名列表
	 */
	public List<TokenSign> tokenSignListCopy() {
		return new ArrayList<>(tokenSignList);
	}

	/**
	 * 返回 Token 签名列表 的拷贝副本，根据 device 筛选 
	 *
	 * @param device 设备类型，填 null 代表不限设备类型  
	 * @return token签名列表
	 */
	public List<TokenSign> tokenSignListCopyByDevice(String device) {
		// 返回全部
		if(device == null) {
			return tokenSignListCopy();
		}
		// 返回筛选后的 
		List<TokenSign> list = new ArrayList<>();
		for (TokenSign tokenSign : tokenSignListCopy()) {
			if(SaFoxUtil.equals(tokenSign.getDevice(), device)) {
				list.add(tokenSign);
			}
		}
		return list;
	}

	/**
	 * 查找一个 Token 签名
	 *
	 * @param tokenValue token值
	 * @return 查找到的 TokenSign
	 */
	public TokenSign getTokenSign(String tokenValue) {
		for (TokenSign tokenSign : tokenSignListCopy()) {
			if (SaFoxUtil.equals(tokenSign.getValue(), tokenValue)) {
				return tokenSign;
			}
		}
		return null;
	}

	/**
	 * 添加一个 Token 签名
	 *
	 * @param tokenSign Token 签名
	 */
	public void addTokenSign(TokenSign tokenSign) {
		// 如果已经存在于列表中，则无需再次添加 
		if(getTokenSign(tokenSign.getValue()) != null) {
			return;
		}
		// 添加并更新
		tokenSignList.add(tokenSign);
		update();
	}

	/**
	 * 添加一个 Token 签名
	 *
	 * @param tokenValue token值
	 * @param device 设备类型
	 */
	public void addTokenSign(String tokenValue, String device) {
		addTokenSign(new TokenSign(tokenValue, device));
	}

	/**
	 * 移除一个 Token 签名
	 *
	 * @param tokenValue token值 
	 */
	public void removeTokenSign(String tokenValue) {
		TokenSign tokenSign = getTokenSign(tokenValue);
		if (tokenSignList.remove(tokenSign)) {
			update();
		}
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
 		// $$ 发布事件 
		SaTokenEventCenter.doLogoutSession(id);
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
	 * 修改此Session的最小剩余存活时间 (只有在 Session 的过期时间低于指定的 minTimeout 时才会进行修改)
	 * @param minTimeout 过期时间 (单位: 秒) 
	 */
	public void updateMinTimeout(long minTimeout) {
		long min = trans(minTimeout);
		long curr = trans(getTimeout());
		if(curr < min) {
			updateTimeout(minTimeout);
		}
	}

	/**
	 * 修改此Session的最大剩余存活时间 (只有在 Session 的过期时间高于指定的 maxTimeout 时才会进行修改)
	 * @param maxTimeout 过期时间 (单位: 秒) 
	 */
	public void updateMaxTimeout(long maxTimeout) {
		long max = trans(maxTimeout);
		long curr = trans(getTimeout());
		if(curr > max) {
			updateTimeout(maxTimeout);
		}
	}
	
	/**
	 * value为 -1 时返回 Long.MAX_VALUE，否则原样返回 
	 * @param value /
	 * @return /
	 */
	protected long trans(long value) {
		return value == SaTokenDao.NEVER_EXPIRE ? Long.MAX_VALUE : value;
	}


	// ----------------------- 存取值 (类型转换)

	// ---- 重写接口方法 
	
	/**
	 * 取值 
	 * @param key key 
	 * @return 值 
	 */
	@Override
	public Object get(String key) {
		return dataMap.get(key);
	}
	
	/**
	 * 写值 
	 * @param key   名称
	 * @param value 值
	 * @return 对象自身
	 */
	@Override
	public SaSession set(String key, Object value) {
		dataMap.put(key, value);
		update();
		return this;
	}

	/**
	 * 写值 (只有在此 key 原本无值的情况下才会写入)
	 * @param key   名称
	 * @param value 值
	 * @return 对象自身
	 */
	@Override
	public SaSession setByNull(String key, Object value) {
		if(has(key) == false) {
			dataMap.put(key, value);
			update();
		}
		return this;
	}

	/**
	 * 删值
	 * @param key 要删除的key
	 * @return 对象自身
	 */
	@Override
	public SaSession delete(String key) {
		dataMap.remove(key);
		update();
		return this;
	}


	// ----------------------- 其它方法

	/**
	 * 返回当前 Session 挂载数据的所有 key
	 *
	 * @return key 列表
	 */
	public Set<String> keys() {
		return dataMap.keySet();
	}
	
	/**
	 * 清空所有挂载数据
	 */
	public void clear() {
		dataMap.clear();
		update();
	}

	/**
	 * 获取数据挂载集合（如果更新map里的值，请调用 session.update() 方法避免产生脏数据 ）
	 *
	 * @return 返回底层储存值的map对象
	 */
	public Map<String, Object> getDataMap() {
		return dataMap;
	}

	/**
	 * 写入数据集合 (不改变底层对象引用，只将此 dataMap 所有数据进行替换)
	 * @param dataMap 数据集合 
	 */
	public void refreshDataMap(Map<String, Object> dataMap) {
		this.dataMap.clear();
		this.dataMap.putAll(dataMap);
		this.update();
	}

}
