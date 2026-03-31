package com.pj.sso;

import com.dtflys.forest.Forest;
import com.pj.sso.util.AjaxJson;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;

/**
 * 封装一些 sso 共用方法
 *
 * @author click33
 * @since 2022-4-30
 */
public class SsoRequestUtil {

	/**
	 * SSO-Server 端主机地址
	 */
	public static String serverUrl = "http://sa-sso-server.com:9000";

	/**
	 * SSO-Server 端统一认证地址
	 */
	public static String authUrl = serverUrl + "/sso/auth";

	/**
	 * SSO-Server 端统一消息推送地址（ticket校验、单点注销、获取用户信息等均通过此入口）
	 */
	public static String pushSUrl = serverUrl + "/sso/pushS";

	/**
	 * 当前应用的客户端标识（需与 sso-server 端 clients 配置一致）
	 */
	public static String clientId = "sso-client3-nosdk";

	/**
	 * 接口调用秘钥（需与 sso-server 端对应 client 配置一致）
	 */
	public static String secretKey = "SSO-C3-NoSdk-kQwIOrYvnXmSDkwEiFngrKidMcdrgKor";

	// -------------------------- 工具方法

	/**
	 * 发出请求，并返回 AjaxJson 结果
	 * @param url 请求地址（含查询参数）
	 * @return 返回的结果
	 */
	public static AjaxJson request(String url) {
		Map<String, Object> map = Forest.post(url).executeAsMap();
		return new AjaxJson(map);
	}

	/**
	 * 将参数 Map 拼接到 baseUrl 后面（值进行 URL 编码），返回完整 URL
	 * @param baseUrl 基础 URL
	 * @param params  请求参数
	 * @return 拼接后的完整 URL
	 */
	public static String buildUrl(String baseUrl, Map<String, String> params) {
		StringBuilder sb = new StringBuilder(baseUrl).append("?");
		for (Map.Entry<String, String> entry : params.entrySet()) {
			sb.append(entry.getKey()).append("=").append(encodeUrl(entry.getValue())).append("&");
		}
		sb.deleteCharAt(sb.length() - 1);
		return sb.toString();
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
