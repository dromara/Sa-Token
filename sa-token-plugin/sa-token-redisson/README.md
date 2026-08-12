## sa-token-redisson

此扩展，不与生态绑定。可用于不同的生态（SpringBoot，Solon，JFinal等）。

### 1、例 solon 集成

添加关键依赖

```xml
<dependencies>
    <dependency>
        <groupId>cn.dev33</groupId>
        <artifactId>sa-token-redisson</artifactId>
        <version>${sa-token.version}</version>
    </dependency>
    <dependency>
        <groupId>org.noear</groupId>
        <artifactId>redisson-solon-plugin</artifactId>
        <version>${solon.version}</version>
    </dependency>
</dependencies>
```

添加 dao 配置

```yaml
sa-token-dao:
    config: |
        singleServerConfig:
          password: "123456"
          address: "redis://localhost:6379"
          database: 0
```

开始组装

```java
@Configuration
public class SaTokenConfigure {
	/**
	 * 构造 RedissonClient
	 * */
	@Bean
	public RedissonClient saTokenDaoInit(@Inject("${sa-token-dao}") RedissonSupplier supplier) {
		return supplier.get();
	}

	/**
	 * 构建  SaTokenDao
	 * */
	@Bean
	public SaTokenDao saTokenDaoInit(RedissonClient redissonClient) {
		return new SaTokenDaoForRedisson(redissonClient);
	}
}
```


### 2、例 springboot 集成


添加关键依赖

```xml
<dependencies>
    <dependency>
        <groupId>cn.dev33</groupId>
        <artifactId>sa-token-redisson</artifactId>
        <version>${sa-token.version}</version>
    </dependency>
    <dependency>
        <groupId>org.redisson</groupId>
        <artifactId>redisson-spring-boot-starter</artifactId>
        <version>${redisson.version}</version>
    </dependency>
</dependencies>
```

添加 dao 配置

```yaml
spring.redis:
  redisson:
    file: classpath:redisson.yml
```

开始组装

```java
@Configuration
public class SaTokenConfigure {
	/**
	 * 构建  SaTokenDao
	 * */
	@Bean
	public SaTokenDao saTokenDaoInit(RedissonClient redissonClient) {
		return new SaTokenDaoForRedisson(redissonClient);
	}
}
```


### 3、Codec

`SaTokenDaoForRedisson` 默认使用 `StringCodec`，与业务 `RedissonClient` 全局 codec 隔离：

```java
// 默认 StringCodec
return new SaTokenDaoForRedisson(redissonClient);

// 指定 codec（例如兼容升级前的 Kryo5 缓存）
return new SaTokenDaoForRedisson(redissonClient, new Kryo5Codec());
```

**升级注意：** 此前跟随 Redisson 全局 codec（未自定义时为 `Kryo5Codec`）。升级后旧缓存无法用 `StringCodec` 读取。请清空 Redis 中的 Sa-Token 数据，或构造时传入 `new Kryo5Codec()`。
