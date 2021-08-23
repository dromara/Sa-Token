# Sa-Token-Alone-Redis 独立Redis插件
--- 

Sa-Token默认的Redis集成方式会把权限数据和业务缓存放在一起，但在部分场景下我们需要将他们彻底分离开来，比如：

> 搭建两个Redis服务器，一个专门用来做业务缓存，另一台专门存放Sa-Token权限数据 

要将Sa-Token的数据单独抽离出来很简单，你只需要为Sa-Token单独配置一个Redis连接信息即可 

--- 


### 1、首先引入Alone-Redis依赖 

``` xml
<!-- Sa-Token插件：权限缓存与业务缓存分离 -->
<dependency>
	<groupId>cn.dev33</groupId>
	<artifactId>sa-token-alone-redis</artifactId>
	<version>${sa.top.version}</version>
</dependency>
```


### 2、然后在application.yml中增加配置
``` yml
# Sa-Token配置
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

具体可参考示例：[码云：application.yml](https://gitee.com/dromara/sa-token/blob/dev/sa-token-demo/sa-token-demo-alone-redis/src/main/resources/application.yml)


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
	public AjaxJson login(@RequestParam(defaultValue="10001") String id) {
		System.out.println("--------------- 测试Sa-Token缓存");
		StpUtil.login(id);	
		return AjaxJson.getSuccess();
	}
	
	// 测试业务缓存
	@RequestMapping("test")
	public AjaxJson test() {
		System.out.println("--------------- 测试业务缓存");
		stringRedisTemplate.opsForValue().set("hello", "Hello World");
		return AjaxJson.getSuccess();
	}
	
}
```

分别访问两个接口，观察Redis中增加的数据 

![alone-redis](https://oss.dev33.cn/sa-token/doc/alone-redis.png 's-w')

测试完毕！
