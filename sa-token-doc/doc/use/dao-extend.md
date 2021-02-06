# 持久层扩展
--- 
- 每次重启项目都需要重新登录一次，我想把会话数据都放在`Redis`、`Memcached`等专业的缓存中间件中，这样项目重启数据不丢失，行不行？**行！**
- sa-token 在架构设计时将数据持久操作全部抽象到接口`SaTokenDao`中，此设计可以保证开发者对数据持久层的灵活扩展
- 除了框架内部对`SaTokenDao`提供的基于内存的默认实现，官方仓库还提供了以下扩展方案：<br>
- (注意: 整合Redis只需要引入对应的pom依赖即可，所有上层API保持不变)


### 1. sa-token 整合 Redis (使用jdk默认序列化方式)
``` xml 
	<!-- sa-token整合redis (使用jdk默认序列化方式) -->
	<dependency>
		<groupId>cn.dev33</groupId>
		<artifactId>sa-token-dao-redis</artifactId>
		<version>1.12.1</version>
	</dependency>
```
优点：兼容性好，缺点：Session序列化后基本不可读，对开发者来讲等同于乱码


### 2. sa-token 整合 Redis (使用jackson序列化方式)
``` xml 
	<!-- sa-token整合redis (使用jackson序列化方式) -->
	<dependency>
		<groupId>cn.dev33</groupId>
		<artifactId>sa-token-dao-redis-jackson</artifactId>
		<version>1.12.1</version>
	</dependency>
```
优点：Session序列化后可读性强，可灵活手动修改，缺点：兼容性稍差


<br>

**请注意，无论使用哪种序列化方式，你都必须为项目提供一个Redis实例化方案，例如：**
``` xml
	<!-- 提供redis连接池 -->
	<dependency>
		<groupId>org.apache.commons</groupId>
		<artifactId>commons-pool2</artifactId>
	</dependency>
```



<br><br>
更多框架的集成方案正在更新中... (欢迎大家提交pr)



