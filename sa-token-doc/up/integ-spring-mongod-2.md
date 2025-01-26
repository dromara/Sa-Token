# Sa-Token 集成 MongoDB 
--- 

在 Spring Boot 下集成 MongoDB：

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

