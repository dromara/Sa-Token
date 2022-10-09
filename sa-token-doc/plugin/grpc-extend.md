# 和 grpc 集成

本插件的作用是让 Sa-Token 和 grpc 做一个整合。

--- 

和dubbo插件一样，解决了以下问题

1. 在 [ 被调用端 ] 安全的调用 Sa-Token 相关 API。
2. 在 [ 调用端 ] 登录的会话，其登录状态可以自动传递到 [ 被调用端 ] ；在 [ 被调用端 ] 登录的会话，其登录状态可以自动回传到 [ 调用端 ]
3. id-token 安全校验

---
和dubbo插件一样，具有以下限制：

1. [ 调用端 ] 与 [ 被调用端 ] 的 `SaStorage` 数据无法互通。
2. [ 被调用端 ] 执行的 `SaResponse.setHeader()`、`setStatus()` 等代码无效。

### 引入插件
需要springboot环境，添加依赖（调用端和被调用端都需要引入）：

``` xml
<!-- Sa-Token 整合 grpc -->
<dependency>
	<groupId>cn.dev33</groupId>
	<artifactId>sa-token-context-grpc</artifactId>
	<version>${sa.top.version}</version>
</dependency>
```

---
### 开启id-token校验：
直接在 `application.yml` 配置即可：

``` yml
sa-token: 
	# 打开 RPC 调用鉴权 
	check-id-token: true
```