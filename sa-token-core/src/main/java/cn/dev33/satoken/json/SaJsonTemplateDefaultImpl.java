package cn.dev33.satoken.json;

import java.util.Map;

import cn.dev33.satoken.exception.ApiDisabledException;

/**
 * JSON 相关操作接口 
 * 
 * @author kong
 *
 */
public class SaJsonTemplateDefaultImpl implements SaJsonTemplate {

	/**
	 * 将 json 字符串解析为 Map 
	 */
	public Map<String, Object> parseJsonToMap(String jsonStr) {
		throw new ApiDisabledException("未实现具体的 json 转换器");
	};
	
}
