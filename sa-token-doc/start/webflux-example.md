# Spring WebFlux 集成 Sa-Token 示例

**Reactor** 是一种非阻塞的响应式模型，本篇将以 **WebFlux** 为例，展示 Sa-Token 与 Reactor 响应式模型框架相整合的示例，
**你可以用同样方式去对接其它 Reactor 模型框架（Netty、ShenYu、SpringCloud Gateway等）**

整合示例在官方仓库的`/sa-token-demo/sa-token-demo-webflux`文件夹下，如遇到难点可结合源码进行测试学习

!> WebFlux 常用于微服务网关架构中，如果您的应用基于单体架构且非 Reactor 模型，可以先跳过本章 

---

### 1、创建项目
在 IDE 中新建一个 SpringBoot 项目，例如：`sa-token-demo-webflux`


### 2、添加依赖
在项目中添加依赖：

<!---------------------------- tabs:start ------------------------------>
<!-------- tab:Maven 方式 -------->
``` xml 
<!-- Sa-Token 权限认证（Reactor响应式集成），在线文档：http://sa-token.dev33.cn/ -->
<dependency>
	<groupId>cn.dev33</groupId>
	<artifactId>sa-token-reactor-spring-boot-starter</artifactId>
	<version>${sa.top.version}</version>
</dependency>
```
<!-------- tab:Gradle 方式 -------->
``` gradle
// Sa-Token 权限认证（Reactor响应式集成），在线文档：http://sa-token.dev33.cn/
implementation 'cn.dev33:sa-token-reactor-spring-boot-starter:${sa.top.version}'
```
<!---------------------------- tabs:end ------------------------------>




### 3、创建启动类
在项目中新建包 `com.pj` ，在此包内新建主类 `SaTokenDemoApplication.java`，输入以下代码：

``` java
@SpringBootApplication
public class SaTokenDemoApplication {
	public static void main(String[] args) throws JsonProcessingException {
		SpringApplication.run(SaTokenDemoApplication.class, args);
		System.out.println("启动成功：Sa-Token配置如下：" + SaManager.getConfig());
	}
}
```

### 4、创建全局过滤器
新建`SaTokenConfigure.java`，注册 Sa-Token 的全局过滤器
``` java
/**
 * [Sa-Token 权限认证] 全局配置类 
 */
@Configuration
public class SaTokenConfigure {
	/**
     * 注册 [Sa-Token全局过滤器] 
     */
    @Bean
    public SaReactorFilter getSaReactorFilter() {
        return new SaReactorFilter()
        		// 指定 [拦截路由]
        		.addInclude("/**")    /* 拦截所有path */
        		// 指定 [放行路由]
        		.addExclude("/favicon.ico")
        		// 指定[认证函数]: 每次请求执行 
        		.setAuth(obj -> {
        			System.out.println("---------- sa全局认证");
                    // SaRouter.match("/test/test", () -> StpUtil.checkLogin());
        		})
        		// 指定[异常处理函数]：每次[认证函数]发生异常时执行此函数 
        		.setError(e -> {
        			System.out.println("---------- sa全局异常 ");
        			return SaResult.error(e.getMessage());
        		})
        		;
    }
}
```
你只需要按照此格式复制代码即可，有关过滤器的详细用法，会在之后的章节详细介绍。


### 5、创建测试Controller
``` java
@RestController
@RequestMapping("/user/")
public class UserController {

	// 测试登录，浏览器访问： http://localhost:8081/user/doLogin?username=zhang&password=123456
	@RequestMapping("doLogin")
	public String doLogin(String username, String password) {
		// 此处仅作模拟示例，真实项目需要从数据库中查询数据进行比对 
		if("zhang".equals(username) && "123456".equals(password)) {
			StpUtil.login(10001);
			return "登录成功";
		}
		return "登录失败";
	}

	// 查询登录状态，浏览器访问： http://localhost:8081/user/isLogin
	@RequestMapping("isLogin")
	public String isLogin() {
		return "当前会话是否登录：" + StpUtil.isLogin();
	}
	
}
```

### 6、运行
启动代码，从浏览器依次访问上述测试接口：

![运行结果](https://oss.dev33.cn/sa-token/doc/test-do-login.png)

![运行结果](https://oss.dev33.cn/sa-token/doc/test-is-login.png)


**注意事项：**

更多使用示例请参考官方仓库demo



