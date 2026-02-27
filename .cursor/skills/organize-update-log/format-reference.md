# 更新日志格式参考

本文档提供 `sa-token-doc/more/update-log.md` 的格式细节与示例，供生成更新日志时参考。

## 版本标题格式

```markdown
### v1.44.0 @2025-6-7
```

- 版本号：`v` + 主.次.修订
- 日期：`@YYYY-M-D` 或 `@YYYY-M-DD`
- 未发布时：`v1.45.0（开发中）` 或 `v1.45.0（未发布）`

## 板块结构

```
- 板块名：
	- 动作词：具体描述。  **[重要]**（可选） merge: [pr N](url)（可选）
```

- 一级：`- 板块名：`
- 二级：`	- 动作词：描述。`（Tab 缩进）

## 动作词

| 动作词 | 含义 | 使用场景 |
|--------|------|----------|
| 新增 | 新功能、新模块 | feat、新增插件、新 starter |
| 修复 | Bug 修复 | fix |
| 重构 | 结构调整 | refactor |
| 优化 | 改进、优化 | 优化、perf |
| 移除 | 删除模块/功能 | 删除、移除 |
| 拆分 | 模块拆分 | 拆分 |
| 同步 | 内容同步 | 文档、赞助者、博客列表 |
| 补全 | 补充内容 | 补全文档、测试 |
| 升级 | 升级、变更 | 升级 API、模块 |

## 链接格式

**PR：**
```markdown
merge: [pr 340](https://gitee.com/dromara/sa-token/pulls/340)
```

**Issue：**
```markdown
fix: [#IA6ZK0](https://gitee.com/dromara/sa-token/issues/IA6ZK0)
```

## 重要标记

对用户影响较大的变更加 `**[重要]**`，通常放在句末、链接前：

```markdown
- 新增：新增 `sa-token-spring-boot4-starter` 集成包。  **[重要]**
- 新增：loveqq-framework 启动器集成。merge: [pr 340](url)
```

## 完整示例

```markdown
### v1.45.0（开发中）

- 插件：
	- 新增：新增 `sa-token-jackson3` 插件，用于 Jackson 3 的 JSON 解析。  **[重要]**
	- 新增：新增 `sa-token-jackson3` 单元测试。
- starter：
	- 新增：新增 `sa-token-spring-boot4-starter` 集成包，支持 Spring Boot 4。  **[重要]**
	- 新增：新增 `sa-token-reactor-spring-boot4-starter` 集成包，支持 WebFlux + Spring Boot 4。  **[重要]**
	- 新增：新增 `sa-token-demo-webflux-springboot4` 示例。
	- 新增：新增 Spring Boot 4 整合 demo 示例。
- 重构：
	- 重构：`sa-token-dependencies` 重构为 `sa-token-basic-dependencies`。  **[重要]**
	- 重构：重构 Spring Boot 相关集成包，优化依赖关系。
	- 移除：移除 `sa-token-spring-boot-autoconfig` 模块，相关逻辑迁移至各 starter 内。  **[重要]**
	- 重构：重构模块依赖层级，新增 `sa-token-special-dependencies`。
- Solon：
	- 优化：`sa-token-solon-plugin` 优化 Gateway 接口的处理，避免使用路由接口。merge: [pr 348](https://gitee.com/dromara/sa-token/pulls/348)
- 其它：
	- 新增：loveqq-framework 启动器集成。merge: [pr 340](https://gitee.com/dromara/sa-token/pulls/340)
	- 修复：修复 Maven 父子项目无法下载依赖的问题。merge: [pr 358](https://gitee.com/dromara/sa-token/pulls/358)
- 文档：
	- 同步：同步公众号文章列表、博客列表、赞助者名单。
	- 新增：新增《Gitee 2025年度开源项目 Web应用开发 Top 2》证书展示。
	- 优化：优化框架 Slogan、README、案例库展示。
	- 修复：错别字修复；文档图片地址更换为本地文件。
- 其它：
	- 新增：增加忽略 .vscode 目录。
	- 优化：注释优化。
```

## 提交信息到条目的映射示例

| 提交信息 | 生成条目 |
|----------|----------|
| `feat: 添加 sa-token-jackson3 插件` | 新增：新增 `sa-token-jackson3` 插件，用于 Jackson 3 的 JSON 解析。  **[重要]** |
| `refactor: 移除 sa-token-spring-boot-autoconfig 模块` | 移除：移除 `sa-token-spring-boot-autoconfig` 模块，相关逻辑迁移至各 starter 内。  **[重要]** |
| `docs: 同步最新赞助者名单` | 同步：同步赞助者名单。 |
| `!358 update maven-pull.md` | 修复：修复 Maven 父子项目无法下载依赖的问题。merge: [pr 358](url) |

## 文档类合并建议

以下类型可合并为一条：

- 同步公众号、博客、赞助者名单 → 「同步：同步公众号文章列表、博客列表、赞助者名单。」
- 多条例错别字修复 → 「修复：错别字修复。」
- 多篇文档图片本地化 → 「修复：文档图片地址更换为本地文件（基础篇、深入篇、SSO篇等）。」
