# Sa-Token 最新版本

在线文档：[https://sa-token.cc/](https://sa-token.cc/)

--- 

### 正式版本 
v1.31.0 正式版，可上生产：

``` xml
<!-- Sa-Token 权限认证 -->
<dependency>
    <groupId>cn.dev33</groupId>
    <artifactId>sa-token-spring-boot-starter</artifactId>
    <version>1.31.0</version>
</dependency>
```

Maven依赖一直无法加载成功？[参考解决方案](https://sa-token.cc/doc.html#/start/maven-pull)

--- 

### 最新版本

**2022-10-19：v1.31.1.temp 过渡版本，仅做学习测试，不推荐上生产：**
``` xml
<!-- Sa-Token 权限认证 -->
<dependency>
    <groupId>cn.dev33</groupId>
    <artifactId>sa-token-spring-boot-starter</artifactId>
    <version>1.31.1.temp</version>
</dependency>
```

更新日志：
- 文档：[自定义 SaTokenContext 指南] 章节新增对三种模型的解释。
- 文档：基础章节新增练习测试。
- 文档：部分章节新增代码示例。
- 文档：增加全局调色功能。
- 升级：`SaFoxUtil.getValueByType()` 新增对 char 类型的转换。
- 修复：修复 sa-token-dao-redis-fastjson 插件多余序列化 `timeout` 字段的问题。
- 修复：修复 sa-token-dao-redis-fastjson 插件 `session.getModel` 无法反序列化实体类的问题。
- 新增：新增 `sa-token-dao-redis-fastjson2` 插件。


**2022-10-22：v1.31.2.temp 过渡版本，仅做学习测试，不推荐上生产：**
``` xml
<!-- Sa-Token 权限认证 -->
<dependency>
    <groupId>cn.dev33</groupId>
    <artifactId>sa-token-spring-boot-starter</artifactId>
    <version>1.31.2.temp</version>
</dependency>
```

更新日志：
- 文档：新增多账号体系混合鉴权代码示例。
- 文档：文档增加 Gradle 依赖方式和 properties 风格配置。
- 修复：修复 `sa-token-quick-login` 插件指定拦截排除路由不生效的问题。
- 修复：修复 `sa-token-alone-redis` + `sa-token-dao-redis-fastson` 时 Redis 无法分离的问题。
- 新增：新增全局配置 `is-write-header`，控制登录后是否将 Token 写入响应头。
- 优化：优化版本号定义，父 pom.xml 统一定义依赖版本号，并升级部分依赖。


**2022-10-26：v1.31.3.temp 过渡版本，仅做学习测试，不推荐上生产：**
``` xml
<!-- Sa-Token 权限认证 -->
<dependency>
    <groupId>cn.dev33</groupId>
    <artifactId>sa-token-spring-boot-starter</artifactId>
    <version>v1.31.3.temp</version>
</dependency>
```

- 新增：二级认证模块新增指定业务标识能力。  **[重要]** 
- 重构：重构会话查询参数作用：由`start=-1`时查询全部会话，改为 `start=0,size=-1` 时查询全部。 **[不向下兼容]** 
- 重构：Sa-Token 依赖包集中在父pom管理。
- 重构：SSO 示例项目 http 请求工具改为 Forest。
- 重构：Id-Token 模块更名为 Same-Token。 **[不向下兼容]**
- 新增：文档新增 SSO 平台中心模式示例，跳连接进入子系统。 **[重要]** 
- 重构：SSO-Server 端单点注销地址修改 `/sso/logout` -> `/sso/signout`，避免与 SSO-Client 端同 path 的冲突。 **[不向下兼容]** 
- 重构：重构SSO模块，静态式API改为实例式：SaSsoHandle -> SaSsoProcessor。 **[重要]** **[不向下兼容]** 
- 新增：SSO模块文档新增单个项目同时搭建 sso-server 和 sso-client 的示例。 **[重要]** 
- 新增：SSO模块文档新增一个项目同时搭建两个 sso-server 服务 的示例。 **[重要]** 
- 新增：新增 sa-token-dependencies，统一定义依赖版本。







