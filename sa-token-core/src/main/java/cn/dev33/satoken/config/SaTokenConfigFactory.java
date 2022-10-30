package cn.dev33.satoken.config;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import cn.dev33.satoken.error.SaErrorCode;
import cn.dev33.satoken.exception.SaTokenException;
import cn.dev33.satoken.util.SaFoxUtil;

/**
 * Sa-Token配置文件的构建工厂类
 * <p>
 * 用于手动读取配置文件初始化 SaTokenConfig 对象，只有在非IOC环境下你才会用到此类 
 * 
 * @author kong
 * @since 2022-10-30
 */
public class SaTokenConfigFactory {

	private SaTokenConfigFactory() {
	}
	
	/**
	 * 配置文件地址 
	 */
	public static String configPath = "sa-token.properties";

	/**
	 * 根据configPath路径获取配置信息 
	 * 
	 * @return 一个SaTokenConfig对象
	 */
	public static SaTokenConfig createConfig() {
		return createConfig(configPath);
	}

	/**
	 * 根据指定路径路径获取配置信息 
	 * 
	 * @param path 配置文件路径 
	 * @return 一个SaTokenConfig对象
	 */
	public static SaTokenConfig createConfig(String path) {
		Map<String, String> map = readPropToMap(path);
		if (map == null) {
			// throw new RuntimeException("找不到配置文件：" + configPath, null);
		}
		return (SaTokenConfig) initPropByMap(map, new SaTokenConfig());
	}

	/**
	 * 工具方法: 将指定路径的properties配置文件读取到Map中 
	 * 
	 * @param propertiesPath 配置文件地址
	 * @return 一个Map
	 */
	private static Map<String, String> readPropToMap(String propertiesPath) {
		Map<String, String> map = new HashMap<String, String>(16);
		try {
			InputStream is = SaTokenConfigFactory.class.getClassLoader().getResourceAsStream(propertiesPath);
			if (is == null) {
				return null;
			}
			Properties prop = new Properties();
			prop.load(is);
			for (String key : prop.stringPropertyNames()) {
				map.put(key, prop.getProperty(key));
			}
		} catch (IOException e) {
			throw new SaTokenException("配置文件(" + propertiesPath + ")加载失败", e).setCode(SaErrorCode.CODE_10021);
		}
		return map;
	}

	/**
	 * 工具方法: 将 Map 的值映射到一个 Model 上 
	 * 
	 * @param map 属性集合
	 * @param obj 对象, 或类型
	 * @return 返回实例化后的对象
	 */
	private static Object initPropByMap(Map<String, String> map, Object obj) {

		if (map == null) {
			map = new HashMap<String, String>(16);
		}

		// 1、取出类型
		Class<?> cs = null;
		if (obj instanceof Class) {
			// 如果是一个类型，则将obj=null，以便完成静态属性反射赋值
			cs = (Class<?>) obj;
			obj = null;
		} else {
			// 如果是一个对象，则取出其类型
			cs = obj.getClass();
		}

		// 2、遍历类型属性，反射赋值
		for (Field field : cs.getDeclaredFields()) {
			String value = map.get(field.getName());
			if (value == null) {
				// 如果为空代表没有配置此项
				continue;
			}
			try {
				Object valueConvert = SaFoxUtil.getValueByType(value, field.getType());
				field.setAccessible(true);
				field.set(obj, valueConvert);
			} catch (IllegalArgumentException | IllegalAccessException e) {
				throw new SaTokenException("属性赋值出错：" + field.getName(), e).setCode(SaErrorCode.CODE_10022);
			}
		}
		return obj;
	}

}
