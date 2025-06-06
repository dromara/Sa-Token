# 更新日志 

### v1.44.0 @2025-6-7

- 修复：修复 sso-server 前后端分离示例无法正常登录的问题。
- 修复：修复 SSO 模式三全端注销失效的问题。
- 修复：修复 SSO `SaSsoClientModel` 部分场景下无法序列化的问题。
- 新增：OAuth2 模块新增支持从 `SaOAuth2DataLoader` 接口获取高级权限与低级权限的方法。
- 修复：修复 `sa-token-dubbo` 与 `sa-token-dubbo3` 每次调用都强制需要上下文的问题。
- 文档：新增 `sa-token-dubbo3` 的说明。
- 文档：更新赞助者名单。
- 文档：新增 `loveqq-framework` 框架集成包。 **[重要]**



### v1.43.0 @2025-5-17
- core: 
	- 新增：`SaLogoutParameter` 新增 `deviceId` 参数，用于控制指定设备 id 的注销。  **[重要]**
	- 新增：新增 `SaHttpTemplate` 请求处理器模块。
	- 新增：TOTP 增加 `issuer` 字段。  merge: [pr 329](https://gitee.com/dromara/sa-token/pulls/329) 
	- 修复：修复 `Http Digest` 认证时 url 上带有查询参数时认证无法通过的问题。merge: [pr 334](https://gitee.com/dromara/sa-token/pulls/334) 
	- 新增：@SaCheckOr 注解添加 `append` 字段，用于抓取未预先定义的注解类型进行批量注解鉴权。
	- 新增：侦听器 `doRenewTimeout` 方法添加 loginType 参数。
	- 新增：`SaInterceptor` 新增 `beforeAuth` 认证前置函数。
- SSO：
	- 新增：单点注销支持单设备注销。   **[重要]**  fix: [#IA6ZK0](https://gitee.com/dromara/sa-token/issues/IA6ZK0) 、[#747](https://github.com/dromara/Sa-Token/issues/747)
	- 新增：新增消息推送机制。  **[重要]**   fix: [#IBGXA7](https://gitee.com/dromara/sa-token/issues/IBGXA7) 
	- 新增：配置项 clients 用于单独配置每个 client 的授权信息。  **[重要]** 
	- 新增：配置项 `allowAnonClient` 决定是否启用匿名 client。
	- 新增：SSO 模块新增配置文件方式启用“不同 client 不同秘钥”能力。
	- 重构：sso-client 封装化获取 client 标识值。
	- 新增：新增 SSO Strategy 策略类。
	- 新增：sso-client 新增 `convertCenterIdToLoginId`、`convertLoginIdToCenterId` 策略函数，用于描述本地 LoginId 与认证中心 loginId 的转换规则。
	- 新增：sso-server 新增 `jumpToRedirectUrlNotice` 策略，用于授权重定向跳转之前的通知。
	- 优化：调整整体 SSO 示例代码。
	- 新增：新增 ReSdk 模式对接示例：`sa-token-demo-sso3-client-resdk`。  **[重要]** 
	- 新增：新增匿名应用模式对接示例：`sa-token-demo-sso3-client-anon`。  **[重要]** 
- OAuth2：
	- 新增：`SaClientModel` 新增 `isAutoConfirm` 配置项，用于决定是否允许应用可以自动确认授权。 **[重要]** 
	- 新增：多 `Access-Token` 并存、多 `Refresh-Token` 并存、多 `Client-Token` 并存能力。 **[重要]**  fix: [#IBHFD1](https://gitee.com/dromara/sa-token/issues/IBHFD1) 、 [#IBLL4Q](https://gitee.com/dromara/sa-token/issues/IBLL4Q) 、[#724](https://github.com/dromara/Sa-Token/issues/724) 
	- 新增：Scope 分割符支持加号。merge: [pr 333](https://gitee.com/dromara/sa-token/pulls/333) 
	- 修复：修复 oidc 协议下，当用户数据变动后，id_token 仍是旧信息的问题。
	- 优化：对 `OAuth2 Password` 认证模式需要重写处理器添加强提醒。
	- 优化：将认证流程回调从 `SaOAuth2ServerConfig` 转移到 `SaOAuth2Strategy`。
	- 新增：新增 `SaOAuth2Strategy.instance.userAuthorizeClientCheck` 策略，用于检查指定用户是否可以授权指定应用。fix: [#553](https://github.com/dromara/Sa-Token/issues/553) 
	- 优化：优化调整 `sa-token-oauth2` 模块代码结构及注释。
	- 新增：`currentAccessToken()`、`currentClientToken()`，简化读取 `access_token`、`client_token` 步骤
- 插件：
	- 新增：新增 `sa-token-forest` 插件，用于在 Http 请求处理器模块整合 Forest。
	- 新增：新增 `sa-token-okhttps` 插件，用于在 Http 请求处理器模块整合 OkHttps。
	- 拆分：API Key 模块拆分独立插件包：`sa-token-apikey`。
	- 拆分：API Sign 模块拆分独立插件包：`sa-token-sign`。
	- 修复：修复 `sa-token-dubbo` 插件部分场景上下文控制出错的问题。
	- 修复：修复 `sa-token-sanck3` `SaSessionForSnack3Customized:getModel` 接收 map 值时会出错的问题。 merge: [pr 330](https://gitee.com/dromara/sa-token/pulls/330) 
	- 修复：修复使用 `sa-token-redis-template-jdk-serializer` 时反序列化错误。merge: [pr 331](https://gitee.com/dromara/sa-token/pulls/331) 
	- 修复：`sa-token-snack3` 优化 `objectToJson` 序列化处理（增加类名，但不增加根类名）。
	- 重构：重构 `sa-token-redis-template`、`sa-token-redis-template-jdk-serializer` 插件中 update 方法 ttl 获取方式改为毫秒，以减少 update 时的 ttl 计算误差。  **[重要]** 
- 示例：
	- 新增：新增 SSE 鉴权示例。
- 文档：
	- 新增：新增文档离线版下载。
	- 新增：新增框架功能列表插图。
	- 新增：新增示例：如何在响应式环境下的 Filter 里调用 Sa-Token 同步 API。
	- 新增：新增 QA：在 idea 导入源码，运行报错：java: 程序包cn.dev33.satoken.oauth2不存在。
	- 新增：新增 QA：新增QA：报错：SaTokenContext 上下文尚未初始化。
	- 新增：新增 QA：在 idea 导入源码，运行报错：java: 程序包cn.dev33.satoken.oauth2不存在。
	- 新增：重写路由匹配算法修正为最新写法。
	- 新增：修复 OAuth2 UnionId 章节相关不正确描述。
	- 优化：完善 QA：访问了一个不存在的路由，报错：SaTokenContext 上下文尚未初始化。   fix: [#771](https://github.com/dromara/Sa-Token/issues/771)
	- 优化：补充 sso 模块遗漏的配置字段介绍。
	- 优化：OAuth2-Server 示例添加真正表单。
	- 新增：文档新增重写 `PasswordGrantTypeHandler` 处理器示例。
	- 新增：sso 章节和 oauth2 章节文档增加可重写策略说明。
- 其它：
	- 新增：readme 新增框架功能介绍图。
	- 新增：SSO 模块新增思维导图说明。
	- 新增：readme 新增 Forest 的友情链接。
									
								


### v1.42.0 @2025-4-11
更新导读：[视频](https://www.bilibili.com/video/BV1h85izzEe8/)、[文字版](https://juejin.cn/post/7491971657201451062)

- core: 
	- 新增: 新增 `API Key` 模块。   **[重要]**
	- 新增: 新增 `TOTP` 实现。   **[重要]**
	- 重构：重构 `TempToken` 模块，新增 value 反查 token 机制。   **[重要]**
	- 升级: 重构升级 `SaTokenContext` 上下文读写策略。   **[重要]**
	- 新增: 新增 Mock 上下文模块。   **[重要]**
	- 删除: 删除二级上下文模块。
	- 新增: 新增异步场景使用 demo。   **[重要]**
	- 新增: 新增 `Base32` 编码工具类。
	- 新增：新增 `CORS` 跨域策略处理函数，提供不同架构下统一的跨域处理方案。
	- 新增：`renewTimeout` 续期方法增加 token 终端信息有效性校验。
	- 新增: 全局配置项 `cookieAutoFillPrefix`：cookie 模式是否自动填充 token 前缀。
	- 新增: 全局配置项 `rightNowCreateTokenSession`：在登录时，是否立即创建对应的 `Token-Session`。
	- 优化：优化 `Token-Session` 获取算法，减少缓存读取次数。
	- 新增：`SaLoginParameter` 支持配置 `SaCookieConfig`，以配置 Cookie 相关参数。
	- 修改：防火墙校验过滤器的注册顺序 修改为 -102。
	- 新增：防火墙 `hook` 注册新增 `registerHookToFirst`、`registerHookToSecond` 方法，以便更灵活的控制 hook 顺序。
- 插件：
	- 新增: `sa-token-quick-login` 插件支持 `Http Basic` 方式通过认证。
- 单元测试：
	- 补全：补全 `Temp Token` 模块单元测试。
- 文档：
	- 补全：补全赞助者名单。
	- 修复：修复 `Thymeleaf` 集成文档不正确的依赖示例说明。
	- 修复：修复 `unionid` 章节错误描述。
	- 优化：采用更细致的描述优化SSO模式三单点注销步骤。
	- 新增：登录认证文档添加 Cookie 查看步骤演示图。
	- 新增：多账号模式新增注意点：运行时不可更改 `LoginType`。
	- 新增: 多账号模式QA：在一个接口里获取是哪个体系的账号正在登录。
	- 新增：新增QA：解决低版本 `SpringBoot (<2.2.0)` 引入 Sa-Token 报错的问题。
	- 新增：新增QA：前后端一体项目下，在拦截未登录进入登录页面时，如何登录完成后原路返回？
	- 新增：新增QA：Sa-Token 集成 Redis 如何集群？
	- 新增：新增QA：如何自定义框架读取 token 的方式？
	- 新增：新增QA：修改 `hosts` 文件无效可能原因排查。
	- 新增：新增QA：如何防止 CSRF 攻击。
	- 新增: “异步 & Mock 上下文” 章节。
	- 升级：升级“自定义 SaTokenContext 指南”章节文档。



### v1.41.0 @2025-3-21
更新导读：[视频](https://www.bilibili.com/video/BV1aNo4YCEM1/)、[文字版](https://juejin.cn/post/7484191942358499368)

- core: 
	- 修复：修复 `StpUtil.setTokenValue("xxx")`、`loginParameter.getIsWriteHeader()` 空指针的问题。 fix: [#IBKSM0](https://gitee.com/dromara/sa-token/issues/IBKSM0)
	- 修复：将 `SaDisableWrapperInfo.createNotDisabled()` 默认返回值封禁等级改为 -2，以保证向之前版本兼容。
	- 新增：新增基于 SPI 的插件体系。   **[重要]** 
	- 重构：JSON 转换器模块。   **[重要]** 
	- 新增：新增 serializer 序列化模块，控制 `Object` 与 `String` 的序列化方式。   **[重要]** 
	- 重构：重构防火墙模块，增加 hooks 机制。   **[重要]** 
	- 新增：防火墙新增：请求 path 禁止字符校验、Host 检测、请求 Method 检测、请求头检测、请求参数检测。重构目录遍历符检测算法。
	- 重构：重构 `SaTokenDao` 模块，将序列化与存储操作分离。   **[重要]**
	- 重构：重构 `SaTokenDao` 默认实现类，优化底层设计。
	- 新增：`isLastingCookie` 配置项支持在全局配置中定义了。
	- 重构：`SaLoginModel` -> `SaLoginParameter`。    **[不向下兼容]** 
	- 重构：`TokenSign` -> `SaTerminalInfo`。    **[不向下兼容]** 
	- 新增：`SaTerminalInfo` 新增 `extraData` 自定义扩展数据设置。
	- 新增：`SaLoginParameter` 支持配置 `isConcurrent`、`isShare`、`maxLoginCount`、`maxTryTimes`。
	- 新增：新增 `SaLogoutParameter`，用于控制注销会话时的各种细节。  **[重要]**
	- 新增：新增 `StpLogic#isTrustDeviceId` 方法，用于判断指定设备是否为可信任设备。
	- 新增：新增 `StpUtil.getTerminalListByLoginId(loginId)`、`StpUtil.forEachTerminalList(loginId)` 方法，以更方便的实现单账号会话管理。
	- 升级：API 参数签名配置支持自定义摘要算法。
	- 新增：新增 `@SaCheckSign` 注解鉴权，用于 API 签名参数校验。
	- 新增：API 参数签名模块新增多应用模式。 fix: [#IAK2BI](https://gitee.com/dromara/sa-token/issues/IAK2BI), [#I9SPI1](https://gitee.com/dromara/sa-token/issues/I9SPI1), [#IAC0P9](https://gitee.com/dromara/sa-token/issues/IAC0P9)   **[重要]**
	- 重构：全局配置 `is-share` 默认值改为 false。    **[不向下兼容]** 
	- 重构：踢人下线、顶人下线默认将删除对应的 token-session 对象。
	- 优化：优化注销会话相关 API。
	- 重构：登录默认设备类型值改为 DEF。   **[不向下兼容]** 
	- 重构：`BCrypt` 标注为 `@Deprecated`。
	- 新增：`sa-token-quick-login` 支持 `SpringBoot3` 项目。 fix: [#IAFQNE](https://gitee.com/dromara/sa-token/issues/IAFQNE)、[#673](https://github.com/dromara/Sa-Token/issues/673)
	- 新增：`SaTokenConfig` 新增 `replacedRange`、`overflowLogoutMode`、`logoutRange`、`isLogoutKeepFreezeOps`、``isLogoutKeepTokenSession`` 配置项。
- OAuth2：
	- 重构：重构 sa-token-oauth2 插件，使注解鉴权处理器的注册过程改为 SPI 插件加载。
- 插件：
	- 新增：`sa-token-serializer-features` 插件，用于实现各种形式的自定义字符集序列化方案。
	- 新增：`sa-token-fastjson` 插件。
	- 新增：`sa-token-fastjson2` 插件。
	- 新增：`sa-token-snack3` 插件。
	- 新增：`sa-token-caffeine` 插件。
- 单元测试：
	- 新增：`sa-token-json-test` json 模块单元测试。
	- 新增：`sa-token-serializer-test` 序列化模块单元测试。
- 文档：
	- 新增：QA “多个项目共用同一个 redis，怎么防止冲突？” 
	- 优化：补全 OAuth2 模块遗漏的相关配置项。
	- 优化：优化 OAuth2 简述章节描述文档。
	- 优化：完善 “SSO 用户数据同步 / 迁移” 章节文档。
	- 修正：补全项目目录结构介绍文档。
	- 新增：文档新增 “登录参数 & 注销参数” 章节。
	- 优化：优化“技术求助”按钮的提示文字。
	- 新增：新增 `preview-doc.bat` 文件，一键启动文档预览。
	- 完善：完善 Redis 集成文档。
	- 新增：新增单账号会话查询的操作示例。
	- 新增：新增顶人下线 API 介绍。
	- 新增：新增 自定义序列化插件 章节。
- 其它：
	- 新增：新增 `sa-token-demo/pom.xml` 以便在 idea 中一键导入所有 demo 项目。
	- 删除：删除不必要的 `.gitignore` 文件
	- 重构：重构 `sa-token-solon-plugin` 插件。
	- 新增：新增设备锁登录示例。


### v1.40.0 @2025-2-1
更新导读：[视频](https://www.bilibili.com/video/BV1uNATeeEvg/)、[文字版](https://juejin.cn/post/7467969744307306505)

- core: 
	- 新增：新增 `Cookie` 自定义属性支持。  fix: [#693](https://github.com/dromara/Sa-Token/issues/693)   **[重要]** 
	- 新增：`SaFirewallStrategy` 防火墙策略：请求 path 黑名单校验、非法字符校验、白名单放行。  **[重要]** 
	- 修复：新增对分号字符的 path 路径校验。   参考：[Sa-Token对url过滤不全存在的风险点](https://mp.weixin.qq.com/s/77CIDZbgBwRunJeluofPTA)   **[漏洞修复]** 
	- 修复: 修复部分场景下登录后已存在的 `token-session` 没有被续期的问题。  fix: [#IA8U1O](https://gitee.com/dromara/sa-token/issues/IA8U1O)
	- 优化：优化 `active-timeout` 的检查与续期操作，同一请求内只会检查与续期一次。
	- 修复：`SaFoxUtil.joinSharpParam` 方法中不正确的注释。
	- 新增：封禁模块新增支持实时从数据库查询数据。
- SSO：
	- 优化：SSO 示例代码的跨域处理由原生方式改为 Sa-Token 过滤器模式。
	- 新增：文档新增 “SSO整合 - NoSdk 模式与非 java 项目” 章节。
	- 新增：“不同 SSO Client 配置不同秘钥” 章节增加部分异常的处理方案提示，fix: [#IAFZXL](https://gitee.com/dromara/sa-token/issues/IAFZXL)
	- 删除：sso demo 示例中部分不必要的代码内容。
- OAuth2：
	- 新增：OAuth2 Client 前端测试页。   **[重要]**
	- 新增：`UnionId` 联合id 实现。   **[重要]** 
	- 新增：`oauth2-server` 端前后台分离示例与文档。 fix: [#I9DQGA](https://gitee.com/dromara/sa-token/issues/I9DQGA)、[#I9W2RU](https://gitee.com/dromara/sa-token/issues/I9W2RU)    **[重要]**
	- 新增：`OIDC` 模式 `nonce` 随机数响应校验。 merge: [pr311](https://gitee.com/dromara/sa-token/pulls/311)
	- 修复：错误方法名 `deleteGrantScope(String state)` -> `deleteState(String state)`。
	- 修复：全局配置项 `sa-token.oauth2-server.oidc.iss` 无效的问题。
	- 新增：回收 Refresh-Token 方法: `revokeRefreshToken`、`revokeRefreshTokenByIndex`。
	- 新增：为 `CodeModel`、`AccessTokenModel`、`RefreshTokenModel`、`ClientTokenModel` 添加 `createTime` 字段，以记录该数据的创建时间。
	- 新增：为 Access-Token、Client-Token 添加 `grantType` 字段，以记录该数据的授权类型。
	- 新增：`SaOAuth2Util.getCode` 等方法，以更方便的获取、校验授权码。
- 插件：
	- 新增：新增 `sa-token-freemarker` 插件，整合 `Freemarker` 视图引擎。 fix: [#651](https://github.com/dromara/sa-token/issues/651)   **[重要]**
	- 新增：新增 `sa-token-spring-el` 插件，用于支持 SpEL 表达式注解鉴权。 fix: [#IB3GBB](https://gitee.com/dromara/sa-token/issues/IB3GBB)、fix: [#IAIXSL](https://gitee.com/dromara/sa-token/issues/IAIXSL)、fix: [#I9P24F](https://gitee.com/dromara/sa-token/issues/I9P24F)   **[重要]**
- 文档：
	- 新增：新增 `MongoDB` 集成示例。 感谢 `@lilihao` 提供的示例。 merge: [pr322](https://gitee.com/dromara/sa-token/pulls/322)、[pr667](https://github.com/dromara/Sa-Token/pull/667)   **[重要]**
	- 新增：“fox说技术” 视频教程链接。
	- 新增：“API接口参数签名”章节 视频讲解链接（B站抓蛙师）。
	- 优化：文档首页首屏增加需求提交按钮。
	- 其它：补全赞助者名单、`Dromara` 项目链接等信息。
	- 新增：`SpringBoot3.x` 版本配置 Redis 注意事项。fix: [#688](https://github.com/dromara/Sa-Token/issues/688)
	- 新增：`gitcode` g-star badge 展示。
	- 修复：`OAuth2` 滞后的配置信息示例。
	- 新增：新增视频账号链接。
	- 新增：新增团队成员展示。




### v1.39.0 @2024-8-28
- 核心：
	- 升级：重构注解鉴权底层，支持自定义鉴权注解了。  **[重要]**
	- 修复：修复前端提交同名 `Cookie` 时的框架错读现象。
	- 更名：`NotBasicAuthException` -> `NotHttpBasicAuthException`。
- 插件：
	- 修复：修复 `sa-token-quick-login` 插件无法正常拦截的问题。
- SSO：
	- 优化：优化 sso-server 前后端分离 demo 代码。
	- 优化：优化 sso-server 前后端分离时的跳转流程。
- OAuth2：
	- 重构：`sa-token-oauth2` 模块整体重构。   **[重要]**  **[不向下兼容]**
	- 新增：新增支持自定义 `scope` 处理器。	 **[重要]**
	- 新增：新增支持自定义 `grant_type`。	 **[重要]**
	- 新增：新增 `scope` 划分等级。		 **[重要]**
	- 新增：新增 `oidc` 协议支持。		 **[重要]**
	- 新增：新增支持默认 `openid` 生成算法。	 **[重要]**
	- 新增：新增 `OAuth2` 注解鉴权支持。		 **[重要]**
	- 修复：`redirect_url` 参数校验增加规则：不允许出现@字符、*通配符只能出现在最后一位。关联 [issue](https://github.com/dromara/Sa-Token/issues/529) **[重要]**
	- 优化：优化代码注释、异常提示信息。
	- 升级：兼容 `Http Basic` 提交 `client` 信息的场景。感谢 github `@CuiGeekYoung` 提交的pr。
	- 升级：兼容 `Bearer Token` 方式提交 `access_token` 和 `client_token`。
	- 升级：适配拆分式路由。
	- 新增：将 `scope` 字段改为 List 类型。
	- 重构：抽离 `SaOAuth2Strategy` 全局策略接口，定义一些创建 token 的算法策略。
	- 新增：新增 `addAllowUrls` `addContractScopes` 方法，简化 `SaClientModel` 构建代码。
	- 重构：抽离 `SaOAuth2Dao` 接口，负责数据持久。
	- 重构：抽离 `SaOAuth2DataLoader` 数据加载器接口。
	- 重构：抽离 `SaOAuth2DataGenerate` 数据构造器接口。
	- 重构：抽离 `SaOAuth2DataConverter` 数据转换器接口。
	- 重构：抽离 `SaOAuth2DataResolver` 数据解析器接口。
	- 重构：重构 `SaOAuth2Handle` -> `SaOAuth2ServerProcessor` 更方便的逻辑重写。
	- 重构：重构 `PastToken` -> `LowerClientToken`。
	- 新增：新增 `state` 值校验，同一 `state` 参数不可重复使用。
	- 优化：完善 `SaOAuth2Util` 相关方法，更方便的二次开发。
	- 新增：新增部分异常类，细分异常 `ClassType`。
	- 优化：优化 `sa-token-oauth2` 异常细分状态码。
- 文档：
	- 新增：新增数据结构说明。
	- 新增：新增不同 `client` 不同登录页说明。
	- 优化：优化文档 [将权限数据放在缓存里] 示例。
	- 新增：新增 从 Shiro、SpringSecurity、JWT 迁移 示例。  **[重要]**



### v1.38.0 @2024-5-12
- sa-token-core：
	- 修复：修复 `StpUtil.getSessionByLoginId(xx)` 参数为 null 时创建无效 `SaSession` 的 bug。
	- 优化：在 `SpringBoot 3.x` 版本下错误的引入依赖时将得到启动失败的提示。 （感谢`Uncarbon`提交的pr）
	- 优化：进一步优化权限校验算法，hasXxx API 只会返回 true 或 false，不再抛出异常。
	- 重构：`InvalidContextException` 更名为 `SaTokenContextException`。 **[已做向下兼容处理]**
	- 重构：彻底删除 `NotPermissionException` 异常中的 `getCode()` 方法。 **[过期API清理]**
	- 重构：重构 `SaTokenException` 类方法 `throwBy-`>`notTrue`、`throwByNull-`>`notEmpty`。**[已做向下兼容处理]**
	- 重构：`StpUtil.getSessionBySessionId` 提供的 `SessionId` 为空时将直接抛出异常，而不是再返回null。**[不向下兼容]**
	- 新增：新增 `Http Digest` 认证模块简单实现。	**[重要]**
	- 重构：更换 `HttpBasic` 认证模块包名。	**[已做向下兼容处理]**
	- 新增：新增 `StpUtil.getLoginDeviceByToken(xxx)` 方法，用于获取任意 token 的登录设备类型。
	- 新增：新增 `StpUtil.getTokenLastActiveTime()` 方法，获取当前 token 最后活跃时间。
	- 修复：修复“当登录时指定 timeout 小于全局 timeout 时，`Account-Session` 有效期为全局 timeout”的问题。
	- 优化：首次获取 `Token-Session` 时，其有效期将保持和 token 有效期相同，而不是再是全局 timeout 值。
	- 移除：移除 `SaSignConfig` 的 `isCheckNonce` 配置项。 **[不向下兼容]**
	- 新增：`SaSignTemplate#checkRequest` 增加“可指定参与签名参数”的功能。
	- 重构：将部分加密算法设置为过期。
	- 重构：优化 token 读取策略，空字符串将视为没有提交token。
	- 修复：`sa-token-bom` 补全缺失依赖。
	- 优化：二级认证校验之前必须先通过登录认证校验。
	- 修复：修复 `StpUtil.getLoginId(T defaultValue)` 传入 null 时无法正确返回值的bug。
- sa-token-sso：
	- 优化：SSO 模式三，API 调用签名校验时，限定参与签名的参数列表，更安全。
	- 新增：新增 `autoRenewTimeout` 配置项：是否在每次下发 ticket 时，自动续期 token 的有效期（根据全局 timeout 值）
	- 新增：`SaSsoConfig` 新增配置 `isCheckSign`（是否校验参数签名），方便本地开发时的调试。
	- 新增：`SaSsoConfig` 新增配置 `currSsoLogin`，用于强制指定当前系统的 sso 登录地址。
	- 重构：整体重构 `sa-token-sso` 模块，将 `server` 端和 `client` 端代码拆分。 **[重要]** **[不向下兼容]**
	- 新增：`SaSsoConfig` 配置项 `ssoLogoutCall` 重命名为 `currSsoLogoutCall`。**[已做向下兼容处理]**
	- 重构：模式三在校验 Ticket 时，也将强制校验签名才能调通请求。**[不向下兼容]**
	- 新增：新增 `maxRegClient` 配置项，用于控制模式三下 client 注册数量。
	- 新增：新增不同 SSO Client 配置不同 `secret-key` 的方案。 **[重要]**
	- 重构：匿名 client 将不再能解析出所有应用的 ticket。**[不向下兼容]**
	- 新增：新增 `homeRoute` 配置项：在 ``/sso/auth`` 登录后不指定 redirect 参数的情况下默认跳转的路由。
	- 优化：优化登录有效期策略，SSO Client 端登录时将延续 SSO Server 端的会话剩余有效期。
	- 新增：新增 `checkTicketAppendData` 策略函数，用于在校验 ticket 后，给 sso-client 端追加返回信息。
	- 新增：SSO章节文档新增用户数据同步/迁移方案的建议。
	- 修复：修复利用@字符可以绕过域名允许列表校验的漏洞。 **[漏洞修复]**
	- 修复：禁止 `allow-url` 配置项 * 符号出现在中间位置，因为这有可能导致校验被绕过。 **[漏洞修复]**
- 新增插件/示例：
	- 新增：新增插件 `sa-token-hutool-timed-cache`，用于整合 Hutool 缓存插件 TimedCache。 **[重要]**
	- 新增：新增 SSM 架构整合 Sa-Token 简单示例。 	**[重要]**
	- 新增：新增 beetl 整合 Sa-Token 简单示例。 	**[重要]**
- 文档：
	- 部分章节将 `@Autowired` 更换为更合适的 `@PostConstruct`
	- 新增过滤器执行顺序更改示例。
- 其它：
	- 优化：将跨域处理demo拆分为独立仓库。
	- 优化：解决 springboot 集成 sa-token 后排除 jackson 依赖无法成功启动的问题。
	- 优化：解决 `sa-token-jwt` 模块重复设置 keyt 秘钥问题。（感谢`KonBAI`提交的pr）
	- 优化：jwt模式 token 过期后，抛出的异常描述是 token 已过期，而不再是 token 无效。
	- 修复：补齐 `sa-token-spring-aop` 模块中遗漏监听的注解。


### v1.37.0 @2023-10-18
- 修复：修复路由拦截鉴权可被绕过的问题。 **[漏洞修复]**
- 重构：未登录时调用鉴权 API 抛出未登录异常而不再是无权限异常。
- 优化：优化 SaTokenDao 组件更换时的逻辑。
- 文档：提供 SpringBoot3.x 路由匹配出错的解决方案。


### v1.36.0 @2023-9-22
- sa-token-core：
	- 修复：API接口签名校验参数接口NPE问题，增加必须参数的非空校验处理。
	- 新增：加密工具类新增 sha384、sha512 实现。 感谢 `@若初995` 提交的pr。   **[重要]**
	- 修复：`SaFoxUtil.vagueMatch()` 正则匹配的一些问题。  **[漏洞修复]**
	- 修复：`SaRouter.match()` 路由匹配的一些问题。  **[漏洞修复]**
- 其它：
	- 优化：`sa-token-alone-redis` 去掉不必要的配置项判断。
	- 新增：`sa-token-solon-plugin` 增加对 solon 网关的支持。
	- 新增：新增第三方插件专用仓库：`sa-token-three-plugin` 。
	- 升级：`sa-token-solon-plugin` 增加对 solon 网关的支持。
- 文档：
	- 新增：新增开启全局懒加载时不能注入上下文处理器的处理方案 。
	- 新增：新增 RefreshToken 示例。 **[重要]**
	- 新增：文档新增 sa-token 小助手，可在线实时技术提问。 **[重要]**
	- 优化：其它一些优化。
- 新增插件：
	- `sa-token-redisson-jackson2`：通用 redisson 集成方案 （spring, solon, jfinal 等都可用）



### v1.35.0 @2023-6-23
- sa-token-core：
	- 优化：前端未提供 token 时，`getTokenSession()` 将抛出未登录异常，而不是返回 null。 **[不向下兼容]**
	- 新增：SaSession 新增字段：`type`、`loginType`、`loginId`、`token`。
	- 重构：全局过滤器抽离 SaFilter 统一接口。
	- 重构：全局过滤器 `includeList`、`excludeList` 改为 public，同时移除对应的 getter 方法。 **[不向下兼容]** 
	- 重构：将全局过滤器的 BeforeAuth 认证设为不受 `includeList` 与 `excludeList` 的限制，所有请求都会进入。 **[不向下兼容]** 
	- 新增：新增循环生成 token 的算法，用于确保 Token 的唯一性。 **[重要]** 
	- 重构：API 接口签名所有方法均迁移至 core 核心模块。 **[重要]** 
	- 新增：新增彩色日志打印，更方便的分辨不同日志等级。 **[重要]** 
	- 重构：重构概念：临时有效期 -> token 最低活跃频率，过期后 token 冻结。
	- 重构：重构概念：`User-Session` -> `Account-Session`。
	- 新增：新增 `getTokenTimeout(String token)` 方法，获取任意 token 剩余有效期。
	- 优化：在登录时增加判断当前 StpLogic 是否支持 extra 扩展参数模式，如果不支持则打印警告信息。
	- 新增：NotLoginException 增加新场景值 -6、-7，提供更精确的未登录异常描述信息。
	- 新增：TokenSign 新增 tag 挂载参数，可在登录时方便的存储一些客户端特有数据。  **[重要]** 
	- 新增：新增 `SaStrategy#createStpLogic`，用于指定动态创建 StpLogic 时的算法策略。
	- 新增：新增 `@SaCheckOr` 批量注解鉴权：只要满足其中一个注解即可通过验证。  **[重要]** 
	- 重构：重命名：`SaStrategy.me` -> `SaStrategy.instance`。
	- 重构：在登录时强制性检查账号 id 是否为异常值，如果是则登录失败。
	- 重构：重构概念：`activity-timeout` -> `active-timeout`。  **[重要]** 
	- 新增：新增动态 `active-timeout` 能力，可在每次登录时指定 `active-timeout` 值。  **[重要]** 
	- 优化：将 `SaStrategy` 所有策略声明抽离为单独的函数式接口。
	- 新增：增加为 StpLogic 单独配置 `SaTokenConfig` 参数的能力。
	
- sa-token-sso：
	- 修复：在 SSO 模式三中 `ticket` 校验地址配错时，会出现 NPE 的问题 
	- 新增：新增 `getData` 接口配置，在模式三拉取数据时可以传递任意参数。 **[重要]** 
	- 重构：模式三秘钥配置更改：`sa-token.sso.secretkey=xxx` -> `sa-token.sign.secret-key=xxx`。 **[不向下兼容]**
	- 重构：模式三校验签名方法更改：`SaSsoUtil.checkSign(req)` -> `SaSignUtil.checkRequest(req)`。 **[不向下兼容]**
	- 新增：新增 `sa-token.sso.mode` 配置项，用于约定此系统使用的 SSO 模式。
	- 优化：优化校验 ticket 的逻辑。
	
- sa-token-jwt：
	- 修复：jwt 令牌的签名类型可以被篡改的问题。 **[重要]** 
	
- 其它：
	- 优化：所有模块优化注释，更方便开发者阅读源码。
	- 优化：在所有 .java 文件中添加 license 头说明。
	- 优化：修复大部分代码警告。
	- 新增：新增 thymeleaf 标签方言命名空间，增强 ide 代码提示。 **[重要]** 
	- 新增：定义 `sa-token-bom` 包，方便引入 sa-token 时对齐版本。
	- 新增：sa-token-dubbo3 插件新增代码示例。
	- 新增：新增跨域文章和示例：Header 参数版和第三方 Cookie 版。 **[重要]** 
	- 修复：修复 `sa-token-alone-redis` 在低版本 springboot 下无法启动成功(缺少 `username` 属性)的问题。
	
- 新增插件：
	- 新增：新增 `sa-token-context-dubbo3` 插件。 感谢 `@qiudaozhang` 提交的 pr。 **[重要]** 

- 文档：
	- 新增：部分常见报错排查。
	- 新增：首页图片增加懒加载效果，节省流量。
	- 新增：增加 Cookie 配置示例。
	- 修复：整理 demo 结构目录结构，修复不正确的路径说明。
	- 新增：新增 api-sign 模块文档。  **[重要]**  
	
- 简化包名  **[重要]**  **[不向下兼容]** 
	- `sa-token-dao-redis` -> `sa-token-redis`
	- `sa-token-dao-redis-jackson` -> `sa-token-redis-jackson`。
	- `sa-token-dao-redis-fastjson` -> `sa-token-redis-fastjson`。
	- `sa-token-dao-redis-fastjson2` -> `sa-token-redis-fastjson2`。
	- `sa-token-dao-redisson-jackson` -> `sa-token-redisson-jackson`。
	- `sa-token-dao-redisx` -> `sa-token-redisx`。
	- `sa-token-context-dubbo` -> `sa-token-dubbo`。
	- `sa-token-context-dubbo3` -> `sa-token-dubbo3`。
	- `sa-token-context-grpc` -> `sa-token-grpc`。


### v1.34.0 @2023-1-11

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



### v1.33.0 @2022-11-16
- 重构：重构异常状态码机制。   **[重要]**
- 重构：重构 sa-token-sso 模块异常码改为 300 开头，sa-token-jwt 异常码改为 302 开头。  **[不向下兼容]**
- 新增：新增全局 Log 模块。   **[重要]**
- 重构：`SaTokenListenerForConsolePrint` 改名 `SaTokenListenerForLog`。   **[不向下兼容]**
- 修复：修复多线程下 `SaFoxUtil.getRandomString()` 随机数重复问题。
- 修复：修复 sa-token-demo-sso3-client-nosdk 项目中单点注销 url 配置错误的问题
- 文档：文档优化。



### v1.32.0 @2022-10-28
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



### v1.31.0 @2022-9-8
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



### v1.30.0 @2022-05-9
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


### v1.29.0 @2022-02-10
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


### v1.28.0 @2021-11-5
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


### v1.27.0 @2021-10-11
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


### v1.26.0 @2021-9-2
- 优化：优化单点登录文档 
- 新增：新增 `Http Basic` 认证 **[重要]** 
- 新增：文档新增跨域解决方案 
- 文档：新增 Nginx 转发请求丢失uri的解决方案
- 文档：新增 SSO 自定义 API 路由示例  **[重要]** 
- 示例：新增 `SSO-Server` 端前后端分离示例  **[重要]** 


### v1.25.0 @2021-8-16
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


### v1.24.0 @2021-7-24
- 修复：修复部分场景下Alone-Redis插件导致项目无法启动的问题
- 优化：增加对SpringBoot1.x版本的兼容性 
- 新增：SaOAuth2Util新增checkScope函数，用于校验令牌是否具备指定权限 
- 新增：OAuth2.0模块新增revoke接口，用于提前回收 Access-Token 令牌 
- 新增：新增`Sa-Id-Token` 模块，解决微服务内部调用鉴权  **[重要]**
- 文档：新增OAuth2.0模块常用方法说明  
- 优化：大幅度优化文档示例 


### v1.23.0 @2021-7-19
- 新增：Sa-Token-OAuth2 模块正式发布   **[重要]** 
- 修复：修复jwt集成demo无法正确注册StpLogic的bug
- 修复：修复登录时某些场景下Session续期可能不正常的bug  
- 优化：代码注释优化，文档优化  


### v1.22.0 @2021-7-10
- 新增：SaSsoConfig 部分属性增加set连缀风格 
- 优化：SaSsoUtil 可定制化底层的 `StpLogic`
- 新增：新增 `SaSsoHandle` 大幅度简化单点登录整合步骤  **[重要]** 
- 新增：新增Sa-Token在线测评，链接：[https://ks.wjx.top/vj/wFKPziD.aspx](https://ks.wjx.top/vj/wFKPziD.aspx)  **[重要]**
- 新增：Sa-Token-Quick-Login 插件新增拦截与放行路径配置
- 优化：大幅度优化文档示例 


### v1.21.0 @2021-7-2
- 新增：新增Token二级认证 	**[重要]** 
- 新增：新增`Sa-Token-Alone-Redis`独立Redis插件   **[重要]**  
- 新增：新增SSO三种模式，彻底解决所有场景下的单点登录问题   **[重要]**  
- 新增：新增多账号模式下，注解合并示例		**[重要]**  
- 新增：新增`SaRouter.back()`函数，用于停止匹配返回结果  
- 不兼容更新重构：
	- 更改yml配置前缀：原`[spring.sa-token.]` 改为 `[sa-token.]`，目前版本暂时向下兼容，请尽快更新 


### v1.20.0 @2021-6-17
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


### v1.19.0 @2021-5-10
- 新增：注解鉴权新增定制loginType功能  **[重要]** 
- 重构：重构目录结构，抽离`plugin`模块  **[重要]** 
- 新增：新增 `sa-token-quick-login` 插件，零代码集成登录功能  **[重要]** 
- 优化：所有函数式接口增加`@FunctionalInterface`注解，感谢群友`@MrXionGe`提供的建议 
- 优化：文档优化... 


### v1.18.0 @2021-4-24
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


### v1.17.0 @2021-4-17
- 修复：在WebFlux环境中引入Redis集成包无法启动的问题 
- 修复：修复JWT集成示例中版本升级API的变更 
- 优化：优化启动时字符画打印
- 文档：新增集成环境说明
- 文档：新增功能介绍图  
- 新增：全局过滤器增加限定[拦截路径]与[排除路径]功能 
- 重构：全局过滤器执行函数放到成员变量里，连缀风格配置 
- 新增：新增全局侦听器，可在用户登陆、注销、被踢下线等关键性操作时进行一些AOP操作 **[重要]** 


### v1.16.0 @2021-4-12
- 新增：新增账号封禁功能，指定时间内账号无法登陆 			**[重要]**
- 新增：核心包脱离`ServletAPI`，彻底零依赖！  				**[重要]**
- 新增：新增基于`ThreadLocal`的上下文容器					**[重要]**
- 新增：新增`Reactor`响应式编程支持，`WebFlux`集成！			**[重要]** 
- 新增：新增全局过滤器，解决拦截器无法拦截静态资源的问题			**[重要]** 
- 新增：新增微服务网关鉴权方案！可接入`ShenYu`、`Gateway`等网关组件!	**[重要]** 
- 新增：AOP切面定义`Order`顺序为`-100`，可保证在多个自定义切面前执行 
- 文档：新增推荐公众号列表 


### v1.15.0 @2021-3-23
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


### v1.14.0 @2021-3-12
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


### v1.13.0 @2021-2-9
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


### v1.12.0 @2021-1-12
- 新增：提供JWT集成示例 **[重要]**
- 新增：新增路由式鉴权，可方便的根据路由匹配鉴权  **[重要]**
- 新增：新增身份临时切换功能，可在一个代码段内将会话临时切换为其它账号  **[重要]**
- 优化：将`SaCheckInterceptor.java`更名为`SaAnnotationInterceptor.java`，更语义化的名称 
- 优化：优化文档
- 升级：v1.12.1，新增`SaRouterUtil`工具类，更方便的路由鉴权   **[重要]**


### v1.11.0 @2021-1-10
- 新增：提供AOP注解鉴权方案 **[重要]**
- 优化自动生成token的算法


### v1.10.0 @2021-1-9
- 新增：提供查询所有会话方案  **[重要]**
- 修复：修复token设置为永不过期时无法正常被顶下线的bug，感谢github用户 @zjh599245299 提出的bug


### v1.9.0 @2021-1-6
- 优化：`spring-boot-starter-data-redis` 由 `2.3.7.RELEASE` 改为 `2.3.3.RELEASE` 
- 修复：补上注解拦截器里漏掉验证`@SaCheckRole`的bug
- 新增：新增同端互斥登录，像QQ一样手机电脑同时在线，但是两个手机上互斥登录  **[重要]**


### v1.8.0 @2021-1-2
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


### v1.7.0 @2020-12-24
- 优化：项目架构改为maven多模块形式，方便增加新模块 **[重要]**
- 优化：与`springboot`的集成改为`springboot-starter`模式，无需`@SaTokenSetup`注解即可完成自动装配 **[重要]**
- 新增：新增`activity-timeout`配置，可控制token临时过期与续签功能 **[重要]**
- 新增：`timeout`过期时间新增-1值，代表永不过期 
- 新增：`StpUtil.getTokenInfo()`改为对象形式，新增部分常用字段 
- 优化：解决在无cookie模式下，不集成redis时会话无法主动过期的问题 
- 修复：修复文档首页样式问题 


### v1.6.0 @2020-12-17
- 新增：花式token生成方案 **[重要]** 
- 优化：优化`readme.md` 
- 修复：修复`SaCookieOper`与`SaTokenAction`无法自动注入的问题 


### v1.5.1 @2020-12-16
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


### v1.4.0 @2020-9-7
- 优化：修改一些函数、变量名称，使其更符合阿里java代码规范
- 优化：`tokenValue`的读取优先级改为：`request` > `body` > `header` > `cookie`  **[重要]**
- 新增：新增`isReadCookie`配置，决定是否从`cookie`里读取`token`信息 
- 优化：如果`isReadCookie`配置为`false`，那么在登录时也不会把`cookie`写入`cookie` 
- 新增：新增`getSessionByLoginId(Object loginId, boolean isCreate)`方法
- 修复：修复文档部分错误，修正群号码


### v1.3.0 @2020-5-2
- 新增：新增 `StpUtil.checkLogin()` 方法，更符合语义化的鉴权方法
- 新增：注册拦截器时可设置 `StpLogic` ，方便不同模块不同鉴权方式
- 新增：抛出异常时增加 `loginType` 区分，方便多账号体系鉴权处理 
- 修复：修复启动时的版本字符画版本号打印不对的bug  
- 修复：修复文档部分不正确之处
- 新增：新增文档的友情链接


### v1.2.0 @2020-3-7
- 新增：新增注解式验证，可在路由方法中使用注解进行权限验证  **[重要]**
- 参考：[注解式验证](use/at-check)


### v1.1.0 @2020-2-12
- 修复：修复`StpUtil.getLoginId(T defaultValue)`取值转换错误的bug


### v1.0.0 @2020-2-4
- 第一个版本出炉 
