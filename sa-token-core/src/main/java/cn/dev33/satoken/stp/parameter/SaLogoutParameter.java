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
import cn.dev33.satoken.stp.parameter.enums.SaLogoutMode;
import cn.dev33.satoken.stp.parameter.enums.SaLogoutRange;

/**
 * 在会话注销时的 配置参数对象，决定注销时的一些细节行为 <br>
 *
 * <pre>
 *     	// 例如：
 *     	StpUtil.logout(10001, new SaLogoutParameter());
 * </pre>
 *
 * @author click33
 * @since 1.41.0
 */
public class SaLogoutParameter {

	/**
	 * 是否保留 Token-Session
	 */
	private Boolean isKeepTokenSession = false;

	/**
	 * 如果 token 已被冻结，是否保留其操作权 (是否允许此 token 调用注销API)
	 * (此参数只在调用 StpUtil.[logout/kickout/replaced]ByTokenValue("xxxxxxxxxxxxxxxxxxx", new SaLogoutParameter()) 时有效)
	 */
	private Boolean isKeepFreezeOps = false;

	/**
	 * 设备类型 (如果不指定，则默认注销所有客户端)
	 */
	private String deviceType;

	/**
	 * 注销类型
	 */
	private SaLogoutMode mode = SaLogoutMode.LOGOUT;

	/**
	 * 注销范围 (此参数只在调用 StpUtil.logout(new SaLogoutParameter()) 时有效)
	 */
	private SaLogoutRange range = SaLogoutRange.TOKEN;


	// ------ 附加方法

	public SaLogoutParameter() {
		this(SaManager.getConfig());
	}
	public SaLogoutParameter(SaTokenConfig config) {
		setDefaultValues(config);
	}

	/**
	 * 根据 SaTokenConfig 对象初始化默认值
	 *
	 * @param config 使用的配置对象
	 * @return 对象自身
	 */
	public SaLogoutParameter setDefaultValues(SaTokenConfig config) {
		return this;
	}

	/**
	 * 静态方法获取一个 SaLoginParameter 对象
	 * @return SaLoginParameter 对象
	 */
	public static SaLogoutParameter create() {
		return new SaLogoutParameter(SaManager.getConfig());
	}



	// ---------------- get set

	/**
	 * @return 是否保留 Token-Session
	 */
	public Boolean getIsKeepTokenSession() {
		return isKeepTokenSession;
	}

	/**
	 * @param isKeepTokenSession 是否保留 Token-Session
	 *
	 * @return 对象自身
	 */
	public SaLogoutParameter setIsKeepTokenSession(Boolean isKeepTokenSession) {
		this.isKeepTokenSession = isKeepTokenSession;
		return this;
	}

	/**
	 * 获取 如果 token 已被冻结，是否保留其操作权 (是否允许此 token 调用注销API)
	 * (此参数只在调用 StpUtil.[logout/kickout/replaced]ByTokenValue("xxxxxxxxxxxxxxxxxxx", new SaLogoutParameter()) 时有效)
	 *
	 * @return /
	 */
	public Boolean getIsKeepFreezeOps() {
		return this.isKeepFreezeOps;
	}

	/**
	 * 设置 如果 token 已被冻结，是否保留其操作权 (是否允许此 token 调用注销API)
	 * (此参数只在调用 StpUtil.[logout/kickout/replaced]ByTokenValue("xxxxxxxxxxxxxxxxxxx", new SaLogoutParameter()) 时有效)
	 *
	 * @param isKeepFreezeOps /
	 * @return 对象自身
	 */
	public SaLogoutParameter setIsKeepFreezeOps(Boolean isKeepFreezeOps) {
		this.isKeepFreezeOps = isKeepFreezeOps;
		return this;
	}
	/**
	 * 获取 设备类型 (如果不指定，则默认注销所有客户端)
	 *
	 * @return deviceType /
	 */
	public String getDeviceType() {
		return this.deviceType;
	}

	/**
	 * 设置 设备类型 (如果不指定，则默认注销所有客户端)
	 *
	 * @param deviceType /
	 * @return /
	 */
	public SaLogoutParameter setDeviceType(String deviceType) {
		this.deviceType = deviceType;
		return this;
	}

	/**
	 * 获取 注销类型
	 *
	 * @return logoutMode 注销类型
	 */
	public SaLogoutMode getMode() {
		return this.mode;
	}

	/**
	 * 设置 注销类型
	 *
	 * @param mode 注销类型
	 * @return /
	 */
	public SaLogoutParameter setMode(SaLogoutMode mode) {
		this.mode = mode;
		return this;
	}

	/**
	 * 获取 注销范围 (此参数只在调用 StpUtil.logout(new SaLogoutParameter()) 时有效)
	 *
	 * @return /
	 */
	public SaLogoutRange getRange() {
		return this.range;
	}

	/**
	 * 设置 注销范围 (此参数只在调用 StpUtil.logout(new SaLogoutParameter()) 时有效)
	 *
	 * @param range /
	 * @return /
	 */
	public SaLogoutParameter setRange(SaLogoutRange range) {
		this.range = range;
		return this;
	}

	/*
	 * toString
	 */
	@Override
	public String toString() {
		return "SaLoginParameter ["
				+ "deviceType=" + deviceType
				+ ", isKeepTokenSession=" + isKeepTokenSession
				+ ", isKeepFreezeOps=" + isKeepFreezeOps
				+ ", mode=" + mode
				+ ", range=" + range
				+ "]";
	}

}
