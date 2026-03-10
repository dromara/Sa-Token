---
name: remove-redundancy-import
description: 检查 Java 类中未被引用的冗余 import 并移除。先输出待审阅计划，用户确认后执行。适用于用户要求清理冗余导包、优化 import、或执行 remove-redundancy-import 时使用。
---

# 移除冗余 import

检查项目中所有 Java 类的未使用 import，生成清理计划供用户审阅，确认后执行移除。

## 使用时机

- 用户要求清理冗余导包
- 用户要求优化 Java import
- 用户明确执行 `remove-redundancy-import` 或提及本 Skill 名称

## 强制流程

**必须先输出计划，用户确认后再执行移除。** 不得在未审阅的情况下直接修改文件。

## 工作流程

### 第一步：扫描与解析

**优先使用内置脚本**：在 Skill 目录下的 [scan_redundant_imports.py](scan_redundant_imports.py) 已实现完整扫描逻辑，可直接复用。

```bash
# 在项目根目录执行
python .cursor/skills/remove-redundancy-import/scan_redundant_imports.py
# 或指定扫描根路径
python .cursor/skills/remove-redundancy-import/scan_redundant_imports.py .
```

脚本输出格式：`文件路径 | 冗余import1; import2 | 数量`，末尾两行为 `TOTAL_FILES:N` 和 `TOTAL_IMPORTS:M`。

**若无 Python 环境**，可手动执行：
1. 使用 `Glob` 查找项目内所有 `**/*.java` 文件
2. 对每个文件：提取 `package`、`import`，按 [reference.md](reference.md) 判定是否被使用
3. 汇总存在冗余 import 的文件及列表

### 第二步：输出计划

使用下方模板生成计划报告，等待用户确认：

```markdown
## 冗余 import 清理计划

| 文件 | 待移除 import | 数量 |
|------|---------------|------|
| path/to/Foo.java | `java.util.Date`, `java.sql.Timestamp` | 2 |
| ... | ... | ... |

**共 N 个文件，M 处冗余 import。确认后执行移除。**
```

### 第三步：执行移除

用户确认后，对计划中的每个文件使用 `StrReplace` 移除对应 import 行：

- 逐行移除，每行格式为 `import ...;` 或 `import static ...;`
- 若某 import 后紧跟空行，可一并移除空行以保持格式整洁
- 移除后确认文件无语法错误

## 检测规则概要

- **普通 import**：取最后一段类名（如 `java.util.List` → `List`），在类体中搜索 `\bList\b`
- **static import**：取方法/字段名，在类体中搜索
- **同包冗余**：import 的包与当前文件 `package` 相同则视为冗余
- **通配符**：`import pkg.*` 跳过，不自动处理

详见 [reference.md](reference.md)。

## 注意事项

- 通配符 import 无法可靠判断，一律跳过
- 注解中的类型引用采用保守策略，宁可漏检不误删
- 移除后建议用户运行 `mvn compile` 验证
