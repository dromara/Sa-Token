package cn.dev33.satoken.dao;

import cn.dev33.satoken.dao.auto.SaTokenDaoByObjectFollowString;
import cn.dev33.satoken.util.SaFoxUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.lang.Nullable;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;
import java.util.Collections;
import java.util.List;


public class SaTokenDaoForReactiveRedisTemplate implements SaTokenDao, SaTokenDaoByObjectFollowString {
    public ReactiveRedisTemplate<String, String> template;
    public boolean isInit;

    @Autowired
    public void init(ReactiveRedisConnectionFactory connectionFactory){
        if (this.isInit){
            return;
        }
        RedisSerializationContext<String,String> redisSerializationContext = RedisSerializationContext.<String,String>newSerializationContext()
                .key(StringRedisSerializer.UTF_8)
                .value(StringRedisSerializer.UTF_8)
                .hashKey(StringRedisSerializer.UTF_8)
                .hashValue(StringRedisSerializer.UTF_8)
                .build();
        this.template = new ReactiveRedisTemplate<>(connectionFactory,redisSerializationContext);
        initMore(connectionFactory);
        this.isInit = true;
    }
    protected void initMore(ReactiveRedisConnectionFactory connectionFactory) {
    }
    @Nullable
    private <T> T doBlock(Mono<T> mono){
        return mono.block();

    }


    @Override
    public String get(String key) {
        return doBlock(this.template.opsForValue().get(key));
    }

    @Override
    public void set(String key, String value, long timeout) {
        if(timeout == 0 || timeout <= SaTokenDao.NOT_VALUE_EXPIRE)  {
            return;
        }
        if (timeout == SaTokenDao.NEVER_EXPIRE) {
            doBlock(this.template.opsForValue().set(key, value));
        } else {
            doBlock(this.template.opsForValue().set(key, value, Duration.ofSeconds(timeout)));
        }
    }

    @Override
    public void update(String key, String value) {
        doBlock(
                this.template.getExpire(key)
                        .flatMap(
                                expireMs -> {
                                    if (expireMs.getSeconds() == SaTokenDao.NOT_VALUE_EXPIRE){
                                        return Mono.empty();
                                    }
                                    if (expireMs.getSeconds() == SaTokenDao.NEVER_EXPIRE) {
                                        return this.template.opsForValue().set(key, value);
                                    }else  {
                                        return this.template.opsForValue().set(key, value, expireMs);
                                    }
                                }
                        )
        );
    }

    @Override
    public void delete(String key) {
        doBlock(this.template.opsForValue().delete(key));
    }

    @Override
    public long getTimeout(String key) {
        Duration timeout = doBlock(this.template.getExpire(key));
        return timeout != null ? timeout.getSeconds() : SaTokenDao.NOT_VALUE_EXPIRE;
    }

    @Override
    public void updateTimeout(String key, long timeout) {
        if (timeout == SaTokenDao.NEVER_EXPIRE) {
            doBlock(
                    this.template.getExpire(key)
                            .flatMap(
                                    expire -> {
                                        if (expire.getSeconds() == SaTokenDao.NOT_VALUE_EXPIRE) {
                                            return Mono.empty();
                                        }else  {
                                            return this.template.expire(key,Duration.ofSeconds(timeout));
                                        }
                                    }
                            )
            );
            return;
        }
        doBlock(this.template.expire(key, Duration.ofSeconds(timeout)));
    }

    @Override
    public List<String> searchData(String prefix, String keyword, int start, int size, boolean sortType) {
        List<String> list = doBlock(this.template.keys(prefix + "*" + keyword + "*").collectList());
        list = list == null ? Collections.emptyList() : list;
        return SaFoxUtil.searchList(list, start, size, sortType);
    }
}

