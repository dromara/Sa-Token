package com.pj.satoken;

import cn.dev33.satoken.dao.SaTokenDao;
import cn.dev33.satoken.dao.SaTokenDaoForRedisx;
import org.noear.solon.annotation.Bean;
import org.noear.solon.annotation.Configuration;
import org.noear.solon.annotation.Inject;

/**
 * Sa-Token 基础配置：注册 Redis 持久层。
 */
@Configuration
public class SaTokenConfigure {

	@Bean
	public SaTokenDao saTokenDaoInit(@Inject("${sa-token-dao.redis}") SaTokenDaoForRedisx saTokenDao) {
		return saTokenDao;
	}

}
