package cn.dev33.satoken.spring;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.env.OriginTrackedMapPropertySource;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertySource;

import cn.dev33.satoken.util.SaTokenConsts;

/**
 * 兼容旧版本的配置信息注入 
 * <p> 目前已处理：
 * <p> 1. yml配置前缀 [spring.sa-token.] 更改为 [sa-token.]
 * @author kong
 */
//SpringBoot 1.5.x 版本下不存在 OriginTrackedMapPropertySource ，不加这个注解会导致项目无法启动 
@ConditionalOnClass(OriginTrackedMapPropertySource.class)	
public class SaHistoryVersionInject implements EnvironmentAware{

	@Override
	@SuppressWarnings("unchecked")
	public void setEnvironment(Environment env) {
		try {
			ConfigurableEnvironment c = (ConfigurableEnvironment) env;
			MutablePropertySources sources = c.getPropertySources();
			
			// 将yml中所有 [spring.sa-token.] 开头的配置转移到 [sa-token.] 下 
			Map<String, Object> newMap = new LinkedHashMap<String, Object>(); 
			for (PropertySource<?> source : sources) {
				// 根据Name开头单词判断是否为 SpringBoot .yml 或者.properties 的配置 
	        	if(source.getName().startsWith("applicationConfig")) {
	        		Map<String, Object> bootProp = (Map<String, Object>)source.getSource();
	        		for (String key : bootProp.keySet()) {
	        			if(key != null && key.startsWith("spring.sa-token.")) {
	        				String newKey = key.substring(7);
	        				newMap.put(newKey, bootProp.get(key));
	        			}
					}
	        	}
			}

			// 追加到总配置里面 
			if(newMap.size() > 0) {
				System.err.println("\n"
						+ "Sa-Token Warning: 当前配置文件方式已过时，请更改配置前缀：原 [spring.sa-token.] 更改为 [sa-token.]\n"
						+ "Sa-Token Warning: 当前版本(" + SaTokenConsts.VERSION_NO + ")暂时向下兼容，未来版本可能会完全移除旧形式");
		        OriginTrackedMapPropertySource source = new OriginTrackedMapPropertySource("SaHistoryVersionInjectProperty", newMap);
		        // 追加到末尾，优先级最低 
		        c.getPropertySources().addLast(source);
			}
		} catch (Exception e) {
			// not handle 
		}
	}
	
}
