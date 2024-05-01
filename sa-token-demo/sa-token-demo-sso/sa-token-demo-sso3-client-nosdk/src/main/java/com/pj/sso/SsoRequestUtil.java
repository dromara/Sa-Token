package com.pj.sso;

import com.dtflys.forest.Forest;
import com.pj.sso.util.AjaxJson;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.util.Map;
import java.util.Random;

/**
 * 封装一些 sso 共用方法 
 * 
 * @author click33
 * @since 2022-4-30
 */
public class SsoRequestUtil {

	/**
	 * SSO-Server端主机地址
	 */
	public static String serverUrl = "http://sa-sso-server.com:9000";

	/**
	 * SSO-Server端 统一认证地址 
	 */
	public static String authUrl = serverUrl + "/sso/auth";

	/**
	 * SSO-Server端 ticket校验地址 
	 */
	public static String checkTicketUrl = serverUrl + "/sso/checkTicket";

	/**
	 * 单点注销地址 
	 */
	public static String sloUrl = serverUrl + "/sso/signout";

	/**
	 * SSO-Server端 查询userinfo地址
	 */
	public static String getDataUrl = serverUrl + "/sso/getData";

	/**
	 * 打开单点注销功能
	 */
	public static boolean isSlo = true;

	/**
	 * 接口调用秘钥 
	 */
	public static String secretKey = "kQwIOrYvnXmSDkwEiFngrKidMcdrgKor";

	
	
	// -------------------------- 工具方法 
	
	/**
	 * 发出请求，并返回 SaResult 结果 
	 * @param url 请求地址 
	 * @return 返回的结果 
	 */
	public static AjaxJson request(String url) {
		Map<String, Object> map = Forest.post(url).executeAsMap();
		return new AjaxJson(map);
	}

	/**
	 * 根据参数计算签名 
	 * @param loginId 账号id
	 * @param timestamp 当前时间戳，13位
	 * @param nonce 随机字符串
	 * @return 签名 
	 */
	public static String getSign(Object loginId, String timestamp, String nonce) {
		return md5("loginId=" + loginId + "&nonce=" + nonce + "&timestamp=" + timestamp + "&key=" + secretKey);
	}
	// 单点注销回调时构建签名
	public static String getSignByLogoutCall(Object loginId, String autoLogout, String timestamp, String nonce) {
		System.out.println("autoLogout=" + autoLogout + "loginId=" + loginId + "&nonce=" + nonce + "&timestamp=" + timestamp + "&key=" + secretKey);
		return md5("autoLogout=" + autoLogout + "&loginId=" + loginId + "&nonce=" + nonce + "&timestamp=" + timestamp + "&key=" + secretKey);
	}
	// 校验ticket 时构建签名
	public static String getSignByTicket(String ticket, String ssoLogoutCall, String timestamp, String nonce) {
		return md5("nonce=" + nonce + "&ssoLogoutCall=" + ssoLogoutCall + "&ticket=" + ticket + "&timestamp=" + timestamp + "&key=" + secretKey);
	}

	/**
	 * 指定元素是否为null或者空字符串
	 * @param str 指定元素 
	 * @return 是否为null或者空字符串
	 */
	public static boolean isEmpty(Object str) {
		return str == null || "".equals(str);
	}
	
	/**
	 * md5加密 
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
	 * 
	 * @param length 字符串的长度
	 * @return 一个随机字符串
	 */
	public static String getRandomString(int length) {
		String str = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
		Random random = new Random();
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < length; i++) {
			int number = random.nextInt(62);
			sb.append(str.charAt(number));
		}
		return sb.toString();
	}

	/**
	 * URL编码 
	 * @param url see note 
	 * @return see note 
	 */
	public static String encodeUrl(String url) {
		try {
			return URLEncoder.encode(url, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}

}
