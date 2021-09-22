# 常见问题排查
本篇整理大家在群聊里经常提问的一些问题，如有补充，欢迎提交pr

--- 

<!-- ---------------------------- 常见报错 ----------------------------- -->

## 一、常见报错

### 报错：非Web上下文无法获取Request？
报错原因：Sa-Token 的部分 API 只能在 Web 上下文中调用，报这个错说明你调用 Sa-Token 的地方不在 Web 上下文中，请排查：

1. 是否在 main 方法中调用了 Sa-Token 的API
2. 是否在带有 `@Async` 注解的方法中调用了 Sa-Token 的API
3. 是否在一些丢失web上下文的子线程中调用了 Sa-Token 的API，例如 `MyBatis-Plus` 的 `insertFill` 自动填充
4. 是否在一些非 Http 协议的 RPC 框架中（例如 Dubbo）调用了 Sa-Token 的API 
5. 是否在 SpringBoot 启动初始化的方法中调用了 Sa-Token 的API，例如`@PostConstruct`

解决方案：先获取你想要的值，再把这个值当做一个参数传递到这些方法中，而不是直接从方法内调用 Sa-Token 的API。


### 报错：未初始化任何有效上下文处理器？
报错原因：Sa-Token底层不能确认最终运行的web容器，所以抽象了 `SaTokenContext` 接口，对接不同容器时需要注入不同的实现，通常这个注入工作都是框架自动完成的，
你只需要按照文档开始部分集成相应的依赖即可

如果报了这个错误，说明框架没有注入正确的上下文实现，请排查：

1. 如果你的项目是微服务项目，请直接参考：[微服务-依赖引入说明](/micro/import-intro)，如果是单体项目，请往下看：
2. 请判断你的项目是 SpringMVC 环境还是 WebFlux 环境
	- 如果是 SpringMVC 环境就引入 `sa-token-spring-boot-starter` 依赖，参考：[在SpringBoot环境集成](/start/example)
	- 如果是 WebFlux 环境就引入 `sa-token-reactor-spring-boot-starter` 依赖，参考：[在WebFlux环境集成](/start/webflux-example)
	- 引入错误的依赖会导致`SaTokenContext`初始化失败，抛出上述异常 
	- 如果你还无法分辨你是哪个环境，就看你的 pom.xml 依赖，如果引入了`spring-boot-starter-web`就是SpringMVC环境，如果引入了 `spring-boot-starter-webflux` 就是WebFlux环境。……什么？你说你两个都引入了？那你的项目能启动成功吗？
	- 你说你两个包都没引入？那你为什么不引入一个呢？
3. 如果是 WebFlux 环境而且正确引入了依赖，依然报错，请检查是否注册了全局过滤器，在 WebFlux 下这一步是必须的。
4. 如果以上步骤排除无误后依然报错，请直接提 issues 或者加入QQ群求助。


### 报错：NotLoginException：xxx
这个错是说明调用接口的人没有通过登录认证，请注意通常**异常提示语已经描述清楚了没有通过认证的具体原因**，例如：没有提供Token、提供的Token是无效的、提供的Token已经过期……等等

请根据异常提示语以及报错位置进行排查，可参考：[NotLoginException 场景值](/fun/not-login-scene)


### 加了注解进行鉴权认证，不生效？
注解鉴权功能默认关闭，两种方式任选其一进行打开：注册注解拦截器、集成AOP模块，参考：[注解式鉴权](/use/at-check)，
如果已经打开仍然没有效果，加群说明一下复现步骤 


### 有时候我不加 Token 也可以通过鉴权，请问是怎么回事？
可能是Cookie帮你自动传了，在浏览器或 Postman 中会自动维护Cookie模式，如不需要可以在配置文件：`is-read-cookie: false`，然后重启项目再测试一下


### 一个User对象存进Session后，再取出来时报错：无法从User类型转换成User类型？
群员亲测，当你打开热部署模式后，先存进去的对象，再热刷新后再取出，会报错，关闭热刷新即可解决


### Springboot环境下采用自定义拦截器排除了某个路径仍然被拦截了？
可能是404了，SpringBoot环境下如果访问接口404后，会被重定向到`/error`，然后被再次拦截，如果是其它原因，欢迎加群反馈


### 我配置了 active-timeout 值，但是当我每次续签时 Redis 中的 ttl 并没有更新，是不是 bug 了？
不更新是正常现象，`active-timeout`不是根据 ttl 计算的，是根据value值计算的，value 记录的是改 Token 最后访问系统的时间戳，
每次验签时用：当前时间 - 时间戳 > active-timeout，来判断这个 Token 是否已经超时 


### 集成 jwt 后为什么在 getSession 时提示 jwt has not session ?
jwt 的招牌便是无须借助服务端完成会话管理，如果集成`jwt`后再使用`Session`功能，那将又回到了传统`Session`模式，属于自断招牌，此种技术组合没有意义，
因此jwt集成模式不提供`Session`功能，如果需要`Session`功能，就不要集成`jwt`


### 整合 Redis 时先选择了默认jdk序列化，后又改成 jackson 序列化，程序开始报错，SerializationException？
两者的序列化算法不一致导致的反序列化失败，如果要更改序列化方式，则需要先将 Redis 中历史数据清除，再做更新 


