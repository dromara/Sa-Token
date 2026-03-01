
## 未完成目标
	
	
### 1、尝试将所有 `<properties>` 依赖版本号定义在同一个 pom.xml 里。 **[❌失败]**

**尝试1：将所有 `<properties>` 定义在 `sa-token-dependencies` 里：**

结果： 无法在 `sa-token-spring-boot2/3/4-dependencies` 中引用这些 `<properties>`，因为 `<dependencyManagement> <dependencies> <scope>import</scope>` 只会导入目标的 `<dependencyManagement>` 版本号定义，不会导入目标的 `<properties>` 属性。

`<properties>` 只会在 父子结构中向下传递，不会在  `<dependencyManagement> <dependencies> <scope>import</scope>` 中传递。


**尝试2：将所有 `<properties>` 定义在 `sa-token-parent` 里：**

结果：在 `sa-token-dependencies` 里无法引用这些 `<properties>`，因为 `sa-token-parent` 不是 `sa-token-dependencies` 的父模块。

将 `sa-token-parent` 定义为 `sa-token-dependencies` 的父模块行吗？

不行，因为在 `sa-token-parent` 通过  `<dependencyManagement> <dependencies> <scope>import</scope>` 导入了 `sa-token-dependencies`，如果再把 `sa-token-parent` 定义为 `sa-token-dependencies` 的父模块，会造成循环依赖。

执行 `mvn package` 打包时，maven 会直接报错：
	
```
[ERROR] [ERROR] Some problems were encountered while processing the POMs:
[ERROR] The dependencies of type=pom and with scope=import form a cycle: cn.dev33:sa-token-parent:1.44.0 -> cn.dev33:sa-token-basic-dependencies:1.44.0 -> cn.dev33:sa-token-basic-dependencies:1.44.0 @ cn.dev33:sa-token-basic-dependencies:1.44.0
 @
[ERROR] The build could not read 1 project -> [Help 1]
[ERROR]
[ERROR]   The project cn.dev33:sa-token-parent:1.44.0 (E:\work\project-yun\sa-token\pom.xml) has 1 error
[ERROR]     The dependencies of type=pom and with scope=import form a cycle: cn.dev33:sa-token-parent:1.44.0 -> cn.dev33:sa-token-basic-dependencies:1.44.0 -> cn.dev33:sa-token-basic-dependencies:1.44.0 @ cn.dev33:sa-token-basic-dependencies:1.44.0
[ERROR]
[ERROR] To see the full stack trace of the errors, re-run Maven with the -e switch.
[ERROR] Re-run Maven using the -X switch to enable full debug logging.
[ERROR]
[ERROR] For more information about the errors and possible solutions, please read the following articles:
[ERROR] [Help 1] http://cwiki.apache.org/confluence/display/MAVEN/ProjectBuildingException
```



