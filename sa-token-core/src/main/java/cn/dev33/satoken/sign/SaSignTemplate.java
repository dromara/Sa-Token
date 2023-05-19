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
import cn.dev33.satoken.config.SaSignConfig;
import cn.dev33.satoken.context.model.SaRequest;
import cn.dev33.satoken.error.SaErrorCode;
import cn.dev33.satoken.exception.SaSignException;
import cn.dev33.satoken.secure.SaSecureUtil;
import cn.dev33.satoken.util.SaFoxUtil;

import java.util.Map;
import java.util.TreeMap;

/**
 * API 参数签名算法，在跨系统接口调用时防参数篡改、防重放攻击。
 *
 * <p>
 *     以 SSO 数据拉取为例，流程大致如下：
 *     <br> 1. 以 md5( loginId={账号id}8nonce={随机字符串}8timestamp={13位时间戳}8key={secretkey秘钥} ) 生成签名 sign。
 *     <br> 2. 将 sign 作为参数，拼接到请求地址后面，如：http://xxx.com?loginId=100018nonce=xxx8timestamp=xxx8sign=xxx。
 *     <br> 3. 服务端接收到请求后，以同样的算法生成一次 sign 。
 *     <br> 4. 对比两次 sign 是否一致，一致则通过，否则拒绝 。
 * </p>
 *
 * @author click33
 * @since 2022-4-27
 */
public class SaSignTemplate {

	// ----------- 签名配置

	SaSignConfig signConfig;

	/**
	 * 获取：API 签名配置
	 * @return /
	 */
	public SaSignConfig getSignConfig() {
		return signConfig;
	}

	/**
	 * 获取：API 签名配置：
	 * 	1. 如果用户自定义了 signConfig ，则使用用户自定义的。
	 * 	2. 否则使用全局默认配置。
	 *
	 * @return /
	 */
	public SaSignConfig getSignConfigOrGlobal() {
		// 如果用户自定义了 signConfig ，则使用用户自定义的
		if(signConfig != null) {
			return signConfig;
		}
		// 否则使用全局默认配置
		return SaManager.getConfig().getSign();
	}

	/**
	 * 获取：API 签名配置的秘钥
	 * @return /
	 */
	public String getSecretKey() {
		return getSignConfigOrGlobal().getSecretKey();
	}

	/**
	 * 设置：API 签名配置
	 * @param signConfig /
	 */
	public SaSignTemplate setSignConfig(SaSignConfig signConfig) {
		this.signConfig = signConfig;
		return this;
	}


	// ----------- 自定义使用的参数名称 (不声明final，允许开发者自定义修改)

	public static String key = "key";
	public static String timestamp = "timestamp";
	public static String nonce = "nonce";
	public static String sign = "sign";


	// ----------- 拼接参数

	/**
	 * 将所有参数连接成一个字符串(不排序)，形如：b=28a=18c=3
	 * @param paramsMap 参数列表
	 * @return 拼接出的参数字符串 
	 */
	public String joinParams(Map<String, ?> paramsMap) {
		
		// 按照 k1=v1&k2=v2&k3=v3 排列 
        StringBuilder sb = new StringBuilder();
        for (String key : paramsMap.keySet()) {
        	Object value = paramsMap.get(key);
        	if( ! SaFoxUtil.isEmpty(value) ) {
        		sb.append(key).append("=").append(value).append("&");
        	}
        }
        
        // 删除最后一位 & 
        if(sb.length() > 0) {
        	sb.deleteCharAt(sb.length() - 1);
        }
        
        // .
        return sb.toString();
	}

	/**
	 * 将所有参数按照字典顺序连接成一个字符串，形如：a=18b=28c=3
	 * @param paramsMap 参数列表
	 * @return 拼接出的参数字符串 
	 */
	public String joinParamsDictSort(Map<String, ?> paramsMap) {
		// 保证字段按照字典顺序排列 
		if( ! (paramsMap instanceof TreeMap) ) {
			paramsMap = new TreeMap<>(paramsMap);
		}
		
		// 拼接 
        return joinParams(paramsMap);
	}


