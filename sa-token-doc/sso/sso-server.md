# 搭建统一认证中心 SSO-Server  

在开始SSO三种模式的对接之前，我们必须先搭建一个 SSO-Server 认证中心 

> [!TIP| label:demo] 
> 搭建示例在官方仓库的 `/sa-token-demo/sa-token-demo-sso/sa-token-demo-sso-server/`，如遇到难点可结合源码进行测试学习，demo里有制作好的登录页面 

--- 

### 1、添加依赖 
创建 SpringBoot 项目 `sa-token-demo-sso-server`，在引入 SpringBoot 依赖的基础上，继续引入：

<!---------------------------- tabs:start ---------------------------->
<!-------- tab:Maven 方式 -------->
``` xml 
<!-- Sa-Token 权限认证，在线文档：https://sa-token.cc -->
<dependency>
	<groupId>cn.dev33</groupId>
	<artifactId>sa-token-spring-boot-starter</artifactId>
	<version>${sa.top.version}</version>
</dependency>

<!-- Sa-Token 插件：整合SSO -->
<dependency>
	<groupId>cn.dev33</groupId>
	<artifactId>sa-token-sso</artifactId>
	<version>${sa.top.version}</version>
</dependency>
        
<!-- Sa-Token 插件：整合 RedisTemplate -->
<dependency>
	<groupId>cn.dev33</groupId>
	<artifactId>sa-token-redis-template</artifactId>
	<version>${sa.top.version}</version>
</dependency>
<dependency>
	<groupId>org.apache.commons</groupId>
	<artifactId>commons-pool2</artifactId>
</dependency>

<!-- 视图引擎（在前后端不分离模式下提供视图支持） -->
<dependency>
	<groupId>org.springframework.boot</groupId>
	<artifactId>spring-boot-starter-thymeleaf</artifactId>
</dependency>

<!-- Sa-Token 插件：整合 Forest 请求工具 (模式三需要通过 http 请求推送消息) -->
<dependency>
	<groupId>cn.dev33</groupId>
	<artifactId>sa-token-forest</artifactId>
	<version>${sa.top.version}</version>
</dependency>
```
<!-------- tab:Gradle 方式 -------->
``` gradle
// Sa-Token 权限认证，在线文档：https://sa-token.cc
implementation 'cn.dev33:sa-token-spring-boot-starter:${sa.top.version}'

// Sa-Token 插件：整合SSO
implementation 'cn.dev33:sa-token-sso:${sa.top.version}'

// Sa-Token 整合 RedisTemplate
implementation 'cn.dev33:sa-token-redis-template:${sa.top.version}'
implementation 'org.apache.commons:commons-pool2'

// 视图引擎（在前后端不分离模式下提供视图支持）
implementation 'org.springframework.boot:spring-boot-starter-thymeleaf'

// Sa-Token 插件：整合 Forest 请求工具 (模式三需要通过 http 请求推送消息)
implementation 'cn.dev33:sa-token-forest:${sa.top.version}'
```
<!---------------------------- tabs:end ---------------------------->


> [!NOTE| label:引包简化] 
> 除了 `sa-token-spring-boot-starter` 和 `sa-token-sso` 以外，其它包都是可选的：
> 
> - 在 SSO 模式三时 Redis 相关包是可选的。
> - 在前后端分离模式下可以删除 thymeleaf 相关包。
> - 在不需要 SSO 模式三单点注销的情况下可以删除 http 工具包。
> 
> 建议先完整测试三种模式之后再对 pom 依赖进行酌情删减。


### 2、开放认证接口  
新建 `SsoServerController`，用于对外开放接口：

