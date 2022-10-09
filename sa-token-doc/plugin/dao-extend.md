# 持久层扩展
--- 

对于权限框架来讲，最容易碰到的扩展点便是数据存储方式，为了方便对接不同的缓存中间件，Sa-Token将所有数据持久化操作抽象到SaTokenDao接口，
开发者要对接不同的平台只需要实现此接口即可，接口签名：[SaTokenDao.java](https://gitee.com/dromara/sa-token/blob/master/sa-token-core/src/main/java/cn/dev33/satoken/dao/SaTokenDao.java)
 
框架已提供的集成包包括：

- 默认方式：储存在内存中，位于core核心包。
- sa-token-dao-redis：Redis集成包，使用 jdk 默认序列化方式。
- sa-token-dao-redis-jackson：Redis集成包，使用 jackson 序列化方式。
- sa-token-dao-redisx：Redisx 集成包。 
- sa-token-dao-redis-fastjson：Redis集成包，使用 fastjson 序列化方式。

有关 Redis 集成，详细参考：[集成Redis](/up/integ-redis)，更多存储方式欢迎提交PR 









