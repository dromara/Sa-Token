# 引入 Sa-Token 依赖

------

## Maven依赖 
在项目中直接通过 `pom.xml` 引入 Sa-Token 的依赖即可（四选一）：

<!------------------------------ tabs:start ------------------------------>

<!-- tab:SpringMVC环境 （ServletAPI）  -->
如果你使用的框架基于 ServletAPI 构建（ SpringMVC、SpringBoot、Zuul等 ），请引入此包
``` xml
<!-- Sa-Token 权限认证, 在线文档：http://sa-token.dev33.cn/ -->
<dependency>
	<groupId>cn.dev33</groupId>
	<artifactId>sa-token-spring-boot-starter</artifactId>
	<version>${sa.top.version}</version>
</dependency>
```

<!-- tab:WebFlux环境 （Reactor）  -->
注：如果你使用的框架基于 Reactor 模型构建（Netty、WebFlux、ShenYu、SC Gateway等），请引入此包
``` xml
<!-- Sa-Token 权限认证（Reactor响应式集成）, 在线文档：http://sa-token.dev33.cn/ -->
<dependency>
	<groupId>cn.dev33</groupId>
	<artifactId>sa-token-reactor-spring-boot-starter</artifactId>
	<version>${sa.top.version}</version>
</dependency>
```

<!-- tab:Servlet容器环境   -->
注：如果你的项目没有使用Spring，但是Web框架是基于 ServletAPI 规范的，可以引入此包
``` xml
<!-- Sa-Token 权限认证（ServletAPI规范）, 在线文档：http://sa-token.dev33.cn/ -->
<dependency>
	<groupId>cn.dev33</groupId>
	<artifactId>sa-token-servlet</artifactId>
	<version>${sa.top.version}</version>
</dependency>
```
引入此依赖需要自定义 SaTokenContext 实现，参考：[自定义 SaTokenContext 指南](/fun/sa-token-context)

<!-- tab:其它   -->
注：如果你的项目既没有使用 SpringMVC、WebFlux，也不是基于 ServletAPI 规范，那么可以引入core核心包
``` xml
<!-- Sa-Token 权限认证（core核心包）, 在线文档：http://sa-token.dev33.cn/ -->
<dependency>
	<groupId>cn.dev33</groupId>
	<artifactId>sa-token-core</artifactId>
	<version>${sa.top.version}</version>
</dependency>
```
引入此依赖需要自定义 SaTokenContext 实现，参考：[自定义 SaTokenContext 指南](/fun/sa-token-context)
<!---------------------------- tabs:end ------------------------------>


## Gradle依赖
<!-- tabs:start -->
<!-- tab:SpringMVC环境 （ServletAPI）  -->
``` xml
implementation 'cn.dev33:sa-token-spring-boot-starter:${sa.top.version}'
```
<!-- tab:WebFlux环境 （Reactor）  -->
``` xml
implementation 'cn.dev33:sa-token-reactor-spring-boot-starter:${sa.top.version}'
```
<!-- tab:Servlet容器环境  -->
``` xml
implementation 'cn.dev33:sa-token-servlet:${sa.top.version}'
```
<!-- tab:其它  -->
``` xml
implementation 'cn.dev33:sa-token-core:${sa.top.version}'
```
<!-- tabs:end -->

注：JDK版本：`v1.8+`，SpringBoot：`建议2.0以上`


