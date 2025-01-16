# Sa-Token 集成 MongoDB 
--- 

此章介绍如何通过扩展 `SaTokenDao` 接口来实现 MongodDB 的集成。

[示例代码：sa-token-mongodb-demo](https://gitee.com/lilihao/sa-token-mongodb-demo)

先决条件：
1. Spring Boot 3
2. Spring Data Mongodb

以下是依赖的引入：

---


<!---------------------------- tabs:start ------------------------------>
<!-------- tab:Maven 方式 -------->
``` xml 
<!-- 引入 spring data mongodb -->
<dependency>
	<groupId>org.springframework.boot</groupId>
	<artifactId>spring-boot-starter-data-mongodb</artifactId>
</dependency>
```
<!-------- tab:Gradle 方式 -------->
``` gradle
// 引入 spring data mongodb
implementation 'org.springframework.boot:spring-boot-starter-data-mongodb'
```
<!---------------------------- tabs:end ------------------------------>

优点：少量改造即可完成集成 MongodDB




### 集成代码：


**1. 创建一个类来包装`Sa—Token`的数据**
```java
@Document("saTokenMongo") // 你也可以自定义集合名称
public class SaTokenMongoData {

    @Id
    private String id;

    // token
    @Indexed(unique = true)
    private String key;

    // sa-token 的 session
    private SaSession session;

    // sa-token 的 token string
    private String string;

    //使用 @SuppressWarnings("removal") 的目的是，防止IDEA报错，因为 expireAfterSeconds是不在支持的属性。
    @SuppressWarnings("removal")
    // 给 expireAt 添加 `@Indexed(expireAfterSeconds = 0)` 注解，当过期时MongoDB会自动帮我删除过期的数据
    @Indexed(expireAfterSeconds = 0)
    private LocalDateTime expireAt; // 你也可以使用 Date 类型，对应的在`SaTokenMongoDao`中，需要将LocalDateTime替换成Date

    // 忽略 getter setter
}
```

**2.实现 SaTokenDao**

这个 SaTokenMongoDao 是仿照官方的 redis 集成实现的
```java
package com.xx.xx.security;

import cn.dev33.satoken.dao.SaTokenDao;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

@Component
public class SaTokenMongoDao implements SaTokenDao {

    private final MongoTemplate mongoTemplate;

    public SaTokenMongoDao(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }


    private Query keyQuery(String key) {
        return Query.query(Criteria.where("key").is(key));
    }

    /**
     * 获取 value，如无返空
     *
     * @param key 键名称
     * @return value
     */
    @Override
    public String get(String key) {

        return Optional.ofNullable(mongoTemplate.findOne(keyQuery(key), SaTokenMongoData.class)).map(SaTokenMongoData::getString).orElse(null);
    }


    LocalDateTime getExpireAtFromTimeout(long timeout) {
        // 当接受到的值是`SaTokenDao.NEVER_EXPIRE`时，说明永不过期，对应的我们需要把 expireAt 设置为null mongodb就不会删除这个记录
        return timeout == SaTokenDao.NEVER_EXPIRE ? null : LocalDateTime.now().plusSeconds(timeout);
    }

    /**
     * 写入 value，并设定存活时间（单位: 秒）
     *
     * @param key     键名称
     * @param value   值
     * @param timeout 数据有效期（值大于0时限时存储，值=-1时永久存储，值=0或小于-2时不存储）
     */
    @Override
    public void set(String key, String value, long timeout) {
        if (timeout == 0 || timeout <= SaTokenDao.NOT_VALUE_EXPIRE) {
            return;
        }

        // 判断是否为永不过期
        mongoTemplate.upsert(
                keyQuery(key),
                Update.update("string", value).set("expireAt", getExpireAtFromTimeout(timeout)),
                SaTokenMongoData.class
        );
    }

    /**
     * 更新 value （过期时间不变）
     *
     * @param key   键名称
     * @param value 值
     */
    @Override
    public void update(String key, String value) {
        long expire = getTimeout(key);
        // -2 = 无此键
        if (expire == SaTokenDao.NOT_VALUE_EXPIRE) {
            return;
        }
        this.set(key, value, expire);
    }

    /**
     * 删除 value
     *
     * @param key 键名称
     */
    @Override
    public void delete(String key) {
        mongoTemplate.remove(keyQuery(key), SaTokenMongoData.class);
    }

    /**
     * 获取 value 的剩余存活时间（单位: 秒）
     *
     * @param key 指定 key
     * @return 这个 key 的剩余存活时间
     */
    @Override
    public long getTimeout(String key) {

        LocalDateTime localDateTime = Optional.ofNullable(mongoTemplate.findOne(keyQuery(key), SaTokenMongoData.class)).map(SaTokenMongoData::getExpireAt).orElse(LocalDateTime.MIN);

        long seconds = Duration.between(LocalDateTime.now(), localDateTime).getSeconds();
        if (seconds < 0) {
            return 0;
        }
        return seconds;
    }

    /**
     * 修改 value 的剩余存活时间（单位: 秒）
     *
     * @param key     指定 key
     * @param timeout 过期时间（单位: 秒）
     */
    @Override
    public void updateTimeout(String key, long timeout) {
        // 判断是否想要设置为永久
        if (timeout == SaTokenDao.NEVER_EXPIRE) {
            long expire = getTimeout(key);
            //noinspection StatementWithEmptyBody
            if (expire == SaTokenDao.NEVER_EXPIRE) {
                // 如果其已经被设置为永久，则不作任何处理
            } else {
                // 如果尚未被设置为永久，那么再次set一次
                this.set(key, this.get(key), timeout);
            }
            return;
        }

        mongoTemplate.upsert(
                keyQuery(key),
                Update.update("expireAt", getExpireAtFromTimeout(timeout)),
                SaTokenMongoData.class
        );
    }

    /**
     * 获取 Object，如无返空
     *
     * @param key 键名称
     * @return object
     */
    @Override
    public Object getObject(String key) {
        return Optional.ofNullable(mongoTemplate.findOne(keyQuery(key), SaTokenMongoData.class)).map(SaTokenMongoData::getSession).orElse(null);
    }

    /**
     * 写入 Object，并设定存活时间 （单位: 秒）
     *
     * @param key     键名称
     * @param object  值
     * @param timeout 存活时间（值大于0时限时存储，值=-1时永久存储，值=0或小于-2时不存储）
     */
    @Override
    public void setObject(String key, Object object, long timeout) {
        if (timeout == 0 || timeout <= SaTokenDao.NOT_VALUE_EXPIRE) {
            return;
        }
        // 判断是否为永不过期
        mongoTemplate.upsert(
                keyQuery(key),
                Update.update("session", object).set("expireAt", getExpireAtFromTimeout(timeout)),
                SaTokenMongoData.class
        );
    }

    /**
     * 更新 Object （过期时间不变）
     *
     * @param key    键名称
     * @param object 值
     */
    @Override
    public void updateObject(String key, Object object) {
        long expire = getObjectTimeout(key);
        // -2 = 无此键
        if (expire == SaTokenDao.NOT_VALUE_EXPIRE) {
            return;
        }
        this.setObject(key, object, expire);
    }

    /**
     * 删除 Object
     *
     * @param key 键名称
     */
    @Override
    public void deleteObject(String key) {
        delete(key);
    }

    /**
     * 获取 Object 的剩余存活时间 （单位: 秒）
     *
     * @param key 指定 key
     * @return 这个 key 的剩余存活时间
     */
    @Override
    public long getObjectTimeout(String key) {
        return getTimeout(key);
    }

    /**
     * 修改 Object 的剩余存活时间（单位: 秒）
     *
     * @param key     指定 key
     * @param timeout 剩余存活时间
     */
    @Override
    public void updateObjectTimeout(String key, long timeout) {
        // 判断是否想要设置为永久
        updateTimeout(key, timeout);
    }

    /**
     * 搜索数据
     *
     * @param prefix   前缀
     * @param keyword  关键字
     * @param start    开始处索引
     * @param size     获取数量  (-1代表从 start 处一直取到末尾)
     * @param sortType 排序类型（true=正序，false=反序）
     * @return 查询到的数据集合
     */
    @Override
    public List<String> searchData(String prefix, String keyword, int start, int size, boolean sortType) {

        List<Criteria> criteriaList = new ArrayList<>();

        if (StringUtils.hasText(prefix)) {
            criteriaList.add(Criteria.where("key").regex(Pattern.compile("^" + Pattern.quote(prefix))));
        }
        if (StringUtils.hasText(keyword)) {
            Pattern keywordPattern = Pattern.compile(Pattern.quote(keyword), Pattern.CASE_INSENSITIVE);
            criteriaList.add(Criteria.where("key").regex(keywordPattern));
        }


        Criteria criteria = new Criteria();

        if (!criteriaList.isEmpty()) {
            criteria.andOperator(criteriaList);
        }

        long skip = (long) Math.max(start, 0) * Math.max(size, 1);

        Query query = Query.query(criteria).skip(skip).limit(size);

        query.fields().include("key");

        return mongoTemplate.find(query, SaTokenMongoData.class).stream().map(SaTokenMongoData::getKey).toList();
    }
}
```



