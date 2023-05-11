package cn.dev33.satoken.reactor.spring;

import org.springframework.context.annotation.Bean;

import cn.dev33.satoken.context.SaTokenContext;

/**
 * 注册 Sa-Token 所需要的 Bean
 *
 * @author click33
 * @since <= 1.34.0
 */
public class SaTokenContextRegister {

	/**
	 * 获取上下文处理器组件 (Spring Reactor 版)
	 * 
	 * @return /
	 */
	@Bean
	public SaTokenContext getSaTokenContextForSpringReactor() {
		return new SaTokenContextForSpringReactor();
	}

}
