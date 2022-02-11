package cn.dev33.satoken.jboot;

import io.jboot.app.config.annotation.ConfigModel;
import io.jboot.support.redis.JbootRedisConfig;

/**
 * SA-Token redis缓存配置,获取database
 */
@ConfigModel(
        prefix = "jboot.redis"
)
public class SaRedisConfig extends JbootRedisConfig{

    private Integer saDb;

    public SaRedisConfig(){

    }
    public Integer getSaDb() {
        return this.saDb;
    }

    public void setSaDb(Integer saDb) {
        this.saDb = saDb;
    }

}
