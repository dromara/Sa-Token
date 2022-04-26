package cn.dev33.satoken.reactor.spring.json;

import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import cn.dev33.satoken.exception.SaTokenException;
import cn.dev33.satoken.json.SaJsonTemplate;

/**
 *  JSON 转换器， Jackson 版实现 
 * 
 * @author kong
 * @date: 2022-4-26
 */
public class SaJsonTemplateForJackson implements SaJsonTemplate {

	/**
	 * 底层 Mapper 对象 
	 */
	public ObjectMapper objectMapper = new ObjectMapper();
	
	/**
	 * 将 json 字符串解析为 Map
	 */
	@Override
	public Map<String, Object> parseJsonToMap(String jsonStr) {
		try {
			@SuppressWarnings("unchecked")
			Map<String, Object> map = objectMapper.readValue(jsonStr, Map.class);
			return map;
		} catch (JsonProcessingException e) {
			throw new SaTokenException(e);
		}
	}

}
