# 常见问题排查
本篇整理大家在群聊里经常提问的一些问题，如有补充，欢迎提交pr

[[toc]]

--- 

<!-- ---------------------------- 常见报错 ----------------------------- -->

## 一、常见报错

### Q：报错：非Web上下文无法获取Request？
报错原因：Sa-Token 的部分 API 只能在 Web 上下文中调用，报这个错说明你调用 Sa-Token 的地方不在 Web 上下文中，请排查：

1. 是否在 main 方法中调用了 Sa-Token 的API
2. 是否在带有 `@Async` 注解的方法中调用了 Sa-Token 的API
3. 是否在一些丢失web上下文的子线程中调用了 Sa-Token 的API，例如 `MyBatis-Plus` 的 `insertFill` 自动填充
4. 是否在一些非 Http 协议的 RPC 框架中（例如 Dubbo）调用了 Sa-Token 的API 
5. 是否在 SpringBoot 启动初始化的方法中调用了 Sa-Token 的API，例如`@PostConstruct`

解决方案：先获取你想要的值，再把这个值当做一个参数传递到这些方法中，而不是直接从方法内调用 Sa-Token 的API。


### Q：报错：未初始化任何有效上下文处理器？
报错原因：Sa-Token底层不能确认最终运行的web容器，所以抽象了 `SaTokenContext` 接口，对接不同容器时需要注入不同的实现，通常这个注入工作都是框架自动完成的，
你只需要按照文档开始部分集成相应的依赖即可

如果报了这个错误，说明框架没有注入正确的上下文实现，请排查：

1. 如果你的项目是微服务项目，请直接参考：[微服务-依赖引入说明](/micro/import-intro)，如果是单体项目，请往下看：
2. 请判断你的项目是 SpringMVC 环境还是 WebFlux 环境
	- 如果是 SpringMVC 环境就引入 `sa-token-spring-boot-starter` 依赖，参考：[在SpringBoot环境集成](/start/example)
	- 如果是 WebFlux 环境就引入 `sa-token-reactor-spring-boot-starter` 依赖，参考：[在WebFlux环境集成](/start/webflux-example)
	- 引入错误的依赖会导致`SaTokenContext`初始化失败，抛出上述异常 
	- 如果你还无法分辨你是哪个环境，就看你的 pom.xml 依赖，如果引入了`spring-boot-starter-web`就是SpringMVC环境，如果引入了 `spring-boot-starter-webflux` 就是WebFlux环境。……什么？你说你两个都引入了？那你的项目能启动成功吗？
3. 如果是 WebFlux 环境而且正确引入了依赖，依然报错，**请检查是否注册了全局过滤器，在 WebFlux 下这一步是必须的**。
4. 如果以上步骤排除无误后依然报错，请直接提 issue 或者加入QQ群求助。

### Q：报错：NotLoginException：xxx

这个错是说明调用接口的人没有通过登录校验，请注意通常**异常提示语已经描述清楚了没有通过认证的具体原因：**
   
