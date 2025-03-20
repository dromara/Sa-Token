# Sa-Token 集成 Redis 
--- 

Sa-Token 默认将数据保存在内存中，此模式读写速度最快，且避免了序列化与反序列化带来的性能消耗，但是此模式也有一些缺点，比如：

1. 重启后数据会丢失。
2. 无法在分布式环境中共享数据。

为此，Sa-Token 提供了扩展接口，你可以轻松将会话数据存储在一些专业的缓存中间件上（比如 Redis），
做到重启数据不丢失，而且保证分布式环境下多节点的会话一致性。

---

### 1、Sa-Token 整合 RedisTemplate 

RedisTemplate 是 SpringBoot 官方推荐的 Redis 客户端，Sa-Token 提供基于 RedisTemplate 的 Redis 整合方案：

<!---------------------------- tabs:start ------------------------------>
<!-------- tab:Maven 方式 -------->
``` xml 
<!-- Sa-Token 整合 RedisTemplate -->
<dependency>
	<groupId>cn.dev33</groupId>
	<artifactId>sa-token-redis-template</artifactId>
	<version>${sa.top.version}</version>
</dependency>

<!-- 提供 Redis 连接池 -->
<dependency>
	<groupId>org.apache.commons</groupId>
	<artifactId>commons-pool2</artifactId>
</dependency>
```
<!-------- tab:Gradle 方式 -------->
``` gradle
// Sa-Token 整合 RedisTemplate
implementation 'cn.dev33:sa-token-redis-template:${sa.top.version}'

// 提供 Redis 连接池
implementation 'org.apache.commons:commons-pool2'
```
<!---------------------------- tabs:end ------------------------------>


Redis 的集成有多种方式，缓存的方案也不止 Redis 一种，Sa-Token 为缓存方案提供多种扩展实现。

如果你对 Sa-Token 还不太熟悉，或者只想“省心省事”，我们推荐你直接使用上述的 RedisTemplate 集成方案，而不必进行过多研究。到此为止，你可以跳转到下一章节了。

如果你想对缓存方案再进行一下深入探究，那么你可以参考：[缓存层扩展](/plugin/dao-extend) 


### 2、自定义序列化方案

如果你按照上述 RedisTemplate 方案进行集成测试，会发现框架在 Redis 中是以 json 格式存储数据的。可以自定义数据序列化格式吗？当然是可以的。

框架的默认序列化层调用为 `String 序列化` -> `JSON 序列化`。要自定义数据序列化方式你可以从这两方面入手：


#### 自定义 JSON 序列化方案：

先说较为底层的 `JSON 序列化`，如果你引入的是 sa-token-spring-boot-starter 集成包 (含SpringBoot3) ，那么框架将会自动引入 Jackson 框架作为 JSON 序列化方案。

如果你想更换为其它 JSON 解析框架，可以引入相关依赖：


<!------------------------------ tabs:start ------------------------------>

<!------------- tab:Fastjson ------------->
``` xml
<!-- Sa-Token 整合 Fastjson -->
<dependency>
	<groupId>cn.dev33</groupId>
	<artifactId>sa-token-fastjson</artifactId>
	<version>${sa.top.version}</version>
</dependency>
```
Gradle 参考：`implementation 'cn.dev33:sa-token-fastjson:${sa.top.version}'`

<!------------- tab:Fastjson2 ------------->
``` xml
<!-- Sa-Token 整合 Fastjson2 -->
<dependency>
	<groupId>cn.dev33</groupId>
	<artifactId>sa-token-fastjson2</artifactId>
	<version>${sa.top.version}</version>
</dependency>
```
Gradle 参考：`implementation 'cn.dev33:sa-token-fastjson2:${sa.top.version}'`

<!------------- tab:Snack3 ------------->
``` xml
<!-- Sa-Token 整合 Snack3 -->
<dependency>
	<groupId>cn.dev33</groupId>
	<artifactId>sa-token-snack3</artifactId>
	<version>${sa.top.version}</version>
</dependency>
```
Gradle 参考：`implementation 'cn.dev33:sa-token-snack3:${sa.top.version}'`

