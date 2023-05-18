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
 * Token 签名 Model
 *
 * <p> 挂在到 SaSession 上的 Token 签名，一般情况下，一个 TokenSign 代表一个登录的会话。</p>
 *
 * @author click33
 * @since <= 1.34.0
 */
public class TokenSign implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = 1406115065849845073L;

	/**
	 * Token 值
	 */
	private String value;

	/**
	 * 所属设备类型
	 */
	private String device;

	/**
	 * 此客户端登录的挂载数据
	 */
	private Object tag;

	/**
	 * 构建一个
	 */
	public TokenSign() {
	}

	/**
	 * 构建一个
	 *
	 * @param value  Token 值
	 * @param device 所属设备类型
	 * @param tag 此客户端登录的挂载数据
	 */
	public TokenSign(String value, String device, Object tag) {
		this.value = value;
		this.device = device;
		this.tag = tag;
	}

	/**
	 * @return Token 值
	 */
	public String getValue() {
		return value;
	}

	/**
	 * @return 所属设备类型
	 */
	public String getDevice() {
		return device;
	}

	/**
	 * 写入 Token 值
	 * 
	 * @param value /
	 * @return 对象自身
	 */
	public TokenSign setValue(String value) {
		this.value = value;
		return this;
	}

	/**
	 * 写入所属设备类型
	 * 
	 * @param device /
	 * @return 对象自身
	 */
	public TokenSign setDevice(String device) {
		this.device = device;
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
	public TokenSign setTag(Object tag) {
		this.tag = tag;
		return this;
	}

	//
	@Override
	public String toString() {
		return "TokenSign [value=" + value + ", device=" + device + ", tag=" + tag + "]";
	}

}
