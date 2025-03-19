/*
 * Copyright 2020-2099 sa-token.cc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cn.dev33.satoken.session;

import cn.dev33.satoken.SaManager;
import cn.dev33.satoken.application.SaSetValueInterface;
import cn.dev33.satoken.dao.SaTokenDao;
import cn.dev33.satoken.fun.SaTwoParamFunction;
import cn.dev33.satoken.listener.SaTokenEventCenter;
import cn.dev33.satoken.util.SaFoxUtil;

import java.io.Serializable;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Session Model，会话作用域的读取值对象
 *
 * <p> 在一次会话范围内: 存值、取值。数据在注销登录后失效。</p>
 * <p>
 *    在 Sa-Token 中，SaSession 分为三种，分别是：	<br>
 *     	- Account-Session: 指的是框架为每个 账号id 分配的 SaSession。	<br>
 * 		- Token-Session: 指的是框架为每个 token 分配的 SaSession。	<br>
 * 		- Custom-Session: 指的是以一个 特定的值 作为SessionId，来分配的 SaSession。	<br>
 * 	  <br>
 * 	  注意：以上分类仅为框架设计层面的概念区分，实际上它们的数据存储格式都是一致的。
 * </p>
 *
 * @author click33
 * @since 1.10.0
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
	 * 当前账号历史总计登录设备数量 （当此 SaSession 属于 Account-Session 时，此值有效）
	 */
	private int historyTerminalCount;

	/**
	 * 此 SaSession 的创建时间（13位时间戳）
	 */
	private long createTime;

	/**
	 * 所有挂载数据
	 */
	private Map<String, Object> dataMap = new ConcurrentHashMap<>();


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


	// ----------------------- SaTerminalInfo 相关

	/**
	 * 登录终端信息列表
	 */
	private List<SaTerminalInfo> terminalList = new Vector<>();

	/**
	 * 写入登录终端信息列表
	 * @param terminalList /
	 */
	public void setTerminalList(List<SaTerminalInfo> terminalList) {
		this.terminalList = terminalList;
	}

	/**
	 * 获取登录终端信息列表
	 *
	 * @return /
	 */
	public List<SaTerminalInfo> getTerminalList() {
		return terminalList;
	}

	/**
	 * 获取 登录终端信息列表 (拷贝副本)
	 *
	 * @return /
	 */
	public List<SaTerminalInfo> terminalListCopy() {
		return new ArrayList<>(terminalList);
	}

	/**
	 * 获取 登录终端信息列表 (拷贝副本)，根据 deviceType 筛选
	 *
	 * @param deviceType 设备类型，填 null 代表不限设备类型
	 * @return /
	 */
	public List<SaTerminalInfo> getTerminalListByDeviceType(String deviceType) {
		// 返回全部
		if(deviceType == null) {
			return terminalListCopy();
		}
		// 返回筛选后的
		List<SaTerminalInfo> copyList = terminalListCopy();
		List<SaTerminalInfo> newList = new ArrayList<>();
		for (SaTerminalInfo terminal : copyList) {
			if(SaFoxUtil.equals(terminal.getDeviceType(), deviceType)) {
				newList.add(terminal);
			}
		}
		return newList;
	}

	/**
	 * 获取 登录终端 token 列表
	 *
	 * @param deviceType 设备类型，填 null 代表不限设备类型
	 * @return 此 loginId 的所有登录 token
	 */
	public List<String> getTokenValueListByDeviceType(String deviceType) {
		List<String> tokenValueList = new ArrayList<>();
		for (SaTerminalInfo terminal : getTerminalListByDeviceType(deviceType)) {
			tokenValueList.add(terminal.getTokenValue());
		}
		return tokenValueList;
	}

	/**
	 * 查找一个终端信息，根据 tokenValue
	 *
	 * @param tokenValue /
	 * @return /
	 */
	public SaTerminalInfo getTerminal(String tokenValue) {
		for (SaTerminalInfo terminal : terminalListCopy()) {
			if (SaFoxUtil.equals(terminal.getTokenValue(), tokenValue)) {
				return terminal;
			}
		}
		return null;
	}

	/**
	 * 添加一个终端信息
	 *
	 * @param terminalInfo /
	 */
	public void addTerminal(SaTerminalInfo terminalInfo) {
		// 根据 tokenValue 值查重，如果存在旧的，则先删除
		SaTerminalInfo oldTerminal = getTerminal(terminalInfo.getTokenValue());
		if(oldTerminal != null) {
			terminalList.remove(oldTerminal);
		}
		// 然后添加新的
		this.historyTerminalCount++;
		terminalInfo.setIndex(this.historyTerminalCount);
		terminalList.add(terminalInfo);
		update();
	}

	/**
	 * 移除一个终端信息
	 *
	 * @param tokenValue token值 
	 */
	public void removeTerminal(String tokenValue) {
		SaTerminalInfo terminalInfo = getTerminal(tokenValue);
		if (terminalList.remove(terminalInfo)) {
			update();
		}
	}

	/**
	 * 获取 当前账号历史总计登录设备数量 （当此 SaSession 属于 Account-Session 时，此值有效）
	 *
	 * @return /
	 */
	public int getHistoryTerminalCount() {
		return this.historyTerminalCount;
	}

	/**
	 * 设置 当前账号历史总计登录设备数量 （当此 SaSession 属于 Account-Session 时，此值有效）
	 *
	 * @param historyTerminalCount /
	 */
	public void setHistoryTerminalCount(int historyTerminalCount) {
		this.historyTerminalCount = historyTerminalCount;
	}

	/**
	 * 遍历 terminalList 列表，执行特定函数
	 *
	 * @param function 需要执行的函数
	 */
	public void forEachTerminalList(SaTwoParamFunction<SaSession, SaTerminalInfo> function) {
		for (SaTerminalInfo terminalInfo: terminalListCopy()) {
			function.run(this, terminalInfo);
		}
	}


	/**
	 * 判断指定设备 id 是否为可信任设备
	 * @param deviceId /
	 * @return /
	 */
	public boolean isTrustDeviceId(String deviceId) {
		if(SaFoxUtil.isEmpty(deviceId)) {
			return false;
		}
		for (SaTerminalInfo terminal : terminalListCopy()) {
			if (SaFoxUtil.equals(terminal.getDeviceId(), deviceId)) {
				return true;
			}
		}
		return false;
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

	/** 当 Session 上的 SaTerminalInfo 数量为零时，注销会话 */
	public void logoutByTerminalCountToZero() {
		if (terminalList.isEmpty()) {
			logout();
		}
	}

	/**
	 * 获取此Session的剩余存活时间 (单位: 秒) 
	 * @return 此Session的剩余存活时间 (单位: 秒)
	 */
	public long timeout() {
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
		long curr = trans(timeout());
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
		long curr = trans(timeout());
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
		if( ! has(key)) {
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
	 * 设置数据挂载集合 (改变底层对象引用，将 dataMap 整个对象替换)
	 * @param dataMap 数据集合
	 *
	 * @return 对象自身
	 */
	public SaSession setDataMap(Map<String, Object> dataMap) {
		this.dataMap = dataMap;
		return this;
	}

	/**
	 * 写入数据集合 (不改变底层对象引用，只将此 dataMap 所有数据进行替换)
	 * @param dataMap 数据集合 
	 */
	public SaSession refreshDataMap(Map<String, Object> dataMap) {
		this.dataMap.clear();
		this.dataMap.putAll(dataMap);
		this.update();
		return this;
	}

	//

}
