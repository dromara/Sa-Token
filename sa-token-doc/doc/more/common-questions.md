# 常见问题
本篇整理大家在群聊里经常提问的一些问题，如有补充，欢迎提交pr

--- 

### 加了注解进行鉴权认证，不生效？
注解鉴权功能默认关闭，两种方式任选其一进行打开：注册注解拦截器、集成`AOP模块`，
如果已经打开仍然没有效果，加群说明一下复现步骤 


### 整合Redis时，除了引入pom依赖，还需要做其它的吗？
引入pom依赖后，在框架层面你无须做其它事情，但是你需要为项目指定一下`Redis`的连接信息，参考此文件：[application-dev](https://gitee.com/click33/sa-plus/blob/master/sp-server/src/main/resources/application-dev.yml)


### 登录方法需要我自己实现吗？
是的，不同于`shiro`等框架，`Sa-Token`不会在登录流程中强插一脚，开发者比对完用户的账号和密码之后，只需要调用`StpUtil.login(id)`通知一下框架即可


### 一个User对象存进Session后，再取出来时报错：无法从User类型转换成User类型？
群员亲测，当你打开热部署模式后，先存进去的对象，再热刷新后再取出，会报错，关闭热刷新即可解决


### 框架抛出的权限不足异常，我想根据自定义提示信息，可以吗？
可以，在全局异常拦截器里捕获`NotPermissionException`，可以通过`getCode()`获取没有通过认证的权限码，可以据此自定义返回信息


### 我的项目权限模型不是RBAC模型，很复杂，可以集成吗？
无论什么模型，只要能把一个用户具有的所有权限塞到一个List里返回给框架，就能集成


### SaRouter.match 有多个路径需要排除怎么办？
可以点进去源码看一下，`SaRouter.match`方法有多个重载，可以放一个集合, 例如：<br>
`SaRouter.match(Arrays.asList("/**"), Arrays.asList("/login", "/reg"), () -> StpUtil.checkLogin());`


### 为什么StpUtil.login() 不能直接写入一个User对象？
`StpUtil.login()`只是为了给当前会话做个唯一标记，通常写入`UserId`即可，如果要存储User对象，可以使用`StpUtil.getSession()`获取Session对象进行存储 


### 前后台分离模式下和普通模式有何不同？
主要是失去了`Cookie`无法自动化保存和提交`token秘钥`，可以参考章节：[前后台分离](/use/not-cookie)


### 前后台分离时，前端提交的header参数是叫token还是satoken还是tokenName？
默认是satoken，如果想换一个名字，更改一下配置文件的`tokenName`即可


### Springboot环境下采用自定义拦截器排除了某个路径仍然被拦截了？
可能是404了，SpringBoot环境下如果访问接口404后，会被重定向到`/error`，然后被再次拦截，如果是其它原因，欢迎加群反馈


### 权限可以做成动态的吗？
权限本来就是动态的，只有jwt那种模式才是非动态的


### 集成jwt后为什么在 getSession 时提示 jwt has not session ?
`jwt`的招牌便是无须借助服务端完成会话管理，如果集成`jwt`后再次使用`Session`功能，那将又回到了传统`Session`模式，属于自断招牌，此种技术组合没有任何意义，因此jwt集成模式不提供`Session`功能，如果需要`Session`功能，就不要集成`jwt`


### 怎么关闭默认的Cookie模式呢？
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

### 整合Redis时先选择了默认jdk序列化，后又改成jackson序列化，程序开始报错，SerializationException？
两者的序列化算法不一致导致的反序列化失败，如果要更改序列化方式，则需要先将Redis中历史数据清除，再做更新 


### 还是有不明白到的地方?
请在`github`提交`issues`，或者加入qq群交流（群链接在[首页](README?id=交流群)）


### 我能为这个框架贡献代码吗？
**可以**，请参照首页的提交pr步骤 ，[贡献代码](README?id=贡献代码)

