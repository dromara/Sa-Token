package cn.dev33.satoken.quick;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.annotation.Order;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.filter.SaServletFilter;
import cn.dev33.satoken.quick.config.SaQuickConfig;
import cn.dev33.satoken.quick.web.SaQuickController;
import cn.dev33.satoken.spring.SpringMVCUtil;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaTokenConsts;

/**
 * 自动注入
 * 
 * @author kong
 *
 */
@Configuration
@Import({ SaQuickController.class })
public class SaQuickBean implements WebMvcConfigurer  {

	/**
	 * quick-login 配置
	 * 
	 * @return see note
	 */
	@Bean
	@ConfigurationProperties(prefix = "sa")
	public SaQuickConfig getSaQuickConfig() {
		return new SaQuickConfig();
	}
	
	/**
	 * 注入quick-login 配置 
	 * 
	 * @param saQuickConfig 配置对象
	 */
	@Autowired
	public void setSaQuickConfig(SaQuickConfig saQuickConfig) {
		SaQuickManager.setConfig(saQuickConfig);
	}

	/**
	 * 注册 [sa-token全局过滤器]
	 * 
	 * @return see note
	 */
	@Bean
	@Order(SaTokenConsts.ASSEMBLY_ORDER - 1)
	public SaServletFilter getSaServletFilter() {
		return new SaServletFilter().

			// 拦截路由 & 放行路由
			addInclude("/**").addExclude("/favicon.ico", "/saLogin", "/doLogin", "/sa-res/**").
	
			// 认证函数: 每次请求执行
			setAuth(r -> {
				// System.out.println("---------- 进入sa-token全局认证 -----------");
	
				// 未登录时直接转发到login.html页面 
				if (SaQuickManager.getConfig().getAuth() && StpUtil.isLogin() == false) {
					try {
						HttpServletRequest request = SpringMVCUtil.getRequest();
						HttpServletResponse response = SpringMVCUtil.getResponse();
						request.getRequestDispatcher("/saLogin").forward(request, response);
					} catch (Exception e) {
						e.printStackTrace();
					}
					// 抛出异常，不再继续执行 
					throw NotLoginException.newInstance(StpUtil.getLoginKey(), "");
				}
	
			}).
	
			// 异常处理函数：每次认证函数发生异常时执行此函数
			setError(e -> {
				// System.out.println("---------- 进入sa-token异常处理 -----------");
				return e.getMessage();
			});
	}

}
