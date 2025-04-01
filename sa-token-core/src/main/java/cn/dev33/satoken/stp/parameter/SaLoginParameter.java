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
package cn.dev33.satoken.stp.parameter;

import cn.dev33.satoken.SaManager;
import cn.dev33.satoken.config.SaTokenConfig;
import cn.dev33.satoken.dao.SaTokenDao;
import cn.dev33.satoken.stp.parameter.enums.SaLogoutMode;
import cn.dev33.satoken.stp.parameter.enums.SaReplacedRange;
import cn.dev33.satoken.util.SaTokenConsts;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 在调用 `StpUtil.login()` 时的 配置参数对象，决定登录的一些细节行为 <br>
 *
 * <pre>
 *     	// 例如：在登录时指定 token 有效期为七天，代码如下：
 *     	StpUtil.login(10001, new SaLoginParameter().setTimeout(60 * 60 * 24 * 7));
 * </pre>
 *
 * @author click33
 * @since 1.13.2
 */
public class SaLoginParameter {

	// --------- 单独参数

	/**
	 * 此次登录的客户端设备类型 
	 */
	private String deviceType;

	/**
	 * 此次登录的客户端设备id
	 */
	private String deviceId;

	/**
	 * 扩展信息（只在 jwt 模式下生效）
	 */
	private Map<String, Object> extraData;

	/**
	 * 预定Token（预定本次登录生成的Token值）
	 */
	private String token;

	/**
	 * 本次登录挂载到 SaTerminalInfo 的自定义扩展数据
	 */
	private Map<String, Object> terminalExtraData;


	// --------- 覆盖性参数

	/**
	 * 指定此次登录 token 有效期，单位：秒 （如未指定，自动取全局配置的 timeout 值）
	 */
	private long timeout;

	/**
	 * 指定此次登录 token 最低活跃频率，单位：秒（如未指定，则使用全局配置的 activeTimeout 值）
	 */
	private Long activeTimeout;

	/**
	 * 是否允许同一账号多地同时登录 （为 true 时允许一起登录, 为 false 时新登录挤掉旧登录）
	 */
	private Boolean isConcurrent;

	/**
	 * 在多人登录同一账号时，是否共用一个 token （为 true 时所有登录共用一个 token, 为 false 时每次登录新建一个 token）
	 */
	private Boolean isShare;

	/**
	 * 同一账号最大登录数量，-1代表不限 （只有在 isConcurrent=true, isShare=false 时此配置项才有意义）
	 */
	private int maxLoginCount;

	/**
	 * 在每次创建 token 时的最高循环次数，用于保证 token 唯一性（-1=不循环尝试，直接使用）
	 */
	private int maxTryTimes;

	/**
	 * 是否为持久Cookie（临时Cookie在浏览器关闭时会自动删除，持久Cookie在重新打开后依然存在）
	 */
	private Boolean isLastingCookie;

	/**
	 * 是否在登录后将 Token 写入到响应头
	 */
	private Boolean isWriteHeader;

	/**
	 * 当 isConcurrent=false 时，顶人下线的范围 (CURR_DEVICE_TYPE=当前指定的设备类型端, ALL_DEVICE_TYPE=所有设备类型端)
	 */
	private SaReplacedRange replacedRange;

	/**
	 * 溢出 maxLoginCount 的客户端，将以何种方式注销下线 (LOGOUT=注销下线, KICKOUT=踢人下线, REPLACED=顶人下线)
	 */
	private SaLogoutMode overflowLogoutMode;

	/**
	 * 在登录时，是否立即创建对应的 Token-Session （true=在登录时立即创建，false=在第一次调用 getTokenSession() 时创建）
	 */
	private Boolean rightNowCreateTokenSession;


	// ------ 附加方法

	public SaLoginParameter() {
		this(SaManager.getConfig());
	}
	public SaLoginParameter(SaTokenConfig config) {
		setDefaultValues(config);
	}

	/**
	 * 根据 SaTokenConfig 对象初始化默认值
	 *
	 * @param config 使用的配置对象
	 * @return 对象自身
	 */
	public SaLoginParameter setDefaultValues(SaTokenConfig config) {
		this.deviceType = SaTokenConsts.DEFAULT_LOGIN_DEVICE_TYPE;
		this.timeout = config.getTimeout();
		this.isConcurrent = config.getIsConcurrent();
		this.isShare = config.getIsShare();
		this.maxLoginCount = config.getMaxLoginCount();
		this.maxTryTimes = config.getMaxTryTimes();
		this.isLastingCookie = config.getIsLastingCookie();
		this.isWriteHeader = config.getIsWriteHeader();
		this.replacedRange = config.getReplacedRange();
		this.overflowLogoutMode = config.getOverflowLogoutMode();
		this.rightNowCreateTokenSession = config.getRightNowCreateTokenSession();
		return this;
	}

