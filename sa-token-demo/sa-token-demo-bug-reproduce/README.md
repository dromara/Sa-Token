# sa-token-demo-bug-reproduce

专门用来**临时复现 Issue / Bug** 的沙盒 Demo。平时保持空壳，有新 Issue 时再往里填用例。

## 当前用例

（无）

## 怎么跑

```bash
cd sa-token-demo/sa-token-demo-bug-reproduce
mvn -DskipTests spring-boot:run
```

健康检查：<http://127.0.0.1:8092/ok>

## 历史用例

| 日期 | Issue | 摘要 | 状态 |
|------|-------|------|------|
| 2026-08 | IC4XFE / todo #24 | SSE / Flux ASYNC dispatch 上下文未初始化 | 已框架修复（Filter REQUEST+ASYNC） |
| 2026-08 | — | Jackson DefaultTyping 白名单 | 已迁至 `sa-token-demo-json-typing-security` |
| 2026-08 | IIAW1A / #916 | WebFlux setStatus 二进制不兼容 | 已修 |
