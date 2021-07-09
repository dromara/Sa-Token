# 更新日志 


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
- 新增：新增微服务网关鉴权方案！可接入`Soul`、`Gateway`等网关组件!	**[重要]** 
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
