
## SSM 架构集成 Sa-Token 示例

说是SSM，其实没有M，仅仅是给使用 SpringMVC 非 SpringBoot 的项目提供一个简单的 Sa-Token 集成示例。

### 集成要点（v1.42.0+）

1. 在 `web.xml` 注册 `SaTokenContextFilter`（本示例已配置，逻辑同官方 `SaTokenContextFilterForServlet`）
2. 在 `SaTokenBeanInjection` 中手动注册 `SaStrategy`（路由匹配、SaRequest/SaResponse/SaStorage 创建策略）
3. 不再注入 `SaTokenContextForSpring`，上下文由 Filter + ThreadLocal 方案自动管理

直接运行项目即可，里面注释挺全的，也不必做过多说明了
（其实就是我懒，光搭建起来这个架子就累瘫了，各种版本兼容问题报起错来大汗淋漓，推荐新项目能上 SpringBoot 就赶紧上吧，千万别在SSM上浪费生命）。

推荐 jdk8 + tomcat8。

### 常见问题

- **Filter 启动失败（One or more Filters failed to start）**：① `javax.servlet-api` 必须 `provided`，不能打进 WAR；② 不要开 `async-supported`（SpringBoot 会自动给 `DispatcherServlet` 开异步，纯 SSM 不会）；③ `web.xml` 注册本工程内的 `com.pj.satoken.SaTokenContextFilter`，不要直接引用 starter 里的 Filter 类。
- **NoSuchMethodError: Expiration.keepTtl()**：`spring-data-redis` 版本过低（须 2.7.x，与 `sa-token-redis-jackson` 对齐），勿手动锁死 2.3.x。
