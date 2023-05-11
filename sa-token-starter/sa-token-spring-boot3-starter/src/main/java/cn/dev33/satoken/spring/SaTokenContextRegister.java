package cn.dev33.satoken.spring;

import org.springframework.context.annotation.Bean;

import cn.dev33.satoken.context.SaTokenContext;

/**
 * 注册 Sa-Token 框架所需要的 Bean
 * 
 * @author click33
 * @since 2023年1月1日
 */
public class SaTokenContextRegister {

	/**
	 * 获取上下文处理器组件 (SpringBoot3 Jakarta Servlet 版)
	 * 
	 * @return / 
	 */
	@Bean
	public SaTokenContext getSaTokenContextForSpringInJakartaServlet() {
		return new SaTokenContextForSpringInJakartaServlet();
	}

}
