package demo;

import cn.dev33.satoken.SaManager;
import cn.dev33.satoken.dao.SaTokenDao;
import cn.dev33.satoken.dao.SaTokenDaoForRedisx;
import org.noear.solon.annotation.Bean;
import org.noear.solon.annotation.Configuration;
import org.noear.solon.annotation.Inject;

/**
 * @author noear 2022/3/30 created
 */
@Configuration
public class Config {
    @Bean
    public void saTokenDaoInit(@Inject("${sa-token.redis}") SaTokenDaoForRedisx saTokenDao) {
        //手动操作，可适用于任何框架
        SaManager.setSaTokenDao(saTokenDao);
    }


    @Bean
    public SaTokenDao saTokenDaoInit2(@Inject("${sa-token.redis}") SaTokenDaoForRedisx saTokenDao) {
        //Solon 项目，可用此案
        return saTokenDao;
    }
}