**如果是：未能读取到有效Token**
- 可能1：前端没有提交 Token（最好从前端f12控制台看看请求参数里有 token 吗）。
- 可能2：前端提交了 Token，但是参数名不对。默认参数名是 `satoken`，可通过配置文件 `sa-token.token-name: satoken` 来更改。
- 可能3：前端提交了 Token，但是你配置了框架不读取，比如说你配置了 `is-read-header=false`（关闭header读取），此时你再从 header 里提交token，框架就无法读取到。
- 可能4：前端提交了 Token，但是 Token前缀 不对，可参考：[自定义 Token 前缀](/up/token-prefix)
- 可能5：你使用了 Nginx 反向代理，而且配置了 自定义Token名称，而且自定义的名称还带有下划线（比如 shop_token），而且还是你的项目还是从 Header头提交Token的，此时 Nginx 默认会吞掉你的下划线参数，可参考：[nginx做转发时，带下划线的header参数丢失](https://blog.csdn.net/zfw_666666/article/details/124420828)

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
- 可能1：前端提交的 token 临时过期（activity-timeout超时了，比如配置了 activity-timeout=120，但是超过了120秒没有访问接口）。
- 可能2：集成jwt，而且使用的是 Mixin 或 Stateless 模式，而且token过期了（timeout超时了）。

**如果是：Token已被顶下线：6ad93254-b286-4ec9-9997-4430b0341ca0**
- 可能1：在项目配置了 `is-concurrent=false` 的前提下，这个账号又被别人登录了，导致旧登录被挤了下去。
- 可能2：这个账号被 `StpUtil.replaced(loginId, device)` 方法强制顶下线了。

**如果是：Token已被踢下线：6ad93254-b286-4ec9-9997-4430b0341ca0**
- 可能1：这个账号被 `StpUtil.kickout(loginId)` 方法强制踢下线了。


### Q：集成 Redis 后，明明 Redis 中有值，却还是提示无效Token？
根据以往的处理经验，发生这种情况 90% 的概率是因为你找错了Redis，即：代码连接的Redis和你用管理工具看到的Redis并不是同一个。

你可能会问：我看配置文件明明是同一个啊？

我的回答是：别光看配置文件，不一定准确，在启动时直接执行 `SaManager.getSaTokenDao().set("name", "value", 100000);`，
随便写入一个值，看看能不能根据你的预期写进这个Redis，如果能的话才能证明`代码连接的Reids` 和`你用管理工具看到的Redis` 是同一个，再进行下一步排查。


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
			System.out.println("--------- flag 2");
			StpUtil.checkLogin();  // 登录校验，只有会话登录后才能通过这句代码 
		}))
		.addPathPatterns("/user/**")
		.excludePathPatterns("/user/doLogin");
	}
}
```

在启动时 `flag 1` 被打印出来，才证明拦截器注册成功了，在访问请求时 `flag 2` 被打印出来，才证明请求进入了拦截器。

如果拦截器没有注册成功，则：
- 可能1：SpringBoot 版本较高（`>= 2.6.0`），请尝试在启动类加上 `@EnableWebMvc` 注解再重新启动。
- 可能2：`SaTokenConfigure` 配置类不在启动类的同包或者子包下，导致没有被 SpringBoot 扫描到。
- 可能3：`SaTokenConfigure` 配置类在启动类的同包或者子包下，但启动类上加了 `@ComponentScan("com.xxx")` 注解，导致包扫描范围不正确，请将此注解删除或移动到其它配置类上。
- 可能4：项目属于 Maven 多模块项目，`SaTokenConfigure` 和启动类没有在一个模块，且启动类模块没有引入配置类的模块，导致加载不到。

如果拦截器已经注册成功，但请求没有进入拦截器：
- 可能1：你访问的 path，没有被 `.addPathPatterns("/user/**")` 拦截住。
- 可能2：你访问的 path，被 `.excludePathPatterns("/xxx/xx")` 排除掉了。
- 可能3：你访问的是另一个项目，请把当前项目停掉，看看你的请求还能不能访问成功。

注：以上的排查步骤，对过滤器不生效的情形一样适用。


### Q：我使用拦截器鉴权时，明明排除了某个路径却仍然被拦截了？
- 可能1：你的项目可能是跨域了，先把跨域问题解决掉，参考：[解决跨域问题](/fun/cors-filter)
- 可能2：你访问的接口可能是404了，SpringBoot环境下如果访问接口404后，会被转发到`/error`，然后被再次拦截。请确保你访问的 path 有对应的 Controller 承接！
- 可能3：可能这里并没有拦截，但是又被其他地方拦截了。请先把这个拦截器给注释掉，看看还会不会拦截，如果依然拦截，那说明不是这个拦截器的锅，请仔细查看一下控制台抛出的堆栈信息，定位一下到底是哪行代码拦截住这个请求的。
- 可能4：后端拦截的 path 未必是你前端访问的这个path，建议先打印一下 path 信息，看看和你预想的是否一致，再做分析。
- 可能5：你写了多个匹配规则，请求只越过了第一个规则，被其它规则拦下了，例如以下代码：
``` java
registry.addInterceptor(new SaInterceptor(handler -> {
	SaRouter.match("/**").notMatch("/user/doLogin").check(r -> StpUtil.checkLogin());  // 第1个规则 
	SaRouter.match("/**").notMatch("/article/getList").check(r -> StpUtil.checkLogin());  // 第2个规则 
	SaRouter.match("/**").notMatch("/goods/getList").check(r -> StpUtil.checkLogin());  // 第3个规则 
})).addPathPatterns("/**");
```
以上代码，当你未登录访问 `/user/doLogin` 时，会被第1条规则越过，然后被第2条拦下，校验登录，然后抛出异常：`NotLoginException：xxx`


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
1、情况1是可能组件没有注入成功，排查方法为在 main 里打印这个组件，是否为自定义的class限定名：
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

2、情况2是，这个组件注入成功了，但是还没到执行时机，比如 `StpInterface` 组件，只有在鉴权时才会触发，如果你的代码仅仅是登录校验，就不会执行到这个组件。




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
参考：[https://blog.csdn.net/shengzhang_/article/details/119928794](https://blog.csdn.net/shengzhang_/article/details/119928794)


### Q：集成redis后对象模型序列化异常
假设执行如下代码:
``` java
@Data
public class User implements Serializable {
    private Long userId;
    private String username;
    private String password;
}

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

springboot 集成 satoken redis 后, 一旦 springboot 切换版本就有可能出现此问题

原因是redis里面有之前的 satoken 会话数据, 清空 Redis 即可





<!-- ---------------------------- 常见疑问 ----------------------------- -->

## 二、常见疑问

### Q：登录方法需要我自己实现吗？
是的，不同于`shiro`等框架，`Sa-Token`不会在登录流程中强插一脚，开发者比对完用户的账号和密码之后，只需要调用`StpUtil.login(id)`通知一下框架即可


### Q：框架抛出的权限不足异常，我想根据自定义提示信息，可以吗？
可以，在全局异常拦截器里捕获`NotPermissionException`，可以通过`getPermission()`获取没有通过认证的权限码，可以据此自定义返回信息


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


### Q：前后台分离模式下和普通模式有何不同？
主要是失去了`Cookie`无法自动化保存和提交`token秘钥`，可以参考章节：[前后台分离](/up/not-cookie)


### Q：前后台分离时，前端提交的 header 参数是叫 token 还是 satoken 还是 tokenName？
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
`StpUtil.getSession()`获取的是`User-Session`，必须登录后才能使用，如果需要在未登录状态下也使用Session功能，请使用`Token-Session` <br>
步骤：先在配置文件里将`tokenSessionCheckLogin`配置为`false`，然后通过`StpUtil.getTokenSession()`获取Session 


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


### Q：还是有不明白到的地方?
请在`gitee` 、 `github` 提交 `issues`，或者加入qq群交流（群链接在[首页](README?id=交流群)）