## 获取源码
如果你想深入了解Sa-Token，你可以通过`Gitee`或者`GitHub`来获取源码 （**学习测试请拉取master分支**，dev为正在开发的分支，有很多特性并不稳定）
- **Gitee**地址：[https://gitee.com/dromara/sa-token](https://gitee.com/dromara/sa-token)
- **GitHub**地址：[https://github.com/dromara/sa-token](https://github.com/dromara/sa-token)
- 开源不易，求鼓励，给个`star`吧
- 源码目录介绍: 

``` js
── sa-token
	├── sa-token-core                         // [核心] Sa-Token 核心模块
	├── sa-token-starter                      // [整合] Sa-Token 与其它框架整合
		├── sa-token-servlet                      // [整合] Sa-Token 整合 Servlet容器实现类包
		├── sa-token-spring-boot-starter          // [整合] Sa-Token 整合 SpringBoot 快速集成 
		├── sa-token-reactor-spring-boot-starter  // [整合] Sa-Token 整合 Reactor 响应式编程 快速集成 
		├── sa-token-solon-plugin                 // [整合] Sa-Token 整合 Solon 快速集成 
		├── sa-token-jfinal-plugin                // [整合] Sa-Token 整合 JFinal 快速集成 
		├── sa-token-jboot-plugin                 // [整合] Sa-Token 整合 jboot 快速集成 
	├── sa-token-plugin                       // [插件] Sa-Token 插件合集
		├── sa-token-dao-redis                    // [插件] Sa-Token 整合 Redis (使用jdk默认序列化方式)
		├── sa-token-dao-redis-jackson            // [插件] Sa-Token 整合 Redis (使用jackson序列化方式)
		├── sa-token-spring-aop                   // [插件] Sa-Token 整合 SpringAOP 注解鉴权
		├── sa-token-temp-jwt                     // [插件] Sa-Token 整合 jwt 临时令牌鉴权 
		├── sa-token-quick-login                  // [插件] Sa-Token 快速注入登录页插件 
		├── sa-token-alone-redis                  // [插件] Sa-Token 独立Redis插件，实现[权限缓存与业务缓存分离]
		├── sa-token-oauth2                       // [插件] Sa-Token 实现 OAuth2.0 模块 
		├── sa-token-dialect-thymeleaf            // [插件] Sa-Token 标签方言（Thymeleaf版）
		├── sa-token-jwt                          // [插件] Sa-Token 整合 jwt 登录认证
	├── sa-token-demo                         // [示例] Sa-Token 示例合集
		├── sa-token-demo-springboot              // [示例] Sa-Token 整合 SpringBoot 
		├── sa-token-demo-webflux                 // [示例] Sa-Token 整合 WebFlux 
		├── sa-token-demo-jwt                     // [示例] Sa-Token 集成 jwt 
		├── sa-token-demo-solon                   // [示例] Sa-Token 集成 Solon 
		├── sa-token-demo-quick-login             // [示例] Sa-Token 集成 quick-login 模块 
		├── sa-token-demo-alone-redis             // [示例] Sa-Token 集成 alone-redis 模块
		├── sa-token-demo-thymeleaf               // [示例] Sa-Token 集成 Thymeleaf 标签方言
		├── sa-token-demo-jwt                     // [示例] Sa-Token 集成 jwt 登录认证 
		├── sa-token-demo-sso-server              // [示例] Sa-Token 集成 SSO单点登录-Server认证中心
		├── sa-token-demo-sso1-client             // [示例] Sa-Token 集成 SSO单点登录-模式一 应用端 
		├── sa-token-demo-sso2-client             // [示例] Sa-Token 集成 SSO单点登录-模式二 应用端
		├── sa-token-demo-sso3-client             // [示例] Sa-Token 集成 SSO单点登录-模式三 应用端
		├── sa-token-demo-sso-server-h5           // [示例] Sa-Token 集成 SSO单点登录-Server认证中心 (前后端分离)
		├── sa-token-demo-sso-client-h5           // [示例] Sa-Token 集成 SSO单点登录-client应用端 (前后端分离)
		├── sa-token-demo-oauth2-server           // [示例] Sa-Token 集成 OAuth2.0 (服务端)
		├── sa-token-demo-oauth2-client           // [示例] Sa-Token 集成 OAuth2.0 (客户端)
		├── sa-token-demo-websocket               // [示例] Sa-Token 集成 Web-Socket 鉴权示例
		├── sa-token-demo-websocket-spring        // [示例] Sa-Token 集成 Web-Socket（Spring封装版） 鉴权示例
	├── sa-token-test                         // [测试] Sa-Token 单元测试合集
		├── sa-token-core-test                    // [测试] Sa-Token Core核心包单元测试
		├── sa-token-springboot-test              // [测试] Sa-Token SpringBoot 整合测试
		├── sa-token-springboot-integrate-test    // [测试] Sa-Token SpringBoot 整合客户端测试
		├── sa-token-jwt-test                     // [测试] Sa-Token jwt 整合测试
	├── sa-token-doc                          // [文档] Sa-Token 开发文档 
	├──pom.xml                                // [依赖] 顶级pom文件 
```




## jar包下载
[点击下载：sa-token-1.6.0.jar](https://oss.dev33.cn/sa-token/sa-token-1.6.0.jar)

(注意：当前仅提供`v1.6.0`版本jar包下载，更多版本请前往maven中央仓库获取，[直达链接](https://search.maven.org/search?q=sa-token))



