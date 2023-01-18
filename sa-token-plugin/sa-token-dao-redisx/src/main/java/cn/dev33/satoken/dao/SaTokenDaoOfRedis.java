package cn.dev33.satoken.dao;

import org.noear.redisx.RedisClient;
import org.noear.solon.annotation.Note;

import java.util.Properties;

/**
 * SaTokenDao 的 redis 适配
 *
 * @author noear
 * @since 1.6
 */
@Note("更名为：SaTokenDaoOfRedisBase64")
@Deprecated
public class SaTokenDaoOfRedis extends SaTokenDaoOfRedisBase64 {

    public SaTokenDaoOfRedis(Properties props) {
        super(props);
    }

    public SaTokenDaoOfRedis(RedisClient redisClient) {
        super(redisClient);
    }
}