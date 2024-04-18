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

		// 获取前端提交的请求头 Authorization 参数
		String authorization = SaHolder.getRequest().getHeader("Authorization");

		// 如果不是以 Digest 作为前缀，则视为无效
		if(authorization == null || ! authorization.startsWith("Digest ")) {
			return null;
		}

		// 裁剪前缀并解码
		return authorization.substring(7);
	}

	/**
	 * 获取浏览器提交的 Digest 参数，并转化为 Map
	 * @return /
	 */
	public static SaHttpDigestModel getAuthorizationValueToModel() {

		// 先获取字符串值
		String authorization = getAuthorizationValue();
		if(authorization == null) {
			//            throw new SaTokenException("请求头中未携带 Digest 认证参数");
			return null;
		}

		// 根据逗号分割，解析为 Map
		Map<String, String> map = new LinkedHashMap<>();
		String[] arr = authorization.split(",");
		for (String s : arr) {
			String[] kv = s.split("=");
			if (kv.length == 2) {
				map.put(kv[0].trim(), kv[1].trim().replace("\"", ""));
			}
		}

        /*
            参考样例：
                username=sa,
                realm=Sa-Token,
                nonce=dcd98b7102dd2f0e8b11d0f600bfb0c093,
                uri=/test/testDigest,
                response=a32023c128e142163dd4856a2f511c70,
                opaque=5ccc069c403ebaf9f0171e9517f40e41,
                qop=auth,
                nc=00000002,
                cnonce=f3ca6bfc0b2f59c4
         */

		// 转化为 Model
		SaHttpDigestModel model = new SaHttpDigestModel();
		model.username = map.get("username");
		model.realm = map.get("realm");
		model.nonce = map.get("nonce");
		model.uri = map.get("uri");
		model.method = SaHolder.getRequest().getMethod();
		model.qop = map.get("qop");
		model.nc = map.get("nc");
		model.cnonce = map.get("cnonce");
		model.opaque = map.get("opaque");
		model.response = map.get("response");

		//
		return model;
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
