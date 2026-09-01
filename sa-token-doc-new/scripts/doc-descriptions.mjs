/**
 * 文档页 meta description 文案（手写，80～160 字）。
 * 由 sync-doc-descriptions.mjs 写入 frontmatter，不改正文。
 */
export const DOC_DESCRIPTIONS = {
  // —— 附录 ——
  'more/noun-intro.md':
    'Sa-Token 框架名词解释：梳理 Token、Session、StpUtil 等易混概念，减少权限认证开发中的理解偏差。',
  'fun/auth-flow.md':
    'Sa-Token 功能结构图与认证流程图：一图看懂登录鉴权、Session 管理与插件模块的整体架构。',
  'fun/update-version-flow.md':
    'Sa-Token 版本更新流程：从 issue 讨论、代码合并到发版与文档同步的标准化维护步骤。',
  'fun/log.md':
    'Sa-Token 全局 Log 输出：开启登录、注销、二级认证等关键操作日志，支持转接到 Slf4j。',
  'fun/async--mock.md':
    'Sa-Token 异步与 Mock 上下文：在定时任务、MQ、单元测试中安全调用 StpUtil 等 Web 上下文 API。',
  'fun/not-login-scene.md':
    'Sa-Token 未登录场景值：按 NotLoginException 细分未登录、被踢、被顶下线等场景定制处理逻辑。',
  'fun/token-timeout.md':
    'Sa-Token Token 有效期：详解 timeout 与 active-timeout 两种过期策略的区别与 yml 配置示例。',
  'fun/session-model.md':
    'Sa-Token Session 模型：Account-Session、Token-Session 分层设计与 HttpSession 的差异说明。',
  'fun/three-scope.md':
    'Sa-Token 数据读写三大作用域：SaStorage 请求域、SaSession 会话域与持久层职责划分。',
  'fun/token-info.md':
    'Sa-Token TokenInfo 参数：tokenName、tokenValue、loginId、isLogin 等登录凭证字段说明。',
  'fun/exception-code.md':
    'Sa-Token 异常细分状态码：NotLoginException、NotPermissionException 等类型与状态码对照。',
  'fun/custom-annotations.md':
    'Sa-Token 自定义注解：扩展 @SaCheck 系列，注入业务定制的注解鉴权与参数校验逻辑。',
  'fun/firewall.md':
    'Sa-Token 防火墙 SaFirewallStrategy：拦截异常 path、SQL 注入等可能造成攻击的危险请求。',
  'fun/jur-cache.md':
    'Sa-Token 权限缓存参考：将角色权限放入 Redis 等缓存，降低 StpInterface 数据库访问压力。',
  'fun/dynamic-router-check.md':
    'Sa-Token 动态路由鉴权：从数据库加载路由规则，实现登录与权限校验的运行时动态配置。',
  'fun/curr-domain.md':
    'Sa-Token 反向代理 URI 丢失：Nginx 等反代环境下 SaHolder.getRequest().getUrl() 正确取址配置。',
  'fun/cors-filter.md':
    'Sa-Token 跨域问题：前后端分离场景下 CORS 过滤器与 Sa-Token 鉴权协同配置方案。',
  'fun/sso-vs-oauth2.md':
    'SSO 与 OAuth2 选型对比：统一认证中心场景下两种协议的功能差异与适用建议。',
  'up/integ-spring-mongod-1.md':
    'Sa-Token 集成 MongoDB（一）：扩展 SaTokenDao 将 Session 持久化到 MongoDB，含 Spring Boot 3 示例。',
  'up/integ-spring-mongod-2.md':
    'Sa-Token 集成 MongoDB（二）：Spring Data MongoDB 反序列化 SaSession 与 Demo 工程参考。',
  'fun/auth-framework-function-test.md':
    '从 Shiro、Spring Security、JWT 迁移到 Sa-Token：常见登录鉴权能力对照与代码示例。',
  'fun/issue-template.md':
    'Sa-Token issue 提问模板：提交 Bug 前先查 FAQ，按模板附上版本号、复现步骤与关键日志。',
  'fun/git-pr.md':
    '为 Sa-Token 贡献代码：文档页跳转 Gitee/GitHub、PR 规范与社区贡献流程说明。',
  'fun/timeline.md':
    'Sa-Token 开源大事记：框架自 2020 年开源以来的版本里程碑与社区成长记录。',
  'fun/team.md':
    'Sa-Token 团队成员：核心维护者与社区角色分工，含代码审核与 issue 处理职责。',
  'fun/sa-token-test.md':
    'Sa-Token 框架掌握度在线考试：测评登录认证、权限校验、SSO 等核心知识的理解程度。',
  'fun/tech-stack.md':
    'Sa-Token 源码技术栈：框架内核、Starter 与插件模块使用的主要技术与依赖说明。',
  'fun/refer-info.md':
    'Sa-Token 开发参考资料：框架设计、权限认证与相关开源项目的阅读索引。',

  // —— API / 架构 ——
  'api/sa-session.md':
    'SaSession 会话对象 API：Account-Session、Token-Session 数据读写与缓存组件用法说明。',
  'api/sa-strategy.md':
    'SaStrategy 全局策略 API：从外部数据源动态读取 Sa-Token 配置，代理封装核心逻辑。',
  'api/sa-token-dao.md':
    'SaTokenDao 数据持久接口：Session、Token 等权限数据的底层读写与 Redis 对接签名说明。',
  'arch/data-structure.md':
    'Sa-Token Redis 数据结构：Token、Session、索引 key 的命名规则与 value 字段格式说明。',
  'arch/dir-intro.md':
    'Sa-Token 仓库目录：core、starter、plugin、demo 等模块职责与 Awesome-Sa-Token 生态介绍。',

  // —— 微服务 ——
  'micro/import-intro.md':
    'Sa-Token 微服务依赖引入：网关与子服务分别引入 Starter，避免父 pom 统一依赖导致鉴权异常。',
  'micro/same-token.md':
    'Sa-Token Same-Token 内网隔离：防止绕过网关直连子服务，含网关转发与 Feign RPC 鉴权。',

  // —— 其它 ——
  'more/blog.md':
    'Sa-Token 框架博客：官方与社区技术文章收录，涵盖鉴权实战、版本发布与集成案例。',
  'more/common-action.md':
    'Sa-Token 全局类与方法：SaManager、SaTokenEventCenter、SaSignManager 等常用全局对象速查。',
  'more/content-cooperation.md':
    'Sa-Token 内容合作群：面向创作者的内容触达与协作交流，加群方式与合作说明。',
  'more/demand-commit.md':
    'Sa-Token 需求提交：文档改进与功能建议在线提交入口，欢迎批评与共建。',
  'more/download-demos.md':
    'Sa-Token 集成示例下载：60+ Demo 覆盖登录、SSO、OAuth2、微服务鉴权、JWT、API 签名等场景。',
  'more/join-group.md':
    '加入 Sa-Token 讨论群：QQ 群与微信入群方式，与社区开发者交流集成与排错经验。',
  'more/link.md':
    'Sa-Token 框架生态：Awesome-Sa-Token 开源案例与周边项目链接，定期同步更新。',
  'more/sa-token-donate-old.md':
    '赞助 Sa-Token：支持框架持续维护与社区运营，Apache-2.0 框架与文档永久免费开放。',
  'more/sa-token-donate.md':
    '赞助 Sa-Token：支持框架持续维护与社区运营，Apache-2.0 框架与文档永久免费开放。',
  'more/tj-gzh.md':
    'Sa-Token 推荐公众号：Java 技术、架构与源码分享类优质公众号收录列表。',
  'more/update-log.md':
    'Sa-Token 更新日志：各版本新增特性、修复项与破坏性变更记录，按发布时间倒序。',
  'more/wenjuan.md':
    'Sa-Token 用户问卷调查：约 1 分钟完成，帮助我们改进文档与社区体验。',

  // —— OAuth2 ——
  'oauth2/oauth2-apidoc.md':
    'OAuth2-Server 开放 API：authorize、token、userinfo 等标准端点 URL 与对接参数说明。',
  'oauth2/oauth2-at-check.md':
    'OAuth2 Access-Token 注解校验：@SaCheckAccessToken 等扩展注解的 scope 与 token 校验用法。',
  'oauth2/oauth2-check-domain.md':
    'OAuth2 Client 域名校验：AllowRedirectUris 配置授权回调白名单，防止非法 redirect 劫持。',
  'oauth2/oauth2-custom-api.md':
    'OAuth2 自定义 API 路由：改写 authorize、token 等默认路径，灵活对接业务 URL 规范。',
  'oauth2/oauth2-custom-grant_type.md':
    'OAuth2 自定义 grant_type：扩展 /oauth2/token 授权模式，除 code 与 password 外的令牌获取方式。',
  'oauth2/oauth2-custom-login.md':
    'OAuth2 定制登录与授权页：重写 notLoginView 策略，前后端分离下的登录与授权 UI 改造。',
  'oauth2/oauth2-custom-scope.md':
    'OAuth2 自定义 Scope：扩展第三方 client 可申请的权限范围与对应数据处理器。',
  'oauth2/oauth2-data-loader.md':
    'OAuth2 自定义数据加载器：SaOAuth2DataLoader 从数据库或配置加载 client 与 scope 信息。',
  'oauth2/oauth2-dev.md':
    'OAuth2 代码 API 参考：SaOAuth2Util 等常用工具类方法，二次开发开放更多资源接口。',
  'oauth2/oauth2-h5.md':
    'OAuth2-Server 前后端分离：authorize、token 等接口的 H5/SPA 对接改造要点。',
  'oauth2/oauth2-interworking.md':
    'OAuth2 与登录会话互通：资源令牌 accesstoken 与会话令牌 satoken 的数据打通方案。',
  'oauth2/oauth2-oidc.md':
    'Sa-Token 开启 OIDC：结合 sa-token-jwt 签发 id_token，配置 client 的 OIDC 签约权限。',
  'oauth2/oauth2-openid.md':
    'OAuth2 OpenId 与 UnionId：clientId、openId、unionId 在授权流程中的含义与区别。',
  'oauth2/oauth2-questions.md':
    'OAuth2 集成常见问题：路由错误、redirect 不匹配、token 无效等高频报错排查汇总。',
  'oauth2/oauth2-scope-level.md':
    'OAuth2 Scope 等级划分：通过配置为 scope 设定高级、低级权限，控制 token 授权粒度。',

  // —— 插件 ——
  'plugin/alone-redis.md':
    'Sa-Token Alone 独立 Redis：权限数据与业务缓存分库部署，降低读写冲突与访问压力。',
  'plugin/alone-redisson.md':
    'Sa-Token Alone 独立 Redisson：为权限数据单独配置 RedissonClient，与业务缓存彻底分离。',
  'plugin/aop-at.md':
    'Sa-Token AOP 注解鉴权：在 Service 等非 Controller 层使用 @SaCheckLogin 等注解校验。',
  'plugin/api-key.md':
    'Sa-Token API Key：为开放接口生成调用秘钥，校验请求来源与访问权限。',
  'plugin/api-sign.md':
    'Sa-Token API 参数签名：防重放与篡改，跨系统 HTTP 调用的 timestamp、nonce、sign 校验。',
  'plugin/custom-serializer.md':
    'Sa-Token 序列化扩展（娱乐向）：天干地支等趣味序列化方案，探索 SaTokenDao 序列化边界。',
  'plugin/dao-extend.md':
    'Sa-Token 缓存层扩展：实现 SaTokenDao 接口，对接 Redis、MongoDB 等不同持久化中间件。',
  'plugin/dubbo-extend.md':
    'Sa-Token 整合 Dubbo：RPC 调用传递 Token 与上下文，被调用端安全使用 StpUtil API。',
  'plugin/freemarker-extend.md':
    'Sa-Token 整合 Freemarker：页面标签方言，在模板中判断登录状态与权限码。',
  'plugin/grpc-extend.md':
    'Sa-Token 整合 gRPC：RPC 链路透传 Token，被调用端恢复 Sa-Token 上下文环境。',
  'plugin/http-extend.md':
    'Sa-Token HTTP 请求扩展：自定义 SaHttpTemplate，用于 SSO 模式三、单点注销等 HTTP 调用。',
  'plugin/json-extend.md':
    'Sa-Token JSON 序列化扩展：自定义 SaJsonTemplate，控制 Session 存 Redis 时的 JSON 转换。',
  'plugin/quick-login.md':
    'Sa-Token Quick-Login：为零代码监控页等轻量系统快速注入登录页面与鉴权能力。',
  'plugin/spel-at.md':
    'Sa-Token SpEL 注解鉴权：@SaCheckEL 使用 Spring 表达式实现灵活的条件权限校验。',
  'plugin/temp-token.md':
    'Sa-Token 临时 Token：五分钟、半小时等短时授权场景，如邀请链接与一次性操作凭证。',
  'plugin/thymeleaf-extend.md':
    'Sa-Token 整合 Thymeleaf：模板标签方言，在页面中调用登录态与权限判断 API。',

  // —— SSO ——
  'sso/anon-client.md':
    'Sa-Token 匿名 Client 接入：无明确 client 标识的应用如何参与 SSO 授权与 ticket 校验。',
  'sso/message-push.md':
    'Sa-Token SSO 消息推送：client 按约定格式调用 server /sso/pushS 实现消息下发。',
  'sso/signout.md':
    'Sa-Token 单点注销：单端注销与全端下线模式，一处退出多应用同步失效。',
  'sso/sso-apidoc.md':
    'SSO-Server 认证中心开放 API：ticket 校验、登录、注销等 HTTP 接口与对接方式。',
  'sso/sso-check-domain.md':
    'SSO 配置域名校验：allow-url 白名单限制授权回调地址，防止非法单点登录跳转。',
  'sso/sso-custom-api.md':
    'SSO 自定义 API 路由：改写默认 /sso/auth 等路径，适配业务 URL 与网关规则。',
  'sso/sso-custom-login.md':
    'SSO 定制化登录页：全局过滤器重定向未登录访问，自定义登录 UI 与跳转逻辑。',
  'sso/sso-dev.md':
    'SSO 代码 API 参考：SaSsoServerUtil、SaSsoClientUtil 等常用工具类方法说明。',
  'sso/sso-h5.md':
    'SSO 前后端分离整合：将 /sso/login 路由中转放到前端，H5/SPA 单点登录对接示例。',
  'sso/sso-home-jump.md':
    'SSO 平台中心跳转模式：认证中心作为统一入口，点击子系统链接免登录进入。',
  'sso/sso-nosdk.md':
    'SSO NoSdk 与非 Java 项目：纯 HTTP 对接认证中心，无需引入 Sa-Token 客户端 SDK。',
  'sso/sso-pro.md':
    'Sa-Sso-Pro 商业版：在开源 SSO 基础上提供企业级认证中心所需的高级功能。',
  'sso/sso-questions.md':
    'SSO 集成常见问题：Redis 分离、ticket 失效、跨域与模式选型等高频问答汇总。',
  'sso/user-data-sync.md':
    'SSO 用户数据同步与迁移：多系统账号体系对齐的架构参考与设计思路。',

  // —— 进阶 ——
  'up/basic-auth.md':
    'Sa-Token Http Basic/Digest 认证：注解与 API 方式启用基础 HTTP 认证，简单场景快速鉴权。',
  'up/disable.md':
    'Sa-Token 账号封禁：按服务维度封禁账号，防止违规用户再次登录指定功能。',
  'up/global-filter.md':
    'Sa-Token 全局过滤器：用 SaServletFilter 实现全站路由拦截鉴权，替代拦截器方案。',
  'up/global-listener.md':
    'Sa-Token 全局侦听器：订阅登录、注销、踢下线等事件，扩展审计与业务钩子逻辑。',
  'up/login-parameter.md':
    'Sa-Token 登录与注销参数：SaLoginParameter 控制设备类型、Cookie 持久化、token 有效期等。',
  'up/mock-person.md':
    'Sa-Token 模拟他人与身份切换：查询指定账号权限、Session，管理员运维与调试场景。',
  'up/password-secure.md':
    'Sa-Token 密码加密模块：封装常见加密算法，配合登录认证完成密码安全存储。',
  'up/safe-auth.md':
    'Sa-Token 二级认证：敏感操作前二次验证密码或凭证，如删除仓库前的安全确认。',
  'up/search-session.md':
    'Sa-Token 会话查询：按 loginId 获取已登录终端列表 SaTerminalInfo 字段说明。',
  'up/token-prefix.md':
    'Sa-Token Token 提交前缀：处理 Bearer 等前缀，避免鉴权时把前缀误当作 token 一部分。',

  // —— 其它文档 ——
  'fun/plugin-dev.md':
    'Sa-Token 插件开发指南：在不改核心架构前提下扩展 SaTokenDao、上下文与鉴权逻辑。',
  'fun/sa-token-context.md':
    '自定义 SaTokenContext：非 Spring Boot/WebFlux/Solon 框架的 Sa-Token 整合步骤。',
  'fun/sa-token-context--backup.md':
    '自定义 SaTokenContext（备份稿）：Web 框架不在官方 Starter 列表时的上下文整合参考。'
}
