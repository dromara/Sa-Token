# sa-token-demo-bug-reproduce

专门用来**临时复现 Issue / Bug** 的沙盒 Demo。  
社区报了问题、本地要验证修复或回归时，在这个模块里改代码跑，避免污染正式示例。

> Jackson DefaultTyping 反序列化白名单的长期回归 Demo 已独立为  
> **`sa-token-demo-json-typing-security`**，本模块不再保留该用例。

## 使用约定

1. **一个时期通常只保留当前正在查的用例**（旧用例可注释掉或写在 README「历史用例」里）
2. 按目标环境改 `pom.xml` 的 Boot 版本 / starter（Web / WebFlux / Gateway 等）
3. 入口类：`BugReproduceApplication`
4. 默认端口：`8092`

## 当前用例

（暂无 — 按新 Issue 在此补充）

健康检查：

```bash
curl http://127.0.0.1:8092/ok
```

## 历史用例

| 日期 | Issue | 摘要 | 状态 |
|------|-------|------|------|
| 2026-08 | — | Jackson DefaultTyping 白名单 | 已迁至 `sa-token-demo-json-typing-security` |
| 2026-08 | IIAW1A / #916 | WebFlux setStatus 二进制不兼容 | 已修 |
