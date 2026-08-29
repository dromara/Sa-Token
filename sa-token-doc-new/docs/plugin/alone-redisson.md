# Sa-Token-Alone-Redisson 独立 Redisson 插件
---

Sa-Token 默认的 Redisson 集成会把权限数据和业务缓存放在同一个 `RedissonClient` 上。需要彻底分离时，可再为 Sa-Token 单独起一个 Redisson 连接。

::: info 业务场景
搭建两个 Redis：一个给业务 RedissonClient，另一台专门存放 Sa-Token 权限数据。
:::

---

### 1、引入依赖

引入 `sa-token-alone-redisson` 即可，**无需再引入** `sa-token-redisson` 或 `sa-token-redisson-spring-boot-starter`（本插件已包含 Dao 实现并完成注册）。

业务代码如果也要用 `RedissonClient`：
- Spring Boot 2 / 3：可再引入官方 `redisson-spring-boot-starter`，或自行注册 Bean。
- Spring Boot 4：当前 Redisson 3.45 的 starter 仍引用已移除的 `RedisAutoConfiguration`，**不要引入** `redisson-spring-boot-starter`，改为引入 `redisson` 并自行注册 Bean。`sa-token-alone-redisson` 本身无需 Boot 4 专用包。

:::tabs
== Maven 方式

``` xml
<!-- Sa-Token 插件：权限缓存与业务缓存分离（Redisson） -->
<dependency>
	<groupId>cn.dev33</groupId>
	<artifactId>sa-token-alone-redisson</artifactId>
	<version>${sa.top.version}</version>
</dependency>

<!-- 业务 Redis（按需） -->
<dependency>
	<groupId>org.redisson</groupId>
	<artifactId>redisson-spring-boot-starter</artifactId>
	<version>${redisson.version}</version>
</dependency>
```

== Gradle 方式

``` gradle
implementation 'cn.dev33:sa-token-alone-redisson:${sa.top.version}'
// 业务 Redis（按需）
implementation 'org.redisson:redisson-spring-boot-starter:${redisson.version}'
```

:::



### 2、配置独立连接（Redisson 原生 yaml）

配置项与官方 `spring.redis.redisson.config` / `file` 相同，单机用 `singleServerConfig`，集群用 `clusterServersConfig`。

:::tabs
== 单机（内嵌 yaml）

``` yaml
sa-token:
    token-name: satoken
    timeout: 2592000
    # Sa-Token 单独使用的 Redisson 连接
    alone-redisson:
        config: |
            singleServerConfig:
                address: "redis://127.0.0.1:6379"
                database: 2
                password: null

spring:
    redis:
        # 业务使用的 Redisson 连接
        redisson:
            config: |
                singleServerConfig:
                    address: "redis://127.0.0.1:6379"
                    database: 0
                    password: null
```

== 集群（内嵌 yaml）

``` yaml
sa-token:
    alone-redisson:
        config: |
            clusterServersConfig:
                nodeAddresses:
                    - "redis://127.0.0.1:3000"
                    - "redis://127.0.0.1:3001"
                    - "redis://127.0.0.1:3002"
                password: null
```

== 外置文件

``` yaml
# application.yml
sa-token:
    alone-redisson:
        file: classpath:sa-redisson.yml
```

`src/main/resources/sa-redisson.yml` 示例（单机）：

``` yaml
singleServerConfig:
    address: "redis://127.0.0.1:6379"
    database: 2
    password: null
```

集群则把内容换成上面「集群」页签里的 `clusterServersConfig` 即可。

:::


`config` 与 `file` 二选一，同时配置时优先 `config`。

完整示例：
- 单机：[sa-token-demo-alone-redisson](https://gitee.com/dromara/sa-token/blob/master/sa-token-demo/sa-token-demo-alone-redisson/src/main/resources/application.yml)
- 集群：[sa-token-demo-alone-redisson-cluster](https://gitee.com/dromara/sa-token/blob/master/sa-token-demo/sa-token-demo-alone-redisson-cluster/src/main/resources/application.yml)
- Spring Boot 3：[sa-token-demo-alone-redisson-sb3](https://gitee.com/dromara/sa-token/blob/master/sa-token-demo/sa-token-demo-alone-redisson-sb3/src/main/resources/application.yml)
- Spring Boot 4：[sa-token-demo-alone-redisson-sb4](https://gitee.com/dromara/sa-token/blob/master/sa-token-demo/sa-token-demo-alone-redisson-sb4/src/main/resources/application.yml)


### 3、测试

``` java
@RestController
@RequestMapping("/test/")
public class TestController {

	@Autowired
	RedissonClient redissonClient;

	// 测试 Sa-Token 缓存 --- http://localhost:8084/test/login
	@RequestMapping("login")
	public SaResult login(@RequestParam(defaultValue="10001") String id) {
		StpUtil.login(id);
		return SaResult.ok();
	}

	// 测试业务缓存 --- http://localhost:8084/test/test
	@RequestMapping("test")
	public SaResult test() {
		redissonClient.getBucket("hello").set("Hello World");
		return SaResult.ok();
	}
}
```

分别访问两个接口，观察两套 Redis 中增加的数据。



### 4、注意点
- 引入本插件后，无需再引入 `sa-token-redisson` 或 `sa-token-redisson-spring-boot-starter`。
- 业务代码注入的 `RedissonClient` 来自官方 `redisson-spring-boot-starter` 或你自己注册的 Bean，与 Sa-Token 使用的独立连接互不影响。Spring Boot 4 请自行注册，不要引入 3.45 的 starter。
- `SaTokenDaoForRedisson` 默认使用 `StringCodec`，与业务 `RedissonClient` 的全局 codec 无关。
- Redis Cluster 没有 database 索引，集群配置里不要写 `database`。
- 哨兵模式同样走 Redisson 原生 yaml（`sentinelServersConfig`），按 [Redisson 配置文档](https://github.com/redisson/redisson/wiki/2.-Configuration) 填写即可。