### 我加了 Sa-Token 的全局过滤器，浏览器报错跨域了怎么办？
参考：[https://blog.csdn.net/shengzhang_/article/details/119928794](https://blog.csdn.net/shengzhang_/article/details/119928794)





<!-- ---------------------------- 常见疑问 ----------------------------- -->

## 二、常见疑问

### 登录方法需要我自己实现吗？
是的，不同于`shiro`等框架，`Sa-Token`不会在登录流程中强插一脚，开发者比对完用户的账号和密码之后，只需要调用`StpUtil.login(id)`通知一下框架即可


### 框架抛出的权限不足异常，我想根据自定义提示信息，可以吗？
可以，在全局异常拦截器里捕获`NotPermissionException`，可以通过`getCode()`获取没有通过认证的权限码，可以据此自定义返回信息


### 我的项目权限模型不是RBAC模型，很复杂，可以集成吗？
无论什么模型，只要能把一个用户具有的所有权限塞到一个List里返回给框架，就能集成


### 当我配置不并发登录时，每次登陆都会产生一个新的 Token，旧 Token 依然被保存在 Redis 中，框架为什么不删除呢？
首先，不删除旧 Token 的原因是为了在旧 Token 再次访问系统时提示他：已被顶下线。

而且这个 Token 不会永远留在 `Redis` 里，在其 TTL 到期后就会自动清除，如果你想让它立即消失，可以：

- 方法一：配置文件把 `is-concurrent` 和 `is-share` 都打开，这样每次登陆都会复用以前的旧 Token，就不会有废弃 Token 产生了。 
- 方法二：每次登录前把先调用注销方法，把这个账号的旧登录都给清除了。
- 方法三：写一个定时任务查询Redis值进行删除。


### 我使用过滤器鉴权 or 全局拦截器鉴权，结果 Swagger 不能访问了，我应该排除哪些地址？
尝试加上排除 `"/swagger-resources/**", "/webjars/**", "/v2/**", "/swagger-ui.html/**" ,"/doc.html/**","/error","/favicon.ico"`

不同版本可能会有所不同，其实在前端摁一下 `F12` 看看哪个 url 报错排除哪个就行了（另附：注解鉴权是不需要排除的，因为 `Swagger` 本身也没有使用 Sa-Token 的注解）



### SaRouter.match 有多个路径需要排除怎么办？
可以点进去源码看一下，`SaRouter.match`方法有多个重载，可以放一个集合, 例如：<br>
`SaRouter.match(Arrays.asList("/**"), Arrays.asList("/login", "/reg"), () -> StpUtil.checkLogin());`


### 为什么StpUtil.login() 不能直接写入一个User对象？
`StpUtil.login()`只是为了给当前会话做个唯一标记，通常写入`UserId`即可，如果要存储User对象，可以使用`StpUtil.getSession()`获取Session对象进行存储。 


### 前后台分离模式下和普通模式有何不同？
主要是失去了`Cookie`无法自动化保存和提交`token秘钥`，可以参考章节：[前后台分离](/up/not-cookie)


### 前后台分离时，前端提交的 header 参数是叫 token 还是 satoken 还是 tokenName？
默认是satoken，如果想换一个名字，更改一下配置文件的`tokenName`即可。

### 权限可以做成动态的吗？
权限本来就是动态的，只有jwt那种模式才是非动态的


### 我不想让框架自动操作Cookie，怎么办？
在配置文件将`isReadCookie`值配置为`false`


### 怎么关掉每次启动时的字符画打印？
在配置文件将`isPrint`值配置为`false`


### StpUtil.getSession()必须登录后才能调用吗？如果我想在用户未登录之前存储一些数据应该怎么办？
`StpUtil.getSession()`获取的是`User-Session`，必须登录后才能使用，如果需要在未登录状态下也使用Session功能，请使用`Token-Session` <br>
步骤：先在配置文件里将`tokenSessionCheckLogin`配置为`false`，然后通过`StpUtil.getTokenSession()`获取Session 


### 我只使用header来传输token，还需要打开Cookie模式吗？
不需要，如果只使用header来传输token，可以在配置文件关闭Cookie模式，例：`isReadCookie=false`


### 我想让用户修改密码后立即掉线重新登录，应该怎么做？
框架内置 [强制指定账号下线] 的APi，在执行修改密码逻辑之后调用此API即可: `StpUtil.logout()`


### 代码鉴权、注解鉴权、路由拦截鉴权，我该如何选择？
这个问题没有标准答案，这里只能给你提供一些建议，从鉴权粒度的角度来看：
1. 路由拦截鉴权：粒度最粗，只能粗略的拦截一个模块进行权限认证
2. 注解鉴权：粒度较细，可以详细到方法级，比较灵活
3. 代码鉴权：粒度最细，不光可以控制到方法级，甚至可以if语句决定是否鉴权

So：从鉴权粒度的角度来看，需要针对一个模块鉴权的时候，就用路由拦截鉴权，需要控制到方法级的时候，就用注解鉴权，需要根据条件判断是否鉴权的时候，就用代码鉴权 


### Sa-Token的全局过滤器我应该怎么指定它的优先级呢？
为了保证相关组件能够及时初始化，框架默认给过滤器注册的优先级为-100，如果你想更改优先级，直接在注册过滤器的方法上加上 `@Order(xxx)` 即可覆盖框架的默认配置


### 还是有不明白到的地方?
请在`gitee` 、 `github` 提交 `issues`，或者加入qq群交流（群链接在[首页](README?id=交流群)）

