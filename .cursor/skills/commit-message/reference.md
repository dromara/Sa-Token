# Commit Message 规范参考

基于 Conventional Commits 与业界最佳实践整理。

## 核心规则

| 规则 | 说明 |
|------|------|
| 50 字规则 | subject 不超过 50 字符，便于 git log 完整显示 |
| 72 字规则 | body 每行不超过 72 字符，便于阅读与 diff |
| 命令式语气 | 用「修复」「新增」而非「修复了」「新增了」 |
| 说明动机 | 重要变更在 body 中说明「为什么」而不仅是「做了什么」 |

## 格式结构

```
<type>[(<scope>)]: <subject>

[optional body]

[optional footer(s)]
```

- **subject**：必填，简明扼要
- **body**：可选，详细说明
- **footer**：可选，如 `Fixes #123`、`BREAKING CHANGE: xxx`

## 类型速查

- **feat**：新功能
- **fix**：修复 bug
- **refactor**：重构（结构、逻辑）
- **perf**：性能优化
- **docs**：文档
- **style**：格式（不影响逻辑）
- **chore**：构建、配置、杂项
- **test**：测试
- **revert**：回滚
