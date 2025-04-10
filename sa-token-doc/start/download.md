# 其它环境引入 Sa-Token 的示例

目前已实现的对接框架综合

------

## Maven依赖 
根据不同基础框架引入不同的 Sa-Token 依赖：

<!------------------------------ tabs:start ------------------------------>

<!------------- tab:SpringBoot环境 （ServletAPI）  ------------->
如果你使用的框架基于 ServletAPI 构建（ SpringMVC、SpringBoot等 ），请引入此包
``` xml
<!-- Sa-Token 权限认证, 在线文档：https://sa-token.cc -->
<dependency>
	<groupId>cn.dev33</groupId>
	<artifactId>sa-token-spring-boot-starter</artifactId>
	<version>${sa.top.version}</version>
</dependency>
```
注：如果你使用的 `SpringBoot 3.x`，只需要将 `sa-token-spring-boot-starter` 修改为 `sa-token-spring-boot3-starter` 即可。

<!------------- tab:WebFlux环境 （Reactor）  ------------->
注：如果你使用的框架基于 Reactor 模型构建（WebFlux、SpringCloud Gateway 等），请引入此包
``` xml
<!-- Sa-Token 权限认证（Reactor响应式集成）, 在线文档：https://sa-token.cc -->
<dependency>
	<groupId>cn.dev33</groupId>
	<artifactId>sa-token-reactor-spring-boot-starter</artifactId>
	<version>${sa.top.version}</version>
</dependency>
```
注：如果你使用的 `SpringBoot 3.x`，只需要将 `sa-token-reactor-spring-boot-starter` 修改为 `sa-token-reactor-spring-boot3-starter` 即可。