	/**
	 * 写入扩展数据（只在jwt模式下生效）
	 * @param key 键
	 * @param value 值
	 * @return 对象自身
	 */
	public SaLoginParameter setExtra(String key, Object value) {
		if(this.extraData == null) {
			this.extraData = new LinkedHashMap<>();
		}
		this.extraData.put(key, value);
		return this;
	}

	/**
	 * 获取扩展数据（只在jwt模式下生效）
	 * @param key 键
	 * @return 扩展数据的值
	 */
	public Object getExtra(String key) {
		if(this.extraData == null) {
			return null;
		}
		return this.extraData.get(key);
	}

	/**
	 * 判断是否设置了扩展数据（只在jwt模式下生效）
	 * @return /
	 */
	public boolean haveExtraData() {
		return extraData != null && !extraData.isEmpty();
	}

	/**
	 * 写入本次登录挂载到 SaTerminalInfo 的自定义扩展数据
	 * @param key 键
	 * @param value 值 
	 * @return 对象自身 
	 */
	public SaLoginParameter setTerminalExtra(String key, Object value) {
		if(this.terminalExtraData == null) {
			this.terminalExtraData = new LinkedHashMap<>();
		}
		this.terminalExtraData.put(key, value);
		return this;
	}

	/**
	 * 获取本次登录挂载到 SaTerminalInfo 的自定义扩展数据
	 * @param key 键
	 * @return 扩展数据的值 
	 */
	public Object getTerminalExtra(String key) {
		if(this.terminalExtraData == null) {
			return null;
		}
		return this.terminalExtraData.get(key);
	}

	/**
	 * 判断是否设置了本次登录挂载到 SaTerminalInfo 的自定义扩展数据
	 * @return / 
	 */
	public boolean haveTerminalExtraData() {
		return terminalExtraData != null && !terminalExtraData.isEmpty();
	}

	/**
	 * 计算 Cookie 时长
	 * @return /
	 */
	public int getCookieTimeout() {
		if( ! getIsLastingCookie()) {
			return -1;
		}
		long _timeout = getTimeout();
		if(_timeout == SaTokenDao.NEVER_EXPIRE || _timeout > Integer.MAX_VALUE) {
			return Integer.MAX_VALUE;
		}
		return (int)_timeout;
	}

	/**
	 * 静态方法获取一个 SaLoginParameter 对象
	 * @return SaLoginParameter 对象
	 */
	public static SaLoginParameter create() {
		return new SaLoginParameter(SaManager.getConfig());
	}



	// ---------------- get set

	/**
	 * @return 此次登录的客户端设备类型
	 */
	public String getDeviceType() {
		return deviceType;
	}

	/**
	 * @param deviceType 此次登录的客户端设备类型
	 * @return 对象自身
	 */
	public SaLoginParameter setDeviceType(String deviceType) {
		this.deviceType = deviceType;
		return this;
	}

	/**
	 * 获取 此次登录的客户端设备id
	 *
	 * @return deviceId 此次登录的客户端设备id
	 */
	public String getDeviceId() {
		return this.deviceId;
	}

	/**
	 * 设置 此次登录的客户端设备id
	 *
	 * @param deviceId 此次登录的客户端设备id
	 */
	public SaLoginParameter setDeviceId(String deviceId) {
		this.deviceId = deviceId;
		return this;
	}

	/**
	 * 当 isConcurrent=false 时，顶人下线的范围 (CURR_DEVICE_TYPE=当前指定的设备类型端, ALL_DEVICE_TYPE=所有设备类型端)
	 *
	 * @return replacedMode 顶人下线的范围
	 */
	public SaReplacedRange getReplacedRange() {
		return this.replacedRange;
	}

	/**
	 * 当 isConcurrent=false 时，顶人下线的范围 (CURR_DEVICE_TYPE=当前指定的设备类型端, ALL_DEVICE_TYPE=所有设备类型端)
	 *
	 * @param replacedRange /
	 * @return 对象自身
	 */
	public SaLoginParameter setReplacedRange(SaReplacedRange replacedRange) {
		this.replacedRange = replacedRange;
		return this;
	}

