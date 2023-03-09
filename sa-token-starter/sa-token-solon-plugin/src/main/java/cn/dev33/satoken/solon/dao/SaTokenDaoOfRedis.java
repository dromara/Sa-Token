package cn.dev33.satoken.solon.dao;

import org.noear.redisx.RedisClient;
import org.noear.solon.annotation.Note;

import java.util.Properties;

/**
 * SaTokenDao 的 redis 适配
 *
 * @author noear
 * @since 1.6
 */
public class SaTokenDaoOfRedis extends SaTokenDaoOfRedisBase64 {

    public SaTokenDaoOfRedis(Properties props) {
        super(props);
    }

    public SaTokenDaoOfRedis(RedisClient redisClient) {
        super(redisClient);
    }
}