# 常见问题排查
本篇整理大家在群聊里经常提问的一些问题，如有补充，欢迎提交pr

[[toc]]

--- 

<!-- ---------------------------- 常见报错 ----------------------------- -->

## 一、常见报错


### Q：报错：SaTokenContext 上下文尚未初始化

报这个错说明你在异步上下文/响应式上下文里调用了 Sa-Token 的同步 API，解决方案参考：[异步 & Mock 上下文](/fun/async--mock)


### Q：报错：NotLoginException：xxx

这个错是说明调用接口的人没有通过登录校验，请注意：**通常情况下，异常提示语已经描述清楚了没有通过校验的具体原因：**
   
**如果是：未能读取到有效Token**
- 可能1：前端没有提交 Token（最好从前端f12控制台看看请求参数里有 token 吗）。
- 可能2：前端提交了 Token，但是参数名不对。默认参数名是 `satoken`，可通过配置文件 `sa-token.token-name: satoken` 来更改。
- 可能3：前端提交了 Token，但是你配置了框架不读取，比如说你配置了 `is-read-header=false`（关闭header读取），此时你再从 header 里提交token，框架就无法读取到。
- 可能4：前端提交了 Token，但是 Token前缀 不对，可参考：[自定义 Token 前缀](/up/token-prefix)
- 可能5：你的项目属于前后端分离架构，此时浏览器默认不自动提交 Cookie，参考：[前后端分离](/up/not-cookie) 
- 可能6：你使用了 Nginx 反向代理，而且配置了 自定义Token名称，而且自定义的名称还带有下划线（比如 shop_token），而且还是你的项目还是从 Header头提交Token的，此时 Nginx 默认会吞掉你的下划线参数，可参考：[nginx做转发时，带下划线的header参数丢失](https://blog.csdn.net/zfw_666666/article/details/124420828)
- 可能7：可能是跨域了，导致前端提交不上 token，看看前端浏览器有没有跨域的报错。

**如果是：Token无效：6ad93254-b286-4ec9-9997-4430b0341ca0**
- 可能1：前端提交的 token 是乱填的，或者从别的项目拷过来的，或者多个项目一起开发时彼此的 Token 串项目了。
- 可能2：前端提交的 token 已过期（timeout超时了）。
- 可能3：在不集成 Redis 的情况下：颁发 token 后，项目重启了，导致 token 无效。
- 可能4：在集成 Redis 的情况下：颁发 token 后，Redis重启了，导致 token 无效。
- 可能5：你提交的 token 和框架读取到的 token 不一致：
	- 可能5.1：比如说你配置了`is-read-header=false`（关闭header读取），然后你从header提交`token-A`，而框架从Cookie里读取`token-B`，导致鉴权不通过（框架读取顺序为`body->header->cookie`）
	- 可能5.2：比如说你配置了`token-name=x-token`（自定义token名称），此时你从header提交：`satoken:token-A`（参数名没对上），然后框架从header里读取不到你提交的token，转而继续从Cookie读取到了`token-B`。
- 可能6：在集成 jwt 插件的情况下：
	- 如果使用的是 Simple 模式：情况和不集成jwt一样。
	- 如果使用的是 Mixin 和 Stateless 模式：查看这个 token 颁发后是否更改了 `jwtSecretKey` 配置项。
- 可能7：同一账号登录数量超过12个，导致最先登录的被强制注销掉，这个值可以通过 `maxLoginCount` 来配置，默认值12，-1代表不做限制。
- 可能8：在配置了 `is-concurrent=true, is-share=true`的情况下，你和别人共同登录了同一账号，此时对方注销了登录，由于你们使用的是同一个token，导致你这边的会话也失效了。
- 可能9：可能是多账号鉴权的关系，在多账号模式下，如果是 `StpUserUtil.login()` 颁发的token，你从 `StpUtil.checkLogin()` 进行校验，永远都是无效token，因为账号体系没对上。

**如果是：Token已过期：6ad93254-b286-4ec9-9997-4430b0341ca0**
- 可能1：前端提交的 token 已被冻结（active-timeout超时了，比如配置了 active-timeout=120，但是超过了120秒没有访问接口）。
- 可能2：集成jwt，而且使用的是 Mixin 或 Stateless 模式，而且token过期了（timeout超时了）。

**如果是：Token已被顶下线：6ad93254-b286-4ec9-9997-4430b0341ca0**
- 可能1：在项目配置了 `is-concurrent=false` 的前提下，这个账号又被别人登录了，导致旧登录被挤了下去。
- 可能2：这个账号被 `StpUtil.replaced(loginId, device)` 方法强制顶下线了。

**如果是：Token已被踢下线：6ad93254-b286-4ec9-9997-4430b0341ca0**
- 可能1：这个账号被 `StpUtil.kickout(loginId)` 方法强制踢下线了。


### Q：加了注解进行鉴权认证，不生效？
1. 注解鉴权功能默认关闭，两种方式任选其一进行打开：注册注解拦截器、集成AOP模块，参考：[注解式鉴权](/use/at-check)
2. 在Spring环境中, 如果同时配置了`WebMvcConfigurer`和`WebMvcConfigurationSupport`时, 也会导致拦截器失效.
   - **常见场景**: 很多项目中会在`WebMvcConfigurationSupport`中配置`addResourceHandlers`方法开放Swagger等相关静态资源映射, 同时基于Sa-Token添加了`WebMvcConfigurer`配置`addInterceptors`方法注册注解拦截器, 这样会导致注解拦截器失效. 
   - **解决方案**: `WebMvcConfigurer`和`WebMvcConfigurationSupport`只选一个配置, 建议统一通过实现`WebMvcConfigurer`接口进行配置.
3. 如果以上步骤处理后仍然没有效果，加群说明一下复现步骤 



### Q：我加了拦截器鉴权，但是好像没有什么效果，请求没有被拦截住？
- 可能1：这个拦截器可能没有注册成功。
- 可能2：你访问的请求没有进入这个拦截器。

尝试按照下面的代码测试一下看看：

``` java
// 注册拦截器 
@Configuration
public class SaTokenConfigure implements WebMvcConfigurer {
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		System.out.println("--------- flag 1");
		registry.addInterceptor(new SaInterceptor(handle -> {
			System.out.println("--------- flag 2，请求进入了拦截器，访问的 path 是：" + SaHolder.getRequest().getRequestPath());
			StpUtil.checkLogin();  // 登录校验，只有会话登录后才能通过这句代码 
		}))
		.addPathPatterns("/user/**")
		.excludePathPatterns("/user/doLogin");
	}
}
```

在启动时 `flag 1` 被打印出来，才证明拦截器注册成功了，在访问请求时 `flag 2` 被打印出来，才证明请求进入了拦截器。

如果拦截器没有注册成功，则：
<!-- - 可能1：SpringBoot 版本较高（`>= 2.6.0`），请尝试在启动类加上 `@EnableWebMvc` 注解再重新启动。 -->
- 可能1：`SaTokenConfigure` 配置类不在启动类的同包或者子包下，导致没有被 SpringBoot 扫描到。
- 可能2：你的项目启动类上加了 `@ComponentScan("com.xxx")` 注解，导致包扫描范围不正确，请将此注解删除或移动到其它配置类上。
- 可能3：项目属于 Maven 多模块项目，`SaTokenConfigure` 和启动类没有在一个模块，且启动类模块没有引入配置类的模块，导致加载不到。

如果拦截器已经注册成功，但请求没有进入拦截器：
- 可能1：你访问的 path，没有被 `.addPathPatterns("/user/**")` 拦截住，或者被 `.excludePathPatterns("/xxx/xx")` 排除掉了。
- 可能2：你访问的是另一个项目，请把当前项目停掉，看看你的请求还能不能访问成功。

如果请求进入拦截器也成功了，那可能是：
- 可能1：前端访问时提交了会话 Token，且这个 Token 是有效的，通过了拦截器的代码校验。
- 可能2：你访问的 path，和你预期不符，仔细观察一下打印出来的 path 信息，和你的预期相符吗。

注：以上的排查步骤，对过滤器不生效的情形一样适用。


### Q：我使用拦截器鉴权时，明明排除了某个路径却仍然被拦截了？
- 可能1：你的项目可能是跨域了，先把跨域问题解决掉，参考：[解决跨域问题](/fun/cors-filter)
- 可能2：你访问的接口可能是404了，SpringBoot环境下如果访问接口404后，会被转发到`/error`，然后被再次拦截。请确保你访问的 path 有对应的 Controller 承接！
- 可能3：可能拦截器这里并没有拦截，但是又被其他地方拦截了。请先把这个拦截器给注释掉，看看还会不会拦截，如果依然拦截，那说明不是这个拦截器的锅，请仔细查看一下控制台抛出的堆栈信息，定位一下到底是哪行代码拦截住这个请求的。
- 可能4：后端拦截的 path 未必是你前端访问的这个path（特别是经过网关转发后的path可能会有变化），建议先打印一下 path 信息，看看和你预想的是否一致，再做分析。
``` java
@Override
public void addInterceptors(InterceptorRegistry registry) {
	registry.addInterceptor(new SaInterceptor(handle -> {
		try {
			System.out.println("-------- 前端访问path：" + SaHolder.getRequest().getRequestPath());
			StpUtil.checkLogin();
			System.out.println("-------- 此 path 校验成功：" + SaHolder.getRequest().getRequestPath());
		} catch (Exception e) { 
			System.out.println("-------- 此 path 校验失败：" + SaHolder.getRequest().getRequestPath());
			throw e;
		}
	})).addPathPatterns("/**"); 
}
```
- 可能5：可能你只提交了一个请求，但是浏览器自动帮你提交了其它请求，举个例子：首次访问网站时，浏览器一般会自动提交 `/favicon.ico`，所以**你需要找出是哪个path被拦截了**，怎么找呢？用【可能4】的代码来测试找。
- 可能6：你的项目配置了 `context-path` 上下文地址，比如 `server.servlet.context-path=/shop`，注意这个地址是不需要加在拦截器上的：
``` java
// 这是错误示例，不需要把 context-path 上下文参数写在下面的 excludePathPatterns 地址上。
registry.addInterceptor(new SaInterceptor(hadnle -> StpUtil.checkLogin()))
			.addPathPatterns("/**").excludePathPatterns("/shop/user/login");
// 这是正确示例，无论你的 context-path 上下文配置了什么样的值，下面的 excludePathPatterns 地址都不需要写上它
registry.addInterceptor(new SaInterceptor(hadnle -> StpUtil.checkLogin()))
			.addPathPatterns("/**").excludePathPatterns("/user/login");
```
- 可能7：你写了多个匹配规则，请求越过了第一个规则，但又被其它规则拦下来了，例如以下代码：
``` java
// 以下代码，当你未登录访问 `/user/doLogin` 时，会被第1条规则越过，然后被第2条拦下，校验登录，然后抛出异常：`NotLoginException：xxx`
registry.addInterceptor(new SaInterceptor(handler -> {
	SaRouter.match("/**").notMatch("/user/doLogin").check(r -> StpUtil.checkLogin());  // 第1个规则 
	SaRouter.match("/**").notMatch("/article/getList").check(r -> StpUtil.checkLogin());  // 第2个规则 
	SaRouter.match("/**").notMatch("/goods/getList").check(r -> StpUtil.checkLogin());  // 第3个规则 
})).addPathPatterns("/**");
```
- 可能8：你自定义的封装方法，并没有按照你的预想情况执行：
``` java
public void addInterceptors(InterceptorRegistry registry) {
	registry.addInterceptor(new SaInterceptor(handle -> {
		// 调用自定义的 excludePaths() 方法获取数据排除鉴权  
		SaRouter.match("/**").notMatch(excludePaths()).check(r -> StpUtil.checkLogin());
	})).addPathPatterns("/**");
}
// 自定义查询排查鉴权的地址方法 
public static List<String> excludePaths() {
	List<String> list = ... // 从数据源查询...;
	return list;
}
```
如上方法， `excludePaths()` 可能并不会像你预想的一样正确执行返回相应的值，请在 `.notMatch()` 处 `一律先硬编码写固定死值来测试`，这时就有两种情况：
	- 情况1：写固定死值时，代码能正常执行了，那说明你自定义的 `excludePaths()` 方法有问题，执行结果不正确。
	- 情况2：写固定也不行，那说明不是 `excludePaths()` 的问题，那再从其它地方开始排查。



### Q：我在配置文件中加了一些关于 Sa-Token 的配置，但是没有生效。
首先，有没有生效的最佳判断方式是，在main方法中加一个打印，看看打印出来的和你配置文件的一致吗：

``` java
@SpringBootApplication
public class SaTokenApplication {
	public static void main(String[] args) {
		SpringApplication.run(SaTokenApplication.class, args); 
		System.out.println("\n启动成功：Sa-Token配置如下：" + SaManager.getConfig());
	}
}
```

如果不一致，请排查：
- 可能1：项目中还存在代码配置，而代码配置会覆盖 `application.yml` 中配置，详细参考：[框架配置](/use/config)。
- 可能2：你的配置文件名字错误，SpringBoot 项目正常情况下配置文件名称应该是：`application.yml` 或 `application.properties`。
- 可能3：可能是你的配置前缀不对，或者配置缩进不对：
``` yaml
# 错误示例，多加了 spring 前缀
spring:
	sa-token: 
		token-name: xxx-token
# 错误示例，缩进不对
sa-token: 
token-name: xxx-token
# 正确的应该是以 sa-token 开头
sa-token: 
	token-name: xxx-token
```


### Q：我自定义了组件，但是好像没有生效？
1、可能组件没有注入成功，排查方法为在 main 里打印这个组件，是否为自定义的class限定名：
``` java
@SpringBootApplication
public class SaTokenApplication {
	public static void main(String[] args) {
		SpringApplication.run(SaTokenApplication.class, args); 
		System.out.println(SaManager.getStpInterface());  // 打印全局的 StpInterface 实现类 
	}
}
```
如果打印出的是你的自定义实现类，则证明注入成功，如果不是，则证明没有注入成功，请排查：
- 自定义的组件实现类上是否加上了 `@Component` 注解，只有加上这个注解，组件才会被 Spring 自动实例化并注入。
- 自定义的组件实现类是否在启动类的同目录或者子目录上，如果不在则无法被 springboot 启动时扫描，扫描不到也就无法注入。
- 启动类上是否加了 `@ComponentScan` 注解，导致包扫描范围不正确，请将此注解删除或移动到其它配置类上。

2、这个组件注入成功了，但是还没到执行时机，比如 `StpInterface` 组件，只有在鉴权时才会触发，如果你的代码仅仅是登录校验，就不会执行到这个组件。


### Q：集成 Redis 后，明明 Redis 中有值，却还是提示无效Token？

根据以往的处理经验，发生这种情况 90% 的概率是因为你找错了Redis，即：代码连接的Redis和你用管理工具看到的Redis并不是同一个。

你可能会问：我看配置文件明明是同一个啊？

我的回答是：别光看配置文件，不一定准确，在启动时直接执行 `SaManager.getSaTokenDao().set("name", "value", 100000);`，
随便写入一个值，看看能不能根据你的预期写进这个Redis，如果能的话才能证明`代码连接的Reids` 和`你用管理工具看到的Redis` 是同一个，再进行下一步排查。


### Q：报错：无效Same-Token：xxxxxxxxxxx
与之类似的的报错还有：
- SSO模式二时，报错：无效ticket：xxxxxxxxxx
- OAuth2模块跨多个项目搭建Server时：报错无效 Access-Token：xxxxxx
- 微服务做分布式 Session 认证时，报错：无效 Token：xxxxxxxxx
- 等等等等.... 

这些功能有个统一的特点，就是需要多个项目连接同一个 Redis 才能搭建成功，如果连接的不是同一个 Redis，就会导致 Token / ticket 无法互相认证。

你可能会问：我看配置文件明明就是连接的同一个 Redis 啊？

别急，和上一个问题一样，**不要凭借肉眼检查下定论**，在你的两个服务之间，分别使用以下代码测试一下：

``` java
@SpringBootApplication
public class SaTokenApplication {
	public static void main(String[] args) {
		SpringApplication.run(SaTokenApplication.class, args);
		// 写值测试：注意一定要用下列方法测试，不要用自己封装的 RedisUtil 之类的测试 
		SaManager.getSaTokenDao().set("name", "value", 100000); 
	}
}
```

如果都能根据你的预期写进同一个 Redis，那才能证明两个服务确实连接的是同一个 Redis。

实际上，在交流群中提问这些问题的同学，90%的经过以上测试以后，都会发现两者连接的不是同一个 Reids，原因大多是：Redis配置没有生效、使用了 Alone-Redis 之类的……

如果你是剩下的 10%，那么继续排查：两边的 sa-token 配置是否完全一致，比如 token-name 配置不一致，也会导致数据无法相互认证。最好是把所有 sa-token 相关的配置都复制过去，试验一下看看。



### Q：我把 token 有效期设置为 30 天，但是总感觉不到 30 天的时候 token 就无效了，怎么回事？
- 可能1：你没有为 sa-token 集成 Redis，框架默认将会话数据保存在内存中，项目重启后数据会消失。
- 可能2：你为 sa-token 集成了 Redis，但是 Redis 重启了，导致会话消失。
- 可能3：你配置了 `is-concurrent=false`，不允许同一账号多端登录，有别人登录了这个账号把你顶下去了。
- 可能4：你配置了 `is-concurrent=true`，但是`is-share=false`，同一账号每次登录产生不同的 token，默认最高可以同时登录12个客户端，超过将自动注销最原先的会话。
- 可能5：你的这个账号，别人也登录了，别人调用了注销方法，把你这边的也注销了。`StpUtil.logout()` 为单 token 注销，`StpUtil.logout(10001)` 为账号所有 token 注销。
- 可能6：你虽然 `sa-token.timeout` 配置了 30 天，但是 `sa-token.active-timeout` 配置了较短的值，超过这个时间无操作，token 就过期了。
- 可能7：你换了浏览器，或者换了电脑，或者清空了浏览器最近缓存记录，自然而然需要重新登录。
- 可能8：你中途改了项目配置，比如改了 `sa-token.token-name` 配置项的值，会导致会话保存的 key 发生改变，效果等同于手动清空了 Redis 数据，需要重新登录。



### Q：有时候我不加 Token 也可以通过鉴权，请问是怎么回事？
- 可能1：你访问的这个接口，根本就没有鉴权的代码，所以可以安全的访问通过。
- 可能2：可能是 Cookie 帮你自动提交了 Token，在浏览器或 Postman 中会自动维护Cookie模式，如不需要可以在配置文件：`is-read-cookie: false`，然后重启项目再测试一下。


### Q：一个 User 对象存进 Session 后，再取出来时报错：无法从 User 类型转换成 User 类型？
- 可能1：你的 User 类中途换了包名，导致存进去时和取出来时对不上，无法成功创建实例。
- 可能2：你打开了代码热刷新模式，先存进去的对象，热刷新后再取出，会报错，关闭热刷新即可解决。


### Q：在 SaServletFilter 中调用 SpringMVCUtil.getRequest() 报错：非Web上下文无法获取Request？

- 可能1：项目中有配置类继承了： `extends WebMvcConfigurationSupport`。
- 可能2：项目中有配置类添加了注解： `@EnableWebMvc`。

解决方案：不要加 `@EnableWebMvc`，不要 `extends WebMvcConfigurationSupport`，要 `implements WebMvcConfigurer`

如果一定要 `extends WebMvcConfigurationSupport` ，可以通过手动注册 Spring 上下文初始化过滤器试试：

``` java
@Configuration
public class SaTokenConfigure extends WebMvcConfigurationSupport {

	// Spring 上下文初始化过滤器 可能由于各种原因没有被注册到，这里手动帮忙注册一下 
	@Bean
	@ConditionalOnMissingBean({ RequestContextListener.class, RequestContextFilter.class })
	@ConditionalOnMissingFilterBean(RequestContextFilter.class)
	public static RequestContextFilter requestContextFilter() {
		System.out.println("--------------------------- 注册了"); // 加个打印语句或者断点确保这里注册到了
		return new OrderedRequestContextFilter();
	}
	
}
```


如果不是以上原因，可以加群提供复现demo。

<!-- 目前能复现此问题的情况是：在项目中有配置类继承 `WebMvcConfigurationSupport` 时，再从 `SaServletFilter` 中调用
 `SpringMVCUtil.getRequest()` 就会报错：`非Web上下文无法获取Request`。

解决方案是将 `extends WebMvcConfigurationSupport` 改为 `implements WebMvcConfigurer`。 -->


### Q：我配置了 active-timeout 值，但是当我每次续签时 Redis 中的 ttl 并没有更新，是不是 bug 了？
不更新是正常现象，`active-timeout`不是根据 ttl 计算的，是根据value值计算的，value 记录的是该 Token 最后访问系统的时间戳，
每次验签时用：当前时间 - 时间戳 > active-timeout，来判断这个 Token 是否已经超时。 


### Q：整合 Redis 时先选择了默认jdk序列化，后又改成 jackson 序列化，程序开始报错，SerializationException？
两者的序列化算法不一致导致的反序列化失败，如果要更改序列化方式，则需要先将 Redis 中历史数据清除，再做更新。


### Q：调用 `StpUtil.getExtra("name")` 报错：`this api is disabled`。
`StpUtil.getExtra(key)` 是给 sa-token-jwt 插件提供的，不集成这个插件就不能调用这个API，如果是普通模式需要存储自定义参数，请在 SaSession 上存储

``` java
// 在登录时缓存参数
StpUtil.getSession().set("name", "zhangsan");

// 然后我们就可以在任意处获取这个参数 
String name = StpUtil.getSession().getString("name");
```


### Q：我加了 Sa-Token 的全局过滤器，浏览器报错跨域了怎么办？
参考：[https://juejin.cn/post/7247376558367981627](https://juejin.cn/post/7247376558367981627)


### Q：前后端分离项目中，前端使用 vue，如果不打开 porxy 代理的话，调用 Sa-Token 登录不会将 token 自动注入到 Cookie 中，是因为跨域么？
是。

参考：[前后端分离](/up/not-cookie) 


### Q：集成redis后对象模型序列化异常
假设执行如下代码:
``` java
@Data
public class User implements Serializable {
    private Long userId;
    private String username;
    private String password;
}
```

``` java
User user = new User();
user.setUserId(10000L);
user.setUsername("oneName");
user.setPassword("onePass");        
StpUtil.getSession().set("userObjKey", user); // 这里报错
```
报错信息如下:
```
SerializationException: Could not read JSON: 
Cannot deserialize value of type `java.lang.Long` from Array value (token `JsonToken.START_ARRAY`)
```

Springboot 集成 Sa-Token Redis 后, 一旦 Springboot 切换版本就有可能出现此问题

原因是 Redis 里面有之前的 Sa-Token 会话数据, 清空 Redis 即可。



### Q：我实现了 StpInterface 接口，但是在登录时没有进入我的实现类代码？
不进入是正常现象， StpInterface 是鉴权接口，在执行鉴权代码时才会进入 StpInterface 实现类，登录认证时不会进入。


### Q：启动时报错，找不到 xx 类 xx 方法：
``` java
Caused by: java.lang.ClassNotFoundException: cn.dev33.satoken.same.SaSameTemplate
```

一般找不到类，或者找不到方法，都是版本冲突了，使用 Sa-Token 时一定要注意**版本对齐**，意思是所有和 Sa-Token 相关的依赖都需要版本一致。

比如说你如果一个依赖是 1.32.0，一个是 1.31.0，就会造成无法启动：

``` xml
<!-- 如下样例：一个是 `1.32.0`，一个是 `1.31.0`， 版本没对齐，就会造成项目无法启动 -->
<dependency>
	<groupId>cn.dev33</groupId>
	<artifactId>sa-token-spring-boot-starter</artifactId>
	<version>1.32.0</version>
</dependency>
<dependency>
	<groupId>cn.dev33</groupId>
	<artifactId>sa-token-core</artifactId>
	<version>1.31.0</version>
</dependency>
```

请仔细排查你的 pom.xml 文件，是否有 Sa-Token 依赖没对齐，**请不要肉眼检查，用全局搜索 "sa-token" 关键词来找**，如果是多模块或者微服务项目，就整个项目搜索。


### Q：在多账号模式的注解鉴权时，报错：未能获取对应StpLogic，type=xxx

报这个错说明对应 type 的 StpLogic 尚未初始化到全局 StpLogicMap 中，一般会有两种原因造成这种情况：
1. 注解里的 loginType 拼写错误，请改正 （建议使用常量）。
2. 自定义 StpUtil 尚未初始化（静态类中的属性至少一次调用后才会初始化），解决方法两种：
	- (1) 从main方法里调用一次
	- (2) 在自定义StpUtil类加上类似 @Component 的注解让容器启动时扫描到自动初始化 


### Q：使用拦截器鉴权，访问一个不存在的 path 时，springboot 会自动在控制台打印一下异常。
可尝试添加以下配置解决：
``` properties
spring.resources.add-mappings=false
spring.mvc.throw-exception-if-no-handler-found=true
```




### Q：开启了全局懒加载后，能启动项目，但是访问接口报“未能获取有效的上下文处理器”
开启了全局懒加载后，能启动项目，但是访问接口报异常 `InvalidContextException`: 未能获取有效的上下文处理器, 配置如下：
``` yaml
spring:
  main:
    lazy-initialization: true
```
原因是 Sa-Token 自动配置入口类 SaBeanInject 被延迟加载了，只需要手动指定懒加载排除掉 SaBeanInject 就可以了,实现代码如下:
``` java
@Configuration
class MyConfiguration {
    @Bean
    LazyInitializationExcludeFilter integrationLazyInitExcludeFilter() {
        return LazyInitializationExcludeFilter.forBeanTypes(SaBeanInject.class);
    }
}
```
[经验来源](https://gitee.com/dromara/sa-token/issues/I7EXIU)


### Q：SpringBoot 3.x 路由拦截鉴权报错：No more pattern data allowed after {*...} or ** pattern element


报错原因：SpringBoot3.x 版本默认将路由匹配机制由 `ant_path_matcher` 改为了 `path_pattern_parser` 模式，
而此模式有一个规则，就是写路由匹配符的时候，不允许 `**` 之后再出现内容。例如：`/admin/**/info` 就是不允许的。

如果你的项目报了这个错，说明你写的路由匹配符出现了上述问题，有三种解决方案：
1. 等待 SpringMVC 官方增强 `path_pattern_parser` 模式能力，使之可以支持 `**` 之后再出现内容。
2. 在写路由匹配规则时，避免使 `**` 之后再出现内容。
3. 将项目的路由匹配机制改为 `ant_path_matcher`。

步骤1：先改项目的：
``` yml
spring:
    mvc:
        pathmatch:
            matching-strategy: ant_path_matcher
```

步骤2：再改 Sa-Token 的：
``` java
/**
 * 重写路由匹配算法，切换为 ant_path_matcher 模式，使之可以支持 `**` 之后再出现内容
 */
@PostConstruct
public void customRouteMatcher() {
	SaStrategy.instance.routeMatcher = (pattern, path) -> {
		return SaPatternsRequestConditionHolder.match(pattern, path);
	};
}
```

**注意点：**

SpringBoot2.x 的 `WebFlux`或 `SC Gateway` 项目，按照上述步骤改造，可能会报错 

``` html
java.lang.NoClassDefFoundError: org/springframework/web/servlet/mvc/condition/PatternsRequestCondition
```

只需要将“步骤2”中的代码 `return SaPatternsRequestConditionHolder.match(pattern, path);` 
更换为 `return SaPathMatcherHolder.getPathMatcher().match(pattern, path);` 即可，例如：

``` java
/**
 * 重写路由匹配算法，切换为 ant_path_matcher 模式，使之可以支持 `**` 之后再出现内容
 */
@PostConstruct
public void customRouteMatcher() {
	SaStrategy.instance.routeMatcher = (pattern, path) -> {
		return SaPathMatcherHolder.getPathMatcher().match(pattern, path);
	};
}
```


### Q：Webflux 环境集成，或者 SpringCloud Gateway 环境集成后，过滤器里路由拦截鉴权报错：`java.lang.NoSuchFieldError: defaultInstance`

``` java
java.lang.NoSuchFieldError: defaultInstance
	at cn.dev33.satoken.spring.pathmatch.SaPathPatternParserUtil.match(SaPathPatternParserUtil.java:40)
	at cn.dev33.satoken.reactor.spring.SaTokenContextForSpringReactor.matchPath(SaTokenContextForSpringReactor.java:34)
	at cn.dev33.satoken.router.SaRouter.isMatch(SaRouter.java:58)
	at cn.dev33.satoken.router.SaRouter.isMatch(SaRouter.java:72)
	... 
```

原因：SpringBoot 版本用的太低了，导致一些类不存在。

- 方案一：升级项目的 SpringBoot 版本至 `2.3.x` 以上
- 方案二：像上面的问题解决方案一样，重写一下相关类：

``` java
/**
 * 重写路由匹配算法，将 PathPatternParser.defaultInstance 改为 SaPathMatcherHolder.getPathMatcher()
 */
@PostConstruct
public void customRouteMatcher() {
	SaStrategy.instance.routeMatcher = (pattern, path) -> {
		return SaPathMatcherHolder.getPathMatcher().match(pattern, path);
	};
}
```


### Q：过低的 SpringBoot 版本引入 Sa-Token 后报错

在低于 2.2.0 时 (不包含2.2.0本身) 的 SpringBoot 项目中引入 Sa-Token 后，项目启动时会报错：

``` txt
org.springframework.beans.factory.BeanCreationException: Error creating bean with name 'cn.dev33.satoken.spring.SaBeanInject': Bean instantiation via constructor failed; nested exception is org.springframework.beans.BeanInstantiationException: Failed to instantiate [cn.dev33.satoken.spring.SaBeanInject]: Constructor threw exception; nested exception is java.lang.NoClassDefFoundError: com/fasterxml/jackson/databind/jsontype/PolymorphicTypeValidator
```

这是由于缺少 jackson 相关依赖导致的，可以手动添加以下依赖来解决：

``` xml
<!-- SpringBoot 版本过低时，需要追加的包 (低于 2.2.0 时，不包含 2.2.0 本身) -->
<dependency>
	<groupId>com.fasterxml.jackson.core</groupId>
	<artifactId>jackson-core</artifactId>
	<version>2.17.3</version>
</dependency>
<dependency>
	<groupId>com.fasterxml.jackson.core</groupId>
	<artifactId>jackson-annotations</artifactId>
	<version>2.17.3</version>
</dependency>
<dependency>
	<groupId>com.fasterxml.jackson.core</groupId>
	<artifactId>jackson-databind</artifactId>
	<version>2.17.3</version>
</dependency>
```




### Q：报错：非 web 上下文无法获取 HttpServletRequest。

报错原因解析：

Sa-Token 的部分 API 只能在 Web 上下文中才能调用，例如：`StpUtil.getLoginId()` 获取当前用户Id，这个方法第一步需要先从前端提交的参数里获取 token 值，
当你在 main 方法里调用这个 API 时，由于 main 方法本质上不是一个 Controller 请求，所以框架无法完成 *“从前端提交的参数里获取 token 值”* 这一步骤，框架就只能抛出异常。

按照此标准，Sa-Token 的 API 可粗浅的分为两大类：
- 必须在 Web 上下文中才能调用的 API，例如：`StpUtil.getLoginId()`、`StpUtil.getTokenValue()` 等等。
- 无需 Web 上下文也能调用的 API，例如：`StpUtil.getLoginType()`、`SaManager.getConfig()` 等等。

此处无法逐一列出到底哪些 API 属于 *“必须依赖 Web 上下文的 API”*，因为太多了，你只需要记住关键的一点：
**当一个 API 执行的代码需要先从前端请求中获取一些数据时，这个 API 就属于 *“必须依赖 Web 上下文的 API”*。**

如果你的代码报这个错，说明你在不是 Web 上下文中的地方，调用了 *“必须依赖 Web 上下文的 API”*，请排查：

1. 是否在 main 方法中调用了 *“必须依赖 Web 上下文的 API”*。
2. 是否在带有 `@Async` 注解的方法中调用了 *“必须依赖 Web 上下文的 API”*。
3. 是否在一些丢失 web 上下文的子线程中调用了 *“必须依赖 Web 上下文的 API”*，例如 `MyBatis-Plus` 的 `insertFill` 自动填充。
4. 是否在一些非 Http 协议的 RPC 框架中（例如 Dubbo）调用了 *“必须依赖 Web 上下文的 API”*。
5. 是否在 SpringBoot 启动初始化的方法中调用了 *“必须依赖 Web 上下文的 API”*，例如 `@PostConstruct` 修饰的方法。
6. 是否在定时任务中调用了 *“必须依赖 Web 上下文的 API”*。


### Q：报错：未能获取有效的上下文处理器。

报错原因解析：

在 sa-token-core 核心包中，Sa-Token 底层不能确认最终运行的 web 容器，所以抽象了 `SaTokenContext` 接口，对接不同容器时需要注入不同的实现，
通常这个注入工作都是框架自动完成的，你只需要按照文档开始部分集成相应的依赖即可。例如：

- 你要在 Springboot2.x 中使用 Sa-Token，就引入：`sa-token-spring-boot-starter`。
- 你要在 Springboot3.x 中使用 Sa-Token，就引入：`sa-token-spring-boot3-starter`。
- 你要在基于 webflux 架构的网关中使用 Sa-Token，就引入：`sa-token-reactor-spring-boot-starter`。
- 你要在 Solon 中使用 Sa-Token，就引入：`sa-token-solon-plugin`。
- 等等等等……

如果你的代码报 *“未能获取有效的上下文处理器”* 这个错，大概率是因为你没有正确引入所需的包，导致框架没有注入正确的 `SaTokenContext` 上下文实现，请排查：

1. 如果你的项目是微服务项目，请直接参考：[微服务-依赖引入说明](/micro/import-intro)，如果是单体项目，请往下看：
2. 请判断你的项目是 SpringMVC 环境还是 WebFlux 环境：
	- 如果是 SpringMVC 环境就引入 `sa-token-spring-boot-starter` 依赖，参考：[在SpringBoot环境集成](/start/example)
	- 如果是 WebFlux 环境就引入 `sa-token-reactor-spring-boot-starter` 依赖，参考：[在WebFlux环境集成](/start/webflux-example)
3. 如果你还无法分辨你是哪个环境，就看你的 pom.xml 依赖：
	- 如果引入了`spring-boot-starter-web`就是 SpringMVC 环境。
	- 如果引入了 `spring-boot-starter-webflux` 就是WebFlux环境。
	- 什么？你说你两个都引入了？那你的项目能启动成功吗？
4. 如果是 WebFlux 环境而且正确引入了依赖，依然报错，**请检查是否注册了 SaReactorFilter 全局过滤器，在 WebFlux 下这一步是必须的**，具体还是请参考上面的 [ 在WebFlux环境集成 ] 章节。
5. 需要仔细注意，如果你使用的是 Springboot3.x 版本，就不要错误的引入 `sa-token-spring-boot-starter`，需要引入的是 `sa-token-spring-boot3-starter`，不然就会导致框架报错。
6. 如果你的项目开启了全局懒加载(spring.main.lazy-initialization=true)后，能启动项目，但是访问接口报异常，请直接参考：[Q：开启了全局懒加载后，能启动项目，但是访问接口报未能获取有效的上下文处理器](/more/common-questions?id=q：开启了全局懒加载后，能启动项目，但是访问接口报未能获取有效的上下文处理器)
7. 如果以上步骤排除无误后依然报错，请直接提 issue 或者加入QQ群求助。





<!-- ---------------------------- 常见疑问 ----------------------------- -->

## 二、常见疑问

### Q：登录方法需要我自己实现吗？
是的，不同于`shiro`等框架，`Sa-Token`不会在登录流程中强插一脚，开发者比对完用户的账号和密码之后，只需要调用`StpUtil.login(id)`通知一下框架即可

``` java
// 会话登录接口 
@RequestMapping("doLogin")
public SaResult doLogin(String name, String pwd) {
    // 第一步：比对前端提交的账号名称、密码
    if("zhang".equals(name) && "123456".equals(pwd)) {
		
        // 第二步：比对成功后，调用通知框架，xxx账号登录成功 
        StpUtil.login(10001);
        return SaResult.ok("登录成功");
    }
    return SaResult.error("登录失败");
}
```

### Q：框架抛出的权限不足异常，我想根据自定义提示信息，可以吗？
可以，在全局异常拦截器里捕获`NotPermissionException`，可以通过`getPermission()`获取没有通过认证的权限码，可以据此自定义返回信息

``` java
@RestControllerAdvice
public class GlobalExceptionHandler {
    // 全局 NotPermissionException 异常捕获 
    @ExceptionHandler(NotPermissionException.class)
    public SaResult handlerException(NotPermissionException e) {
        e.printStackTrace();
        return SaResult.error("缺少权限：" + e.getPermission());
    }
}
```

### Q：在 SaInterceptor 中，注解鉴权总是先于路由拦截鉴权执行，能调整一下顺序吗？
框架没有提供直接的 API，但你有以下两种方式可以做到这一点：
- 方式1：将 SaInterceptor 里的代码复制出来一份，按照你的需求改一下，然后使用你这个自定义的拦截器，不再使用官方的。
- 方式2：注册两次 SaInterceptor 拦截器，例如：

``` java
@Configuration
public class SaTokenConfigure implements WebMvcConfigurer {
	
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		// 路由拦截鉴权
		registry.addInterceptor(new SaInterceptor(r -> {
			// 路由拦截鉴权的代码 ...
		}).isAnnotation(false)).addPathPatterns("/**");

		// 打开注解鉴权
		registry.addInterceptor(new SaInterceptor()).addPathPatterns("/**");
	}
}
```
如上，第一个完成路由拦截鉴权功能，第二个完成注解鉴权功能。


### Q：我的项目权限模型不是RBAC模型，很复杂，可以集成吗？
无论什么模型，只要能把一个用户具有的所有权限塞到一个List里返回给框架，就能集成


### Q：StpInterface 接口的  方法，在什么时候执行？
每次鉴权时执行，例如你调用了 `StpUtil.checkgetPermission("xxx")` 方法，框架就会调用底层的 `StpInterface#getPermissionList` 方法来获取权限数据。

如果你的 `getPermissionList` 里有读数据库的代码，那么你每鉴一次权，系统将访问一次数据库。如果要减小性能消耗，可以把权限数据放在缓存中，参考：[把权限放在缓存里](/fun/jur-cache)。


### Q：当我配置不并发登录时，每次登陆都会产生一个新的 Token，旧 Token 依然被保存在 Redis 中，框架为什么不删除呢？
首先，不删除旧 Token 的原因是为了在旧 Token 再次访问系统时提示他：已被顶下线。

而且这个 Token 不会永远留在 `Redis` 里，在其 TTL 到期后就会自动清除，如果你想让它立即消失，可以：

- 方法一：配置文件把 `is-concurrent` 和 `is-share` 都打开，这样每次登陆都会复用以前的旧 Token，就不会有废弃 Token 产生了。 
- 方法二：每次登录前把先调用注销方法 `StpUtil.logout(10001)` ，把这个账号的旧登录都给清除了。
- 方法三：写一个定时任务查询 Redis 值进行删除。


### Q：我使用过滤器鉴权 or 全局拦截器鉴权，结果 Swagger 不能访问了，我应该排除哪些地址？
尝试加上排除 `"/swagger-resources/**", "/webjars/**", "/v2/**", "/swagger-ui.html/**" ,"/doc.html/**","/error","/favicon.ico"`

不同版本可能会有所不同，其实在前端摁一下 `F12` 看看哪个 url 报错排除哪个就行了（另附：注解鉴权是不需要排除的，因为 `Swagger` 本身也没有使用 Sa-Token 的注解）


### Q：SaRouter.match 有多个路径需要排除怎么办？
可以点进去源码看一下，`SaRouter.match`方法有多个重载，可以放一个集合, 例如：
``` java
SaRouter.match("/**").notMatch("/login", "/reg").check(r -> StpUtil.checkLogin());
```


### Q：为什么StpUtil.login() 不能直接写入一个User对象？
`StpUtil.login()`只是为了给当前会话做个唯一标记，通常写入`UserId`即可，如果要存储User对象，可以使用`StpUtil.getSession()`获取Session对象进行存储。 


### Q：前后端分离模式下和普通模式有何不同？
主要是失去了`Cookie`无法自动化保存和提交`token秘钥`，可以参考章节：[前后端分离](/up/not-cookie)


### Q：前后端分离时，前端提交的 header 参数是叫 token 还是 satoken 还是 tokenName？
默认是satoken，如果想换一个名字，更改一下配置文件的`tokenName`即可。


### Q：一个账号拥有哪些权限，可以做成动态的吗？
权限本来就是动态的，框架预留的 `StpInterface` 接口，就是为了让你可以写任意代码来获取数据


### Q：路由拦截鉴权，可以做成动态的吗？
参考：[把路由拦截鉴权动态化](/fun/dynamic-router-check)


### Q：我不想让框架自动操作Cookie，怎么办？
在配置文件将`isReadCookie`值配置为`false`


### Q：怎么关掉每次启动时的字符画打印？
在配置文件将`isPrint`值配置为`false`


### Q：StpUtil.getSession()必须登录后才能调用吗？如果我想在用户未登录之前存储一些数据应该怎么办？
`StpUtil.getSession()`获取的是`Account-Session`，必须登录后才能使用，如果需要在未登录状态下也使用Session功能，请使用`Token-Session` <br>
步骤：先在配置文件里将`tokenSessionCheckLogin`配置为`false`，然后通过`StpUtil.getTokenSession()`获取Session 。或者直接调用 `StpUtil.getAnonTokenSession()` 获取匿名 Token-Session。


### Q：我只使用header来传输token，还需要打开Cookie模式吗？
不需要，如果只使用header来传输token，可以在配置文件关闭Cookie模式，例：`isReadCookie=false`


### Q：我想让用户修改密码后立即掉线重新登录，应该怎么做？
框架内置 [强制指定账号下线] 的APi，在执行修改密码逻辑之后调用此API即可: `StpUtil.logout()`


### Q：代码鉴权、注解鉴权、路由拦截鉴权，我该如何选择？
这个问题没有标准答案，这里只能给你提供一些建议，从鉴权粒度的角度来看：
1. 路由拦截鉴权：粒度最粗，只能粗略的拦截一个模块进行权限认证
2. 注解鉴权：粒度较细，可以详细到方法级，比较灵活
3. 代码鉴权：粒度最细，不光可以控制到方法级，甚至可以if语句决定是否鉴权

So：从鉴权粒度的角度来看，需要针对一个模块鉴权的时候，就用路由拦截鉴权，需要控制到方法级的时候，就用注解鉴权，需要根据条件判断是否鉴权的时候，就用代码鉴权 


### Q：Sa-Token的全局过滤器我应该怎么指定它的优先级呢？
为了保证相关组件能够及时初始化，框架默认给过滤器注册的优先级为-100，如果你想更改优先级，直接在注册过滤器的方法上加上 `@Order(xxx)` 即可覆盖框架的默认配置


### Q：timeout 过期了，获取到的 NotLoginException 场景值是-2，按照文档说的应该是-3吧。是我理解的不对还是操作有误？
你的理解是对的，但是框架现在只能做到返回-2，因为 token 过期后，就从 Redis 中消失了，框架没法分辨这个 token 是曾经有过然后过期的，还是从来就没有在Redis中存在过，
所以只能统一抛出-2，这个行为也和具体使用的 SaTokenDao 有关联，例如集成 sa-token-jwt 插件后，框架就能分辨出来是 token 过期了，抛出-3。


### Q：Sa-Token 是否提供类似 RefreshToken 的概念，与 AccessToken 相互配合刷新令牌鉴权。
关于长短 token，Sa-Token 没有提供直接的 API 支持，但是你可以利用 “临时 token 认证模块” 轻易的达到这一点：

1. 把 `sa-token.timeout` 的值配置小一点，然后把 `StpUtil.login(10001)` 生成的 token 作为短 token ，用来鉴权。
2. 用 “临时 token 认证模块” 生成长 token， `String refreshToken = SaTempUtil.createToken(10001, 2592000);`。
3. 把这两个 token 一起返回到前端。
4. 你再开个接口，可以让前端通过长 token，刷新短 token，参考代码：

``` java
@RequestMapping("/refreshToken")
public SaResult refreshToken(String refreshToken) {
	// 1、验证
	Object userId = SaTempUtil.parseToken(refreshToken);
	if(userId == null) {
		return SaResult.error("无效 refreshToken");
	}

	// 2、为其生成新的短 token
	String accessToken = StpUtil.createLoginSession(userId);

	// 3、返回
	return SaResult.data(accessToken);
}
```


### Q：前后端一体项目下，在拦截未登录进入登录页面时，如何登录完成后原路返回？
可以在拦截跳转登录页面时，把原 url 作为 back 参数挂载到登录页后方，登录完成后读取 back 参数并跳转
``` java
@RestControllerAdvice
public class GlobalException {
	// 未登录异常拦截 
	@ExceptionHandler(NotLoginException.class)
	public Object handlerException(NotLoginException e) {
		e.printStackTrace();
		return SaHolder.getResponse().redirect("/login?back=" + SaHolder.getRequest().getUrl());
	}
}
```



### Q：怎么改变请求返回的 http 状态码？
``` java
SaHolder.getResponse().setStatus(401)
```



### Q：Sa-Token 集成 Redis 如何集群？
以 `sa-token-redis-template` 为例：Sa-Token 底层使用的是 RedisTemplate 对象来操作数据的，也就是说，你只要给 RedisTemplate 配置上集群模式，Sa-Token 自动就是集群模式了。


### Q：多个项目共用同一个 redis，怎么防止冲突？

首先，如无特殊需求，建议多个项目不要共用同一个 redis，如果非要共用，有以下方式避免数据冲突：

- 方式 1：使用不同的 db 索引，Redis 默认提供 16 个 database 容器，每个项目配置不同的 db 索引即可。
- 方式 2：给项目配置不同的 `sa-token.token-name` 值，此配置项默认为 `satoken`，是框架在 Redis 存储数据时使用的统一前缀。
- 方式 3：使用 `sa-token-three-redis-jackson-add-prefix` 插件，参考：[sa-token-three-plugin](https://gitee.com/sa-tokens/sa-token-three-plugin)。


### Q：如何防止 CSRF 攻击？
CSRF 攻击的核心在于利用浏览器自动提交 Cookie 的特性，代替用户发送自己不想发送的请求。

**方案一：关闭 Cookie模式。**

在配置文件里配置 `sa-token.is-read-cookie=false` 关闭 Cookie 读取模式，采用 localStorage 存储 token + header 头提交，即可避免 CSRF 攻击。

**方案二：增加 csrf-token 验证**

如果项目必须采用 Cookie 模式验证，可以在请求中增加 csrf-token 验证的环节：

1、在登录时，生成一个 `csrf_token` 返回到前端：
``` java
// 测试登录 
@RequestMapping("/login")
public SaResult login() {
	StpUtil.login(10001);
	String csrfToken = StpUtil.getSession().get("csrf_token", () -> SaFoxUtil.getRandomString(60));
	return SaResult.ok().set("csrf_token", csrfToken);
}
```

2、前端将 csrf_token 存储在 localStorage 中（注意一定要存储在 localStorage 而非 Cookie 中，存储在 Cookie 中还是可能会被浏览器自动提交）
``` java
localStorage.setItem('csrf_token', csrf_token);
```
每次请求将 csrf_token 塞到 Header 中。

3、在需要防止 CSRF 攻击的接口验证 csrf_token：
``` java
@RequestMapping("/test")
public SaResult test() {

	// 先验证 csrfToken 
	String csrfToken = SaHolder.getRequest().getHeader("csrf_token");
	if (csrfToken == null || ! csrfToken.equals(StpUtil.getSession().get("csrf_token")) ) {
		throw new SaTokenException("csrf_token 不匹配");
	}

	// 通过后再处理具体业务
	// ...

	return SaResult.ok();
}
```

也可以将验证代码写到全局拦截器中，为所有接口提供校验。



### Q：如何自定义框架读取 token 的方式？
**方式一：通过 StpUtil.getStpLogic().setTokenValueToStorage("abcdefgxxxxxxxx") 自定义 token 值**

如果你可以在框架读取 token 之前写一些代码，那么你可以通过如下代码自定义当前请求的 token 值：
``` java
@RequestMapping("/test")
public SaResult test() {
	System.out.println(StpUtil.getTokenValue()); // 此时读取到的是前端提交的: cebcc930-c0f5-4009-8eb0-1b6aee63b4aa
	StpUtil.getStpLogic().setTokenValueToStorage("abcdefgxxxxxxxx");
	System.out.println(StpUtil.getTokenValue()); // 此时读取到的是我们自定义的: abcdefgxxxxxxxx
	return SaResult.ok();
}
```

**方式二：重写 StpLogic 读取 token 的方法**

``` java
@Component
public class MyStpLogic extends StpLogic {
    public MyStpLogic() {
        super("login");
    }
	// 自定义 token 读取方式，例如此处改为读取请求头为 my-token 的值 
    @Override
    public String getTokenValue() {
        String token = SaHolder.getRequest().getHeader("my-token");
        return token;
    }
}
```




### Q：文档是否能下载？是否有离线版？
文档已完整开源，请访问 Sa-Token 官方仓库，根目录下的 sa-token-doc 文件夹就是文档。




### Q：还是有不明白到的地方?
请在`gitee` 、 `github` 提交 `issues`，或者加入qq群交流，[群链接](/more/join-group)

