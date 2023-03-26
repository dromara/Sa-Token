# Sa-Token-SSO整合-常见问题总结

SSO 集成常见问题整理

[[toc]]

--- 

### 问：在模式一与模式二中，Client端 必须通过 Alone-Redis 插件来访问 Redis 吗？
答：不必须，只是推荐，权限缓存与业务缓存分离后会减少 `SSO-Redis` 的访问压力，且可以避免多个 `Client端` 的缓存读写冲突。


### 问：搭建好 sso-server 或 sso-client 服务后，访问返回：`{"msg": "not handle"}`。

返回这个信息，代表你访问的路由有错误，比如说：

- 统一认证登录地址是：`http://{host}:{port}/sso/auth`。
- 而你访问的却是：`http://{host}:{port}/sso/auth2`。

地址写错了，框架就不会处理这个请求，会直接返回 `{"msg": "not handle"}`，所有开放地址可参考：[SSO 开放接口](/sso/sso-apidoc)

如果仔细检查地址后没有写错，却依然返回了这个信息，那有可能是对应的接口没有打开，比如说：

- sso-server 端的单点注销地址：`http://{host}:{port}/sso/signout`；
- sso-client 端的注销地址：`http://{host}:{port}/sso/logout`；

都需要在配置文件配置：`sa-token.sso.is-slo=true`后，才会打开。


### 问：我参照文档搭建SSO-Client，一直提示：Ticket无效，请问怎么回事？
如果使用的是模式二，出现此异常概率最大的原因是因为 `Client` 与 `Server` 没有连接同一个Redis，SSO模式二中两者必须连接同一个 Redis 才可以登录成功。

你可能会问：我看配置文件明明是同一个啊？

我的建议是：排查时不要仅凭肉眼判断，分别在你的 `Client` 与 `Server` 启动后调用 `SaManager.getSaTokenDao().set("name", "value", 100000);` 
随便写入一个值，看看能不能根据你的预期写进同一个Redis里，如果能的话才能证明 `Client` 与 `Server` 连接的Reids 是同一个，再进行下一步排查。

``` java
@SpringBootApplication
public class SaSsoServerApplication {
	public static void main(String[] args) {
		SpringApplication.run(SaSsoServerApplication.class, args);
		System.out.println("\n------ Sa-Token-SSO 统一认证中心启动成功 ");
		// 分别在 Client 与 Server 启动后调用 set 数据代码，看看能否根据预期写入同一个 reids 
		SaManager.getSaTokenDao().set("name", "value", 100000);
	}	
}
```

如果使用的是模式三，则排查是否有重复校验 ticket 的代码，一个 ticket 码只能使用一次，多次重复使用就会提示这个。


### 模式一或者模式二报错：Could not write JSON: No serializer found for class com.pj.sso.SysUser and no properties discovered to create BeanSerializer 

一般是因为在 sso-server 端往 session 上写入了某个实体类（比如 User），而在 sso-client 端没有这个实体类，导致反序列化失败。

解决方案：在 sso-client 也新建上这个类，而且包名需要与 sso-server 端的一致（直接从 sso-server 把实体类复制过来就好了）



### 问：SSO模式二或模式三，第一个 client 登录成功之后再访问其它两个 client 不会自动登录，需要点一下登录按钮才会登录上？
答：这是正常现象，系统 1 登录成功之后，系统 2 与系统 3 需要点击登录按钮，才会登录成功。

> 第一个系统，需要：点击 [登录] 按钮 -> 跳转到登录页 -> 输账号密码 -> 登录成功 <br>
> 第二个系统，需要：点击 [登录] 按钮 -> 登录成功 <br>
> 第三个系统，需要：点击 [登录] 按钮 -> 登录成功 
> 
> （系统二、三 免去重复跳转登录页输入账号密码的步骤）


### 追问：那我是否可以设计成不需要点登录按钮的，只要访问页面，它就能登录成功？
可以的。

其实思路很简单，我们只需要给 client 项目加个过滤器，拦截所有请求，只要检测到未登录就将其重定向至登录页面：

``` java
/**
 * Sa-Token 配置类
 */
@Configuration
public class SaTokenConfigure implements WebMvcConfigurer {
    /** 注册 [Sa-Token全局过滤器] */
    @Bean
    public SaServletFilter getSaServletFilter() {
        return new SaServletFilter()
                .addInclude("/**")
                .addExclude("/sso/*", "/favicon.ico")   // 这里需要注意排除掉 /sso/* 相关请求不拦截，否则就会触发无限重定向
                .setAuth(obj -> {
                    /*
                     * 这里会被分为两种情况：
                     *  情况1：这个请求在当前 client 已经登录，此时会顺利进入网站
                     *  情况2：这个请求在当前 client 尚未登录，此时会被拦截，重定向至当前系统的 /sso/login?back=当前地址
                     *
                     *  情况2会带领着用户继续重定向至 sso-server 认证中心，此时又分为两种情况：
                     *      情况2.1：此用户在 sso-server 尚未登录，此时会停留在登录页面，开始输入账号密码进行登录
                     *      情况2.2：此用户在 sso-server 已经登录（这证明此用户已经在其它至少一个 sso-client 处完成了登录）
                     *              此时用户会继续重定向回当前 client，并携带 ticket 参数，完成登录。
                     */
                    if(StpUtil.isLogin() == false) {
                        String back = SaFoxUtil.joinParam(SaHolder.getRequest().getUrl(), SpringMVCUtil.getRequest().getQueryString());
                        SaHolder.getResponse().redirect("/sso/login?back=" + SaFoxUtil.encodeUrl(back));
                        SaRouter.back();
                    }
                })
                ;
    }
}
```


