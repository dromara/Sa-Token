package cn.dev33.satoken.reactor.spring;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;

import cn.dev33.satoken.config.SaTokenConfig;
import cn.dev33.satoken.context.SaTokenContext;
import cn.dev33.satoken.context.SaTokenContextForThreadLocal;

/**
 * 注册Sa-Token所需要的Bean 
 * <p> Bean 的注册与注入应该分开在两个文件中，否则在某些场景下会造成循环依赖 
 * @author kong
 *
 */
public class SaBeanRegister {

	/**
	 * 获取配置Bean
	 * 
	 * @return 配置对象
	 */
	@Bean
	@ConfigurationProperties(prefix = "sa-token")
	public SaTokenConfig getSaTokenConfig() {
		return new SaTokenConfig();
	}
	
	/**
	 * 获取容器交互Bean (ThreadLocal版)
	 * 
	 * @return 容器交互Bean (ThreadLocal版)
	 */
	@Bean
	public SaTokenContext getSaTokenContext() {
		return new SaTokenContextForThreadLocal() {
			/**
			 * 重写路由匹配方法
			 */
			@Override
			public boolean matchPath(String pattern, String path) {
				return SaPathMatcherHolder.getPathMatcher().match(pattern, path);
			}
		};
	}

}
