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
// 提供Redis连接池
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


> [!WARNING| label:小提示 ] 
> 如果你使用的是 SpringBoot3.x 版本，则需要将前缀 `spring.redis` 改为 `spring.data.redis`。



**3. 集成 Redis 后，是我额外手动保存数据，还是框架自动保存？** <br>
框架自动保存。集成 `Redis` 只需要引入对应的 `pom依赖` 即可，框架所有上层 API 保持不变。

**4. 集成包版本问题** <br>
Sa-Token-Redis 集成包的版本尽量与 Sa-Token-Starter 集成包的版本一致，否则可能出现兼容性问题。

### 示例：在 Spring Boot 下集成 MongoDB：

<!---------------------------- tabs:start ------------------------------>
<!-------- tab:Maven 方式 -------->
``` xml 
<!-- 提供MongoDB依赖 -->
<dependency>
	<groupId>org.springframework.boot</groupId>
	<artifactId>spring-boot-starter-data-mongodb</artifactId>
</dependency>
```
<!-------- tab:Gradle 方式 -------->
``` gradle
// 提供MongoDB依赖
implementation 'org.springframework.boot:spring-boot-starter-data-mongodb'
```
<!---------------------------- tabs:end ------------------------------>

1. 创建一个 `MySaSession` 并继承 `SaSession`
```java
public class MySaSession extends SaSession {
    public MySaSession(String id) {
        super(id);
    }

    public void setDataMap(Map<String, Object> dataMap) {
        refreshDataMap(dataMap);
    }
}
```
原因：由于 `SaSession` 中的 `dataMap` 字段没有 `setter` 方法，当 `spring-data-mongodb` 反序列化 `SaSession` 时会报 `Cannot set property dataMap because no setter, no wither and it's not part of the persistence constructor public cn.dev33.satoken.session.SaSession()` 错误

2. 在 `SpringBoot` 启动方法中重写 `SaStrategy.instance.createSession` 方法，使我们自定义的 `MySaSession` 生效
```java
@SpringBootApplication
public class SpringApplication {

    public static void main(String[] args) {

	// 重写 SaStrategy.instance.createSession 方法
        SaStrategy.instance.createSession = (sessionId) -> {
            return new MySaSession(sessionId);
        };

        SpringApplication.run(SpringApplication.class, args);
    }
}
```

3. 实现 SaTokenDao 接口
```java
//定义一个类用于保存SaSession
@Document
public class SaTokenWrap {
    private String id;
    private String value;
    private Object object;
    // 这里利用MongoDB的TTL索引，当过期时MongoDB会自动删除过期的数据，同时如果timeout如果为null那么视为永不删除
    @Indexed(expireAfterSeconds = 1, background = true)
    private Date timeout;


    public boolean live() {
        return getTimeout() == null || getTimeout().after(new Date());
    }
}
```
```java
// SaTokenDao 实现
@Component
public class SaTokenDaoMongo implements SaTokenDao {


    private final MongoTemplate mongoTemplate;

    public SaTokenDaoMongo(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }


    Optional<SaTokenWrap> getByKey(String key) {
        SaTokenWrap tokenWrap = mongoTemplate.findById(key, SaTokenWrap.class);

        return Optional.ofNullable(tokenWrap).filter(SaTokenWrap::live);
    }

    Date timeoutToDate(long timeout) {

        return new Date(timeout * 1000 + System.currentTimeMillis());
    }

    void upsertByPath(String key, String path, Object value, long timeout) {
        if (timeout == 0 || timeout <= SaTokenDao.NOT_VALUE_EXPIRE) {
            return;
        }
        Update update = Update.update(path, value);

        if (timeout != SaTokenDao.NEVER_EXPIRE) {
            update.set("timeout", timeoutToDate(timeout));
        } else {
            update.unset("timeout");
        }

        mongoTemplate.upsert(
                Query.query(Criteria.where("id").is(key)),
                update,
                SaTokenWrap.class
        );
    }

    void updateByPath(String key, String path, Object value) {
        mongoTemplate.updateFirst(
                Query.query(Criteria.where("id").is(key).and("timeout").gte(new Date())),
                Update.update(path, value),
                SaTokenWrap.class
        );
    }

    // ------------------------ String 读写操作

    @Override
    public String get(String key) {

        return getByKey(key).map(SaTokenWrap::getValue).orElse(null);
    }

    @Override
    public void set(String key, String value, long timeout) {
        upsertByPath(key, "value", value, timeout);
    }

    @Override
    public void update(String key, String value) {
        updateByPath(key, "value", value);
    }

    @Override
    public void delete(String key) {
        mongoTemplate.remove(Query.query(Criteria.where("id").is(key)));
    }

    @Override
    public long getTimeout(String key) {

        SaTokenWrap tokenWrap = mongoTemplate.findById(key, SaTokenWrap.class);

        if (tokenWrap == null) {
            return SaTokenDao.NOT_VALUE_EXPIRE;
        }

        if (tokenWrap.getTimeout() == null) {
            return SaTokenDao.NEVER_EXPIRE;
        }

        long expire = tokenWrap.getTimeout().getTime();
        long timeout = (expire - System.currentTimeMillis()) / 1000;

        // 小于零时，视为不存在
        if (timeout < 0) {
            mongoTemplate.remove(Query.query(Criteria.where("id").is(key)));
            return SaTokenDao.NOT_VALUE_EXPIRE;
        }
        return timeout;
    }

    @Override
    public void updateTimeout(String key, long timeout) {

        Update update = new Update();

        if (timeout == SaTokenDao.NEVER_EXPIRE) {
            update.unset("timeout");
        } else {
            update.set("timeout", timeoutToDate(timeout));
        }

        mongoTemplate.upsert(
                Query.query(Criteria.where("id").is(key)),
                update,
                SaTokenWrap.class
        );
    }


    // ------------------------ Object 读写操作

    @Override
    public Object getObject(String key) {
        return getByKey(key).map(SaTokenWrap::getObject).orElse(null);
    }

    @Override
    public void setObject(String key, Object object, long timeout) {
        upsertByPath(key, "object", object, timeout);
    }

    @Override
    public void updateObject(String key, Object object) {
        updateByPath(key, "object", object);
    }

    @Override
    public void deleteObject(String key) {
        delete(key);
    }

    @Override
    public long getObjectTimeout(String key) {
        return getTimeout(key);
    }

    @Override
    public void updateObjectTimeout(String key, long timeout) {
        updateTimeout(key, timeout);
    }


    // ------------------------ Session 读写操作
    // 使用接口默认实现


    // --------- 会话管理

    @Override
    public List<String> searchData(String prefix, String keyword, int start, int size, boolean sortType) {

        List<SaTokenWrap> wrapList = mongoTemplate.find(
                Query.query(Criteria.where("id").regex(prefix + "*" + keyword + "*").and("timeout").gte(new Date())),
                SaTokenWrap.class
        );

        List<String> list = wrapList.stream().map(SaTokenWrap::getValue).filter(StringUtils::hasText).collect(Collectors.toList());

        return SaFoxUtil.searchList(list, start, size, sortType);
    }


}
```

<!-- <br><br>
更多框架的集成方案正在更新中... -->


### 扩展：集成 MongoDB 

由 `@lilihao` 提供的 MongoDB 集成示例，参考：[集成 Spring MongodDB](/up/integ-spring-mongod)