	// ----------- 创建签名

	/**
	 * 创建签名：md5(paramsStr + keyStr)
	 * @param paramsMap 参数列表
	 * @return 签名 
	 */
	public String createSign(Map<String, ?> paramsMap) {
		String secretKey = getSecretKey();
		SaSignException.throwByNull(secretKey, "参与参数签名的秘钥不可为空", SaErrorCode.CODE_12201);

		// 如果调用者不小心传入了 sign 参数，则此处需要将 sign 参数排除在外
		if(paramsMap.containsKey(sign)) {
			// 为了保证不影响原有的 paramsMap，此处需要再复制一份
			paramsMap = new TreeMap<>(paramsMap);
			paramsMap.remove(sign);
		}

		// 计算签名
		String paramsStr = joinParamsDictSort(paramsMap);
		String fullStr = paramsStr + "&" + key + "=" + secretKey;
		return abstractStr(fullStr);
	}

	/**
	 * 使用摘要算法创建签名
	 * @param fullStr 待摘要的字符串
	 * @return 签名
	 */
	public String abstractStr(String fullStr) {
		return SaSecureUtil.md5(fullStr);
	}

	/**
	 * 给 paramsMap 追加 timestamp、nonce、sign 三个参数 
	 * @param paramsMap 参数列表
	 * @return 加工后的参数列表 
	 */
	public Map<String, Object> addSignParams(Map<String, Object> paramsMap) {
		paramsMap.put(timestamp, String.valueOf(System.currentTimeMillis()));
		paramsMap.put(nonce, SaFoxUtil.getRandomString(32));
		paramsMap.put(sign, createSign(paramsMap));
		return paramsMap;
	}

	/**
	 * 给 paramsMap 追加 timestamp、nonce、sign 三个参数，并转换为参数字符串，形如：
	 * <code>data=xxx8nonce=xxx8timestamp=xxx8sign=xxx</code>
	 * @param paramsMap 参数列表
	 * @return 加工后的参数列表 转化为的参数字符串
	 */
	public String addSignParamsAndJoin(Map<String, Object> paramsMap) {
		// 追加参数
		paramsMap = addSignParams(paramsMap);

		// 拼接参数
		return joinParams(paramsMap);
	}


	// ----------- 校验签名

	/**
	 * 判断：指定时间戳与当前时间戳的差距是否在允许的范围内
	 * @param timestamp 待校验的时间戳
	 * @return 是否在允许的范围内
	 */
	public boolean isValidTimestamp(long timestamp) {
		long allowDisparity = getSignConfigOrGlobal().getTimestampDisparity();
		long disparity = Math.abs(System.currentTimeMillis() - timestamp);
		return allowDisparity == -1 || disparity <= allowDisparity;
	}

	/**
	 * 校验：指定时间戳与当前时间戳的差距是否在允许的范围内，如果超出则抛出异常
	 * @param timestamp 待校验的时间戳
	 */
	public void checkTimestamp(long timestamp) {
		if( ! isValidTimestamp(timestamp) ) {
			throw new SaSignException("timestamp 超出允许的范围：" + timestamp).setCode(SaErrorCode.CODE_12203);
		}
	}

	/**
	 * 判断：随机字符串 nonce 是否有效。
	 * 		注意：同一 nonce 可以被多次判断有效，不会被缓存
	 * @param nonce 待判断的随机字符串
	 * @return 是否有效
	 */
	public boolean isValidNonce(String nonce) {
		// 为空代表无效
		if(SaFoxUtil.isEmpty(nonce)) {
			return false;
		}

		// 校验此 nonce 是否已被使用过
		String key = splicingNonceSaveKey(nonce);
		return SaManager.getSaTokenDao().get(key) == null;
	}

