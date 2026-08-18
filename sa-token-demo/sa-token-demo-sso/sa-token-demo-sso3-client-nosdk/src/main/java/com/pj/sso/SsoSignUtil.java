package com.pj.sso;

import com.pj.sso.util.CacheUtil;

import java.security.MessageDigest;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;

/**
 * SSO 签名工具类：签名生成、注入与校验
 *
 * @author click33
 */
public class SsoSignUtil {

	/** 时间戳允许的最大偏差（与官方 SaSignConfig.timestampDisparity 默认值一致：15 分钟） */
	private static final long TIMESTAMP_DISPARITY = 1000L * 60 * 15;



	// -------------------------- 签名方法

	/**
	 * 计算签名：将 params（排除 sign 字段）按 key 字典序升序排列，
	 * 拼接为 k=v&k=v 后追加 &key={secretKey}，整体 MD5
	 * @param params 请求参数（不含 sign）
	 * @return 签名值
	 */
	public static String computeSign(Map<String, String> params) {
		TreeMap<String, String> sorted = new TreeMap<>(params);
		sorted.remove("sign");
		StringBuilder sb = new StringBuilder();
		for (Map.Entry<String, String> entry : sorted.entrySet()) {
			if (sb.length() > 0) sb.append("&");
			sb.append(entry.getKey()).append("=").append(entry.getValue());
		}
		sb.append("&key=").append(SsoRequestUtil.secretKey);
		return md5(sb.toString());
	}

	/**
	 * 向参数 Map 中注入 timestamp、nonce、sign 三个签名参数
	 * @param params 请求参数（已填好业务参数，此方法自动追加签名参数）
	 */
	public static void addSignParams(Map<String, String> params) {
		params.put("timestamp", String.valueOf(System.currentTimeMillis()));
		params.put("nonce", getRandomString(20));
		params.put("sign", computeSign(params));
	}

	/**
	 * 校验请求中的 timestamp、nonce、sign 是否合法（对齐官方 SaSignTemplate.checkParamMap）
	 * @param params 包含 sign 的请求参数
	 * @return 是否合法
	 */
	public static boolean verifySign(Map<String, String> params) {
		String sign = params.get("sign");
		String timestamp = params.get("timestamp");
		String nonce = params.get("nonce");
		if (SsoRequestUtil.isEmpty(sign) || SsoRequestUtil.isEmpty(timestamp) || SsoRequestUtil.isEmpty(nonce)) {
			return false;
		}

		long ts;
		try {
			ts = Long.parseLong(timestamp);
		} catch (NumberFormatException e) {
			return false;
		}
		if (Math.abs(System.currentTimeMillis() - ts) > TIMESTAMP_DISPARITY) {
			return false;
		}

		if (!sign.equals(computeSign(params))) {
			return false;
		}

		return CacheUtil.setIfAbsent(nonce, nonce, TIMESTAMP_DISPARITY);
	}


	// -------------------------- 基础工具

	/**
	 * MD5 加密
	 * @param str 指定字符串
	 * @return 加密后的字符串
	 */
	public static String md5(String str) {
		str = (str == null ? "" : str);
		char[] hexDigits = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
		try {
			byte[] btInput = str.getBytes();
			MessageDigest mdInst = MessageDigest.getInstance("MD5");
			mdInst.update(btInput);
			byte[] md = mdInst.digest();
			int j = md.length;
			char[] strA = new char[j * 2];
			int k = 0;
			for (byte byte0 : md) {
				strA[k++] = hexDigits[byte0 >>> 4 & 0xf];
				strA[k++] = hexDigits[byte0 & 0xf];
			}
			return new String(strA);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 生成指定长度的随机字符串
	 * @param length 字符串的长度
	 * @return 一个随机字符串
	 */
	public static String getRandomString(int length) {
		String str = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
		Random random = new Random();
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < length; i++) {
			sb.append(str.charAt(random.nextInt(62)));
		}
		return sb.toString();
	}

}
