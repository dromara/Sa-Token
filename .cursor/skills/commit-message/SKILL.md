---
name: commit-message
description: 根据 git 变更生成符合 Sa-Token 项目风格的 commit message。遵循 Conventional Commits 格式，以中文为主。当用户要求生成提交信息、写 commit message、或根据变更生成提交说明时使用。
---

# 生成 Commit Message

根据当前 git 变更（staged 或 unstaged），生成符合 Sa-Token 项目规范的 commit message。

## 使用时机

- 用户要求生成 commit message
- 用户要求根据变更写提交说明
- 用户说「帮我写个 commit」「生成提交信息」等

## 工作流程

### 第一步：获取变更内容

```bash
git status
git diff --staged
git diff
```

**必须包含的变更范围**：
- **staged 变更**：`git diff --staged`
- **unstaged 变更**：若无 staged，则用 `git diff` 查看工作区修改
- **未跟踪文件**：`git status` 中的 Untracked files 也要纳入分析，生成 commit message 时需一并考虑

若存在未跟踪的新增文件（如新 skill、新配置等），应在 message 中体现，或给出「包含全部变更」与「仅已修改文件」两种方案供用户选择。

### 第二步：分析变更类型

根据变更内容选择 type 前缀：

| type | 适用场景 |
|------|----------|
| feat | 新增功能、新模块、新插件 |
| fix | 修复 bug、修正错误 |
| refactor | 重构、优化结构、重命名、移除冗余 |
| perf | 性能优化（与 refactor 区分：侧重性能） |
| docs | 文档更新、README、错别字、同步链接 |
| style | 代码格式调整（缩进、空格等，不影响逻辑） |
| chore | 构建配置、.gitignore、注释修复、依赖更新 |
| test | 单元测试、测试用例 |
| demo | 示例项目、demo 相关 |
| memo | 备忘录、内部记录 |
| revert | 回滚某次提交 |
| AI | AI 创建的 skill、规则等 |

### 第三步：撰写描述

**基础格式**：`type: 简短描述` 或 `type(scope): 简短描述`

**scope 可选**：涉及特定模块时使用，如 `feat(sign)`、`fix(oauth2)`、`refactor(dependencies)`。

**规范**：
- **50 字规则**：subject 不超过 50 字符，保证在 git log 中完整显示
- **命令式语气**：用「修复」「新增」「优化」，不用「修复了」「新增了」
- **说明「做了什么」**：清晰表达变更内容，必要时说明「为什么」
- **以中文为主**：技术术语可保留英文（如 `StrFormatter`、`sa-token-jackson3`）
- **动词开头**：新增、修复、优化、重构、移除、同步、订正 等

**可选 Body/Footer**（重要变更时）：
- Body：详细说明变更背景、动机，每行不超过 72 字符
- Footer：关联 Issue，如 `Fixes #123` 或 `merge: [pr N](url)`

### 第四步：输出

直接输出可复制的 commit message。若有多条合理方案，可给出 1～2 个备选。

## 格式示例

**简单提交**（常用）：
```
feat: 添加 sa-token-jackson3 插件
fix(sign): 修复签名校验在空参数时的空指针
```

**带 scope**：
```
refactor(dependencies): 重构模块依赖层级
perf(oauth2): 优化 Client 信息读取算法
```

**带 body**（复杂变更）：
```
feat: 新增重复登录处理策略

当同一账号不允许多客户端同时登录时，支持选择踢人下线或拦截本次登录。
```

## 参考资源

- 示例：详见 [examples.md](examples.md)
- 规范详解：详见 [reference.md](reference.md)

## 快速对照

| 变更内容 | 示例输出 |
|----------|----------|
| 新增插件 | `feat: 添加 sa-token-jackson3 插件` |
| 修复 bug | `fix: 修复 StpUtil.getLoginIdByTokenNotThinkFreeze 方法缺少 static 的问题` |
| 性能优化 | `perf: 优化 StrFormatter 常量封装` |
| 重构模块 | `refactor: 重构模块依赖层级` |
| 移除冗余 | `refactor: 移除冗余导包` |
| 文档更新 | `docs: 同步最新文章列表、赞助者名单` |
| 注释修复 | `chore: 修复注释错别字` |
| 新增 skill | `AI: 新增 skills/commit-message/SKILL.md，用于根据 git 变更生成符合项目风格的 commit message` |