<!------------- tab:Solon 集成  ------------->
参考：[Solon官网](https://solon.noear.org/)
``` xml
<!-- Sa-Token 整合 Solon, 在线文档：https://sa-token.cc -->
<dependency>
	<groupId>cn.dev33</groupId>
	<artifactId>sa-token-solon-plugin</artifactId>
	<version>${sa.top.version}</version>
</dependency>
```

<!------------- tab:JFinal 集成  ------------->
参考：[JFinal官网](https://jfinal.com/)
``` xml
<!-- Sa-Token 整合 JFinal, 在线文档：https://sa-token.cc -->
<dependency>
	<groupId>cn.dev33</groupId>
	<artifactId>sa-token-jfinal-plugin</artifactId>
	<version>${sa.top.version}</version>
</dependency>
```

<!------------- tab:Jboot 集成  ------------->
参考：[Jboot官网](http://www.jboot.com.cn/)
``` xml
<!-- Sa-Token 整合 Jboot, 在线文档：https://sa-token.cc -->
<dependency>
	<groupId>cn.dev33</groupId>
	<artifactId>sa-token-jboot-plugin</artifactId>
	<version>${sa.top.version}</version>
</dependency>
```

<!------------- tab:Quarkus 集成  ------------->
参考：[quarkus-sa-token](https://github.com/quarkiverse/quarkus-sa-token)
``` xml
<!-- Sa-Token 整合 Quarkus, 在线文档：https://sa-token.cc -->
<dependency>
	<groupId>io.quarkiverse.satoken</groupId>
	<artifactId>quarkus-satoken-resteasy</artifactId>
	<version>1.30.0</version>
</dependency>
```

<!------------- tab:裸Servlet容器环境   ------------->
注：如果你的项目没有使用Spring，但是Web框架是基于 ServletAPI 规范的，可以引入此包
``` xml
<!-- Sa-Token 权限认证（ServletAPI规范）, 在线文档：https://sa-token.cc -->
<dependency>
	<groupId>cn.dev33</groupId>
	<artifactId>sa-token-servlet</artifactId>
	<version>${sa.top.version}</version>
</dependency>
```
引入此依赖需要自定义 SaTokenContext 实现，参考：[自定义 SaTokenContext 指南](/fun/sa-token-context)

<!------------- tab:其它   ------------->
注：如果你的项目既没有使用 SpringMVC、WebFlux，也不是基于 ServletAPI 规范，那么可以引入core核心包
``` xml
<!-- Sa-Token 权限认证（core核心包）, 在线文档：https://sa-token.cc -->
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
<!-- tab:SpringBoot环境 （ServletAPI）  -->
``` gradle
implementation 'cn.dev33:sa-token-spring-boot-starter:${sa.top.version}'
```

<!-- tab:WebFlux环境 （Reactor）  -->
``` gradle
implementation 'cn.dev33:sa-token-reactor-spring-boot-starter:${sa.top.version}'
```

<!-- tab:Solon 集成  -->
``` gradle
implementation 'cn.dev33:sa-token-solon-plugin:${sa.top.version}'
```

<!-- tab:JFinal 集成  -->
``` gradle
implementation 'cn.dev33:sa-token-jfinal-plugin:${sa.top.version}'
```

<!-- tab:Jboot 集成  -->
``` gradle
implementation 'cn.dev33:sa-token-jboot-plugin:${sa.top.version}'
```

<!-- tab:Quarkus 集成  -->
``` gradle
implementation 'io.quarkiverse.satoken:quarkus-satoken-resteasy:1.30.0'
```

<!-- tab:裸Servlet容器环境  -->
``` gradle
implementation 'cn.dev33:sa-token-servlet:${sa.top.version}'
```

<!-- tab:其它  -->
``` gradle
implementation 'cn.dev33:sa-token-core:${sa.top.version}'
```

<!-- tabs:end -->

注：JDK版本：`v1.8+`，SpringBoot：`建议2.0以上`


## 测试版
更多内测版本了解：[Sa-Token 最新版本](https://gitee.com/dromara/sa-token/blob/dev/sa-token-doc/start/new-version.md)

Maven依赖一直无法加载成功？[参考解决方案](https://sa-token.cc/doc.html#/start/maven-pull)

## jar包下载
[点击下载：sa-token-1.6.0.jar](https://oss.dev33.cn/sa-token/sa-token-1.6.0.jar)

注：当前仅提供 `v1.6.0` 版本jar包下载，更多版本请前往 maven 中央仓库获取，[直达链接](https://search.maven.org/search?q=sa-token)


## 获取源码
如果你想深入了解 Sa-Token，你可以通过`Gitee`或者`GitHub`来获取源码 （**学习测试请拉取 master 分支**，dev为正在开发的分支，有很多特性并不稳定）
- **Gitee**地址：[https://gitee.com/dromara/sa-token](https://gitee.com/dromara/sa-token)
- **GitHub**地址：[https://github.com/dromara/sa-token](https://github.com/dromara/sa-token)
- 开源不易，求鼓励，点个`star`吧
- 源码目录介绍: 

``` js
── sa-token
	├── sa-token-core                         // [核心] Sa-Token 核心模块
	├── sa-token-dependencies                 // [依赖] Sa-Token 依赖版本信息
	├── sa-token-bom                         // [核心] Sa-Token bom 包
	├── sa-token-starter                      // [整合] Sa-Token 与其它框架整合
		├── sa-token-servlet                      // [整合] Sa-Token 整合 Servlet 容器实现类包
		├── sa-token-spring-boot-starter          // [整合] Sa-Token 整合 SpringBoot2 快速集成 
		├── sa-token-reactor-spring-boot-starter  // [整合] Sa-Token 整合 SpringBoot2 Reactor 响应式编程 快速集成 
		├── sa-token-jakarta-servlet              // [整合] Sa-Token 整合 Jakarta-Servlet 容器实现类包
		├── sa-token-spring-boot3-starter         // [整合] Sa-Token 整合 SpringBoot3 快速集成 
		├── sa-token-reactor-spring-boot3-starter // [整合] Sa-Token 整合 SpringBoot3 Reactor 响应式编程 快速集成 
		├── sa-token-spring-boot-autoconfig       // [整合] Sa-Token 整合 SpringBoot 自动配置包
		├── sa-token-solon-plugin                 // [整合] Sa-Token 整合 Solon 快速集成 
		├── sa-token-jfinal-plugin                // [整合] Sa-Token 整合 JFinal 快速集成 
		├── sa-token-jboot-plugin                 // [整合] Sa-Token 整合 jboot 快速集成 
	├── sa-token-plugin                       // [插件] Sa-Token 插件合集
		├── sa-token-jackson                      // [插件] Sa-Token 整合 Jackson (json序列化插件) 
		├── sa-token-fastjson                     // [插件] Sa-Token 整合 Fastjson (json序列化插件) 
		├── sa-token-fastjson2                    // [插件] Sa-Token 整合 Fastjson (json序列化插件) 
		├── sa-token-snack3                       // [插件] Sa-Token 整合 Snack3 (json序列化插件) 
		├── sa-token-hutool-timed-cache           // [插件] Sa-Token 整合 Hutool 缓存组件 Timed-Cache（基于内存） (数据缓存插件) 
		├── sa-token-caffeine                     // [插件] Sa-Token 整合 Caffeine 缓存组件（基于内存） (数据缓存插件) 
		├── sa-token-thymeleaf                    // [插件] Sa-Token 整合 Thymeleaf (自定义标签方言) 
		├── sa-token-freemarker                   // [插件] Sa-Token 整合 Freemarker (自定义标签方言) 
		├── sa-token-dubbo                        // [插件] Sa-Token 整合 Dubbo (RPC 调用鉴权、状态传递) 
		├── sa-token-dubbo3                       // [插件] Sa-Token 整合 Dubbo3 (RPC 调用鉴权、状态传递) 
		├── sa-token-temp-jwt                     // [插件] Sa-Token 整合 jjwt (临时 Token) 
		├── sa-token-sso                          // [插件] Sa-Token 实现 SSO 单点登录
		├── sa-token-oauth2                       // [插件] Sa-Token 实现 OAuth2.0 认证
		├── sa-token-redisson                     // [插件] Sa-Token 整合 Redisson (数据缓存插件) 
		├── sa-token-redisx                       // [插件] Sa-Token 整合 Redisx (数据缓存插件) 
		├── sa-token-serializer-features          // [插件] Sa-Token 序列化实现扩展 
		├── sa-token-redis-template               // [插件] Sa-Token 整合 RedisTemplate (数据缓存插件) 
		├── sa-token-redis-template-jdk-serializer // [插件] Sa-Token 整合 RedisTemplate - 使用 jdk 序列化算法 (数据缓存插件) 
		├── sa-token-redis-jackson                // [插件] Sa-Token 整合 RedisTemplate - 使用 Jackson 序列化算法 (数据缓存插件) 
		├── sa-token-alone-redis                  // [插件] Sa-Token 独立 Redis 插件，实现 [ 权限缓存与业务缓存分离 ]
		├── sa-token-spring-aop                   // [插件] Sa-Token 整合 SpringAOP 注解鉴权
		├── sa-token-spring-el                    // [插件] Sa-Token 实现 SpringEL 表达式注解鉴权
		├── sa-token-grpc                         // [插件] Sa-Token 整合 gRPC (RPC 调用鉴权、状态传递) 
		├── sa-token-quick-login                  // [插件] Sa-Token 快速注入登录页插件 
		├── sa-token-redisson-spring-boot-starter // [插件] Sa-Token 整合 Redisson - SpringBoot 自动配置包 (数据缓存插件) 
	├── sa-token-demo                         // [示例] Sa-Token 示例合集
		├── sa-token-demo-alone-redis             // [示例] Sa-Token 集成 alone-redis 模块
		├── sa-token-demo-alone-redis-cluster     // [示例] Sa-Token 集成 alone-redis 模块、集群模式
		├── sa-token-demo-apikey                  // [示例] Sa-Token API Key 模块示例
		├── sa-token-demo-async                   // [示例] Sa-Token 异步场景示例
		├── sa-token-demo-beetl                   // [示例] Sa-Token 集成 beetl 示例
		├── sa-token-demo-bom-import              // [示例] Sa-Token bom 包导入示例 
		├── sa-token-demo-case                    // [示例] Sa-Token 各模块示例
		├── sa-token-demo-device-lock             // [示例] Sa-Token 设备锁登录示例 - 后端
		├── sa-token-demo-device-lock-h5          // [示例] Sa-Token 设备锁登录示例 - 前端 
		├── sa-token-demo-dubbo                   // [示例] Sa-Token 集成 dubbo
			├── sa-token-demo-dubbo-consumer          // [示例] Sa-Token 集成 dubbo 鉴权，消费端（调用端）
			├── sa-token-demo-dubbo-provider          // [示例] Sa-Token 集成 dubbo 鉴权，生产端（被调用端）
			├── sa-token-demo-dubbo3-consumer         // [示例] Sa-Token 集成 dubbo3 鉴权，消费端（调用端）
			├── sa-token-demo-dubbo3-provider         // [示例] Sa-Token 集成 dubbo3 鉴权，生产端（被调用端）
		├── sa-token-demo-freemarker              // [示例] Sa-Token 集成 Freemarker 标签方言
		├── sa-token-demo-grpc                    // [示例] Sa-Token 集成 grpc 鉴权
			├── client                                // [示例] Sa-Token 集成 grpc 鉴权，client 端
			├── server                                // [示例] Sa-Token 集成 grpc 鉴权，server 端
		├── sa-token-demo-hutool-timed-cache      // [示例] Sa-Token 集成 hutool timed-cache
		├── sa-token-demo-caffeine                // [示例] Sa-Token 集成 Caffeine
		├── sa-token-demo-jwt                     // [示例] Sa-Token 集成 jwt 登录认证 
		├── sa-token-demo-oauth2                  // [示例] Sa-Token 集成 OAuth2.0
			├── sa-token-demo-oauth2-client           // [示例] Sa-Token 集成 OAuth2.0 (客户端)
			├── sa-token-demo-oauth2-client-h5        // [示例] Sa-Token OAuth2 前端测试页 
			├── sa-token-demo-oauth2-server           // [示例] Sa-Token 集成 OAuth2.0 (服务端)
			├── sa-token-demo-oauth2-server-h5        // [示例] Sa-Token 集成 OAuth2.0 (服务端 - 前后台分离示例)
		├── sa-token-demo-quick-login             // [示例] Sa-Token 集成 quick-login 模块 
		├── sa-token-demo-quick-login-sb3         // [示例] Sa-Token 集成 quick-login 模块 (SpringBoot3)
		├── sa-token-demo-remember-me             // [示例] Sa-Token 实现 [ 记住我 ] 模式
			├── page_project                          // [示例] Sa-Token 实现 [ 记住我 ] 模式、前端页面
			├── sa-token-demo-remember-me-server      // [示例] Sa-Token 实现 [ 记住我 ] 模式、后端接口
		├── sa-token-demo-solon                   // [示例] Sa-Token 集成 Solon 
		├── sa-token-demo-solon-reisson           // [示例] Sa-Token 集成 Solon、Reisson
		├── sa-token-demo-springboot              // [示例] Sa-Token 整合 SpringBoot 
		├── sa-token-demo-springboot3-redis       // [示例] Sa-Token 整合 SpringBoot3 整合 Redis 
		├── sa-token-demo-springboot-low-version  // [示例] Sa-Token 整合 SpringBoot2 低版本
		├── sa-token-demo-springboot-redis        // [示例] Sa-Token 整合 SpringBoot 整合 Redis 
		├── sa-token-demo-springboot-redisson     // [示例] Sa-Token 整合 SpringBoot 整合 redisson 
		├── sa-token-demo-ssm                     // [示例] 在 SSM 中使用 Sa-Token 
		├── sa-token-demo-sso                     // [示例] Sa-Token 集成 SSO 单点登录
			├── sa-token-demo-sso-server              // [示例] Sa-Token 集成 SSO单点登录-Server认证中心
			├── sa-token-demo-sso1-client             // [示例] Sa-Token 集成 SSO单点登录-模式一 应用端 (同域、同Redis)
			├── sa-token-demo-sso2-client             // [示例] Sa-Token 集成 SSO单点登录-模式二 应用端 (跨域、同Redis)
			├── sa-token-demo-sso3-client             // [示例] Sa-Token 集成 SSO单点登录-模式三 应用端 (跨域、跨Redis)
			├── sa-token-demo-sso3-client-nosdk       // [示例] Sa-Token 集成 SSO单点登录-模式三 应用端 (不使用sdk，纯手动对接)
			├── sa-token-demo-sso3-client-test2       // [示例] Sa-Token 集成 SSO单点登录-模式三 应用端 (待 client 标识)
			├── sa-token-demo-sso-server-h5           // [示例] Sa-Token 集成 SSO单点登录-Server认证中心 (前后端分离)
			├── sa-token-demo-sso-client-h5           // [示例] Sa-Token 集成 SSO单点登录-client应用端 (前后端分离-原生h5 版本)
			├── sa-token-demo-sso-server-vue2         // [示例] Sa-Token 集成 SSO单点登录-client应用端 (前后端分离-Vue2 版本)
			├── sa-token-demo-sso-client-vue3         // [示例] Sa-Token 集成 SSO单点登录-client应用端 (前后端分离-Vue3 版本)
		├── sa-token-demo-sso-for-solon           // [示例] Sa-Token 集成 SSO 单点登录（Solon 版）
			├── sa-token-demo-sso-server-solon        // [示例] Sa-Token 集成 SSO单点登录-Server认证中心
			├── sa-token-demo-sso1-client-solon       // [示例] Sa-Token 集成 SSO单点登录-模式一 应用端 (同域、同Redis)
			├── sa-token-demo-sso2-client-solon       // [示例] Sa-Token 集成 SSO单点登录-模式二 应用端 (跨域、同Redis)
			├── sa-token-demo-sso3-client-solon       // [示例] Sa-Token 集成 SSO单点登录-模式三 应用端 (跨域、跨Redis)
		├── sa-token-demo-test                    // [示例] Sa-Token 整合测试项目
		├── sa-token-demo-thymeleaf               // [示例] Sa-Token 集成 Thymeleaf 标签方言
		├── sa-token-demo-webflux                 // [示例] Sa-Token 整合 WebFlux 
		├── sa-token-demo-webflux-springboot3     // [示例] Sa-Token 整合 WebFlux （SpringBoot3）
		├── sa-token-demo-websocket               // [示例] Sa-Token 集成 Web-Socket 鉴权示例
		├── sa-token-demo-websocket-spring        // [示例] Sa-Token 集成 Web-Socket（Spring封装版） 鉴权示例
		├── pom.xml                               // 示例 pom 文件，用于帮助在 idea 中一键导入所有 demo 
		
	├── sa-token-test                         // [测试] Sa-Token 单元测试合集
		├── sa-token-springboot-test              // [测试] Sa-Token SpringBoot 整合测试
		├── sa-token-jwt-test                     // [测试] Sa-Token jwt 整合测试
	├── sa-token-doc                          // [文档] Sa-Token 开发文档 
	├── pom.xml                               // [依赖] 顶级pom文件 
```

其它：

- [sa-token-demo-cross](https://gitee.com/sa-tokens/sa-token-demo-cross)：Sa-Token 处理跨域示例。
- [sa-token-three-plugin](https://gitee.com/sa-tokens/sa-token-three-plugin)：Sa-Token 第三方插件合集。
- [sa-token-study](https://gitee.com/sa-tokens/sa-token-study)：Sa-Token 涉及知识点学习。



## 运行示例

- 1、下载代码（学习测试用 master 分支）。
- 2、从根目录导入项目。
- 3、选择相应的示例添加为 Maven 项目，打开 XxxApplication.java 运行。

![运行示例](https://oss.dev33.cn/sa-token/doc/import-demo-run.png  's-w-sh')