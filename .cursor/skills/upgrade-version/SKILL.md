---
name: upgrade-version
description: 将 Sa-Token 项目版本号升级到指定新版本。每次调用时先读取当前版本并提示用户，待用户输入目标版本后再执行批量修改。修改范围：pom.xml、核心常量、Demo 子项目及文档。当用户要求升级版本、修改版本号、或 version bump 时使用。
---

# Sa-Token 版本升级

将项目版本号升级到用户指定的新版本。每次调用时**先读取当前版本并询问目标版本**，用户确认后再批量修改核心构建、Demo 项目及文档中的版本引用。

## 使用时机

- 用户要求升级项目版本、修改版本号
- 用户说「版本从 vX.Y.Z 升级到 vX.Y.Z」「bump version」等

## 工作流程

### 第零步：询问目标版本（必须执行，不得跳过）

1. **读取当前版本**：从 `pom.xml` 的 `<revision>` 或 `SaTokenConsts.java` 的 `VERSION_NO` 中读取当前版本号
2. **提示用户**：明确告知「当前版本号是：xxx」
3. **等待输入**：询问「请输入要升级到的目标版本号（如 1.46.0）：」
4. **确认后再执行**：**必须**等用户明确回复目标版本号后，才能执行后续修改步骤。若用户仅说「升级版本」而未给出目标版本，先完成本步骤再继续

### 第一步：核心构建配置（3 个文件）

使用「当前版本」「目标版本」进行替换：

| 文件 | 修改内容 |
|------|----------|
| `pom.xml` | `<revision>当前版本</revision>` → 目标版本 |
| `sa-token-bom/pom.xml` | `<revision>当前版本</revision>` → 目标版本 |
| `sa-token-core/.../SaTokenConsts.java` | `VERSION_NO = "v当前版本"` → `"v目标版本"` |

### 第二步：Demo 项目（sa-token-demo 下所有 pom.xml）

- 将 `<sa-token.version>当前版本</sa-token.version>` 改为目标版本
- **sa-token-demo-bom-import** 额外修改：`<dependencyManagement>` 内 `sa-token-bom` 的 `<version>当前版本</version>` → 目标版本

**查找方式**：`grep "当前版本" sa-token-demo --output-mode files_with_matches` 定位所有需修改的 pom.xml。

### 第三步：文档（6 个文件）

| 文件 | 修改内容 |
|------|----------|
| `README.md` | 标题 `v当前版本` → `v目标版本`；Maven 依赖 `<version>当前版本</version>` → 目标版本 |
| `sa-token-doc/README.md` | 标题 `v当前版本` → `v目标版本` |
| `sa-token-doc/index.html` | `<small>v当前版本</small>` → `v目标版本` |
| `sa-token-doc/doc.html` | `<sub>v当前版本</sub>` 和 `saTokenTopVersion = '当前版本'` → 目标版本 |
| `sa-token-doc/start/new-version.md` | 文案及 Maven 示例中的当前版本 → 目标版本 |

### 第四步：不修改的文件

以下为历史记录或示例，**保持原样**：

- `.cursor/skills/` 下的示例（format-reference.md、SKILL.md 等）
- `MEMO/` 下的历史备忘录
- `sa-token-core/.../*.java` 中的 `@since X.Y.Z`（表示 API 引入版本，不随发布升级）
- `sa-token-doc/more/update-log.md`：更新日志应**新增**新版本条目，而非修改旧条目
- `sa-token-doc/more/blog.md`：历史博客链接

## 替换规则

- **pom.xml**：`当前版本` → `目标版本`
- **Java**：`"v当前版本"` → `"v目标版本"`
- **HTML/MD**：`v当前版本` 和 `当前版本` 按上下文分别替换为目标版本

## 执行顺序建议

1. 第零步：读取当前版本 → 提示用户 → 等待用户输入目标版本
2. 根 POM、sa-token-bom
3. SaTokenConsts.java
4. 批量修改 Demo pom.xml
5. 修改文档

## 验证

执行完成后，用 `grep "被替换的版本号"` 在项目根目录搜索，确认仅剩「不修改」列表中的文件仍含该版本（即修改已生效）。
