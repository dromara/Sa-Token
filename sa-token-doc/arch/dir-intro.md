# 仓库目录介绍

--- 

### 1、仓库根目录介绍：

``` js
── sa-token
	├── sa-token-core                         // [核心] Sa-Token 核心模块
	├── sa-token-dependencies                 // [依赖] Sa-Token 依赖版本信息
	├── sa-token-special-dependencies         // [依赖] Sa-Token 特殊依赖（SpringBoot2/3/4 版本隔离）
	├── sa-token-bom                          // [核心] Sa-Token bom 包
	├── sa-token-starter                      // [整合] Sa-Token 与其它框架整合
	├── sa-token-plugin                       // [插件] Sa-Token 插件合集
	├── sa-token-demo                         // [示例] Sa-Token 示例合集
	├── sa-token-test                         // [测试] Sa-Token 单元测试合集
	├── sa-token-doc                          // [文档] Sa-Token 开发文档 
	├── MEMO                                  // [备忘] 内部备忘录、开发记录
	├── pom.xml                               // [依赖] 顶级pom文件 
	├── LICENSE                               // 开源协议
	├── mvn clean.bat                         // 一键 mvn clean 核心包+所有示例包
	├── mvn test.bat                          // 一键单元测试 
	├── preview-doc.bat                       // 一键预览开发文档
	├── README.md                             // 仓库自述文件 
```


### 2、所有目录详细介绍：

``` js
── sa-token
	├── sa-token-core                         // [核心] Sa-Token 核心模块
	├── sa-token-dependencies                 // [依赖] Sa-Token 依赖版本信息
	├── sa-token-special-dependencies         // [依赖] Sa-Token 特殊依赖（SpringBoot2/3/4 版本隔离）
	├── sa-token-bom                          // [核心] Sa-Token bom 包
	├── sa-token-starter                      // [整合] Sa-Token 与其它框架整合
		├── sa-token-servlet                      // [整合] Sa-Token 整合 Servlet 容器实现类包
		├── sa-token-jakarta-servlet              // [整合] Sa-Token 整合 Jakarta-Servlet 容器实现类包
		├── sa-token-spring-boot-webmvc-reactor-v2v3v4-common  // [整合] Sa-Token SpringBoot WebMvc+Reactor 公共包 (2/3/4)
		├── sa-token-spring-boot-reactor-v2v3v4-common         // [整合] Sa-Token SpringBoot Reactor 公共包 (2/3/4)
		├── sa-token-spring-boot-starter                       // [整合] Sa-Token 整合 SpringBoot2 快速集成 
		├── sa-token-spring-boot-webmvc-v3v4-common            // [整合] Sa-Token SpringBoot WebMvc 公共包 (3/4)
		├── sa-token-spring-boot3-starter         // [整合] Sa-Token 整合 SpringBoot3 快速集成 
		├── sa-token-spring-boot4-starter         // [整合] Sa-Token 整合 SpringBoot4 快速集成 
		├── sa-token-reactor-spring-boot-starter  // [整合] Sa-Token 整合 SpringBoot2 Reactor 响应式编程 快速集成 
		├── sa-token-reactor-spring-boot3-starter // [整合] Sa-Token 整合 SpringBoot3 Reactor 响应式编程 快速集成 
		├── sa-token-reactor-spring-boot4-starter // [整合] Sa-Token 整合 SpringBoot4 Reactor 响应式编程 快速集成 
		├── sa-token-solon-plugin                 // [整合] Sa-Token 整合 Solon 快速集成 
		├── sa-token-jfinal-plugin                // [整合] Sa-Token 整合 JFinal 快速集成 
		├── sa-token-jboot-plugin                 // [整合] Sa-Token 整合 jboot 快速集成 
		├── sa-token-loveqq-boot-starter          // [整合] Sa-Token 整合 LoveQQ-Boot 快速集成 
	├── sa-token-plugin                       // [插件] Sa-Token 插件合集
		├── sa-token-jackson                      // [插件] Sa-Token 整合 Jackson (json序列化插件) 
		├── sa-token-jackson3                     // [插件] Sa-Token 整合 Jackson3 (json序列化插件) 
		├── sa-token-fastjson                     // [插件] Sa-Token 整合 Fastjson (json序列化插件) 
		├── sa-token-fastjson2                    // [插件] Sa-Token 整合 Fastjson2 (json序列化插件) 
		├── sa-token-snack3                       // [插件] Sa-Token 整合 Snack3 (json序列化插件) 
		├── sa-token-snack4                       // [插件] Sa-Token 整合 Snack4 (json序列化插件) 
		├── sa-token-hutool-timed-cache           // [插件] Sa-Token 整合 Hutool 缓存组件 Timed-Cache（基于内存） (数据缓存插件) 
		├── sa-token-caffeine                     // [插件] Sa-Token 整合 Caffeine 缓存组件（基于内存） (数据缓存插件) 
		├── sa-token-thymeleaf                    // [插件] Sa-Token 整合 Thymeleaf (自定义标签方言) 
		├── sa-token-freemarker                   // [插件] Sa-Token 整合 Freemarker (自定义标签方言) 
		├── sa-token-dubbo                        // [插件] Sa-Token 整合 Dubbo (RPC 调用鉴权、状态传递) 
		├── sa-token-dubbo3                       // [插件] Sa-Token 整合 Dubbo3 (RPC 调用鉴权、状态传递) 
		├── sa-token-temp-jwt                     // [插件] Sa-Token 整合 jjwt (临时 Token) 
		├── sa-token-jwt                          // [插件] Sa-Token 整合 jjwt (JWT 登录认证) 
		├── sa-token-sso                          // [插件] Sa-Token 实现 SSO 单点登录
		├── sa-token-oauth2                       // [插件] Sa-Token 实现 OAuth2.0 认证
		├── sa-token-apikey                       // [插件] Sa-Token 实现 API Key 认证
		├── sa-token-sign                         // [插件] Sa-Token 实现 API 参数签名  
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
		├── sa-token-forest                       // [插件] Sa-Token 整合 Forest，http 请求处理器 
		├── sa-token-okhttps                      // [插件] Sa-Token 整合 OkHttps，http 请求处理器 
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
		├── sa-token-demo-springboot4-redis       // [示例] Sa-Token 整合 SpringBoot4 整合 Redis 
		├── sa-token-demo-springboot-low-version  // [示例] Sa-Token 整合 SpringBoot2 低版本
		├── sa-token-demo-springboot-redis        // [示例] Sa-Token 整合 SpringBoot 整合 Redis 
		├── sa-token-demo-springboot-redisson     // [示例] Sa-Token 整合 SpringBoot 整合 redisson 
		├── sa-token-demo-sse                     // [示例] 在 SSE 中使用 Sa-Token 
		├── sa-token-demo-ssm                     // [示例] 在 SSM 中使用 Sa-Token 
		├── sa-token-demo-sso                     // [示例] Sa-Token 集成 SSO 单点登录
			├── sa-token-demo-sso-server              // [示例] Sa-Token 集成 SSO单点登录-Server认证中心
			├── sa-token-demo-sso1-client             // [示例] Sa-Token 集成 SSO单点登录-模式一 应用端 (同域、同Redis)
			├── sa-token-demo-sso2-client             // [示例] Sa-Token 集成 SSO单点登录-模式二 应用端 (跨域、同Redis)
			├── sa-token-demo-sso3-client             // [示例] Sa-Token 集成 SSO单点登录-模式三 应用端 (跨域、跨Redis)
			├── sa-token-demo-sso3-client-nosdk       // [示例] Sa-Token 集成 SSO单点登录-模式三 应用端 (不使用sdk，纯手动对接)
			├── sa-token-demo-sso3-client-resdk       // [示例] Sa-Token 集成 SSO单点登录-模式三 应用端 (ReSdk 模式，重写部分方法对接任意技术栈)
			├── sa-token-demo-sso3-client-anon        // [示例] Sa-Token 集成 SSO单点登录-模式三 应用端 (匿名应用接入示例)
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
		├── sa-token-demo-webflux-springboot3    // [示例] Sa-Token 整合 WebFlux （SpringBoot3）
		├── sa-token-demo-webflux-springboot4    // [示例] Sa-Token 整合 WebFlux （SpringBoot4）
		├── sa-token-demo-websocket               // [示例] Sa-Token 集成 Web-Socket 鉴权示例
		├── sa-token-demo-websocket-spring        // [示例] Sa-Token 集成 Web-Socket（Spring封装版） 鉴权示例
		├── sa-token-demo-loveqq-boot             // [示例] Sa-Token 集成 LoveQQ-Boot
		├── pom.xml                               // 示例 pom 文件，用于帮助在 idea 中一键导入所有 demo 
	├── sa-token-test                         // [测试] Sa-Token 单元测试合集
		├── sa-token-easy-test                  // [测试] Sa-Token 简易测试
		├── sa-token-springboot-test            // [测试] Sa-Token SpringBoot 整合测试
		├── sa-token-jwt-test                   // [测试] Sa-Token jwt 整合测试
		├── sa-token-temp-jwt-test              // [测试] Sa-Token temp-jwt 整合测试
		├── sa-token-json-test                  // [测试] Sa-Token json 序列化测试
		├── sa-token-jackson3-test              // [测试] Sa-Token Jackson3 整合测试
		├── sa-token-serializer-test            // [测试] Sa-Token 序列化测试
	├── sa-token-doc                          // [文档] Sa-Token 开发文档 
	├── MEMO                                  // [备忘] 内部备忘录、开发记录
	├── pom.xml                               // [依赖] 顶级pom文件 
	├── LICENSE                               // 开源协议
	├── mvn clean.bat                         // 一键 mvn clean 核心包+所有示例包
	├── mvn test.bat                          // 一键单元测试 
	├── preview-doc.bat                       // 一键预览开发文档
	├── README.md                             // 仓库自述文件 
```



