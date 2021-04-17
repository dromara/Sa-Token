# Spring WebFlux 集成 Sa-Token 示例

WebFlux基于Reactor响应式模型开发，有着与标准ServletAPI完全不同的底层架构，因此要适配WebFlux, 必须提供与Reactor相关的整合实现，
本篇将以WebFlux为例，展示sa-token与Reactor响应式模型web框架相整合的示例, **你可以用同样方式去对接其它Reactor模型Web框架**

整合示例在官方仓库的`/sa-token-demo-webflux`文件夹下，如遇到难点可结合源码进行测试学习

---

### 1、创建项目
在IDE中新建一个SpringBoot项目，例如：`sa-token-demo-webflux`（不会的同学请自行百度或者参考github示例）


### 2、设置依赖
在 `pom.xml` 中添加依赖：

``` xml 
<!-- Sa-Token 权限认证（Reactor响应式集成）, 在线文档：http://sa-token.dev33.cn/ -->
<dependency>
	<groupId>cn.dev33</groupId>
	<artifactId>sa-token-reactor-spring-boot-starter</artifactId>
	<version>1.16.0.RELEASE</version>
</dependency>
```


### 4、创建启动类
在项目中新建包 `com.pj` ，在此包内新建主类 `SaTokenDemoApplication.java`，输入以下代码：

``` java
@SpringBootApplication
public class SaTokenDemoApplication {
	public static void main(String[] args) throws JsonProcessingException {
		SpringApplication.run(SaTokenDemoApplication.class, args);
		System.out.println("启动成功：sa-token配置如下：" + SaTokenManager.getConfig());
	}
}
```

### 5、创建全局过滤器
新建`SaTokenConfigure.java`，注册Sa-Token的全局过滤器
``` java
/**
 * [Sa-Token 权限认证] 全局配置类 
 */
@Configuration
public class SaTokenConfigure {
	/**
     * 注册 [sa-token全局过滤器] 
     */
    @Bean
    public SaReactorFilter getSaReactorFilter() {
        return new SaReactorFilter()
        		// 指定 [拦截路由]
        		.addInclude("/**")
        		// 指定 [放行路由]
        		.addExclude("/favicon.ico")
        		// 指定[认证函数]: 每次请求执行 
        		.setAuth(r -> {
        			System.out.println("---------- sa全局认证");
                    // SaRouterUtil.match("/test/test", () -> StpUtil.checkLogin());
        		})
        		// 指定[异常处理函数]：每次[认证函数]发生异常时执行此函数 
        		.setError(e -> {
        			System.out.println("---------- sa全局异常 ");
        			return AjaxJson.getError(e.getMessage());
        		})
        		;
    }
}
```
?> 你只需要按照此格式复制代码即可，有关过滤器的详细用法，会在之后的章节详细介绍


### 6、创建测试Controller
``` java
@RestController
@RequestMapping("/user/")
public class UserController {

	// 测试登录，浏览器访问： http://localhost:8081/user/doLogin?username=zhang&password=123456
	@RequestMapping("doLogin")
	public String doLogin(String username, String password) {
		// 此处仅作模拟示例，真实项目需要从数据库中查询数据进行比对 
		if("zhang".equals(username) && "123456".equals(password)) {
			StpUtil.setLoginId(10001);
			return "登录成功";
		}
		return "登录失败";
	}

	// 查询登录状态，浏览器访问： http://localhost:8081/user/isLogin
	@RequestMapping("isLogin")
	public String isLogin(String username, String password) {
		return "当前会话是否登录：" + StpUtil.isLogin();
	}
	
}
```

### 7、运行
启动代码，从浏览器依次访问上述测试接口：

![运行结果](https://oss.dev33.cn/sa-token/doc/test-do-login.png)

![运行结果](https://oss.dev33.cn/sa-token/doc/test-is-login.png)


**注意事项**

更多使用示例请参考官方仓库demo



