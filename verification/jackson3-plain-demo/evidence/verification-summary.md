# Jackson3 Plain 真实链路验证

验证时间：2026-09-21（Asia/Shanghai）

## 运行方式

在 `jackson3-plain-demo` 目录执行：

```powershell
$env:REDIS_PASSWORD = '<local-password>'
mvn spring-boot:run
```

再依次请求 `POST /verify/write` 与 `GET /verify/read`，或打开 `http://127.0.0.1:18080` 点击页面按钮。

## 已保存证据

- `application.log`：Spring Boot 4 启动日志，包含 SPI 注入的 JSON 模板及 Session 类型。
- `write-response.json`：真实 Redis 写入后的原始 Session JSON；`containsClassMarker` 为 `false`。
- `read-response.json`：从 Redis 读取后，`Profile` 和 `Map` 的 `getModel(key, Class)` 结果。

## 环境兼容性说明

本机 Redis 支持 RESP2，不支持 `HELLO 3`。为让真实 Redis 验证不受 Spring Data Redis 4 的 RESP3 协商影响，演示应用使用了仅用于验证的 `RedisSessionStore` RESP2 适配器。它执行的链路仍为：

`SaSession -> SaJsonTemplateForJackson3Plain -> Redis -> SaSessionForJackson3PlainCustomized -> getModel(key, Class)`。

Redis 密码通过进程环境变量读取，未保存在源码、日志或本目录证据中。

## 内置示例的范围

仓库现有的 `sso`、`oauth2`、`apikey` 示例均基于 Spring Boot 2.5.14；其中 OAuth2 示例明确为 Java 8。Jackson 3 插件要求 Java 17 及以上，不能在不迁移这些示例到 Boot 4/Java 17 的前提下诚实地宣称“加载并运行本插件”。本次真实运行使用独立的 Boot 4/Java 21 小项目验证插件；三类旧示例的迁移与插件运行验证应作为后续单独改动处理。
