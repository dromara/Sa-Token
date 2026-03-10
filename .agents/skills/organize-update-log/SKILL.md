---
name: organize-update-log
description: 根据 git 提交记录生成符合 Sa-Token 项目规范的更新日志内容。适用于分析指定版本之后的提交、提取变更并格式化为 update-log.md 风格。当用户需要生成更新日志、整理版本变更、或分析 release 之后的提交时使用。
---

# 整理更新日志

根据 git 提交记录，生成符合 `sa-token-doc/more/update-log.md` 格式的更新日志内容。

## 使用时机

- 用户要求生成/整理更新日志
- 用户要求分析「某版本之后」的提交变更
- 用户要求将 git 提交格式化为更新日志风格
- 准备发布新版本前整理 changelog

## 工作流程

### 第一步：确定基准版本

1. 询问用户基准版本（如 `v1.44.0`），或从上下文推断
2. 查找该版本的发布提交：
   - 在 `SaTokenConsts.java` 或 `pom.xml` 中搜索版本号
   - 或执行：`git log --oneline --all -- sa-token-core/src/main/java/cn/dev33/satoken/util/SaTokenConsts.java` 查找含 `release vX.X.X` 的提交
3. 记录基准提交 hash（如 `7bde74bc`）

### 第二步：获取提交列表

执行：
```bash
git log <基准提交>..HEAD --oneline --format="%h %s"
```

可选，获取更详细的变更文件：
```bash
git log <基准提交>..HEAD --stat --format="=== %h %s ==="
```

### 第三步：分类与映射

将每条提交按以下规则归类到对应板块：

| 提交关键词/内容 | 归属板块 |
|----------------|----------|
| feat.*jackson、plugin、插件 | 插件 |
| feat.*starter、spring-boot、reactor | starter |
| refactor.*依赖、dependencies、模块 | 重构 |
| refactor.*solon、gateway | Solon（单独列出） |
| fix.*dubbo、dubbo3 | 插件 |
| demo.*、示例 | 示例 或 starter |
| docs.*、文档 | 文档 |
| chore、.gitignore、.vscode | 其它 |
| merge.*loveqq、maven-pull | 其它（含 PR 链接） |

### 第四步：动作词映射

根据提交类型选择正确的动作词：

| 提交类型 | 动作词 | 示例 |
|----------|--------|------|
| feat、新增 | 新增 | 新增 `sa-token-jackson3` 插件 |
| fix、修复 | 修复 | 修复 Maven 父子项目依赖下载问题 |
| refactor、重构 | 重构 / 移除 | 重构模块依赖层级；移除 xxx 模块 |
| 优化、perf | 优化 | 优化 Gateway 接口处理 |
| 拆分 | 拆分 | （少见） |
| 文档更新 | 同步/新增/优化/修复 | 按具体内容选择 |

### 第五步：按格式输出

使用下方模板生成最终内容。详见 [format-reference.md](format-reference.md)。

## 输出模板

```markdown
### vX.X.X @YYYY-M-D（或：开发中 / 未发布）

- 插件：
	- 新增：xxx。  **[重要]**（如适用）
	- 修复：xxx。merge: [pr N](https://gitee.com/dromara/sa-token/pulls/N)（如适用）
- starter：
	- 新增：xxx。
- 重构：
	- 重构：xxx。
	- 移除：xxx。
- Solon：（如有）
	- 优化：xxx。merge: [pr N](url)
- 示例：（如有）
	- 新增：xxx。
- 文档：
	- 同步：xxx。
	- 新增：xxx。
	- 优化：xxx。
	- 修复：xxx。
- 其它：
	- 新增/修复/优化：xxx。
```

## 格式规则

1. **层级**：一级用 `-`，二级用 `	-`（Tab + 短横线）
2. **动作词**：每条以「新增」「修复」「重构」「优化」「移除」「同步」等开头
3. **重要标记**：对用户影响大的变更加 `**[重要]**`
4. **PR/Issue 链接**：提交信息含 `!358`、`pr 340` 等时，补充 `merge: [pr N](https://gitee.com/dromara/sa-token/pulls/N)`
5. **代码/模块名**：用反引号包裹，如 `` `sa-token-jackson3` ``
6. **合并同类**：多条相似文档类提交可合并为一条（如「同步公众号、博客、赞助者名单」）

## 常见板块

- **core**：核心逻辑、API、配置变更
- **SSO**：单点登录相关
- **OAuth2**：OAuth2 相关
- **插件**：插件包（jackson、dubbo、redis 等）
- **starter**：Spring Boot / Reactor 等 starter
- **示例**：demo 项目
- **文档**：文档、README、错别字
- **其它**：其它杂项

## 注意事项

- 合并提交（Merge branch）可忽略，只保留实际变更的提交
- 纯文档/错别字可适度合并，避免条目过多
- 版本号未发布时，可写 `v1.45.0（开发中）` 或 `未发布`
- 输出为可直接粘贴到 `update-log.md` 的 Markdown 片段
