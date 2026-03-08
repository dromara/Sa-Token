# 其它环境引入 Sa-Token 的示例

目前已实现的对接框架综合

------

## Maven依赖 
根据不同基础框架引入不同的 Sa-Token 依赖：

<!------------------------------ tabs:start ------------------------------>

<!------------- tab:SpringBoot环境 （ServletAPI）  ------------->
如果你使用的框架基于 ServletAPI 构建（ SpringMVC、SpringBoot等 ），请引入此包
``` xml
<!-- Sa-Token 权限认证, 在线文档：https://sa-token.cc -->
<dependency>
	<groupId>cn.dev33</groupId>
	<artifactId>sa-token-spring-boot-starter</artifactId>
	<version>${sa.top.version}</version>
</dependency>
```
- 如果你使用的 `SpringBoot 3.x`，请引入 `sa-token-spring-boot3-starter`。
- 如果你使用的 `SpringBoot 4.x`，请引入 `sa-token-spring-boot4-starter`。

<!------------- tab:WebFlux环境 （Reactor）  ------------->
注：如果你使用的框架基于 Reactor 模型构建（WebFlux、SpringCloud Gateway 等），请引入此包
``` xml
<!-- Sa-Token 权限认证（Reactor响应式集成）, 在线文档：https://sa-token.cc -->
<dependency>
	<groupId>cn.dev33</groupId>
	<artifactId>sa-token-reactor-spring-boot-starter</artifactId>
	<version>${sa.top.version}</version>
</dependency>
```
- 如果你使用的 `SpringBoot 3.x`，请引入 `sa-token-reactor-spring-boot3-starter`。
- 如果你使用的 `SpringBoot 4.x`，请引入 `sa-token-reactor-spring-boot4-starter`。

