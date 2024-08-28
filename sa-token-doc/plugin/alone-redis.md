# Sa-Token-Alone-Redis 独立Redis插件
--- 

Sa-Token默认的Redis集成方式会把权限数据和业务缓存放在一起，但在部分场景下我们需要将他们彻底分离开来，比如：

> [!NOTE| label:业务场景] 
> 搭建两个Redis服务器，一个专门用来做业务缓存，另一台专门存放Sa-Token权限数据 


<button class="show-img" img-src="https://oss.dev33.cn/sa-token/doc/g/g3--alone-redis.gif">加载动态演示图</button>


要将Sa-Token的数据单独抽离出来很简单，你只需要为Sa-Token单独配置一个Redis连接信息即可 

--- 


### 1、首先引入Alone-Redis依赖 
<!---------------------------- tabs:start ---------------------------->
<!-------- tab:Maven 方式 -------->
``` xml 
<!-- Sa-Token插件：权限缓存与业务缓存分离 -->
<dependency>
	<groupId>cn.dev33</groupId>
	<artifactId>sa-token-alone-redis</artifactId>
	<version>${sa.top.version}</version>
</dependency>
```
<!-------- tab:Gradle 方式 -------->
``` gradle
// Sa-Token 整合 Redis （使用 jackson 序列化方式）
implementation 'cn.dev33:sa-token-alone-redis:${sa.top.version}'
```
<!---------------------------- tabs:end ---------------------------->


### 2、然后在application.yml中增加配置

<!---------------------------- tabs:start ---------------------------->

<!------------- tab:yaml 风格  ------------->
``` yaml
# Sa-Token 配置
sa-token: 
	# Token名称
	token-name: satoken
	# Token有效期
	timeout: 2592000
	# Token风格
	token-style: uuid
	
	# 配置 Sa-Token 单独使用的 Redis 连接 
	alone-redis: 
		# Redis数据库索引（默认为0）
		database: 2
		# Redis服务器地址
		host: 127.0.0.1
		# Redis服务器连接端口
		port: 6379
		# Redis服务器连接密码（默认为空）
		password: 
		# 连接超时时间
		timeout: 10s

spring: 
	# 配置业务使用的 Redis 连接 
	redis: 
		# Redis数据库索引（默认为0）
		database: 0
		# Redis服务器地址
		host: 127.0.0.1
		# Redis服务器连接端口
		port: 6379
		# Redis服务器连接密码（默认为空）
		password: 
		# 连接超时时间
		timeout: 10s
```

<!------------- tab:properties 风格  ------------->
``` properties
############## Sa-Token 配置 ############## 
# Token名称
sa-token.token-name=satoken
# Token有效期
sa-token.timeout=2592000
# Token风格
sa-token.token-style=uuid

############## 配置 Sa-Token 单独使用的 Redis 连接  ############## 
# Redis数据库索引（默认为0）
sa-token.alone-redis.database=2
# Redis服务器地址
sa-token.alone-redis.host=127.0.0.1
# Redis服务器连接端口
sa-token.alone-redis.port=6379
# Redis服务器连接密码（默认为空）
sa-token.alone-redis.password=
# 连接超时时间
sa-token.alone-redis.timeout=10s

############## 配置业务使用的 Redis 连接 ############## 
# Redis数据库索引（默认为0）
spring.redis.database=0
# Redis服务器地址
spring.redis.host=127.0.0.1
# Redis服务器连接端口
spring.redis.port=6379
# Redis服务器连接密码（默认为空）
spring.redis.password=
# 连接超时时间
spring.redis.timeout=10s

```

<!---------------------------- tabs:end ---------------------------->


具体可参考示例：[码云：application.yml](https://gitee.com/dromara/sa-token/blob/master/sa-token-demo/sa-token-demo-alone-redis/src/main/resources/application.yml)

集群配置说明: alone-redis同样可以配置集群(cluster模式和sentinel模式), 且基础配置参数和spring redis集群配置别无二致

集群配置示例可参考demo项目sa-token-demo-alone-redis-cluster


### 3、测试
新建Controller测试一下 
``` java
@RestController
@RequestMapping("/test/")
public class TestController {

	@Autowired
	StringRedisTemplate stringRedisTemplate;
	
	// 测试Sa-Token缓存
	@RequestMapping("login")
	public SaResult login(@RequestParam(defaultValue="10001") String id) {
		System.out.println("--------------- 测试Sa-Token缓存");
		StpUtil.login(id);	
		return SaResult.ok();
	}
	
	// 测试业务缓存
	@RequestMapping("test")
	public SaResult test() {
		System.out.println("--------------- 测试业务缓存");
		stringRedisTemplate.opsForValue().set("hello", "Hello World");
		return SaResult.ok();
	}
	
}
```

分别访问两个接口，观察Redis中增加的数据 

![alone-redis](https://oss.dev33.cn/sa-token/doc/alone-redis.png 's-w')

测试完毕！

### 4、注意点
目前 Sa-Token-Alone-Redis 仅对以下插件有 Redis 分离效果：
- sa-token-redis
- sa-token-redis-jackson
- sa-token-redis-fastjson
- sa-token-redis-fastjson2