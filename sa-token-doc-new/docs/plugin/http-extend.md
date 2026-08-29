# HTTP 请求扩展

---

## HTTP 请求插件大全

Sa-Token 在 SSO 模式三（`isHttp`）、单点注销、消息推送等场景需要发起 HTTP 请求。框架将 HTTP 调用逻辑抽象到 `SaHttpTemplate` 接口，
开发者只需引入对应的 HTTP 插件依赖，框架会通过 SPI 机制自动完成注入，接口签名：[SaHttpTemplate.java](https://gitee.com/dromara/sa-token/blob/master/sa-token-core/src/main/java/cn/dev33/satoken/http/SaHttpTemplate.java)

框架已提供的 HTTP 请求插件包括：

- **sa-token-forest**：整合 Forest，适用于 Spring / 非 Spring 环境（SSO 示例默认使用）。
- **sa-token-okhttps**：整合 OkHttps，适用于 Spring / 非 Spring 环境。
- **sa-token-rest-template**：整合 Spring `RestTemplate`，适用于 Spring Boot 2 / 3 / 4 环境。
- **sa-token-rest-client**：整合 Spring `RestClient`，适用于 Spring Boot 3.2+ / Spring Framework 6.1+ 环境。

> 默认实现：
>
> - `SaHttpTemplateDefaultImpl` 未实现 HTTP 调用，未引入插件时会抛出「本地系统没有配置 http 请求处理器」（错误码 30010）。
> - 同一 classpath 只应存在一个 HTTP 插件；多插件并存时后加载的会覆盖 `SaHttpTemplate`。

:::tabs
== Forest

``` xml
<!-- Sa-Token 整合 Forest -->
<dependency>
	<groupId>cn.dev33</groupId>
	<artifactId>sa-token-forest</artifactId>
	<version>${sa.top.version}</version>
</dependency>
```
Gradle 参考：`implementation 'cn.dev33:sa-token-forest:${sa.top.version}'`

== OkHttps

``` xml
<!-- Sa-Token 整合 OkHttps -->
<dependency>
	<groupId>cn.dev33</groupId>
	<artifactId>sa-token-okhttps</artifactId>
	<version>${sa.top.version}</version>
</dependency>
```
Gradle 参考：`implementation 'cn.dev33:sa-token-okhttps:${sa.top.version}'`

== RestTemplate

``` xml
<!-- Sa-Token 整合 RestTemplate（需 Spring Boot 环境，项目已引入 spring-web） -->
<dependency>
	<groupId>cn.dev33</groupId>
	<artifactId>sa-token-rest-template</artifactId>
	<version>${sa.top.version}</version>
</dependency>
```
Gradle 参考：`implementation 'cn.dev33:sa-token-rest-template:${sa.top.version}'`

== RestClient

``` xml
<!-- Sa-Token 整合 RestClient（需 Spring Boot 3.2+ / Spring Framework 6.1+） -->
<dependency>
	<groupId>cn.dev33</groupId>
	<artifactId>sa-token-rest-client</artifactId>
	<version>${sa.top.version}</version>
</dependency>
```
Gradle 参考：`implementation 'cn.dev33:sa-token-rest-client:${sa.top.version}'`

:::



## 选型建议

| 运行环境 | 推荐插件 |
| :-------- | :-------- |
| 非 Spring / Solon 等 | `sa-token-forest` 或 `sa-token-okhttps` |
| Spring Boot 2，或 Boot 3.0–3.1 | `sa-token-rest-template` 或 `sa-token-forest` |
| Spring Boot 3.2+ / 4 | **优先** `sa-token-rest-client`，或 `sa-token-rest-template`（兼容旧代码） |

- `RestTemplate` 在 Spring Boot 4 中已标记为 deprecated，但仍可使用。
- `RestClient` 需要 Spring Framework 6.1+，无法在 Spring Boot 2 或 Boot 3.0–3.1 中使用。


## 使用场景

- **SSO 模式三**：ticket 校验、单点注销、消息推送（参考 [模式三](/sso/sso-type3)、[消息推送](/sso/message-push)）
- **SSO-Server 模式三**：Server 端需配置 HTTP 请求处理器（参考 [搭建 SSO-Server](/sso/sso-server)）


## 自定义 Http 请求处理器

**方式一（推荐）**：引入官方插件，SPI 自动注册。

**方式二**：手动注册（参考 [插件开发指南 - 更改全局组件](/fun/plugin-dev#方式2：更改全局组件实现)）：

``` java
// 使用官方插件实现
SaManager.setSaHttpTemplate(new SaHttpTemplateForForest());

// 或自定义实现 SaHttpTemplate 接口
SaManager.setSaHttpTemplate(new SaHttpTemplate() {
	@Override
	public String get(String url) {
		// ...
	}
	@Override
	public String postByFormData(String url, Map<String, Object> params) {
		// ...
	}
});
```

在 Spring Boot 环境中，也可通过注入 `SaHttpTemplate` Bean 覆盖默认实现（参考 [全局类、方法](/more/common-action)）。


## 错误码 30010

若运行时抛出「本地系统没有配置 http 请求处理器」，说明当前 classpath 未引入任何 HTTP 插件，且未手动设置 `SaHttpTemplate`。
请按上文选型建议引入一个插件，或参考 [异常码表 - 30010](/fun/exception-code#sa-token-sso-单点登录相关：) 排查。
