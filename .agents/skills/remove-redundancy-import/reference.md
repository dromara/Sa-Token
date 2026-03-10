# 冗余 import 检测规则

## 解析步骤

### 1. 提取 package

匹配 `package\s+([\w.]+)\s*;`，得到当前文件所在包。

### 2. 提取 import

匹配以下模式（每行一条）：

- `import\s+([\w.]+)\s*;` — 普通 import
- `import\s+static\s+([\w.]+)\s*;` — static 导入类
- `import\s+static\s+([\w.]+)\.(\w+)\s*;` — static 导入成员（方法/字段）
- `import\s+[\w.]+\s*\.\s*\*\s*;` — 通配符，**跳过不处理**

### 3. 确定简单名（Simple Name）

| import 类型 | 示例 | 简单名 |
|-------------|------|--------|
| 普通类 | `import java.util.List;` | `List` |
| 内部类 | `import pkg.Outer.Inner;` | `Inner` |
| static 类 | `import static pkg.Utils;` | `Utils` |
| static 成员 | `import static pkg.Utils.foo;` | `foo` |

### 4. 同包冗余

若 `import x.y.Z` 的包 `x.y` 与当前文件 `package x.y` 相同，则该 import 冗余（同包无需导入）。

### 5. 使用检测

在**类体**（`package` 和所有 `import` 之后）中搜索：

- 使用正则 `\bSimpleName\b` 匹配整词，避免误匹配子串
- 排除：注释、字符串字面量中的出现
- 若未找到匹配，则该 import 视为未使用

## 边界情况

| 情况 | 处理方式 |
|------|----------|
| `import pkg.*;` | 跳过，不自动移除 |
| 注解中的类型 `@Foo` | 若 `Foo` 为 import 的简单名，视为已使用 |
| 泛型 `List<String>` | `List` 会匹配，视为已使用 |
| 同名类（如 `java.util.Date` 与 `java.sql.Date`） | 两 import 都保留；若仅一个被使用，只移除未使用的 |
| Javadoc `@param` 中的类型 | 保守：若不确定则保留 |

## 正则参考

```
// package
package\s+([\w.]+)\s*;

// 普通 import（非通配符）
import\s+(?!static)([\w.]+)\s*;

// static import 成员
import\s+static\s+[\w.]+\.(\w+)\s*;

// static import 类
import\s+static\s+([\w.]+)\s*;
```
