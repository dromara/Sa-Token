package cn.dev33.satoken.oauth2.util;

/**
 * sa-token-oauth2 模块内部算法util 
 * @author kong
 *
 */
public class SaOAuth2InsideUtil {

	/**
	 * 验证URL的正则表达式 
	 */
    static final String URL_REGEX = "(https?|ftp|file)://[-A-Za-z0-9+&@#/%?=~_|!:,.;]+[-A-Za-z0-9+&@#/%=~_|]"; 
	
	/**
	 * 使用正则表达式判断一个字符串是否为URL
	 * @param str 字符串 
	 * @return 拼接后的url字符串 
	 */
	public static boolean isUrl(String str) {
		if(str == null) {
			return false;
		}
        return str.toLowerCase().matches(URL_REGEX);
	}
	
	
}
