# sa-token-demo-json-typing-security

**长期保留**的 Jackson DefaultTyping 反序列化白名单回归 Demo。  
与 `sa-token-demo-bug-reproduce`（临时 Issue 沙盒）分离，避免新漏洞复现时覆盖本用例。

## 验证内容

模拟 Redis 中被写入带 `@class` 的恶意 JSON，经 `SaTokenDao.getObject()` 反序列化时：

- **期望**：`SaJsonStrategy` 白名单拦截，抛出 `SaJsonConvertException`（消息含 `无法反序列化的类型：…`）
- **失败**：未授权类型被实例化（PoC gadget 会尝试弹出计算器）

关联实现：`SaJsonStrategy`、`SaJsonTemplateForJackson`

## 前提

- 本地 Redis 已启动（默认 `127.0.0.1:6379`，database `1`）
- 开发本仓库时，请先根目录 `mvn install`，并将 `pom.xml` 中 `sa-token.version` 改为当前开发版本

## 怎么跑

IDEA 运行 `JsonTypingSecurityApplication`，或：

```bash
mvn -DskipTests spring-boot:run
```

启动后会自动执行一次 PoC。也可 HTTP 重复触发：

```bash
curl http://127.0.0.1:8093/ok
curl http://127.0.0.1:8093/poc/jackson-default-typing
```

**警告：** 仅用于本地验证，勿在生产或公网环境使用。
