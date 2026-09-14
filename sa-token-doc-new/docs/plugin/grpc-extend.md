---
title: "Sa-Token 和 gRPC 集成"
keywords: "Sa-Token,sa-token,satoken,Sa-Token文档,和 gRPC 集成,插件"
description: "Sa-Token 整合 gRPC：RPC 链路透传 Token，被调用端恢复 Sa-Token 上下文环境。"
---

# 和 grpc 集成

本插件的作用是让 Sa-Token 和 grpc 做一个整合。

--- 

和dubbo插件一样，解决了以下问题

1. 在 [ 被调用端 ] 安全的调用 Sa-Token 相关 API。
2. 在 [ 调用端 ] 登录的会话，其登录状态可以自动传递到 [ 被调用端 ] ；在 [ 被调用端 ] 登录的会话，其登录状态可以自动回传到 [ 调用端 ]
3. Same-Token 安全校验

---
和dubbo插件一样，具有以下限制：

1. [ 调用端 ] 与 [ 被调用端 ] 的 `SaStorage` 数据无法互通。
2. [ 被调用端 ] 执行的 `SaResponse.setHeader()`、`setStatus()` 等代码无效。

### 引入插件
需要springboot环境，添加依赖（调用端和被调用端都需要引入）：

:::tabs
== Maven 方式

``` xml 
<!-- Sa-Token 整合 grpc -->
<dependency>
	<groupId>cn.dev33</groupId>
	<artifactId>sa-token-grpc</artifactId>
	<version>${sa.top.version}</version>
</dependency>
```

== Gradle 方式

``` gradle
// Sa-Token 整合 grpc
implementation 'cn.dev33:sa-token-grpc:${sa.top.version}'
```

:::



---
### 开启 Same-Token 校验：
直接在 `application.yml` 配置即可：

:::tabs
== yaml 风格

``` yaml
sa-token: 
	# 打开 RPC 调用鉴权 
	check-same-token: true
```

== properties 风格

``` properties
# 打开 RPC 调用鉴权 
sa-token.check-same-token=true
```

:::
