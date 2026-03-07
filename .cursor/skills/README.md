# Cursor Agent Skills

本目录存放用于辅助 Sa-Token 项目开发的 Cursor Agent Skills。这些 skills 封装了项目特定的知识和操作规范，可在对话中直接调用。

## Skill 列表

| Skill 名称 | 功能描述 | 使用场景 | 入口文件 |
|-----------|---------|---------|---------|
| `commit-message` | 根据 git 变更生成符合 Sa-Token 项目风格的 commit message | 生成提交信息、写 commit message | [SKILL.md](commit-message/SKILL.md) |
| `organize-update-log` | 根据 git 提交记录生成符合项目规范的更新日志内容 | 整理更新日志、分析版本变更 | [SKILL.md](organize-update-log/SKILL.md) |
| `remove-redundancy-import` | 检查并移除 Java 类中未被引用的冗余 import | 清理冗余导包、优化 import | [SKILL.md](remove-redundancy-import/SKILL.md) |
| `upgrade-version` | 将项目版本号从旧版本升级到新版本，批量修改 pom、常量、Demo 及文档 | 升级版本、修改版本号、version bump | [SKILL.md](upgrade-version/SKILL.md) |

### 详细说明

#### commit-message
根据当前 git 变更（staged 或 unstaged），生成符合 [Conventional Commits](https://www.conventionalcommits.org/) 格式、以中文为主的 commit message。支持识别新增文件、修复 bug、重构等多种变更类型。

#### organize-update-log
根据 git 提交记录，生成符合 `sa-token-doc/more/update-log.md` 格式的更新日志内容。自动分类到插件、starter、重构、Solon、示例、文档等版块。

#### remove-redundancy-import
扫描项目中所有 Java 类，检测未被引用的冗余 import，生成清理计划供审阅，确认后执行移除。支持通过内置 Python 脚本快速扫描。

#### upgrade-version
将 Sa-Token 项目版本号从旧版本升级到新版本。批量修改根 POM、BOM、SaTokenConsts、所有 Demo 子项目 pom.xml 及文档（README、index.html、doc.html、new-version.md 等）中的版本引用。明确排除历史记录、@since 标注、更新日志等不应修改的文件。

## 快速使用

在 Cursor 对话中，直接描述你的需求即可自动触发相应 skill：

```
用户：帮我生成 commit message
→ 自动使用 commit-message skill 分析 git 变更并生成提交信息

用户：整理一下更新日志
→ 自动使用 organize-update-log skill 生成更新日志

用户：清理一下冗余 import
→ 自动使用 remove-redundancy-import skill 扫描并清理未使用的 import

用户：把版本从 v1.44.0 升级到 v1.45.0
→ 自动使用 upgrade-version skill 批量修改版本号
```

## 新增 Skill 维护指南

当新增 skill 时，请同步更新本 README 文件，保持 skill 列表的完整性。

### Skill 目录结构规范

每个 skill 应创建独立的子目录，结构如下：

```
.cursor/skills/
├── README.md              # 本文件
└── skill-name/            # skill 目录（小写，短横线分隔）
    ├── SKILL.md           # skill 主文件（必须包含 YAML 元数据和使用说明）
    ├── examples.md        # 使用示例（可选）
    ├── reference.md       # 参考文档（可选）
    └── scan_redundant_imports.py  # 辅助脚本（如需要）
```

### SKILL.md 文件格式

每个 `SKILL.md` 必须包含 YAML Front Matter：

```yaml
---
name: skill-name
description: 简要描述 skill 的功能和使用场景
---
```

### 更新 README 清单

新增 skill 后，请在本文件中：

1. 在 **Skill 列表** 表格中添加新行
2. 在 **详细说明** 小节添加对应的描述段落
3. （可选）在 **快速使用** 中添加使用示例

## 注意事项

- 所有 skill 遵循 Sa-Token 项目特定的规范和风格
- 部分 skill（如 `remove-redundancy-import`）在执行前需要用户确认
- 可参考每个 skill 目录下的 `examples.md` 或 `reference.md` 获取更多使用帮助