<!---------------------------- tabs:end ------------------------------>


#### 自定义 String 序列化方案：

或者你想更直接点，不使用 json 序列化方案，也是可以的。你可以直接自定义数据的 String 序列化方案：

<!------------------------------ tabs:start ------------------------------>

<!------------- tab:jdk序列化 (base64编码) ------------->
``` java
// 设置序列化方案: jdk序列化 (base64编码)
@PostConstruct
public void rewriteComponent() {
	SaManager.setSaSerializerTemplate(new SaSerializerTemplateForJdkUseBase64());
}
```

<!------------- tab:jdk序列化 (16进制编码) ------------->
``` java
// 设置序列化方案: jdk序列化 (16进制编码)
@PostConstruct
public void rewriteComponent() {
	SaManager.setSaSerializerTemplate(new SaSerializerTemplateForJdkUseHex());
}
```

<!------------- tab:jdk序列化 (ISO-8859-1编码) ------------->
``` java
// 设置序列化方案: jdk序列化 (ISO-8859-1编码)
@PostConstruct
public void rewriteComponent() {
	SaManager.setSaSerializerTemplate(new SaSerializerTemplateForJdkUseISO_8859_1());
}
```
<!---------------------------- tabs:end ------------------------------>

除了以上的几种序列化方案，我们还提供了序列化扩展包，详细可参考：[序列化插件扩展包](/plugin/custom-serializer)


### 3、集成 Redis 请注意：

**1. 引入了依赖，我还需要为 Redis 配置连接信息吗？** <br>
需要！只有项目初始化了正确的 Redis 实例，`Sa-Token`才可以使用 Redis 进行数据持久化，参考以下`yml配置`：

<!---------------------------- tabs:start ------------------------------>
<!-------- tab:yaml 风格 -------->
``` yaml
spring: 
    # redis配置 
    redis:
        # Redis数据库索引（默认为0）
        database: 1
        # Redis服务器地址
        host: 127.0.0.1
        # Redis服务器连接端口
        port: 6379
        # Redis服务器连接密码（默认为空）
        # password: 
        # 连接超时时间
        timeout: 10s
        lettuce:
            pool:
                # 连接池最大连接数
                max-active: 200
                # 连接池最大阻塞等待时间（使用负值表示没有限制）
                max-wait: -1ms
                # 连接池中的最大空闲连接
                max-idle: 10
                # 连接池中的最小空闲连接
                min-idle: 0
```
<!-------- tab:properties 风格 -------->
``` properties
# Redis数据库索引（默认为0）
spring.redis.database=1
# Redis服务器地址
spring.redis.host=127.0.0.1
# Redis服务器连接端口
spring.redis.port=6379
# Redis服务器连接密码（默认为空）
# spring.redis.password=
# 连接超时时间
spring.redis.timeout=10s
# 连接池最大连接数
spring.redis.lettuce.pool.max-active=200
# 连接池最大阻塞等待时间（使用负值表示没有限制）
spring.redis.lettuce.pool.max-wait=-1ms
# 连接池中的最大空闲连接
spring.redis.lettuce.pool.max-idle=10
# 连接池中的最小空闲连接
spring.redis.lettuce.pool.min-idle=0
```
<!---------------------------- tabs:end ------------------------------>

> [!WARNING| label:小提示 ] 
> 如果你使用的是 SpringBoot3.x 版本，则需要将前缀 `spring.redis` 改为 `spring.data.redis`。


**2. 集成 Redis 后，是我额外手动保存数据，还是框架自动保存？** <br>
框架自动保存。集成 `Redis` 只需要引入对应的 `pom依赖` 即可，框架所有上层 API 保持不变。

**3. 集成包版本问题** <br>
Sa-Token-Redis 集成包的版本尽量与 Sa-Token-Starter 集成包的版本一致，否则可能出现兼容性问题。



### 4、扩展：集成 MongoDB 

- [集成 MongoDB 参考一](/up/integ-spring-mongod-1)
- [集成 MongoDB 参考二](/up/integ-spring-mongod-2)