	/**
	 * 获取 溢出 maxLoginCount 的客户端，将以何种方式注销下线 (LOGOUT=注销下线, KICKOUT=踢人下线, REPLACED=顶人下线)
	 *
	 * @return overflowLogoutMode /
	 */
	public SaLogoutMode getOverflowLogoutMode() {
		return this.overflowLogoutMode;
	}

	/**
	 * 设置 溢出 maxLoginCount 的客户端，将以何种方式注销下线 (LOGOUT=注销下线, KICKOUT=踢人下线, REPLACED=顶人下线)
	 *
	 * @param overflowLogoutMode /
	 * @return 对象自身
	 */
	public SaLoginParameter setOverflowLogoutMode(SaLogoutMode overflowLogoutMode) {
		this.overflowLogoutMode = overflowLogoutMode;
		return this;
	}

	/**
	 * @return 是否为持久Cookie（临时Cookie在浏览器关闭时会自动删除，持久Cookie在重新打开后依然存在）
	 */
	public Boolean getIsLastingCookie() {
		return isLastingCookie;
	}

	/**
	 * @param isLastingCookie 是否为持久Cookie（临时Cookie在浏览器关闭时会自动删除，持久Cookie在重新打开后依然存在）
	 * @return 对象自身
	 */
	public SaLoginParameter setIsLastingCookie(Boolean isLastingCookie) {
		this.isLastingCookie = isLastingCookie;
		return this;
	}

	/**
	 * @return 指定此次登录 token 有效期，单位：秒
	 */
	public long getTimeout() {
		return timeout;
	}

	/**
	 * @param timeout 指定此次登录 token 有效期，单位：秒 （如未指定，自动取全局配置的 timeout 值）
	 * @return 对象自身
	 */
	public SaLoginParameter setTimeout(long timeout) {
		this.timeout = timeout;
		return this;
	}

	/**
	 * @return 此次登录 token 最低活跃频率，单位：秒（如未指定，则使用全局配置的 activeTimeout 值）
	 */
	public Long getActiveTimeout() {
		return activeTimeout;
	}

	/**
	 * @param activeTimeout 指定此次登录 token 最低活跃频率，单位：秒（如未指定，则使用全局配置的 activeTimeout 值）
	 * @return 对象自身
	 */
	public SaLoginParameter setActiveTimeout(long activeTimeout) {
		this.activeTimeout = activeTimeout;
		return this;
	}

	/**
	 * @return 是否允许同一账号多地同时登录 （为 true 时允许一起登录, 为 false 时新登录挤掉旧登录）
	 */
	public Boolean getIsConcurrent() {
		return isConcurrent;
	}

	/**
	 * @param isConcurrent 是否允许同一账号多地同时登录 （为 true 时允许一起登录, 为 false 时新登录挤掉旧登录）
	 * @return 对象自身
	 */
	public SaLoginParameter setIsConcurrent(Boolean isConcurrent) {
		this.isConcurrent = isConcurrent;
		return this;
	}

	/**
	 * @return 在多人登录同一账号时，是否共用一个 token （为 true 时所有登录共用一个token, 为 false 时每次登录新建一个 token）
	 */
	public Boolean getIsShare() {
		return isShare;
	}

	/**
	 * @param isShare 在多人登录同一账号时，是否共用一个 token （为 true 时所有登录共用一个token, 为 false 时每次登录新建一个 token）
	 * @return 对象自身
	 */
	public SaLoginParameter setIsShare(Boolean isShare) {
		this.isShare = isShare;
		return this;
	}

	/**
	 * @return 同一账号最大登录数量，-1代表不限 （只有在 isConcurrent=true, isShare=false 时此配置项才有意义）
	 */
	public int getMaxLoginCount() {
		return maxLoginCount;
	}

	/**
	 * @param maxLoginCount 同一账号最大登录数量，-1代表不限 （只有在 isConcurrent=true, isShare=false 时此配置项才有意义）
	 * @return 对象自身
	 */
	public SaLoginParameter setMaxLoginCount(int maxLoginCount) {
		this.maxLoginCount = maxLoginCount;
		return this;
	}

	/**
	 * @return 在每次创建 token 时的最高循环次数，用于保证 token 唯一性（-1=不循环尝试，直接使用）
	 */
	public int getMaxTryTimes() {
		return maxTryTimes;
	}

