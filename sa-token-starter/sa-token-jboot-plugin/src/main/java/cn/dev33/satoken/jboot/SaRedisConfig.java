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

    private Integer db;

    public Integer getDb() {
        return this.db;
    }

    public void setDb(Integer db) {
        this.db = db;
    }

}
