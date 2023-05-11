package cn.dev33.satoken.spring;

import org.springframework.context.annotation.Bean;

import cn.dev33.satoken.context.SaTokenContext;

/**
 * 注册 Sa-Token 框架所需要的 Bean
 *
 * @author click33
 * @since <= 1.34.0
 */
public class SaTokenContextRegister {

	/**
	 * 获取上下文处理器组件 (Spring版)
	 * 
	 * @return /
	 */
	@Bean
	public SaTokenContext getSaTokenContextForSpring() {
		return new SaTokenContextForSpring();
	}

}
