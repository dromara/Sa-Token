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
package cn.dev33.satoken.sign;

import cn.dev33.satoken.SaManager;
import cn.dev33.satoken.context.model.SaRequest;

import java.util.Map;

/**
 * API 参数签名算法 - 工具类
 *
 * @author click33
 * @since 1.34.0
 */
public class SaSignUtil {

	// ----------- 拼接参数

	/**
	 * 将所有参数连接成一个字符串(不排序)，形如：b=28a=18c=3
	 * @param paramsMap 参数列表
	 * @return 拼接出的参数字符串 
	 */
	public static String joinParams(Map<String, ?> paramsMap) {
		return SaManager.getSaSignTemplate().joinParams(paramsMap);
	}

	/**
	 * 将所有参数按照字典顺序连接成一个字符串，形如：a=18b=28c=3
	 * @param paramsMap 参数列表
	 * @return 拼接出的参数字符串 
	 */
	public static String joinParamsDictSort(Map<String, ?> paramsMap) {
		return SaManager.getSaSignTemplate().joinParamsDictSort(paramsMap);
	}


	// ----------- 创建签名

	/**
	 * 创建签名：md5(paramsStr + keyStr)
	 * @param paramsMap 参数列表
	 * @return 签名 
	 */
	public static String createSign(Map<String, ?> paramsMap) {
		return SaManager.getSaSignTemplate().createSign(paramsMap);
	}

	/**
	 * 给 paramsMap 追加 timestamp、nonce、sign 三个参数 
	 * @param paramsMap 参数列表
	 * @return 加工后的参数列表 
	 */
	public static Map<String, Object> addSignParams(Map<String, Object> paramsMap) {
		return SaManager.getSaSignTemplate().addSignParams(paramsMap);
	}

	/**
	 * 给 paramsMap 追加 timestamp、nonce、sign 三个参数，并转换为参数字符串，形如：
	 * <code>data=xxx8nonce=xxx8timestamp=xxx8sign=xxx</code>
	 * @param paramsMap 参数列表
	 * @return 加工后的参数列表 转化为的参数字符串
	 */
	public static String addSignParamsAndJoin(Map<String, Object> paramsMap) {
		return SaManager.getSaSignTemplate().addSignParamsAndJoin(paramsMap);
	}


	// ----------- 校验签名

	/**
	 * 判断：指定时间戳与当前时间戳的差距是否在允许的范围内
	 * @param timestamp 待校验的时间戳
	 * @return 是否在允许的范围内
	 */
	public static boolean isValidTimestamp(long timestamp) {
		return SaManager.getSaSignTemplate().isValidTimestamp(timestamp);
	}

	/**
	 * 校验：指定时间戳与当前时间戳的差距是否在允许的范围内，如果超出则抛出异常
	 * @param timestamp 待校验的时间戳
	 */
	public static void checkTimestamp(long timestamp) {
		SaManager.getSaSignTemplate().checkTimestamp(timestamp);
	}

	/**
	 * 判断：随机字符串 nonce 是否有效。
	 * 		注意：同一 nonce 可以被多次判断有效，不会被缓存
	 * @param nonce 待判断的随机字符串
	 * @return 是否有效
	 */
	public static boolean isValidNonce(String nonce) {
		return SaManager.getSaSignTemplate().isValidNonce(nonce);
	}

	/**
	 * 校验：随机字符串 nonce 是否有效，如果无效则抛出异常。
	 * 		注意：同一 nonce 只可以被校验通过一次，校验后将保存在缓存中，再次校验将无法通过
	 * @param nonce 待校验的随机字符串
	 */
	public static void checkNonce(String nonce) {
		SaManager.getSaSignTemplate().checkNonce(nonce);
	}

	/**
	 * 判断：给定的参数 生成的签名是否为有效签名
	 * @param paramsMap 参数列表
	 * @param sign 待验证的签名
	 * @return 签名是否有效
	 */
	public static boolean isValidSign(Map<String, ?> paramsMap, String sign) {
		return SaManager.getSaSignTemplate().isValidSign(paramsMap, sign);
	}

	/**
	 * 校验：给定的参数 生成的签名是否为有效签名，如果签名无效则抛出异常
	 * @param paramsMap 参数列表
	 * @param sign 待验证的签名
	 */
	public static void checkSign(Map<String, ?> paramsMap, String sign) {
		SaManager.getSaSignTemplate().checkSign(paramsMap, sign);
	}

	/**
	 * 判断：参数列表中的 nonce、timestamp、sign 是否均为合法的
	 * @param paramMap 待校验的请求参数集合
	 * @return 是否合法
	 */
	public static boolean isValidParamMap(Map<String, String> paramMap) {
		return SaManager.getSaSignTemplate().isValidParamMap(paramMap);
	}

	/**
	 * 校验：参数列表中的 nonce、timestamp、sign 是否均为合法的，如果不合法，则抛出对应的异常
	 * @param paramMap 待校验的请求参数集合
	 */
	public static void checkParamMap(Map<String, String> paramMap) {
		SaManager.getSaSignTemplate().checkParamMap(paramMap);
	}


	// ----------- Web 请求相关 封装

	/**
	 * 判断：一个请求中的 nonce、timestamp、sign 是否均为合法的
	 * @param request 待校验的请求对象
	 * @param paramNames 指定参与签名的参数有哪些，如果不填写则默认为全部参数
	 * @return 是否合法
	 */
	public static boolean isValidRequest(SaRequest request, String... paramNames) {
		return SaManager.getSaSignTemplate().isValidRequest(request, paramNames);
	}

	/**
	 * 校验：一个请求的 nonce、timestamp、sign 是否均为合法的，如果不合法，则抛出对应的异常
	 * @param request 待校验的请求对象
	 * @param paramNames 指定参与签名的参数有哪些，如果不填写则默认为全部参数
	 */
	public static void checkRequest(SaRequest request, String... paramNames) {
		SaManager.getSaSignTemplate().checkRequest(request, paramNames);
	}

}
