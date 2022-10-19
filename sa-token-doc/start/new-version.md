# Sa-Token 最新版本

在线文档：[https://sa-token.dev33.cn/](https://sa-token.dev33.cn/)

--- 

### 正式版本 
v1.31.0 正式版，可上生产：

``` xml
<!-- Sa-Token 权限认证 -->
<dependency>
    <groupId>cn.dev33</groupId>
    <artifactId>sa-token-spring-boot-starter</artifactId>
    <version>1.31.0</version>
</dependency>
```

Maven依赖一直无法加载成功？[参考解决方案](https://sa-token.dev33.cn/doc.html#/start/maven-pull)

--- 

### 最新版本

v1.31.1.temp 过渡版本，仅做学习测试，不推荐上生产：
``` xml
<!-- Sa-Token 权限认证 -->
<dependency>
    <groupId>cn.dev33</groupId>
    <artifactId>sa-token-spring-boot-starter</artifactId>
    <version>1.31.1.temp</version>
</dependency>
```

更新日志：
- 文档：[自定义 SaTokenContext 指南] 章节新增对三种模型的解释。
- 文档：基础章节新增练习测试。
- 文档：部分章节新增代码示例。
- 文档：增加全局调色功能。
- 升级：`SaFoxUtil.getValueByType()` 新增对 char 类型的转换。
- 修复：修复 sa-token-dao-redis-fastjson 插件多余序列化 `timeout` 字段的问题。
- 修复：修复 sa-token-dao-redis-fastjson 插件 `session.getModel` 无法反序列化实体类的问题。
- 新增：新增 `sa-token-dao-redis-fastjson2` 插件。











