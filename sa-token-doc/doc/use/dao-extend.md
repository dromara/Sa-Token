# 持久层扩展
--- 

Sa-token默认将会话数据保存在内存中，此模式读写速度最快，且避免了序列化与反序列化带来的性能消耗，但是此模式也有一些缺点，比如：重启后数据会丢失，无法在集群模式下共享数据

为此，Sa-Token将数据持久操作全部抽象到 `SaTokenDao` 接口中，保证大家对框架进行灵活扩展，比如我们可以将会话数据存储在 `Redis`、`Memcached`等专业的缓存中间件中，做到重启数据不丢失，而且保证分布式环境下多节点的会话一致性

除了框架内部对`SaTokenDao`提供的基于内存的默认实现，官方仓库还提供了以下扩展方案：<br>


### 1. Sa-Token 整合 Redis (使用jdk默认序列化方式)
``` xml 
<!-- Sa-Token整合redis (使用jdk默认序列化方式) -->
<dependency>
	<groupId>cn.dev33</groupId>
	<artifactId>sa-token-dao-redis</artifactId>
	<version>1.20.0</version>
</dependency>
```
优点：兼容性好，缺点：Session序列化后基本不可读，对开发者来讲等同于乱码


### 2. Sa-Token 整合 Redis (使用jackson序列化方式)
``` xml 
<!-- Sa-Token整合redis (使用jackson序列化方式) -->
<dependency>
	<groupId>cn.dev33</groupId>
	<artifactId>sa-token-dao-redis-jackson</artifactId>
	<version>1.20.0</version>
</dependency>
```
优点：Session序列化后可读性强，可灵活手动修改，缺点：兼容性稍差


<br>

### 集成Redis请注意：


**1. 无论使用哪种序列化方式，你都必须为项目提供一个Redis实例化方案，例如：**
``` xml
<!-- 提供redis连接池 -->
<dependency>
	<groupId>org.apache.commons</groupId>
	<artifactId>commons-pool2</artifactId>
</dependency>
```

**2. 引入了依赖，我还需要为Redis配置连接信息吗？** <br>
需要！只有项目初始化了正确的Redis实例，`Sa-Token`才可以使用Redis进行数据持久化，参考以下`yml配置`：
``` java
# 端口
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
        # 连接超时时间（毫秒）
        timeout: 1000ms
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


**3. 集成Redis后，是我额外手动保存数据，还是框架自动保存？** <br>
框架自动保存。集成`Redis`只需要引入对应的`pom依赖`即可，框架所有上层API保持不变


<br><br>
更多框架的集成方案正在更新中... (欢迎大家提交pr)



