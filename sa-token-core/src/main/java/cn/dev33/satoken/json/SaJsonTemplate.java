package cn.dev33.satoken.json;

import java.util.Map;

/**
 * JSON 转换器 
 * 
 * @author click33
 * @since <= 1.34.0
 */
public interface SaJsonTemplate {

	/**
	 * 将任意对象序列化为 json 字符串
	 *
	 * @param obj 对象
	 * @return 转换后的 json 字符串
	 */
	String toJsonString(Object obj);

	/**
	 * 解析 json 字符串为 map 对象
	 * @param jsonStr json 字符串
	 * @return map 对象
	 */
	Map<String, Object> parseJsonToMap(String jsonStr);
	
}
