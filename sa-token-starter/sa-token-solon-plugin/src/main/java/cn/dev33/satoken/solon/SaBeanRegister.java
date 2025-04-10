/*
 * Copyright 2020-2099 sa-token.cc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cn.dev33.satoken.solon;

import cn.dev33.satoken.config.SaTokenConfig;
import cn.dev33.satoken.solon.integration.SaFirewallCheckFilterForSolon;
import cn.dev33.satoken.solon.integration.SaTokenContextFilterForSolon;
import cn.dev33.satoken.solon.integration.SaTokenCorsFilterForSolon;
import cn.dev33.satoken.strategy.SaStrategy;
import cn.dev33.satoken.util.SaTokenConsts;
import org.noear.solon.annotation.Bean;
import org.noear.solon.annotation.Configuration;
import org.noear.solon.annotation.Inject;
import org.noear.solon.core.handle.Filter;
import org.noear.solon.core.util.PathAnalyzer;

/**
 * 注册Sa-Token所需要的Bean 
 * <p> Bean 的注册与注入应该分开在两个文件中，否则在某些场景下会造成循环依赖 
 * @author click33
 *
 */
@Configuration
public class SaBeanRegister {

	public SaBeanRegister() {
		// 重写路由匹配算法
		SaStrategy.instance.routeMatcher = (pattern, path) -> {
			return PathAnalyzer.get(pattern).matches(path);
		};
	}

	/**
	 * 获取配置Bean
	 *
	 * @return 配置对象
	 */
	@Bean
	public SaTokenConfig getSaTokenConfig(@Inject(value = "${sa-token}", required = false) SaTokenConfig config) {
		if (config == null) {
			return new SaTokenConfig();
		} else {
			return config;
		}
	}

	/**
	 * 上下文过滤器
	 *
	 * @return /
	 */
	@Bean(index = SaTokenConsts.SA_TOKEN_CONTEXT_FILTER_ORDER)
	public Filter saTokenContextFilterForSolon() {
		return new SaTokenContextFilterForSolon();
	}

	/**
	 * CORS 跨域策略过滤器
	 *
	 * @return /
	 */
	@Bean(index = SaTokenConsts.CORS_FILTER_ORDER)
	public Filter saTokenCorsFilterForSolon() {
		return new SaTokenCorsFilterForSolon();
	}

	/**
	 * 防火墙过滤器
	 *
	 * @return /
	 */
	@Bean(index = SaTokenConsts.FIREWALL_CHECK_FILTER_ORDER)
	public Filter saFirewallCheckFilterForSolon() {
		return new SaFirewallCheckFilterForSolon();
	}

}
