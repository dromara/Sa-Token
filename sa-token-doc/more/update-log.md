# 更新日志 


### 2023-1-11 @v1.34.0

新增插件：
- 新增：新增 `SpringBoot3.x` 集成插件，感谢 `@jry` 提供的参考思路。   **[重要]**
- 新增：新增 `sa-token-dao-redisson-jackson` 插件，感谢 `@疯狂的狮子Li` 提交的pr。   **[重要]**

sa-token-core 核心包：
- 升级：升级 Sign 签名模块，增加部分重载方法。
- 重构：`SaSignTemplate#joinParams` 更名为 `joinParamsDictSort`。  **[不向下兼容]**
- 升级：升级临时 Token 认证模块，可指定 service 参数。
- 删除：彻底删除过期类 `SaAnnotationInterceptor` 和 `SaRouteInterceptor`。
- 修复：修复源码注释和文档的部分不合适之处。

sa-token-sso 单点登录：
- 删除：SSO 模块移除过期类 `SaSsoHandle` 类。
- 新增：SSO 模块增加 ticket 的 client 锁定功能，解决部分场景下的 ticket 劫持问题。  **[重要]**
- 修复：修复 SSO 模式2，在 client 端配置 `is-share=false` 时无法单点注销的问题。
- 修复：修复 SSO 模式3 部分场景下注销时无法正常回退页面的问题。

其它模块：
- sa-token-oauth2：修复 OAuth2 模块示例 getClientModel 方法 clientId 写错的问题。
- sa-token-alone-redis：新增：Alone-Redis 新增集群配置能力，感谢 `@appleOfGray` 提交的pr。   **[重要]**
- sa-token-jwt：重构：使用 jwt-simple 模式后 is-share 恒等于 false，无论是否有设定 `setExtra` 数据。



### 2022-11-16 @v1.33.0
- 重构：重构异常状态码机制。   **[重要]**
- 重构：重构 sa-token-sso 模块异常码改为 300 开头，sa-token-jwt 异常码改为 302 开头。  **[不向下兼容]**
- 新增：新增全局 Log 模块。   **[重要]**
- 重构：`SaTokenListenerForConsolePrint` 改名 `SaTokenListenerForLog`。   **[不向下兼容]**
- 修复：修复多线程下 `SaFoxUtil.getRandomString()` 随机数重复问题。
- 修复：修复 sa-token-demo-sso3-client-nosdk 项目中单点注销 url 配置错误的问题
- 文档：文档优化。



### 2022-10-28 @v1.32.0
- 修复：修复 sa-token-dao-redis-fastjson 插件多余序列化 `timeout` 字段的问题。
- 修复：修复 sa-token-dao-redis-fastjson 插件 `session.getModel` 无法反序列化实体类的问题。
- 修复：修复 `sa-token-quick-login` 插件指定拦截排除路由不生效的问题。
- 修复：修复 `sa-token-alone-redis` + `sa-token-dao-redis-fastson` 时 Redis 无法分离的问题。
- 修复：修复在配置了 cookie.path 后，注销时无法彻底清除 Cookie 的问题。
- 升级：`SaFoxUtil.getValueByType()` 新增对 char 类型的转换。
- 新增：新增 `sa-token-dao-redis-fastjson2` 插件。 **[重要]** 
- 新增：新增全局配置 `is-write-header`，控制登录后是否将 Token 写入响应头。 **[重要]** 
- 新增：二级认证模块新增指定业务标识能力。  **[重要]** 
- 重构：Id-Token 模块更名为 Same-Token。 **[重要]** **[不向下兼容]**
- 重构：重构会话查询参数作用：由`start=-1`时查询全部会话，改为 `start=0,size=-1` 时查询全部。 **[不向下兼容]** 
- 重构：`SaManager.getStpLogic("type")` 默认当对应type不存在时不再抛出异常，而是自动创建并返回。
- 重构：重构SSO模块，静态式API改为实例式：SaSsoHandle -> SaSsoProcessor。 **[重要]** **[不向下兼容]** 
- 重构：SSO-Server 端单点注销地址修改 `/sso/logout` -> `/sso/signout`，避免与 SSO-Client 端同 path 的冲突。 **[不向下兼容]** 
- 新增：文档新增 SSO 平台中心模式示例，跳连接进入子系统。 **[重要]** 
- 新增：新增SSO前后端分离集成示例 vue2 & vue3 版本。  **[重要]** 
- 重构：SSO 示例项目 http 请求工具改为 Forest。
- 新增：SSO模块文档新增单个项目同时搭建 `sso-server` 和 `sso-client` 的示例。 **[重要]** 
- 新增：SSO模块文档新增一个项目同时搭建两个 `sso-server` 服务 的示例。 **[重要]** 
- 文档：在线文档新增代码示例。
- 文档：在线文档增加全局调色功能。
- 文档：[自定义 SaTokenContext 指南] 章节新增对三种模型的解释。
- 文档：新增多账号体系混合鉴权代码示例。
- 文档：文档增加 `Gradle` 依赖方式和 `properties` 风格配置。
- 新增：新增 `sa-token-dependencies`，统一定义依赖版本。 **[重要]** 

