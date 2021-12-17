package cn.dev33.satoken.jboot;

import io.jboot.app.config.annotation.ConfigModel;
import io.jboot.support.redis.JbootRedisConfig;

/**
 * SA-Token redis缓存配置
 */
@ConfigModel(
        prefix = "sa.redis"
)
public class SaRedisConfig extends JbootRedisConfig{

    private Integer sadb = 1;

    public Integer getSadb() {
        return this.sadb;
    }

    public void setSadb(Integer sadb) {
        this.sadb = sadb;
    }

}
