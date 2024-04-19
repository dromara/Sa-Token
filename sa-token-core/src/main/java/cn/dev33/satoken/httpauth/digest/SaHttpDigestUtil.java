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
package cn.dev33.satoken.httpauth.digest;

import cn.dev33.satoken.annotation.SaCheckHttpDigest;
import cn.dev33.satoken.context.SaHolder;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Sa-Token Http Digest 认证模块，Util 工具类
 *
 * @author click33
 * @since 1.38.0
 */
public class SaHttpDigestUtil {

	private SaHttpDigestUtil() {
	}
	
	/**
	 * 底层使用的 SaHttpDigestTemplate 对象
	 */
	public static SaHttpDigestTemplate saHttpDigestTemplate = new SaHttpDigestTemplate();


	/**
	 * 获取浏览器提交的 Digest 参数 （裁剪掉前缀）
	 * @return 值
	 */
	public static String getAuthorizationValue() {
		return saHttpDigestTemplate.getAuthorizationValue();
	}

	/**
	 * 获取浏览器提交的 Digest 参数，并转化为 Map
	 * @return /
	 */
	public static SaHttpDigestModel getAuthorizationValueToModel() {
		return saHttpDigestTemplate.getAuthorizationValueToModel();
	}

	// ---------- 校验 ----------

	/**
	 * 校验：根据提供 Digest 参数计算 res，与 request 请求中的 Digest 参数进行校验，校验不通过则抛出异常
	 * @param hopeModel 提供的 Digest 参数对象
	 */
	public static void check(SaHttpDigestModel hopeModel) {
		saHttpDigestTemplate.check(hopeModel);
	}

	/**
	 * 校验：根据提供的参数，校验不通过抛出异常
	 * @param username 用户名
	 * @param password 密码
	 */
	public static void check(String username, String password) {
		saHttpDigestTemplate.check(username, password);
	}

	/**
	 * 校验：根据提供的参数，校验不通过抛出异常
	 * @param username 用户名
	 * @param password 密码
	 * @param realm 领域
	 */
	public static void check(String username, String password, String realm) {
		saHttpDigestTemplate.check(username, password, realm);
	}

	/**
	 * 校验：根据全局配置参数，校验不通过抛出异常
	 */
	public static void check() {
		saHttpDigestTemplate.check();
	}

	/**
	 * 根据注解 ( @SaCheckHttpDigest ) 鉴权
	 *
	 * @param at 注解对象
	 */
	public static void checkByAnnotation(SaCheckHttpDigest at) {
		saHttpDigestTemplate.checkByAnnotation(at);
	}

}
