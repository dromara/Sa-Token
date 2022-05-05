package cn.dev33.satoken.sign;

import java.util.Map;
import java.util.TreeMap;

import cn.dev33.satoken.secure.SaSecureUtil;
import cn.dev33.satoken.util.SaFoxUtil;

/**
 * 参数签名算法 
 * 
 * @author kong
 * @since: 2022-4-27
 */
public interface SaSignTemplate {

	/**
	 * 将所有参数连接成一个字符串 
	 * @param paramsMap 参数列表
	 * @return 字符串
	 */
	public default String joinParams(Map<String, Object> paramsMap) {
		// 保证字段按照字典顺序排列 
		if(paramsMap instanceof TreeMap == false) {
			paramsMap = new TreeMap<>(paramsMap);
		}
		
		// 按照 k1=v1&k2=v2&k3=v3 排列 
        StringBuilder sb = new StringBuilder();
        for (String key : paramsMap.keySet()) {
        	Object value = paramsMap.get(key);
        	if(SaFoxUtil.isEmpty(value) == false) {
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
	 * 创建签名：md5(paramsStr + keyStr)
	 * @param paramsMap 参数列表 
	 * @param key 秘钥 
	 * @return 签名 
	 */
	public default String createSign(Map<String, Object> paramsMap, String key) {
		String paramsStr = joinParams(paramsMap);
		String fullStr = paramsStr + "&key=" + key;
		return SaSecureUtil.md5(fullStr);
	}
	
	
}
