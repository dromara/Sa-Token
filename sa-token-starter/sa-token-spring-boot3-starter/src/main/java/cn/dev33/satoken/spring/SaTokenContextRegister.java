package cn.dev33.satoken.spring;

import org.springframework.context.annotation.Bean;

import cn.dev33.satoken.context.SaTokenContext;

/**
 * SaTokenContext 上下文注册 
 * 
 * @author click33
 * @since 2023年1月1日
 *
 */
public class SaTokenContextRegister {

	/**
	 * 获取上下文Bean [ SpringBoot3 Jakarta Servlet 版 ] 
	 * 
	 * @return / 
	 */
	@Bean
	public SaTokenContext getSaTokenContextForSpringInJakartaServlet() {
		return new SaTokenContextForSpringInJakartaServlet();
	}

}
