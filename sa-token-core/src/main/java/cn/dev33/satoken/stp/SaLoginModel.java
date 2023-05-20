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
package cn.dev33.satoken.stp;

import java.util.LinkedHashMap;
import java.util.Map;

import cn.dev33.satoken.SaManager;
import cn.dev33.satoken.config.SaTokenConfig;
import cn.dev33.satoken.dao.SaTokenDao;
import cn.dev33.satoken.util.SaTokenConsts;

/**
 * 在调用 `StpUtil.login()` 时的 配置参数 Model，决定登录的一些细节行为 <br>
 *
 * <pre>
 *     	// 例如：在登录时指定 token 有效期为七天，代码如下：
 *     	StpUtil.login(10001, new SaLoginModel().setTimeout(60 * 60 * 24 * 7));
 * </pre>
 *
 * @author click33
 * @since 1.13.2
 */
public class SaLoginModel {

	/**
	 * 此次登录的客户端设备类型 
	 */
	public String device;
	
	/**
	 * 是否为持久Cookie（临时Cookie在浏览器关闭时会自动删除，持久Cookie在重新打开后依然存在）
	 */
	public Boolean isLastingCookie = true;

	/**
	 * 指定此次登录token的有效期, 单位:秒 （如未指定，自动取全局配置的timeout值）
	 */
	public Long timeout;

	/**
	 * 扩展信息（只在jwt模式下生效）
	 */
	public Map<String, Object> extraData;

	/**
	 * 预定Token（预定本次登录生成的Token值）
	 */
	public String token;

	/** 是否在登录后将 Token 写入到响应头 */
	private Boolean isWriteHeader;

	/** 本次登录挂载到 TokenSign 的数据 */
	private Object tokenSignTag;


	/**
	 * @return 此次登录的客户端设备类型
	 */
	public String getDevice() {
		return device;
	}

	/**
	 * @param device 此次登录的客户端设备类型
	 * @return 对象自身
	 */
	public SaLoginModel setDevice(String device) {
		this.device = device;
		return this;
	}

	/**
	 * @return 是否为持久Cookie（临时Cookie在浏览器关闭时会自动删除，持久Cookie在重新打开后依然存在）
	 */
	public Boolean getIsLastingCookie() {
		return isLastingCookie;
	}

	/**
	 * @return 是否为持久Cookie（临时Cookie在浏览器关闭时会自动删除，持久Cookie在重新打开后依然存在）
	 */
	public Boolean getIsLastingCookieOrFalse() {
		if(isLastingCookie == null) {
			return false;
		}
		return isLastingCookie;
	}

	/**
	 * @param isLastingCookie 是否为持久Cookie（临时Cookie在浏览器关闭时会自动删除，持久Cookie在重新打开后依然存在）
	 * @return 对象自身
	 */
	public SaLoginModel setIsLastingCookie(Boolean isLastingCookie) {
		this.isLastingCookie = isLastingCookie;
		return this;
	}

	/**
	 * @return 指定此次登录token的有效期, 单位:秒 （如未指定，自动取全局配置的timeout值）
	 */
	public Long getTimeout() {
		return timeout;
	}

	/**
	 * @return timeout 值 （如果此配置项尚未配置，则取全局配置的值）
	 */
	public Long getTimeoutOrGlobalConfig() {
		if(timeout == null) {
			timeout = SaManager.getConfig().getTimeout();
		}
		return timeout;
	}
	
	/**
	 * @param timeout 指定此次登录token的有效期, 单位:秒 （如未指定，自动取全局配置的timeout值）
	 * @return 对象自身
	 */
	public SaLoginModel setTimeout(long timeout) {
		this.timeout = timeout;
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
	public SaLoginModel setExtraData(Map<String, Object> extraData) {
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
	public SaLoginModel setToken(String token) {
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
	 * @return 是否在登录后将 Token 写入到响应头 （如果此配置项尚未配置，则取全局配置的值）
	 */
	public Boolean getIsWriteHeaderOrGlobalConfig() {
		if(isWriteHeader == null) {
			isWriteHeader = SaManager.getConfig().getIsWriteHeader();
		}
		return isWriteHeader;
	}

	/**
	 * @param isWriteHeader 是否在登录后将 Token 写入到响应头
	 * @return 对象自身
	 */
	public SaLoginModel setIsWriteHeader(Boolean isWriteHeader) {
		this.isWriteHeader = isWriteHeader;
		return this;
	}

	/**
	 * 获取 本次登录挂载到 TokenSign 的数据
	 *
	 * @return tokenSignTag 本次登录挂载到 TokenSign 的数据
	 */
	public Object getTokenSignTag() {
		return this.tokenSignTag;
	}

	/**
	 * 设置 本次登录挂载到 TokenSign 的数据
	 *
	 * @param tokenSignTag 本次登录挂载到 TokenSign 的数据
	 * @return 对象自身
	 */
	public SaLoginModel setTokenSignTag(Object tokenSignTag) {
		this.tokenSignTag = tokenSignTag;
		return this;
	}
	/*
	 * toString 
	 */
	@Override
	public String toString() {
		return "SaLoginModel ["
				+ "device=" + device
				+ ", isLastingCookie=" + isLastingCookie
				+ ", timeout=" + timeout
				+ ", extraData=" + extraData
				+ ", token=" + token
				+ ", isWriteHeader=" + isWriteHeader
				+ ", tokenSignTag=" + tokenSignTag
				+ "]";
	}


	// ------ 附加方法 

	/**
	 * 写入扩展数据（只在jwt模式下生效） 
	 * @param key 键
	 * @param value 值 
	 * @return 对象自身 
	 */
	public SaLoginModel setExtra(String key, Object value) {
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
	 * 判断是否设置了扩展数据 
	 * @return / 
	 */
	public boolean isSetExtraData() {
		return extraData != null && extraData.size() != 0;
	}

	/**
	 * @return Cookie时长
	 */
	public int getCookieTimeout() {
		if( ! getIsLastingCookieOrFalse()) {
			return -1;
		}
		if(getTimeoutOrGlobalConfig() == SaTokenDao.NEVER_EXPIRE) {
			return Integer.MAX_VALUE;
		}
		return (int)(long)timeout;
	}

	/**
	 * @return 获取device参数，如果为null，则返回默认值
	 */
	public String getDeviceOrDefault() {
		if(device == null) {
			return SaTokenConsts.DEFAULT_LOGIN_DEVICE;
		}
		return device;
	}
	
	/**
	 * 构建对象，初始化默认值 
	 * @return 对象自身
	 */
	public SaLoginModel build() {
		return build(SaManager.getConfig());
	}
	
	/**
	 * 构建对象，初始化默认值 
	 * @param config 配置对象 
	 * @return 对象自身
	 */
	public SaLoginModel build(SaTokenConfig config) {
//		if(device == null) {
//			device = SaTokenConsts.DEFAULT_LOGIN_DEVICE;
//		}
//		if(isLastingCookie == null) {
//			isLastingCookie = true;
//		}
		if(timeout == null) {
			timeout = config.getTimeout();
		}
		if(isWriteHeader == null) {
			isWriteHeader = config.getIsWriteHeader();
		}
		return this;
	}
	
	/**
	 * 静态方法获取一个 SaLoginModel 对象
	 * @return SaLoginModel 对象 
	 */
	public static SaLoginModel create() {
		return new SaLoginModel();
	}

}