	/**
	 * @param maxTryTimes 在每次创建 token 时的最高循环次数，用于保证 token 唯一性（-1=不循环尝试，直接使用）
	 * @return 对象自身
	 */
	public SaLoginParameter setMaxTryTimes(int maxTryTimes) {
		this.maxTryTimes = maxTryTimes;
		return this;
	}

	/**
	 * @return 扩展信息（只在jwt模式下生效）
	 */
	public Map<String, Object> getExtraData() {
		return extraData;
	}

	/**
	 * @param extraData 扩展信息（只在jwt模式下生效）
	 * @return 对象自身
	 */
	public SaLoginParameter setExtraData(Map<String, Object> extraData) {
		this.extraData = extraData;
		return this;
	}

	/**
	 * @return 预定Token（预定本次登录生成的Token值）
	 */
	public String getToken() {
		return token;
	}

	/**
	 * @param token 预定Token（预定本次登录生成的Token值）
	 * @return 对象自身
	 */
	public SaLoginParameter setToken(String token) {
		this.token = token;
		return this;
	}

	/**
	 * @return 是否在登录后将 Token 写入到响应头
	 */
	public Boolean getIsWriteHeader() {
		return isWriteHeader;
	}

	/**
	 * @param isWriteHeader 是否在登录后将 Token 写入到响应头
	 * @return 对象自身
	 */
	public SaLoginParameter setIsWriteHeader(Boolean isWriteHeader) {
		this.isWriteHeader = isWriteHeader;
		return this;
	}

	/**
	 * 获取 本次登录挂载到 SaTerminalInfo 的自定义扩展数据
	 *
	 * @return /
	 */
	public Map<String, Object> getTerminalExtraData() {
		return this.terminalExtraData;
	}

	/**
	 * 设置 本次登录挂载到 SaTerminalInfo 的自定义扩展数据
	 *
	 * @param terminalExtraData /
	 * @return 对象自身
	 */
	public SaLoginParameter setTerminalExtraData(Map<String, Object> terminalExtraData) {
		this.terminalExtraData = terminalExtraData;
		return this;
	}

	/**
	 * 获取 在登录时，是否立即创建对应的 Token-Session （true=在登录时立即创建，false=在第一次调用 getTokenSession() 时创建）
	 *
	 * @return /
	 */
	public Boolean getRightNowCreateTokenSession() {
		return this.rightNowCreateTokenSession;
	}

	/**
	 * 设置 在登录时，是否立即创建对应的 Token-Session （true=在登录时立即创建，false=在第一次调用 getTokenSession() 时创建）
	 *
	 * @param rightNowCreateTokenSession /
	 * @return 对象自身
	 */
	public SaLoginParameter setRightNowCreateTokenSession(Boolean rightNowCreateTokenSession) {
		this.rightNowCreateTokenSession = rightNowCreateTokenSession;
		return this;
	}

	/*
	 * toString
	 */
	@Override
	public String toString() {
		return "SaLoginParameter ["
				+ "deviceType=" + deviceType
				+ ", deviceId=" + deviceId
				+ ", replacedRange=" + replacedRange
				+ ", overflowLogoutMode=" + overflowLogoutMode
				+ ", isLastingCookie=" + isLastingCookie
				+ ", timeout=" + timeout
				+ ", activeTimeout=" + activeTimeout
				+ ", isConcurrent=" + isConcurrent
				+ ", isShare=" + isShare
				+ ", maxLoginCount=" + maxLoginCount
				+ ", maxTryTimes=" + maxTryTimes
				+ ", extraData=" + extraData
				+ ", token=" + token
				+ ", isWriteHeader=" + isWriteHeader
				+ ", terminalTag=" + terminalExtraData
				+ ", rightNowCreateTokenSession=" + rightNowCreateTokenSession
				+ "]";
	}




	/**
	 * <h2> 请更换为 getDeviceType </h2>
	 * @return 此次登录的客户端设备类型
	 */
	@Deprecated
	public String getDevice() {
		return deviceType;
	}

	/**
	 * <h2> 请更换为 setDeviceType </h2>
	 * @param device 此次登录的客户端设备类型
	 * @return 对象自身
	 */
	@Deprecated
	public SaLoginParameter setDevice(String device) {
		this.deviceType = device;
		return this;
	}

}
