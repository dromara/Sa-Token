package cn.dev33.satoken.jboot;

import io.jboot.Jboot;

public class SaRedisManager {

    private static SaRedisManager manager = new SaRedisManager();

    private SaRedisManager() {
    }

    public static SaRedisManager me() {
        return manager;
    }

    private SaJedisImpl redis;

    public SaJedisImpl getRedis() {
        if (redis == null) {
            SaRedisConfig config = Jboot.config(SaRedisConfig.class);
            redis = getRedis(config);
        }

        return redis;
    }

    public SaJedisImpl getRedis(SaRedisConfig config) {
        if (config == null || !config.isConfigOk()) {
            return null;
        }
        return getJedisClient(config);
    }


    private SaJedisImpl getJedisClient(SaRedisConfig config) {
        return new SaJedisImpl(config);
    }
}
