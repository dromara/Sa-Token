package com.pj;

import cn.dev33.satoken.dao.SaTokenDao;
import cn.dev33.satoken.solon.dao.SaTokenDaoOfRedis;
import org.noear.solon.annotation.Bean;
import org.noear.solon.annotation.Configuration;
import org.noear.solon.annotation.Inject;

/**
 * @author noear 2023/3/13 created
 */
@Configuration
public class SaConfig {

    /**
     * 构建建 SaToken redis dao（如果不需要 redis；可以注释掉）
     * */
    @Bean
    public SaTokenDao saTokenDaoInit(@Inject("${sa-token-dao.redis}") SaTokenDaoOfRedis saTokenDao) {
        return saTokenDao;
    }
}