更多登录姿势可以参考 [[何时引导用户去登录]](/sso/sso-custom-login) 给出的建议进行设计。


### 问：如果 sso-client 端我没有集成 sa-token-sso，如何对接？
需要手动调用 http 请求来对接 sso-server 开放的接口，参考示例：[sa-token-demo-sso3-client-nosdk](https://gitee.com/dromara/sa-token/tree/master/sa-token-demo/sa-token-demo-sso3-client-nosdk)


### 问：如果 sso-client 端不是 java语言，可以对接吗？
可以，只不过有点麻烦，基本思路和上个问题一致，需要手动调用 http 请求来对接 sso-server 开放的接口，参考：
[SSO-Server 认证中心开放接口](/sso/sso-apidoc)


### 问：将旧有系统改造为单点登录时，应该注意哪些？
答：建议不要把其中一个系统改造为SSO服务端，而是新起一个项目作为 SSO-Server 端，所有旧有项目全部作为 Client 端与此对接。


### 问：怎么在一个项目里同时搭建 sso-server 和 sso-client？

难点在于解决两边的路由冲突，示例代码：

``` java
// Sa-Token SSO Controller 
@RestController
public class SsoController {
	
	// 处理 SSO-Server 端所有请求 
	@RequestMapping({"/sso/auth", "/sso/doLogin", "/sso/checkTicket", "/sso/signout"})
	public Object ssoServerRequest() {
		return SaSsoProcessor.instance.serverDister();
	}
	
	// 处理 SSO-Client 端所有请求 
	@RequestMapping({"/sso/login", "/sso/logout", "/sso/logoutCall"})
	public Object ssoClientRequest() {
		return SaSsoProcessor.instance.clientDister();
	}
	
	// 配置SSO相关参数 
	@Autowired
	private void configSso(SaSsoConfig sso) {
		// SSO配置代码，参考文档前几章 ... 
	}
	
}
```


### 问：我一个项目里有两套账号体系，都需要单点登录，怎么在一个项目里同时搭建两个 sso-server 服务？

首先推荐你不要在一个项目里同时搭建两个 sso-server，建议创建两个项目，分别搭建各自的 sso-server 服务。

如果一定要在一个项目中搭建两套 sso-server 服务，参考方案如下：

第一套，还是用前面几章文档给出的示例代码。

第二套，修改一些参数属性，使之与第一套不产生冲突，参考代码如下：

``` java
/**
 * Sa-Token-SSO 第二套 SSO-Server端 Controller 
 */
@RestController
public class SsoUserServerController {

	/**
	 * 新建一个 SaSsoProcessor 请求处理器 
	 */
	public static SaSsoProcessor ssoUserProcessor = new SaSsoProcessor();
	static {
		// 自定义一个 SaSsoTemplate 对象
		SaSsoTemplate ssoUserTemplate = new SaSsoTemplate() {
			// 使用的会话对象 是自定义的 StpUserUtil 
			@Override
			public StpLogic getStpLogic() {
				return StpUserUtil.stpLogic;
			}
		};
		// 让这个SSO请求处理器，使用的路由前缀是 /sso-user，而不是原先的 /sso 
		ssoUserTemplate.apiName.replacePrefix("/sso-user");
		
		// 给这个 SSO 请求处理器使用自定义的 SaSsoTemplate 对象 
		ssoUserProcessor.ssoTemplate = ssoUserTemplate;
	}

	/*
	 * 第二套 sso-server 服务：处理所有SSO相关请求 
	 * 		http://{host}:{port}/sso-user/auth			-- 单点登录授权地址，接受参数：redirect=授权重定向地址 
	 * 		http://{host}:{port}/sso-user/doLogin		-- 账号密码登录接口，接受参数：name、pwd 
	 * 		http://{host}:{port}/sso-user/checkTicket	-- Ticket校验接口（isHttp=true时打开），接受参数：ticket=ticket码、ssoLogoutCall=单点注销回调地址 [可选] 
	 * 		http://{host}:{port}/sso-user/signout		-- 单点注销地址（isSlo=true时打开），接受参数：loginId=账号id、secretkey=接口调用秘钥 
	 */
	@RequestMapping("/sso-user/*")
	public Object ssoUserRequest() {
		return ssoUserProcessor.serverDister();
	}

	// 自定义 doLogin 方法 */
	// 注意点：
	// 		1、第2套 sso-server 对应的 RestApi 登录接口也应该更换为 /sso-user/doLogin，而不是原先的 /sso/doLogin 
	// 		2、在这里，登录函数要使用自定义的 StpUserUtil.login()，而不是原先的 StpUtil.login() 
	@RequestMapping("/sso-user/doLogin")
	public Object ssoUserRequest(String name, String pwd) {
		if("sa".equals(name) && "123456".equals(pwd)) {
			StpUserUtil.login(10001);
			return SaResult.ok("登录成功！").setData(StpUserUtil.getTokenValue());
		}
		return SaResult.error("登录失败！");
	}
	
}
```



<br/>

--- 

<details>
<summary>还有其它问题？</summary>
	
可以加群反馈一下，比较典型的问题我们解决之后都会提交到此页面方便大家快速排查
</details>




