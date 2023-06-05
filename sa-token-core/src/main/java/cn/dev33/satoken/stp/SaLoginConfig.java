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

import java.util.Map;

/**
 * 快速、简洁的构建：调用 `StpUtil.login()` 时的 [ 配置参数 SaLoginModel ]
 *
 * <pre>
 *     	// 例如：在登录时指定 token 有效期为七天，代码如下：
 *     	StpUtil.login(10001, SaLoginConfig.setTimeout(60 * 60 * 24 * 7));
 *
 *     	// 上面的代码与下面的代码等价
 *     	StpUtil.login(10001, new SaLoginModel().setTimeout(60 * 60 * 24 * 7));
 * </pre>
 * 
 * @author click33
 * @since 1.29.0
 */
public class SaLoginConfig {
	
	private SaLoginConfig() {
	}

	/**
	 * @param device 此次登录的客户端设备类型 
	 * @return 登录参数 Model
	 */
	public static SaLoginModel setDevice(String device) {
		return create().setDevice(device);
	}

	/**
	 * @param isLastingCookie 是否为持久Cookie（临时Cookie在浏览器关闭时会自动删除，持久Cookie在重新打开后依然存在）
	 * @return 登录参数 Model
	 */
	public static SaLoginModel setIsLastingCookie(Boolean isLastingCookie) {
		return create().setIsLastingCookie(isLastingCookie);
	}

	/**
	 * @param timeout 指定此次登录token的有效期, 单位:秒 （如未指定，自动取全局配置的timeout值）
	 * @return 登录参数 Model
	 */
	public static SaLoginModel setTimeout(long timeout) {
		return create().setTimeout(timeout);
	}

	/**
	 * @param activeTimeout 指定此次登录 token 最低活跃频率，单位：秒（如未指定，自动取全局配置的 activeTimeout 值）
	 * @return 对象自身
	 */
	public static SaLoginModel setActiveTimeout(long activeTimeout) {
		return create().setActiveTimeout(activeTimeout);
	}

	/**
	 * @param extraData 扩展信息（只在jwt模式下生效）
	 * @return 登录参数 Model
	 */
	public static SaLoginModel setExtraData(Map<String, Object> extraData) {
		return create().setExtraData(extraData);
	}

	/**
	 * @param token 预定Token（预定本次登录生成的Token值）
	 * @return 登录参数 Model
	 */
	public static SaLoginModel setToken(String token) {
		return create().setToken(token);
	}

	/**
	 * 写入扩展数据（只在jwt模式下生效） 
	 * @param key 键
	 * @param value 值 
	 * @return 登录参数 Model
	 */
	public static SaLoginModel setExtra(String key, Object value) {
		return create().setExtra(key, value);
	}

	/**
	 * @param isWriteHeader 是否在登录后将 Token 写入到响应头
	 * @return 登录参数 Model
	 */
	public static SaLoginModel setIsWriteHeader(Boolean isWriteHeader) {
		return create().setIsWriteHeader(isWriteHeader);
	}

	/**
	 * 设置 本次登录挂载到 TokenSign 的数据
	 *
	 * @param tokenSignTag /
	 * @return 登录参数 Model
	 */
	public static SaLoginModel setTokenSignTag(Object tokenSignTag) {
		return create().setTokenSignTag(tokenSignTag);
	}

	/**
	 * 静态方法获取一个 SaLoginModel 对象
	 * @return SaLoginModel 对象 
	 */
	public static SaLoginModel create() {
		return new SaLoginModel();
	}

}