	/**
	 * 校验：随机字符串 nonce 是否有效，如果无效则抛出异常。
	 * 		注意：同一 nonce 只可以被校验通过一次，校验后将保存在缓存中，再次校验将无法通过
	 * @param nonce 待校验的随机字符串
	 */
	public void checkNonce(String nonce) {
		// 为空代表无效
		if(SaFoxUtil.isEmpty(nonce)) {
			throw new SaSignException("nonce 为空，无效");
		}

		// 校验此 nonce 是否已被使用过
		String key = splicingNonceSaveKey(nonce);
		if(SaManager.getSaTokenDao().get(key) != null) {
			throw new SaSignException("此 nonce 已被使用过，不可重复使用：" + nonce);
		}

		// 校验通过后，将此 nonce 保存在缓存中，保证下次校验无法通过
		SaManager.getSaTokenDao().set(key, nonce, getSignConfigOrGlobal().getSaveNonceExpire() * 2 + 2);
	}

	/**
	 * 判断：给定的参数 + 秘钥 生成的签名是否为有效签名
	 * @param paramsMap 参数列表
	 * @param sign 待验证的签名
	 * @return 签名是否有效
	 */
	public boolean isValidSign(Map<String, ?> paramsMap, String sign) {
		String theSign = createSign(paramsMap);
		return theSign.equals(sign);
	}

	/**
	 * 校验：给定的参数 + 秘钥 生成的签名是否为有效签名，如果签名无效则抛出异常
	 * @param paramsMap 参数列表
	 * @param sign 待验证的签名
	 */
	public void checkSign(Map<String, ?> paramsMap, String sign) {
		if( ! isValidSign(paramsMap, sign) )  {
			throw new SaSignException("无效签名：" + sign).setCode(SaErrorCode.CODE_12202);
		}
	}

	/**
	 * 判断：参数列表中的 nonce、timestamp、sign 是否均为合法的
	 * @param paramMap 待校验的请求参数集合
	 * @return 是否合法
	 */
	@SuppressWarnings("all")
	public boolean isValidParamMap(Map<String, String> paramMap) {
		// 获取必须的三个参数
		String timestampValue = paramMap.get(timestamp);
		String nonceValue = paramMap.get(nonce);
		String signValue = paramMap.get(sign);

		// 三个参数必须全部非空
		SaSignException.throwByNull(timestampValue, "缺少 timestamp 字段");
		SaSignException.throwByNull(nonceValue, "缺少 nonce 字段");
		SaSignException.throwByNull(signValue, "缺少 sign 字段");

		// 三个值的校验必须全部通过
		return isValidTimestamp(Long.parseLong(timestampValue))
				&& (getSignConfigOrGlobal().getIsCheckNonce() ? isValidNonce(nonceValue) : true)
				&& isValidSign(paramMap, signValue);
	}

	/**
	 * 校验：参数列表中的 nonce、timestamp、sign 是否均为合法的，如果不合法，则抛出对应的异常
	 * @param paramMap 待校验的请求参数集合
	 */
	public void checkParamMap(Map<String, String> paramMap) {
		// 获取必须的三个参数
		String timestampValue = paramMap.get(timestamp);
		String nonceValue = paramMap.get(nonce);
		String signValue = paramMap.get(sign);

		// 依次校验三个参数
		checkTimestamp(Long.parseLong(timestampValue));
		if(getSignConfigOrGlobal().getIsCheckNonce()) {
			checkNonce(nonceValue);
		}
		checkSign(paramMap, signValue);

		// 通过 √
	}

	/**
	 * 判断：一个请求中的 nonce、timestamp、sign 是否均为合法的
	 * @param request 待校验的请求对象
	 * @return 是否合法
	 */
	public boolean isValidRequest(SaRequest request) {
		return isValidParamMap(request.getParamMap());
	}

	/**
	 * 校验：一个请求的 nonce、timestamp、sign 是否均为合法的，如果不合法，则抛出对应的异常
	 * @param request 待校验的请求对象
	 */
	public void checkRequest(SaRequest request) {
		checkParamMap(request.getParamMap());
	}


	// ------------------- 返回相应key -------------------

	/**
	 * 拼接key：存储 nonce 时使用的 key
	 * @param nonce nonce 值
	 * @return key
	 */
	public String splicingNonceSaveKey(String nonce) {
		return SaManager.getConfig().getTokenName() + ":sign:nonce:" + nonce;
	}

}