其它（[sa-tokens](https://gitee.com/sa-tokens) 组织下相关仓库）：

- [Awesome-Sa-Token](https://gitee.com/sa-tokens/awesome-sa-token)：集成 Sa-Token 的优秀开源案例收集。
- [sa-token-rust](https://gitee.com/sa-tokens/sa-token-rust)：Sa-Token 的 Rust 版本，轻量级 Rust 权限认证框架。
- [sa-token-go](https://gitee.com/sa-tokens/sa-token-go)：Sa-Token 的 Go 版本，轻量级 Go 权限认证框架。
- [Sa-Token-Study](https://gitee.com/sa-tokens/Sa-Token-Study)：Sa-Token 涉及技术点学习笔记与实战。
- [Sa-Token-Login-Demos](https://gitee.com/sa-tokens/Sa-Token-Login-Demos)：各种登录方式示例集合，一站式学习 Sa-Token 登录认证。
- [sa-token-doc-big-file](https://gitee.com/sa-tokens/sa-token-doc-big-file)：sa-token-doc 文档中的图片资源文件。
- [sa-token-three-plugin](https://gitee.com/sa-tokens/sa-token-three-plugin)：Sa-Token 第三方插件合集。
- [sa-token-demo-cross](https://gitee.com/sa-tokens/sa-token-demo-cross)：Sa-Token 处理跨域场景示例。
- [auth-framework-function-test](https://gitee.com/sa-tokens/auth-framework-function-test)：Java 权限认证框架功能 测试 / 对比 / 迁移。