``` java
/**
 * Sa-Token-SSO Server端 Controller 
 */
@RestController
public class SsoServerController {

	/**
	 * SSO-Server端：处理所有SSO相关请求 
	 * 		http://{host}:{port}/sso/auth			-- 单点登录授权地址
	 * 		http://{host}:{port}/sso/doLogin		-- 账号密码登录接口，接受参数：name、pwd
	 * 		http://{host}:{port}/sso/signout		-- 单点注销地址（isSlo=true时打开）
	 */
	@RequestMapping("/sso/*")
	public Object ssoRequest() {
		return SaSsoServerProcessor.instance.dister();
	}

	/**
	 * 配置SSO相关参数 
	 */
	@Autowired
	private void configSso(SaSsoServerTemplate ssoServerTemplate) {
		// 配置：未登录时返回的View 
		ssoServerTemplate.strategy.notLoginView = () -> {
			// 简化模拟表单
			String doLoginCode =
					"fetch(`/sso/doLogin?name=${document.querySelector('#name').value}&pwd=${document.querySelector('#pwd').value}`) " +
					" .then(res => res.json()) " +
					" .then(res => { if(res.code === 200) { location.reload() } else { alert(res.msg) } } )";
			String res =
					"<h2>当前客户端在 SSO-Server 认证中心尚未登录，请先登录</h2>" +
					"用户：<input id='name' /> <br> " +
					"密码：<input id='pwd' /> <br>" +
					"<button onclick=\"" + doLoginCode + "\">登录</button>";
			return res;
		};
		
		// 配置：登录处理函数 
		ssoServerTemplate.strategy.doLoginHandle = (name, pwd) -> {
			// 此处仅做模拟登录，真实环境应该查询数据库进行登录 
			if("sa".equals(name) && "123456".equals(pwd)) {
				StpUtil.login(10001);
				return SaResult.ok("登录成功！").setData(StpUtil.getTokenValue());
			}
			return SaResult.error("登录失败！");
		};
	}
	
}
```

注意：在`doLoginHandle`函数里如果要获取 name, pwd 以外的参数，可通过`SaHolder.getRequest().getParam("xxx")`来获取。
<!-- - `deviceId` 参数代表登录端设备id，是为了后续的 “单设备注销” 功能做准备，如果不需要此功能可以省略此参数。 -->

全局异常处理：
``` java
@RestControllerAdvice
public class GlobalExceptionHandler {
	// 全局异常拦截 
	@ExceptionHandler
	public SaResult handlerException(Exception e) {
		e.printStackTrace(); 
		return SaResult.error(e.getMessage());
	}
}
```


### 3、application.yml配置

<!---------------------------- tabs:start ---------------------------->

<!------------- tab:yaml 风格  ------------->
``` yml
# 端口
server:
    port: 9000

# Sa-Token 配置
sa-token: 
    # 打印操作日志
    is-log: true
	
    # SSO-模式一相关配置  (非模式一不需要配置) 
    # cookie: 
        # 配置 Cookie 作用域 
        # domain: stp.com 
        
    # SSO-Server 配置
    sso-server:
        # Ticket有效期 (单位: 秒)，默认五分钟 
        ticket-timeout: 300
        # 应用列表：配置接入的应用信息
        clients:
            # 应用 sso-client1：采用模式一对接 (同域、同Redis)
            sso-client1:
                client: sso-client1
                allow-url: "*"
            # 应用 sso-client2：采用模式二对接 (跨域、同Redis)
            sso-client2:
                client: sso-client2
                allow-url: "*"
                secret-key: SSO-C2-kQwIOrYvnXmSDkwEiFngrKidMcdrgKor
            # 应用 sso-client3：采用模式三对接 (跨域、跨Redis)
            sso-client3:
                # 应用名称
                client: sso-client3
                # 允许授权地址
                allow-url: "*"
                # 是否接收消息推送
                is-push: true
                # 消息推送地址
                push-url: http://sa-sso-client1.com:9003/sso/pushC
                # 接口调用秘钥 (如果不配置则使用全局默认秘钥)
                secret-key: SSO-C3-kQwIOrYvnXmSDkwEiFngrKidMcdrgKor
        
spring: 
    # Redis配置 （SSO模式一和模式二使用Redis来同步会话）
    redis:
        # Redis数据库索引（默认为0）
        database: 1
        # Redis服务器地址
        host: 127.0.0.1
        # Redis服务器连接端口
        port: 6379
        # Redis服务器连接密码（默认为空）
        password: 
```
<!------------- tab:properties 风格  ------------->
``` properties
# 端口
server.port=9000

################## Sa-Token 配置 ##################
# 打印操作日志
sa-token.is-log=true

# SSO-模式一相关配置  (非模式一不需要配置) 
# 配置 Cookie 作用域 
# sa-token.cookie.domain=stp.com

# SSO-Server 配置
# Ticket有效期 (单位: 秒)，默认五分钟 
sa-token.sso-server.ticket-timeout=300

# 应用列表：配置接入的应用信息
# 应用 sso-client1：采用模式一对接 (同域、同Redis)
sa-token.sso-server.clients.sso-client1.client=sso-client1
sa-token.sso-server.clients.sso-client1.allow-url=*

# 应用 sso-client2：采用模式二对接 (跨域、同Redis)
sa-token.sso-server.clients.sso-client2.client=sso-client2
sa-token.sso-server.clients.sso-client2.allow-url=*
sa-token.sso-server.clients.sso-client2.secret-key=SSO-C2-kQwIOrYvnXmSDkwEiFngrKidMcdrgKor

# 应用 sso-client3：采用模式三对接 (跨域、跨Redis)
# 应用名称
sa-token.sso-server.clients.sso-client3.client=sso-client3
# 允许授权地址
sa-token.sso-server.clients.sso-client3.allow-url=*
# 是否接收消息推送
sa-token.sso-server.clients.sso-client3.is-push=true
# 消息推送地址
sa-token.sso-server.clients.sso-client3.push-url=http://sa-sso-client1.com:9003/sso/pushC
# 接口调用秘钥 (如果不配置则使用全局默认秘钥)
sa-token.sso-server.clients.sso-client3.secret-key=SSO-C3-kQwIOrYvnXmSDkwEiFngrKidMcdrgKor

# Redis配置 （SSO模式一和模式二使用Redis来同步会话）
# Redis数据库索引（默认为0）
spring.redis.database=1
# Redis服务器地址
spring.redis.host=127.0.0.1
# Redis服务器连接端口
spring.redis.port=6379
# Redis服务器连接密码（默认为空）
spring.redis.password=
```

