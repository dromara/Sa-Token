package cn.dev33.satoken.json;

import java.util.Map;

/**
 * JSON 转换器 
 * 
 * @author kong
 *
 */
public interface SaJsonTemplate {

	/**
	 * 将任意对象转换为 json 字符串
	 *
	 * @param obj 对象
	 * @return 转换后的 json 字符串
	 */
	public String toJsonString(Object obj);

	/**
	 * 解析 json 字符串为map对象 
	 * @param jsonStr json字符串 
	 * @return map对象
	 */
	public Map<String, Object> parseJsonToMap(String jsonStr);
	
}
