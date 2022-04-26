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
	 * 将 json 字符串解析为 Map
	 * 
	 * @param jsonStr json  字符串
	 * @return 转换后的 Map 对象
	 */
	public Map<String, Object> parseJsonToMap(String jsonStr);
	
}
