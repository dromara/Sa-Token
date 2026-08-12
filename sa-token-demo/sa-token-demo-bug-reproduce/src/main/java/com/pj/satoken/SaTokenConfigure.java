package com.pj.satoken;

import cn.dev33.satoken.httpauth.basic.SaHttpBasicUtil;
import cn.dev33.satoken.reactor.filter.SaReactorFilter;
import cn.dev33.satoken.router.SaRouter;
import cn.dev33.satoken.util.SaResult;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 当前用例（IIAW1A / #916）：SaReactorFilter + SaHttpBasicUtil.check 失败时会调 setStatus(401)
 */
@Configuration
public class SaTokenConfigure {

	@Bean
	public SaReactorFilter getSaReactorFilter() {
		return new SaReactorFilter()
				.addInclude("/**")
				.addExclude("/favicon.ico", "/ok")
				.setAuth(obj -> {
					// 访问 /basic/** 时走 Http Basic；无凭证 → setStatus(401)
					SaRouter.match("/basic/**", () -> SaHttpBasicUtil.check("sa:123456"));
				})
				.setError(e -> {
					System.out.println("---------- sa全局异常: " + e);
					e.printStackTrace();
					return SaResult.error(e.getMessage());
				});
	}

}
