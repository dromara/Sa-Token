# 集成

------

## Maven依赖
在项目中直接通过 `pom.xml` 导入 `sa-token` 的依赖即可
``` xml
<!-- sa-token 权限认证, 在线文档：http://sa-token.dev33.cn/ -->
<dependency>
	<groupId>cn.dev33</groupId>
	<artifactId>sa-token-spring-boot-starter</artifactId>
	<version>1.15.2</version>
</dependency>
```

## Gradle依赖
Gradle用户引入依赖：
```
implementation 'cn.dev33:sa-token-spring-boot-starter:1.15.2'
```



## 获取源码
如果你想深入了解`sa-token`，你可以通过`github`或者`gitee`来获取源码
- github地址：[https://github.com/dromara/sa-token](https://github.com/dromara/sa-token)
- gitee地址：[https://gitee.com/dromara/sa-token](https://gitee.com/dromara/sa-token)
- 开源不易，求鼓励，给个`star`吧
- 源码目录介绍: 
```
── sa-token
	├── sa-token-core                         // sa-token核心模块
	├── sa-token-spring-boot-starter           // sa-token整合springboot快速集成 
	├── sa-token-dao-redis                    // sa-token整合redis (使用jdk默认序列化方式)
	├── sa-token-dao-redis-jackson            // sa-token整合redis (使用jackson序列化方式)
	├── sa-token-spring-aop                   // sa-token整合SpringAOP 注解鉴权
	├── sa-token-oauth2                       // sa-token实现 OAuth2.0 模块(内测暂未发布)
	├── sa-token-demo-springboot              // sa-token示例 
	├── sa-token-demo-jwt                    // sa-token集成jwt示例 
	├── sa-token-demo-oauth2                 // sa-token集成OAuth2.0模块示例 
		├── sa-token-demo-oauth2-client       // OAuth2.0 客户端
		├── sa-token-demo-oauth2-server       // OAuth2.0 服务端
	├── sa-token-doc                        // sa-token开发文档 
	├──pom.xml
```




## jar包下载
[点击下载：sa-token-1.6.0.jar](https://oss.dev33.cn/sa-token/sa-token-1.6.0.jar)

(注意：当前仅提供`v1.6.0`版本jar包下载，更多版本请前往maven中央仓库获取，[直达链接](https://search.maven.org/search?q=sa-token))



