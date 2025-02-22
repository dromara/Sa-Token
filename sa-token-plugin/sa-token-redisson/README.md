## sa-token-redisson-jackson2 

此扩展，不与生态绑定。可用于不同的生态。

### 1、例 solon 集成

添加关键依赖

```xml
<dependencies>
    <dependency>
        <groupId>cn.dev33</groupId>
        <artifactId>sa-token-redisson-jackson2</artifactId>
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
		return new SaTokenDaoRedissonJackson(redissonClient);
	}
}
```


### 2、例 springboot 集成


添加关键依赖

```xml
<dependencies>
    <dependency>
        <groupId>cn.dev33</groupId>
        <artifactId>sa-token-redisson-jackson2</artifactId>
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
		return new SaTokenDaoRedissonJackson(redissonClient);
	}
}
```