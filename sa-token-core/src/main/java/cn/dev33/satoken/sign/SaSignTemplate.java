package cn.dev33.satoken.sign;

import cn.dev33.satoken.error.SaErrorCode;
import cn.dev33.satoken.exception.SaTokenException;
import cn.dev33.satoken.secure.SaSecureUtil;
import cn.dev33.satoken.util.SaFoxUtil;

import java.util.Map;
import java.util.TreeMap;

/**
 * 参数签名算法
 *
 * @author kong
 * @since 2022-4-27
 */
public interface SaSignTemplate {

	/**
	 * 将所有参数连接成一个字符串(不排序)，形如：b=28a=18c=3
	 * @param paramsMap 参数列表
	 * @return 拼接出的参数字符串 
	 */
	public default String joinParams(Map<String, ?> paramsMap) {
		
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
	public default String joinParamsDictSort(Map<String, ?> paramsMap) {
		// 保证字段按照字典顺序排列 
		if( ! (paramsMap instanceof TreeMap) ) {
			paramsMap = new TreeMap<>(paramsMap);
		}
		
		// 拼接 
        return joinParams(paramsMap);
	}

	/**
	 * 创建签名：md5(paramsStr + keyStr)
	 * @param paramsMap 参数列表 
	 * @param key 秘钥 
	 * @return 签名 
	 */
	public default String createSign(Map<String, ?> paramsMap, String key) {
		SaTokenException.throwByNull(key, "参与参数签名的秘钥不可为空", SaErrorCode.CODE_12201);

		// 如果调用者不小心传入了 sign 参数，则此处需要将 sign 参数排除在外
		// 为了保证不影响原有的 paramsMap，此处需要再复制一份
		if(paramsMap.containsKey("sign")) {
			paramsMap = new TreeMap<>(paramsMap);
			paramsMap.remove("sign");
		}

		// 计算签名
		String paramsStr = joinParamsDictSort(paramsMap);
		String fullStr = paramsStr + "&key=" + key;
		return SaSecureUtil.md5(fullStr);
	}

	/**
	 * 判断：给定的参数 + 秘钥 生成的签名是否为有效签名
	 * @param paramsMap 参数列表
	 * @param key 秘钥
	 * @param sign 待验证的签名
	 * @return 签名是否有效
	 */
	public default boolean isValidSign(Map<String, ?> paramsMap, String key, String sign) {
		String theSign = createSign(paramsMap, key);
		return theSign.equals(sign);
	}

	/**
	 * 校验：给定的参数 + 秘钥 生成的签名是否为有效签名，如果签名无效则抛出异常
	 * @param paramsMap 参数列表
	 * @param key 秘钥
	 * @param sign 待验证的签名
	 */
	public default void checkSign(Map<String, ?> paramsMap, String key, String sign) {
		if( ! isValidSign(paramsMap, key, sign) ) {
			throw new SaTokenException("无效签名：" + sign).setCode(SaErrorCode.CODE_12202);
		}
	}

	/**
	 * 给 paramsMap 追加 timestamp、nonce、sign 三个参数 
	 * @param paramsMap 参数列表 
	 * @param key 秘钥 
	 * @return 加工后的参数列表 
	 */
	public default Map<String, Object> addSignParams(Map<String, Object> paramsMap, String key) {
		paramsMap.put("timestamp", String.valueOf(System.currentTimeMillis()));
		paramsMap.put("nonce", SaFoxUtil.getRandomString(32));
		paramsMap.put("sign", createSign(paramsMap, key));
		return paramsMap;
	}

	/**
	 * 给 paramsMap 追加 timestamp、nonce、sign 三个参数，并转换为参数字符串，形如：
	 * <code>data=xxx8nonce=xxx8timestamp=xxx8sign=xxx</code>
	 * @param paramsMap 参数列表
	 * @param key 秘钥
	 * @return 加工后的参数列表 转化为的参数字符串
	 */
	public default String addSignParamsAndToString(Map<String, Object> paramsMap, String key) {
		// 追加参数
		paramsMap = addSignParams(paramsMap, key);

		// .
		return joinParams(paramsMap);
	}

	/**
	 * 判断：指定时间戳与当前时间戳的差距是否在允许的范围内
	 * @param timestamp 待校验的时间戳
	 * @param allowDisparity 允许的最大时间差（单位：ms），-1 代表不限制
	 * @return 是否在允许的范围内
	 */
	public default boolean isValidTimestamp(long timestamp, long allowDisparity) {
		long disparity = Math.abs(System.currentTimeMillis() - timestamp);
		return allowDisparity == -1 || disparity <= allowDisparity;
	}

	/**
	 * 校验：指定时间戳与当前时间戳的差距是否在允许的范围内，如果超出则抛出异常
	 * @param timestamp 待校验的时间戳
	 * @param allowDisparity 允许的最大时间差（单位：ms），-1 代表不限制
	 */
	public default void checkTimestamp(long timestamp, long allowDisparity) {
		if( ! isValidTimestamp(timestamp, allowDisparity) ) {
			throw new SaTokenException("timestamp 超出允许的范围：" + timestamp).setCode(SaErrorCode.CODE_12203);
		}
	}

	// ------------------ 以下为兼容旧版本的方法 ------------------

	/**
	 * 请更换为 addSignParamsAndToString
	 * @param paramsMap 参数列表
	 * @param key 秘钥
	 * @return 加工后的参数列表 转化为的参数字符串
	 */
	@Deprecated
	public default String addSignParamsToString(Map<String, Object> paramsMap, String key) {
		// 追加参数
		paramsMap = addSignParams(paramsMap, key);

		// .
		return joinParams(paramsMap);
	}

}
