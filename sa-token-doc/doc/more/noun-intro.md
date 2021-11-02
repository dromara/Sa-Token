# Sa-Token 名词解释 

Sa-Token 无意发明任何晦涩概念提升逼格，但在处理 issues 、Q群解答时还是发现不少同学因为一些基本概念理解偏差导致代码出错，
所以整理本篇针对一些比较容易混淆的地方加以解释说明。

也希望各位同学在提交 issues、Q群提问之前充分阅读本篇文章，保证不要因为基本概念理解偏差，增加不必要的沟通成本。


--- 

#### 几种 Token
- token：指通过 `StpUtil.login()` 登录产生的身份令牌，用来维护用户登录状态，也称：satoken、会话Token。 
- temp-token：指通过 `SaTempUtil.createToken()` 临时验证模块产生的Token，也称：临时Token。
- Access-Token：在 OAuth2 模块产生的身份令牌，也称：访问令牌、资源令牌。
- Refresh-Token：在 OAuth2 模块产生的刷新令牌，也称：刷新令牌。
- Id-Token：在 SaIdUtil 模块生成的Token令牌，用于提供子服务外网隔离功能。


#### 两种过期时间：
- timeout：会话 Token 的长久有效期。
- activity-timeout：会话的临时有效期。

两者的差别详见：[Token有效期详解](/fun/token-timeout)


#### 三种Session：
- User-Session：框架为每个账号分配的 Session 对象，也称：账号Session。 
- Token-Session：框架为每个 Token 分配的 Session 对象，也称：令牌Session。 
- Custom-Session：以一个特定的值作为SessionId，来分配的 Session 对象，也称：自定义Session。

三者差别详见：[Session模型详解](/fun/session-model)


#### 账号标识：
- loginId：账号id，用来区分不同账号，通过 `StpUtil.login(id)` 来指定。
- device：登录设备端，例如：`PC`、`APP`，通过 `StpUtil.login(id, device)` 来指定。
- loginType：账号类型，用来区分不同体系的账号，如同一系统的 `User账号` 和 `Admin账号`，详见：[多账号认证](/up/many-account) 


#### 几种登录策略：
- 单地登录：指同一时间只能在一个地方登录，新登录会挤掉旧登录，也可以叫：单端登录。
- 多地登录：指同一时间可以在不同地方登录，新登录会和旧登录共存，也可以叫：多端登录。
- 同端互斥登录：在同一类型设备上只允许单地点登录，在不同类型设备上允许同时在线，参考腾讯QQ的登录模式：手机和电脑可以同时在线，但不能两个手机同时在线。
- 单点登录：在进入多个系统时，只需要登录一次即可。解决用户在不同系统间频繁登录的问题。
- 同端多登录：指在一个终端可以同时登录多个账号。
- 记住我模式：指在一个设备终端登录成功，该设备重启之后依然保持登录状态。


#### 几种注销策略：
- 单端注销：只在调用登录的一端注销。
- 全端注销：一端注销，全端下线。
- 单点注销：与单点登录对应，一个系统注销，所有系统一起下线。


#### 几种鉴权方式：
- 代码鉴权：在代码里直接调用 `StpUtil.checkXxx` 相关 API 进行鉴权。
- 注解鉴权：在方法或类上添加 `@SaCheckXxx` 注解进行鉴权。
- 路由拦截鉴权：在全局过滤器或拦截里通过：`SaRouter.match()` 拦截路由进行鉴权。