<!------------- tab:Solon 集成  ------------->
参考：[Solon官网](https://solon.noear.org/)
``` xml
<!-- Sa-Token 整合 Solon, 在线文档：https://sa-token.cc -->
<dependency>
	<groupId>cn.dev33</groupId>
	<artifactId>sa-token-solon-plugin</artifactId>
	<version>${sa.top.version}</version>
</dependency>
```

<!------------- tab:JFinal 集成  ------------->
参考：[JFinal官网](https://jfinal.com/)
``` xml
<!-- Sa-Token 整合 JFinal, 在线文档：https://sa-token.cc -->
<dependency>
	<groupId>cn.dev33</groupId>
	<artifactId>sa-token-jfinal-plugin</artifactId>
	<version>${sa.top.version}</version>
</dependency>
```

<!------------- tab:Jboot 集成  ------------->
参考：[Jboot官网](http://www.jboot.com.cn/)
``` xml
<!-- Sa-Token 整合 Jboot, 在线文档：https://sa-token.cc -->
<dependency>
	<groupId>cn.dev33</groupId>
	<artifactId>sa-token-jboot-plugin</artifactId>
	<version>${sa.top.version}</version>
</dependency>
```

<!------------- tab:LoveQQ-Framework 集成  ------------->
参考：[LoveQQ-Framework](https://gitee.com/kfyty725/loveqq-framework)
``` xml
<!-- Sa-Token 整合 LoveQQ-Framework, 在线文档：https://sa-token.cc -->
<dependency>
	<groupId>cn.dev33</groupId>
	<artifactId>sa-token-loveqq-boot-starter</artifactId>
	<version>${sa.top.version}</version>
</dependency>
```

<!------------- tab:Quarkus 集成  ------------->
参考：[quarkus-sa-token](https://github.com/quarkiverse/quarkus-sa-token)
``` xml
<!-- Sa-Token 整合 Quarkus, 在线文档：https://sa-token.cc -->
<dependency>
	<groupId>io.quarkiverse.satoken</groupId>
	<artifactId>quarkus-satoken-resteasy</artifactId>
	<version>1.30.0</version>
</dependency>
```

<!------------- tab:裸Servlet容器环境   ------------->
注：如果你的项目没有使用Spring，但是Web框架是基于 ServletAPI 规范的，可以引入此包
``` xml
<!-- Sa-Token 权限认证（ServletAPI规范）, 在线文档：https://sa-token.cc -->
<dependency>
	<groupId>cn.dev33</groupId>
	<artifactId>sa-token-servlet</artifactId>
	<version>${sa.top.version}</version>
</dependency>
```
引入此依赖需要自定义 SaTokenContext 实现，参考：[自定义 SaTokenContext 指南](/fun/sa-token-context)

<!------------- tab:其它   ------------->
注：如果你的项目既没有使用 SpringMVC、WebFlux，也不是基于 ServletAPI 规范，那么可以引入core核心包
``` xml
<!-- Sa-Token 权限认证（core核心包）, 在线文档：https://sa-token.cc -->
<dependency>
	<groupId>cn.dev33</groupId>
	<artifactId>sa-token-core</artifactId>
	<version>${sa.top.version}</version>
</dependency>
```
引入此依赖需要自定义 SaTokenContext 实现，参考：[自定义 SaTokenContext 指南](/fun/sa-token-context)

<!---------------------------- tabs:end ------------------------------>


## Gradle依赖
<!-- tabs:start -->
<!-- tab:SpringBoot环境 （ServletAPI）  -->
``` gradle
implementation 'cn.dev33:sa-token-spring-boot-starter:${sa.top.version}'
```
- 如果你使用的 `SpringBoot 3.x`，请引入 `sa-token-spring-boot3-starter`。
- 如果你使用的 `SpringBoot 4.x`，请引入 `sa-token-spring-boot4-starter`。

<!-- tab:WebFlux环境 （Reactor）  -->
``` gradle
implementation 'cn.dev33:sa-token-reactor-spring-boot-starter:${sa.top.version}'
```
- 如果你使用的 `SpringBoot 3.x`，请引入 `sa-token-reactor-spring-boot3-starter`。
- 如果你使用的 `SpringBoot 4.x`，请引入 `sa-token-reactor-spring-boot4-starter`。

<!-- tab:Solon 集成  -->
``` gradle
implementation 'cn.dev33:sa-token-solon-plugin:${sa.top.version}'
```

<!-- tab:JFinal 集成  -->
``` gradle
implementation 'cn.dev33:sa-token-jfinal-plugin:${sa.top.version}'
```

<!-- tab:Jboot 集成  -->
``` gradle
implementation 'cn.dev33:sa-token-jboot-plugin:${sa.top.version}'
```

<!-- tab:LoveQQ-Framework 集成  -->
``` gradle
implementation 'cn.dev33:sa-token-loveqq-boot-starter:${sa.top.version}'
```

<!-- tab:Quarkus 集成  -->
``` gradle
implementation 'io.quarkiverse.satoken:quarkus-satoken-resteasy:1.30.0'
```

<!-- tab:裸Servlet容器环境  -->
``` gradle
implementation 'cn.dev33:sa-token-servlet:${sa.top.version}'
```

<!-- tab:其它  -->
``` gradle
implementation 'cn.dev33:sa-token-core:${sa.top.version}'
```

<!-- tabs:end -->

注：JDK版本：`v1.8+`，SpringBoot：`建议2.0以上`


## 测试版
更多内测版本了解：[Sa-Token 最新版本](https://gitee.com/dromara/sa-token/blob/dev/sa-token-doc/start/new-version.md)

Maven依赖一直无法加载成功？[参考解决方案](https://sa-token.cc/doc.html#/start/maven-pull)

## jar包下载
<!-- [点击下载：sa-token-1.6.0.jar](https://oss.dev33.cn/sa-token/sa-token-1.6.0.jar) -->
[点击下载：sa-token-1.6.0.jar](https://pan.quark.cn/s/85e4d75f500c)

注：当前仅提供 `v1.6.0` 版本jar包下载，更多版本请前往 maven 中央仓库获取，[直达链接](https://search.maven.org/search?q=sa-token)


## 获取源码
如果你想深入了解 Sa-Token，你可以通过`Gitee`或者`GitHub`来获取源码 （**学习测试请拉取 master 分支**，dev为正在开发的分支，有很多特性并不稳定）
- **Gitee**地址：[https://gitee.com/dromara/sa-token](https://gitee.com/dromara/sa-token)
- **GitHub**地址：[https://github.com/dromara/sa-token](https://github.com/dromara/sa-token)
- 开源不易，求鼓励，点个`star`吧
- 源码目录介绍: - [仓库目录](/arch/dir-intro)





## 运行示例

- 1、下载代码（学习测试用 master 分支）。
- 2、从根目录导入项目。
- 3、选择相应的示例添加为 Maven 项目，打开 XxxApplication.java 运行。

<img src="/big-file/doc/start/import-demo-run.png" alt="运行示例" title="s-w-sh">