##### 已知问题：

> 部分场景下 Token 重复问题，受影响版本 `=v1.32.0`
> - 受影响模块：
> 	- sa-token-core 切换了 Token 风格：tik、random-32、random-64、random-128，如果使用 默认uuid、simple-uuid 风格则不受影响。
> 	- sa-token-core 使用了临时 Token 认证模块，如果集成了 sa-token-temp-jwt 则不受影响。
> 	- sa-token-core 使用了 Same-Token 模块。
> 	- sa-token-jwt 全模块
> 	- sa-token-oauth2 全模块
> 	- sa-token-sso 模式二和模式三



### 2022-9-8 @v1.31.0
- 文档：新增优秀开源案例展示。
- 文档：新增博客展示，欢迎大家投稿。 
- 新增：新增 `SaInterceptor` 综合拦截器。   **[重要]**  **[不向下兼容]**
- 新增：新增 新增 `@SaIgnore` 忽略鉴权注解。   **[重要]** 
- 新增：新增插件 `sa-token-dao-redis-fastjson`，感谢 `@sikadai` 提交的pr。  **[重要]**
- 新增：新增插件 `sa-token-context-grpc`，感谢 `@LiYiMing666` 提交的pr。  **[重要]**
- 重构：SaSession 取消 `tokenSignList` 的 final 修饰符。
- 新增：SaSession 添加 `setTokenSignList` 方法。
- 重构：TokenSign 新增 `setValue` 和 `setDevice` 方法。
- 修复：修复多账号模式下不能正确重置 `StpLogic` 的问题。
- 修复：修复 SaSession 对象中 TokenSign 判断有可能空指针的问题。 
- 修复：解决当权限码为 null 时可能带来的空指针问题。
- 新增：新增 `StpUtil.getExtra(tokenValue, key)` 方法，用于获取任意 token 的扩展参数。 
- 优化：优化 `StpLogic#logoutByTokenValue` 方法逻辑，精简代码。
- 重构：`SaTokenConfig` 配置类字段 `isReadHead` 改为 `isReadHeader`。 **[不向下兼容]** 
- 修复：修复部分场景下踢人下线会抛出异常 `非Web上下文无法获取Request` 的问题。 
- 新增：新增方法 `StpLogic#getAnonTokenSession`，可在未登录情况下安全的获取 Token-Session。  **[重要]** 
- 新增：新增 `SaApplication` 对象，用于全局作用域存取值。   **[重要]** 
- 重构：将 `SaTokenListener` 改为事件发布订阅模式，允许同时注册多个侦听器。  **[重要]**  **[不向下兼容]**
- 重构：**StpUtil.login(id) 不再强制校验账号是否禁用，需要手动校验。** **[不向下兼容]** 
- 重构：`DisableLoginException` 更换名称为 `DisableServiceException`。 **[不向下兼容]** 
- 新增：新增对账号限制、分类封禁、阶梯封禁功能。	 **[重要]** 
- 新增：会话查询API增加反序获取会话方式。
- 新增：SSO模块增加 server-url 属性，用于简化各种 url 配置。  **[重要]**
- 修复：修复单点登录模块 `ssoLogoutCall` 配置项无效的问题。 
- 优化：优化 `SaSsoHandle.checkTicket(ticket, currUri);` 方法，使其不提供 currUri 参数时将不再注册单点注销回调。
- 修复：修复 `SaOAuth2Handle` 类中 `doLogin` 方法没有使用 `Param.pwd` 常量的问题。 
- 新增：新增 `SaOAuth2Util.checkClientTokenScope(clientToken, scopes)` 方法，校验 Client-Token 是否含有指定 Scope。
- 删除：删除 `sa-token-jwt` 模块过期 class。
- 重构：`sa-token-jwt` 模块依赖改为 `hutool-jwt`，并升级版本为 5.8.5。
- 重构：`sa-token-jwt` 模块改为 `Util + Template` 形式，方便针对部分代码重写。  **[重要]** 
- 新增：在线文档添加API手册。
- 重构：`sa-token-oauth2` 模块密码模式新增 `client_secret` 参数校验。**[不向下兼容]** 
- 新增：集成 `jacoco` 插件，核心包单元测试覆盖率提高至 90% 以上。
- 优化：开源案例分离专属仓库：[Awesome-Sa-Token](https://gitee.com/sa-token/awesome-sa-token)



### 2022-05-9 @v1.30.0
- 新增：新增集成 Web-Socket 鉴权示例。 **[重要]**
- 新增：新增集成 Web-Socket（Spring封装版） 鉴权示例。
- 新增：新增 jfinal 集成包 `sa-token-jfinal-plugin`  **[重要]**
- 新增：新增 jboot 集成包 `sa-token-jboot-plugin` （感谢 @nxstv 提交的pr）
- 修复：修复整合 sa-token-jwt Style 模式时，`StpUtil.getExtra("key")` 无效的bug  
- 升级：升级 `sa-token-context-dubbo` dubbo版本：`2.7.11` -> `2.7.15`
- 升级：借助 `flatten-maven-plugin` 统一版本号定义 （感谢 @ruansheng8 提交的pr）   **[重要]**
- 修复：修复在 `springboot 2.6.x` 下 `quick-login` 插件循环依赖无法启动的问题 
- 优化：`sa-token-spring-aop` 依赖改为 `sa-token-core`，避免在webflux环境下启动报错的问题 
- 优化：源码注释 设备标识 改为 设备类型 更符合语义 
- 修复：解决部分协议下 dubbo 参数变为小写导致 `Id-Token` 鉴权无效的问题 
- 升级：单元测试升级为 JUnit5 
- 新增：新增 `maxLoginCount` 配置，指定同一账号可同时在线的最大数量   **[重要]** 
- 升级：彻底删除 SaTokenAction 接口，完全由 SaStrategy 代替 
- 新增：新增 `sa-token-dao-redisx` 插件，感谢 @noear 提交的pr  **[重要]** 
- 优化：增加 parseToken 未配置 jwt 密钥时的异常提示，感谢 @BATTLEHAWK00 提交的pr 
- 优化：sso,oauth2 插件中调用配置类使用 getter 方法，感谢 @Naah 提交的pr 
- 新增：新增 json 转换器模块 
- 重构：SaTokenListener#doLogin 方法新增 tokenValue 参数  **[不向下兼容]** 
- 升级：SpringBoot 相关组件依赖版本升级至 `2.5.12` 
- 文档：在线文档所有 `AjaxJson` 改为 `SaResult` 
- 文档：“多账号认证” -> 改为 “多账户认证” 
- 文档：部分章节新增动态演示图  **[重要]** 
- 升级：顶级异常类 `SaTokenException` 增加 code 异常细分状态码。[详见](/fun/exception-code) **[重要]** 
- **注意升级：受异常细分状态码影响，`NotPermissionException` 类中 `getCode()` 方法改为 `getPermission()`。** **[不向下兼容]**
- SSO 模块升级：
	- 重构：SSO 模块从核心包拆分为独立插件 `sa-token-sso` **[重要]** 
	- 优化：SSO模式三单点注销回调方法中，注销语句改为：`stpLogic.logout(loginId)` 更符合情景  
	- 修复：解决 sso 构建认证地址时，部分 Servlet 版本内部实现不一致带来的双 back 参数问题。
	- 升级：SSO 模块提供精细化异常处理 
	- 重构：SSO 模式三接口 `/sso/checkTicket`、`/sso/logout`，更改响应体格式   **[不向下兼容]** 
	- 优化：SSO 模式三单点注销搭建示例增加 `try-catch`，提高容错性  
	- 优化：`SsoUtil.singleLogout` 改为 `SsoUtil.ssoLogout`，且无需再提供 secretkey 参数   **[不向下兼容]** 
	- 升级：将 SSO 模式三的接口调用改为签名式校验。  **[重要]**  **[不向下兼容]** 
	- 新增：新增 SSO 模式三下无 sdk 的对接示例， 感谢 @Sa-药水 的建议反馈 	**[重要]** 
- sa-token-jwt 模块升级：
	- 重构：`sa-token-jwt` 的创建，强制校验loginType  **[不向下兼容]** 
	- 重构：`StpLogicJwtForStateless` 由重写 login 方法改为重写 createLoginSession 
	- 重构：`SaJwtUtil` 工具类不再吞并异常消息，且提供精细化异常 code 码。
	- 重构：改名：StpLogicJwtForStyle -> StpLogicJwtForSimple
	- 重构：改名：StpLogicJwtForMix -> StpLogicJwtForMixin
	- 修复：修复 `StpLogicJwtForSimple` 模式下 Extra 数据可能受到旧 token 影响的bug


### 2022-02-10 @v1.29.0
- 升级：sa-token-jwt插件可在登录时添加额外数据。
- 重构：优化Dubbo调用时向下传递Token的规则，可避免在项目启动时由于Context无效引发的bug。
- 重构：OAuth2 授权模式开放由全局配置和Client单独配置共同设定。
- 重构：OAuth2 模块部分属性支持每个 Client 单独配置。
- 重构：OAuth2 模块部分方法名修复单词拼写错误：converXxx -> convertXxx。
- 重构：修复 OAuth2 模块 `deleteAccessTokenIndex` 回收 token 不彻底的bug。
- 新增：OAuth2 模块新增 `pastClientTokenTimeout`，用于指定 PastClientToken 默认有效期。
- 文档：常见报错章节增加目录树，方便查阅。
- 文档：优化文档样式。
- 新增：新增 BCrypt 加密。
- 修复：修复StpUtil.getLoginIdByToken(token) 在部分场景下返回出错的bug。
- 重构：优化OAuth2模块密码式校验步骤。
- 新增：新增Jackson定制版Session，避免timeout属性的序列化。
- 新增：SaLoginModel新增setToken方法，用于预定本次登录产生的Token。 
- 新增：新增 StpUtil.createLoginSession() 方法，用于无Token注入的方式创建登录会话。 
- 新增：OAuth2 与 StpUtil 登录会话数据互通。
- 新增：新增 `StpUtil.renewTimeout(100);` 方法，用于 Token 的 Timeout 值续期。 
- 修复：修复默认dao实现类中 `updateObject` 无效的bug 
- 完善：完善单元测试。


### 2021-11-5 @v1.28.0
- 新增：新增 `sa-token-jwt` 插件，用于与jwt的整合 **[重要]**
- 新增：新增 `sa-token-context-dubbo` 插件，用于与 Dubbo 的整合 **[重要]**
- 文档：文档新增章节：Sa-Token 插件开发指南 **[重要]**
- 文档：文档新增章节：名称解释
- 优化：抽离 `getSaTokenDao()` 方法，方便重写 
- 新增：单元测试新增多账号模式数据不互通测试
- 优化：优化在线文档，修复部分错误之处 	
- 优化：优化未登录异常抛出提示，标注无效的Token值 
- 修复：修复单词拼写错误 `getDeviceOrDefault` 
- 优化：优化 jwt 集成示例 
- 文档：新增常见问题总结


### 2021-10-11 @v1.27.0
- 升级：增强 SaRouter 链式匹配能力  	**[重要]**  	
- 新增：新增插件 Thymeleaf 标签方言   **[重要]**  	
- 新增：@SaCheckPermission 增加 orRole 字段，用于权限角色“双重or”匹配    **[重要]**
- 升级：Cookie模式增加 `secure`、`httpOnly`、`sameSite`等属性的配置 	**[重要]**  	
- 重构：重构SSO三种模式，抽离出统一的认证中心   **[重要]**   
- 新增：新增 SaStrategy 策略类，方便内部逻辑按需重写 **[重要]**		
- 新增：临时认证模块新增 deleteToken 方法用于回收 Token  
- 新增：新增 kickout、replaced 等注销会话的方法，更灵活的控制会话周期  **[重要]** 
- 新增：权限认证增加API：`StpUtil.hasPermissionAnd`、`StpUtil.hasPermissionOr` 
- 新增：角色认证增加API：`StpUtil.hasRoleAnd`、`StpUtil.hasRoleOr` 
- 新增：新增 `StpUtil.getRoleList()` 和 `StpUtil.getPermissionList()` 方法  
- 新增：新增 StpLogic 自动注入特性，可快速方便的扩展 StpLogic 对象 
- 优化：优化同端互斥登录逻辑，如果登录时没有指定设备类型标识，则默认顶替所有设备类型下线  
- 优化：在未登录时调用 hasRole 和 hasPermission 不再抛出异常，而是返回false 
- 升级：升级注解鉴权算法，并提供更简单的重写方式    
- 文档：新增常见报错排查，方便快速排查异常报错 
- 文档：文档新增SSO单点登录与OAuth2技术选型对比  
- 破坏式更新：
	- [向下兼容] 废弃 SaTokenAction 接口，替代方案： SaStrategy  
	- [向下兼容] 移除 `StpUtil.logoutByLoginId()` 更换为 `StpUtil.kickout()`;
	- [不向下兼容] 侦听器 doLogoutByLoginId 与 doReplaced 方法移除 device 参数 
	- [不向下兼容] 侦听器 doLogoutByLoginId 方法重命名为 doKickout  


### 2021-9-2 @v1.26.0
- 优化：优化单点登录文档 
- 新增：新增 `Http Basic` 认证 **[重要]** 
- 新增：文档新增跨域解决方案 
- 文档：新增 Nginx 转发请求丢失uri的解决方案
- 文档：新增 SSO 自定义 API 路由示例  **[重要]** 
- 示例：新增 `SSO-Server` 端前后端分离示例  **[重要]** 


### 2021-8-16 @v1.25.0
- 新增：`SaRequest`新增`getHeader(name, defaultValue)`方法，用于获取header默认值 
- 新增：`SaRequest` 添加 `forward` 转发方法  
- 新增：Readme新增源码模块介绍、友情链接、正在使用Sa-Token的项目 
- 重构：重构SSO单点登录模块源码，增加可读性 
- 新增：SSO配置表新增所属端说明 
- 新增：SSO模式三新增账号资料同步示例  **[重要]** 
- 新增：前后端分离模式下接入SSO的示例  **[重要]** 
- 优化：优化SSO单点注销重定向逻辑 
- 重构：重构SSO单点登录模块部分API 
- 优化：优化SaQuickBean中过滤器处理逻辑  
- 文档：优化文档样式，增加示例  
- 文档：代码鉴权、注解鉴权、路由拦截鉴权，选择指南 
- 文档：文档新增 SSO旧有系统改造指南 
- 文档：SSO集成文档里添加API列表 
- 文档：新增 `Sa-Token-Study` 链接，讲解 Sa-Token 源码涉及到的技术点 
- 不兼容更新重构：
	- 重构：修复 `SaReactorHolder.getContent()` 拼写错误：`content` -> `context` 


### 2021-7-24 @v1.24.0
- 修复：修复部分场景下Alone-Redis插件导致项目无法启动的问题
- 优化：增加对SpringBoot1.x版本的兼容性 
- 新增：SaOAuth2Util新增checkScope函数，用于校验令牌是否具备指定权限 
- 新增：OAuth2.0模块新增revoke接口，用于提前回收 Access-Token 令牌 
- 新增：新增`Sa-Id-Token` 模块，解决微服务内部调用鉴权  **[重要]**
- 文档：新增OAuth2.0模块常用方法说明  
- 优化：大幅度优化文档示例 


### 2021-7-19 @v1.23.0
- 新增：Sa-Token-OAuth2 模块正式发布   **[重要]** 
- 修复：修复jwt集成demo无法正确注册StpLogic的bug
- 修复：修复登录时某些场景下Session续期可能不正常的bug  
- 优化：代码注释优化，文档优化  


### 2021-7-10 @v1.22.0
- 新增：SaSsoConfig 部分属性增加set连缀风格 
- 优化：SaSsoUtil 可定制化底层的 `StpLogic`
- 新增：新增 `SaSsoHandle` 大幅度简化单点登录整合步骤  **[重要]** 
- 新增：新增Sa-Token在线测评，链接：[https://ks.wjx.top/vj/wFKPziD.aspx](https://ks.wjx.top/vj/wFKPziD.aspx)  **[重要]**
- 新增：Sa-Token-Quick-Login 插件新增拦截与放行路径配置
- 优化：大幅度优化文档示例 


### 2021-7-2 @v1.21.0
- 新增：新增Token二级认证 	**[重要]** 
- 新增：新增`Sa-Token-Alone-Redis`独立Redis插件   **[重要]**  
- 新增：新增SSO三种模式，彻底解决所有场景下的单点登录问题   **[重要]**  
- 新增：新增多账号模式下，注解合并示例		**[重要]**  
- 新增：新增`SaRouter.back()`函数，用于停止匹配返回结果  
- 不兼容更新重构：
	- 更改yml配置前缀：原`[spring.sa-token.]` 改为 `[sa-token.]`，目前版本暂时向下兼容，请尽快更新 


### 2021-6-17 @v1.20.0
- 新增：新增Solon适配插件，感谢大佬 `@刘西东` 提供的pr **[重要]** 
- 新增：新增`SaRouter.stop()`函数，用于一次性跳出匹配链功能 **[重要]** 
- 新增：新增单元测试   **[重要]** 
- 新增：新增临时令牌验证模块   **[重要]**  
- 新增：新增`sa-token-temp-jwt`模块整合jwt临时令牌鉴权    **[重要]**  
- 新增：会话 `SaSession.get()` 增加缓存API，简化代码 
- 新增：新增框架调查问卷 
- 修复：修复同时引入 `Spring Cloud Bus` 与 `Sa-Token` 冲突的问题   **[重要]** 
- 修复：修复`SaServletFilter`异常函数中无法自定义`Content-Type`的问题 
- 文档：新增微服务依赖引入说明 
- 文档：新增认证流程图 
- 不兼容更新重构：
	- 方法：`StpUtil.setLoginId(id)` -> `StpUtil.login(id)` 
	- 方法：`StpUtil.getLoginKey()` -> `StpUtil.getLoginType()` (注意其它所有地方的`LoginKey`均已更改为`loginType`)
	- 工具类：`SaRouterUtil` -> `SaRouter` 
	- 配置类：`allowConcurrentLogin` -> `isConcurrent` 
	- 配置类：`isV` -> `isPrint` 
	- 为保证平滑更新，旧API仍旧保留，但已增加`@Deprecated`注解，请尽快更新至新API  


### 2021-5-10 @v1.19.0
- 新增：注解鉴权新增定制loginType功能  **[重要]** 
- 重构：重构目录结构，抽离`plugin`模块  **[重要]** 
- 新增：新增 `sa-token-quick-login` 插件，零代码集成登录功能  **[重要]** 
- 优化：所有函数式接口增加`@FunctionalInterface`注解，感谢群友`@MrXionGe`提供的建议 
- 优化：文档优化... 


### 2021-4-24 @v1.18.0
- 新增：新增权限通配符功能，灵活设置权限  **[重要]** 
- 修复：修复自动续签处的逻辑错误 
- 新增：新增Web开发常见漏洞防护建议 
- 修复：修复`SaRequest`中缺少`getMethod()`的bug 
- 修复：修复自动续签时的逻辑错误，感谢群成员`@N`的建议 
- 新增：全局过滤器新增 `beforAuth` 前置函数 
- 修复：修复在带有上下文的项目中无法正确获取请求路径的bug，感谢群成员`@dlwlrma`提供的建议
- 新增：新增`SaHolder`上下文持有类，可方便的在上下文中读写数据 
- 重构：`SaTokenManager` -> `SaManager` 
- 重构：`SaTokenInsideUtil` -> `SaFoxUtil` 


### 2021-4-17 @v1.17.0
- 修复：在WebFlux环境中引入Redis集成包无法启动的问题 
- 修复：修复JWT集成示例中版本升级API的变更 
- 优化：优化启动时字符画打印
- 文档：新增集成环境说明
- 文档：新增功能介绍图  
- 新增：全局过滤器增加限定[拦截路径]与[排除路径]功能 
- 重构：全局过滤器执行函数放到成员变量里，连缀风格配置 
- 新增：新增全局侦听器，可在用户登陆、注销、被踢下线等关键性操作时进行一些AOP操作 **[重要]** 


### 2021-4-12 @v1.16.0
- 新增：新增账号封禁功能，指定时间内账号无法登陆 			**[重要]**
- 新增：核心包脱离`ServletAPI`，彻底零依赖！  				**[重要]**
- 新增：新增基于`ThreadLocal`的上下文容器					**[重要]**
- 新增：新增`Reactor`响应式编程支持，`WebFlux`集成！			**[重要]** 
- 新增：新增全局过滤器，解决拦截器无法拦截静态资源的问题			**[重要]** 
- 新增：新增微服务网关鉴权方案！可接入`ShenYu`、`Gateway`等网关组件!	**[重要]** 
- 新增：AOP切面定义`Order`顺序为`-100`，可保证在多个自定义切面前执行 
- 文档：新增推荐公众号列表 


### 2021-3-23 @v1.15.0
- 新增：文档添加源码涉及技术栈说明 
- 优化：优化路由拦截器模块文档，更简洁的示例
- 修复：修复非web环境下的错误提示，Request->Response
- 修复：修复Cookie注入时path判断错误，感谢@zhangzi0291提供的PR
- 新增：文档集成Redis章节新增redis配置示例说明，感谢群友 `@-)` 提供的建议
- 新增：增加token前缀模式，可在配置token读取前缀，适配`Bearer token`规范 **[重要]**
- 优化：`SaTokenManager`初始化Bean去除`initXxx`方法，优化代码逻辑
- 新增：`SaTokenManager`新增`stpLogicMap`集合，记录所有`StpLogic`的初始化，方便查找
- 新增：`Session`新增timeout操作API，可灵活修改Session的剩余有效时间 
- 新增：token前缀改为强制校验模式，如果配置了前缀，则前端提交token时必须带有
- 优化：精简`SaRouteInterceptor`，只保留自定义验证和默认的登陆验证，去除冗余功能 
- 优化：`SaRouterUtil`迁移到core核心包，优化依赖架构
- 优化：默认Dao实现类里`Timer定时器`改为子线程 + sleep 模拟 
- 新增：`Session`新增各种类型转换API，可快速方便存取值  **[重要]** 
- 升级注意：
	- `SaRouterUtil`类迁移到核心包，注意更换import地址
	- `SaRouteInterceptor`去出冗余API，详情参考路由鉴权部分


### 2021-3-12 @v1.14.0
- 新增：新增`SaLoginModel`登录参数Model，适配 [记住我] 模式	 **[重要]**
- 新增：新增 `StpUtil.login()` 时指定token有效期，可灵活控制用户的一次登录免验证时长 
- 新增：新增Cookie时间判断，在`timeout`设置为-1时，`Cookie`有效期将为`Integer.MAX_VALUE`	 **[重要]**
- 新增：新增密码加密工具类，可快速MD5、SHA1、SHA256、AES、RSA加密 	**[重要]**
- 新增：新增 OAuth2.0 模块  	**[重要]** 
- 新增：`SaTokenConfig`配置类所有set方法支持链式调用 
- 新增：`SaOAuth2Config` sa-token oauth2 配置类所有set方法新增支持链式调用 
- 优化：`StpLogic`类所有`getKey`方法重名为`splicingKey`，更语义化的函数名称 
- 新增：`IsRunFunction`新增`noExe`函数，用于指定当`isRun`值为`false`时执行的函数 
- 新增：`SaSession`新增数据存取值操作API 
- 优化：优化`SaTokenDao`接口，增加Object操作API
- 优化：jwt示例`createToken`方法去除默认秘钥判断，只在启动项目时打印警告 
- 文档：常见问题新增示例(修改密码后如何立即掉线)
- 文档：权限认证文档新增[如何把权限精确搭到按钮级]示例说明 
- 文档：优化文档，部分模块添加图片说明 


### 2021-2-9 @v1.13.0
- 优化：优化源码注释与文档
- 新增：文档集成Gitalk评论系统 
- 优化：源码包`Maven`版本号更改为变量形式 
- 修复：文档处方法名`getPermissionList`错误的bug 
- 修复：修复`StpUtil.getTokenInfo()`会触发自动续签的bug 
- 修复：修复接口 `SaTokenDao` 的 `searchData` 函数注释错误 
- 新增：`SaSession`的创建抽象到`SaTokenAction`接口，方便按需重写 
- 新建：框架内异常统一继承 `SaTokenException` 方便在异常处理时分辨处理 
- 新增：`SaSession`新增`setId()`与`setCreateTime()`方法，方便部分框架的序列化 
- 新增：新增`autoRenew`配置，用于控制是否打开自动续签模式
- 新增：同域模式下的单点登录  **[重要]**
- 新增：完善分布式会话的文档说明


### 2021-1-12 @v1.12.0
- 新增：提供JWT集成示例 **[重要]**
- 新增：新增路由式鉴权，可方便的根据路由匹配鉴权  **[重要]**
- 新增：新增身份临时切换功能，可在一个代码段内将会话临时切换为其它账号  **[重要]**
- 优化：将`SaCheckInterceptor.java`更名为`SaAnnotationInterceptor.java`，更语义化的名称 
- 优化：优化文档
- 升级：v1.12.1，新增`SaRouterUtil`工具类，更方便的路由鉴权   **[重要]**


### 2021-1-10 @v1.11.0
- 新增：提供AOP注解鉴权方案 **[重要]**
- 优化自动生成token的算法


### 2021-1-9 @v1.10.0
- 新增：提供查询所有会话方案  **[重要]**
- 修复：修复token设置为永不过期时无法正常被顶下线的bug，感谢github用户 @zjh599245299 提出的bug


### 2021-1-6 @v1.9.0
- 优化：`spring-boot-starter-data-redis` 由 `2.3.7.RELEASE` 改为 `2.3.3.RELEASE` 
- 修复：补上注解拦截器里漏掉验证`@SaCheckRole`的bug
- 新增：新增同端互斥登录，像QQ一样手机电脑同时在线，但是两个手机上互斥登录  **[重要]**


### 2021-1-2 @v1.8.0
- 优化：优化源码注释
- 修复：修复部分文档错别字 
- 修复：修复项目文件夹名称错误
- 优化：优化文档配色，更舒服的代码展示
- 新增：提供`sa-token`集成 `redis` 的 `spring-boot-starter` 方案  **[重要]**
- 新增：新增集成 `redis` 时，以`jackson`作为序列化方案  **[重要]**
- 新增：dao层默认实现增加定时清理过期数据功能  **[重要]**
- 新增：新增`token专属session`, 更灵活的会话管理  **[重要]**
- 新增：增加配置，指定在获取`token专属session`时是否必须登录
- 新增：在无token时自动创建会话，完美兼容`token-session`会话模型!   **[重要]**
- 修改：权限码限定必须为String类型 
- 优化：注解验证模式由boolean属性改为枚举方式
- 删除：`StpUtil`删除部分冗长API，保持API清爽性
- 新增：新增角色验证 (角色验证与权限验证已完全分离)  **[重要]**
- 优化：移除`StpUtil.kickoutByLoginId()`API，由`logoutByLoginId`代替
- 升级：开源协议修改为`Apache-2.0`


### 2020-12-24 @v1.7.0
- 优化：项目架构改为maven多模块形式，方便增加新模块 **[重要]**
- 优化：与`springboot`的集成改为`springboot-starter`模式，无需`@SaTokenSetup`注解即可完成自动装配 **[重要]**
- 新增：新增`activity-timeout`配置，可控制token临时过期与续签功能 **[重要]**
- 新增：`timeout`过期时间新增-1值，代表永不过期 
- 新增：`StpUtil.getTokenInfo()`改为对象形式，新增部分常用字段 
- 优化：解决在无cookie模式下，不集成redis时会话无法主动过期的问题 
- 修复：修复文档首页样式问题 


### 2020-12-17 @v1.6.0
- 新增：花式token生成方案 **[重要]** 
- 优化：优化`readme.md` 
- 修复：修复`SaCookieOper`与`SaTokenAction`无法自动注入的问题 


### 2020-12-16 @v1.5.1
- 新增：细化未登录异常类型，提供五种场景值：未提供token、token无效、token已过期 、token已被顶下线、token已被踢下线 **[重要]**
- 修复：修复`StpUtil.getSessionByLoginId(String loginId)`方法转换key出错的bug，感谢群友 @(＃°Д°)、@一米阳光 发现的bug 
- 优化：修改方法`StpUtil.getSessionByLoginId(Object loginId)`的isCreate值默认为true 
- 修改：`方法delSaSession`修改为`deleteSaSession`，更加语义化的函数名称 
- 新增：新增`StpUtil.getTokenName()`方法，更语义化的获取tokenName 
- 新增：新增`SaTokenAction`框架行为Bean，方便重写逻辑 
- 优化：`Cookie操作`改为接口代理模式，使其可以被重写 
- 优化：文档里集成redis部分增加redis的pom依赖示例
- 修复：登录验证-> `StpUtil.getLoginId_defaultNull()` 修复方法名错误的问题 
- 优化：优化`readme.md` 
- 升级：开源协议修改为`MIT`


### 2020-9-7 @v1.4.0
- 优化：修改一些函数、变量名称，使其更符合阿里java代码规范
- 优化：`tokenValue`的读取优先级改为：`request` > `body` > `header` > `cookie`  **[重要]**
- 新增：新增`isReadCookie`配置，决定是否从`cookie`里读取`token`信息 
- 优化：如果`isReadCookie`配置为`false`，那么在登录时也不会把`cookie`写入`cookie` 
- 新增：新增`getSessionByLoginId(Object loginId, boolean isCreate)`方法
- 修复：修复文档部分错误，修正群号码


### 2020-5-2 @v1.3.0
- 新增：新增 `StpUtil.checkLogin()` 方法，更符合语义化的鉴权方法
- 新增：注册拦截器时可设置 `StpLogic` ，方便不同模块不同鉴权方式
- 新增：抛出异常时增加 `loginType` 区分，方便多账号体系鉴权处理 
- 修复：修复启动时的版本字符画版本号打印不对的bug  
- 修复：修复文档部分不正确之处
- 新增：新增文档的友情链接


### 2020-3-7 @v1.2.0
- 新增：新增注解式验证，可在路由方法中使用注解进行权限验证  **[重要]**
- 参考：[注解式验证](use/at-check)


### 2020-2-12 @v1.1.0
- 修复：修复`StpUtil.getLoginId(T defaultValue)`取值转换错误的bug


### 2020-2-4 @v1.0.0
- 第一个版本出炉 
