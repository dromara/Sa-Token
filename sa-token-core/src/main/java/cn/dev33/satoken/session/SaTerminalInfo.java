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

import java.io.Serializable;

/**
 * 登录设备终端信息 Model
 *
 * @author click33
 * @since 1.41.0
 */
public class SaTerminalInfo implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = 1406115065849845073L;

	/**
	 * 登录会话索引值 (该账号第几个登录的设备, 从 1 开始)
	 */
	private int index;

	/**
	 * Token 值
	 */
	private String tokenValue;

	/**
	 * 所属设备类型，例如：PC、WEB、HD、MOBILE、APP
	 */
	private String deviceType;

	/**
	 * 登录设备唯一标识，例如：kQwIOrYvnXmSDkwEiFngrKidMcdrgKorXmSDkwEiFngrKidM
	 */
	private String deviceId;

	/**
	 * 此次登录的自定义挂载数据
	 */
	private Object tag;

	/**
	 * 创建时间
	 */
	private long createTime;

	/**
	 * 构建一个
	 */
	public SaTerminalInfo() {
	}

	/**
	 * 构建一个
	 *
	 * @param index 		登录会话索引值 (该账号第几个登录的设备)
	 * @param tokenValue  	Token 值
	 * @param deviceType 	所属设备类型
	 * @param tag 			此客户端登录的挂载数据
	 */
	public SaTerminalInfo(int index, String tokenValue, String deviceType, Object tag) {
		this.index = index;
		this.tokenValue = tokenValue;
		this.deviceType = deviceType;
		this.tag = tag;
		this.createTime = System.currentTimeMillis();
	}

	/**
	 * 获取 登录会话索引值 (该账号第几个登录的设备)
	 *
	 * @return index 登录会话索引值 (该账号第几个登录的设备)
	 */
	public int getIndex() {
		return this.index;
	}

	/**
	 * 设置 登录会话索引值 (该账号第几个登录的设备)
	 *
	 * @param index 登录会话索引值 (该账号第几个登录的设备)
	 * @return 对象自身
	 */
	public SaTerminalInfo setIndex(int index) {
		this.index = index;
		return this;
	}

	/**
	 * @return Token 值
	 */
	public String getTokenValue() {
		return tokenValue;
	}

	/**
	 * 写入 Token 值
	 *
	 * @param tokenValue /
	 * @return 对象自身
	 */
	public SaTerminalInfo setTokenValue(String tokenValue) {
		this.tokenValue = tokenValue;
		return this;
	}

	/**
	 * @return 所属设备类型
	 */
	public String getDeviceType() {
		return deviceType;
	}

	/**
	 * 写入所属设备类型
	 * 
	 * @param deviceType /
	 * @return 对象自身
	 */
	public SaTerminalInfo setDeviceType(String deviceType) {
		this.deviceType = deviceType;
		return this;
	}

	/**
	 * 获取 登录设备唯一标识
	 *
	 * @return deviceId 登录设备唯一标识
	 */
	public String getDeviceId() {
		return this.deviceId;
	}

	/**
	 * 设置 登录设备唯一标识，例如：kQwIOrYvnXmSDkwEiFngrKidMcdrgKorXmSDkwEiFngrKidM
	 *
	 * @param deviceId 登录设备唯一标识，例如：kQwIOrYvnXmSDkwEiFngrKidMcdrgKorXmSDkwEiFngrKidM
	 * @return 对象自身
	 */
	public SaTerminalInfo setDeviceId(String deviceId) {
		this.deviceId = deviceId;
		return this;
	}

	/**
	 * 获取 此客户端登录的挂载数据
	 *
	 * @return /
	 */
	public Object getTag() {
		return this.tag;
	}

	/**
	 * 设置 此客户端登录的挂载数据
	 *
	 * @param tag /
	 * @return 对象自身
	 */
	public SaTerminalInfo setTag(Object tag) {
		this.tag = tag;
		return this;
	}

	/**
	 * 获取 创建时间
	 *
	 * @return createTime 创建时间
	 */
	public long getCreateTime() {
		return this.createTime;
	}

	/**
	 * 设置 创建时间
	 *
	 * @param createTime 创建时间
	 * @return 对象自身
	 */
	public SaTerminalInfo setCreateTime(long createTime) {
		this.createTime = createTime;
		return this;
	}

	//
	@Override
	public String toString() {
		return "TokenSign [" +
				"index=" + index +
				", tokenValue=" + tokenValue +
				", deviceType=" + deviceType +
				", deviceId=" + deviceId +
				", tag=" + tag +
				", createTime=" + createTime +
				"]";
	}



	/*
	 * Expand in the future:
	 * 		deviceName  登录设备端名称，一般为浏览器名称
	 * 		systemName  登录设备操作系统名称
	 * 		loginIp  登录IP地址
	 * 		address  登录设备地理位置
	 * 		loginTime  登录时间
	 */

}
