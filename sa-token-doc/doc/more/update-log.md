# 更新日志 


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
- 新增：抛出异常时增加 `loginKey` 区分，方便多账号体系鉴权处理 
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
- GitHub：[https://github.com/click33/sa-token](https://github.com/click33/sa-token)
- gitee：[https://gitee.com/sz6/sa-token](https://gitee.com/sz6/sa-token)
