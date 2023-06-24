# Sa-Token 集成 Redis 
--- 

Sa-Token 默认将数据保存在内存中，此模式读写速度最快，且避免了序列化与反序列化带来的性能消耗，但是此模式也有一些缺点，比如：

1. 重启后数据会丢失。
2. 无法在分布式环境中共享数据。

为此，Sa-Token 提供了扩展接口，你可以轻松将会话数据存储在一些专业的缓存中间件上（比如 Redis），
做到重启数据不丢失，而且保证分布式环境下多节点的会话一致性。

以下是框架提供的 Redis 集成包：

---

### 方式1、Sa-Token 整合 Redis （使用 jdk 默认序列化方式）

<!---------------------------- tabs:start ------------------------------>
<!-------- tab:Maven 方式 -------->
``` xml 
<!-- Sa-Token 整合 Redis （使用 jdk 默认序列化方式） -->
<dependency>
	<groupId>cn.dev33</groupId>
	<artifactId>sa-token-redis</artifactId>
	<version>${sa.top.version}</version>
</dependency>
```
<!-------- tab:Gradle 方式 -------->
``` gradle
// Sa-Token 整合 Redis （使用 jdk 默认序列化方式）
implementation 'cn.dev33:sa-token-redis:${sa.top.version}'
```
<!---------------------------- tabs:end ------------------------------>

优点：兼容性好，缺点：Session 序列化后基本不可读，对开发者来讲等同于乱码。


### 方式2、Sa-Token 整合 Redis（使用 jackson 序列化方式）

<!---------------------------- tabs:start ------------------------------>
<!-------- tab:Maven 方式 -------->
``` xml 
<!-- Sa-Token 整合 Redis （使用 jackson 序列化方式） -->
<dependency>
	<groupId>cn.dev33</groupId>
	<artifactId>sa-token-redis-jackson</artifactId>
	<version>${sa.top.version}</version>
</dependency>
```
<!-------- tab:Gradle 方式 -------->
``` gradle
// Sa-Token 整合 Redis （使用 jackson 序列化方式）
implementation 'cn.dev33:sa-token-redis-jackson:${sa.top.version}'
```
<!---------------------------- tabs:end ------------------------------>

优点：Session 序列化后可读性强，可灵活手动修改，缺点：兼容性稍差。


### 集成 Redis 请注意：


**1. 无论使用哪种序列化方式，你都必须为项目提供一个 Redis 实例化方案，例如：**

<!---------------------------- tabs:start ------------------------------>
<!-------- tab:Maven 方式 -------->
``` xml 
<!-- 提供Redis连接池 -->
<dependency>
	<groupId>org.apache.commons</groupId>
	<artifactId>commons-pool2</artifactId>
</dependency>
```
<!-------- tab:Gradle 方式 -------->
``` gradle
// Sa-Token 整合 Redis （使用 jackson 序列化方式）
implementation 'org.apache.commons:commons-pool2'
```
<!---------------------------- tabs:end ------------------------------>


**2. 引入了依赖，我还需要为 Redis 配置连接信息吗？** <br>
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




**3. 集成 Redis 后，是我额外手动保存数据，还是框架自动保存？** <br>
框架自动保存。集成 `Redis` 只需要引入对应的 `pom依赖` 即可，框架所有上层 API 保持不变。

**4. 集成包版本问题** <br>
Sa-Token-Redis 集成包的版本尽量与 Sa-Token-Starter 集成包的版本一致，否则可能出现兼容性问题。


<br><br>
更多框架的集成方案正在更新中...


