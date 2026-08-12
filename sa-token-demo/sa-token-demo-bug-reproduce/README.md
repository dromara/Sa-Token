# sa-token-demo-bug-reproduce

专门用来**复现 Issue / Bug** 的沙盒 Demo。  
社区报了问题、本地要验证修复或回归时，在这个模块里改代码跑，避免污染正式示例。

## 使用约定

1. **一个时期通常只保留当前正在查的用例**（旧用例可注释掉或写在 README「历史用例」里）
2. 按目标环境改 `pom.xml` 的 Boot 版本 / starter（Web / WebFlux / Gateway 等）
3. 入口类：`BugReproduceApplication`
4. 默认端口：`8092`

## 当前用例：setStatus NoSuchMethodError（已修）

关联：

- Gitee [IIAW1A](https://gitee.com/dromara/sa-token/issues/IIAW1A)
- GitHub [#916](https://github.com/dromara/Sa-Token/issues/916)
- 提案 `#92` / `#221`

现象（修复前）：Boot 3.5 + WebFlux 下 `SaHolder.getResponse().setStatus(401)` →  
`NoSuchMethodError: ServerHttpResponse.setStatusCode(HttpStatus)`

根因：Reactor common 按 Spring 5 编译，高版本运行签名不兼容。  
修复：Boot2 实现收在 `sa-token-reactor-spring-boot-starter`，Boot3/4 共用 `reactor-v3v4-common`（`HttpStatusCode`）。

### 怎么跑当前用例

```bash
# IDEA 运行 BugReproduceApplication，或：
mvn -DskipTests spring-boot:run
```

```bash
curl -i http://127.0.0.1:8092/repro/direct-set-status
curl -i http://127.0.0.1:8092/basic/ping
```

期望（修复后）：返回 HTTP 401，进程不崩、无 `NoSuchMethodError`。

## 历史用例

| 日期 | Issue | 摘要 | 状态 |
|------|-------|------|------|
| 2026-08 | IIAW1A / #916 | WebFlux setStatus 二进制不兼容 | 已修 |