<!---------------------------- tabs:end ---------------------------->

注意点：`sa-token.sso-server.clients.xxx.allow-url`为了方便测试配置为`*`，线上生产环境一定要配置为详细 URL 地址 （之后的章节我们会详细阐述此配置项）


### 4、创建启动类
``` java 
@SpringBootApplication
public class SaSsoServerApplication {
	public static void main(String[] args) {
		SpringApplication.run(SaSsoServerApplication.class, args);

		System.out.println();
		System.out.println("---------------------- Sa-Token SSO 统一认证中心启动成功 ----------------------");
		System.out.println("配置信息：" + SaSsoManager.getServerConfig());
		System.out.println("统一认证登录地址：http://sa-sso-server.com:9000/sso/auth");
		System.out.println("测试前需要根据官网文档修改 hosts 文件，测试账号密码：sa / 123456");
		System.out.println();
	}
}
```

启动项目，不出意外的情况下我们将看到如下输出：

![sso-server-start](https://oss.dev33.cn/sa-token/doc/sso/sso-server-start.png 's-w-sh')

访问统一授权地址（仅测试 SSO-Server 是否部署成功，暂时还不需要点击登录）：
- [http://localhost:9000/sso/auth](http://localhost:9000/sso/auth)

![sso-server-init-login.png](https://oss.dev33.cn/sa-token/doc/sso/sso-server-init-login--v43.png 's-w-sh')

可以看到这个页面目前非常简陋，这是因为我们以上的代码示例，主要目标是为了带大家从零搭建一个可用的SSO认证服务端，所以就对一些不太必要的步骤做了简化。

大家可以下载运行一下官方仓库里的示例`/sa-token-demo/sa-token-demo-sso/sa-token-demo-sso-server/`，里面有制作好的登录页面：

![sso-server-init-login2.png](https://oss.dev33.cn/sa-token/doc/sso/sso-server-init-login2.png 's-w-sh')

默认账号密码为：`sa / 123456`，先别着急点击登录，因为我们还没有搭建对应的 Client 端项目，
真实项目中我们一般不会直接从浏览器访问 `/sso/auth` 授权地址的，我们需要在 Client 端点击登录按钮重定向而来。


---

现在我们先来看看除了 `/sso/auth` 统一授权地址，这个 SSO-Server 认证中心还开放了哪些API：[SSO-Server 认证中心开放接口](/sso/sso-apidoc)。







