<!-- 这是目录树文件 -->

- **开始**
	- [框架介绍](/)
	- [在 SpringBoot 环境集成](/start/example) 	
	- [在 WebFlux 环境集成](/start/webflux-example) 	
	- [在 Solon 环境集成](/start/solon-example) 	
	- [其它环境集成示例](/start/download)


- **基础**
	- [登录认证](/use/login-auth) 
	- [权限认证](/use/jur-auth) 
	- [踢人下线](/use/kick) 
	- [注解鉴权](/use/at-check) 
	- [路由拦截鉴权](/use/route-check) 
	- [Session会话](/use/session) 
	- [框架配置](/use/config) 

- **深入**
	- [集成 Redis](/up/integ-redis)
	- [前后端分离](/up/not-cookie) 
	- [自定义 Token 风格](/up/token-style) 
	- [Token 提交前缀](/up/token-prefix) 
	- [同端互斥登录](/up/mutex-login) 
	- [记住我模式](/up/remember-me)
	- [登录参数 & 注销参数](/up/login-parameter) 
	- [二级认证](/up/safe-auth) 
	- [模拟他人 & 身份切换](/up/mock-person) 
	- [账号封禁](/up/disable) 
	- [密码加密](/up/password-secure) 
	- [会话查询](/up/search-session) 
	- [Http Basic/Digest 认证](/up/basic-auth) 
	- [全局侦听器](/up/global-listener) 
	- [全局过滤器](/up/global-filter) 
	- [多账号认证](/up/many-account) 

- **单点登录**
	- [单点登录简述](/sso/readme)
	- [搭建统一认证中心：SSO-Server](/sso/sso-server)
	- [SSO-Server 认证中心开放 API 接口](/sso/sso-apidoc)
	- [SSO模式一 共享Cookie同步会话](/sso/sso-type1)
	- [SSO模式二 URL重定向传播会话](/sso/sso-type2)
	- [SSO模式三 Http请求获取会话](/sso/sso-type3)
	- [配置域名校验](/sso/sso-check-domain)
	- [定制化登录页面](/sso/sso-custom-login)
	- [自定义API路由](/sso/sso-custom-api)
	- [前后端分离下的整合方案](/sso/sso-h5)
	- [NoSdk 模式与非 java 项目](/sso/sso-nosdk)
	- [平台中心跳转模式](/sso/sso-home-jump)
	- [不同 Client 不同秘钥](/sso/sso-diff-key)
	- [用户数据同步 / 迁移](/sso/user-data-sync)
	- [常见问题总结](/sso/sso-questions)
	- [Sa-Sso-Pro：单点登录商业版](/sso/sso-pro)

- **OAuth2.0**
	- [OAuth2.0简述](/oauth2/readme)
	- [OAuth2-Server搭建](/oauth2/oauth2-server)
	- [OAuth2-Server端开放 API 接口](/oauth2/oauth2-apidoc)
	- [自定义数据加载器](/oauth2/oauth2-data-loader)
	- [配置 client 域名校验 ](/oauth2/oauth2-check-domain)
	- [自定义 Scope 权限及处理器](/oauth2/oauth2-custom-scope)
	- [为 Scope 划分等级](/oauth2/oauth2-scope-level)
	- [自定义 grant_type](/oauth2/oauth2-custom-grant_type)
	- [定制化登录页面与授权页面](/oauth2/oauth2-custom-login)
	- [自定义 API 路由 ](/oauth2/oauth2-custom-api)
	- [OAuth2-Server端前后台分离](/oauth2/oauth2-h5)
	- [OpenId 与 UnionId](/oauth2/oauth2-openid)
	- [开启 OIDC 协议](/oauth2/oauth2-oidc)
	- [使用注解校验 Access-Token](/oauth2/oauth2-at-check)
	- [OAuth2-与登录会话实现数据互通](/oauth2/oauth2-interworking)
	- [OAuth2 代码 API 参考](/oauth2/oauth2-dev)
	- [常见问题总结](/oauth2/oauth2-questions)
	<!-- - [前后端分离模式整合方案](/oauth2/4) -->
	<!-- - [平台中心模式开发](/oauth2/5) -->
	<!-- - [jwt 风格 token](/oauth2/6) -->

- **微服务**
	- [分布式Session会话](/micro/dcs-session)
	- [网关统一鉴权](/micro/gateway-auth)
	- [内部服务外网隔离](/micro/same-token)
	- [依赖引入说明](/micro/import-intro)

- **插件**
	- [AOP注解鉴权](/plugin/aop-at)
	- [临时 Token 认证](/plugin/temp-token)
	- [Quick-Login快速登录插件](/plugin/quick-login)
	- [Alone独立Redis插件](/plugin/alone-redis)
	- [缓存层扩展](/plugin/dao-extend)
	- [序列化插件扩展包](/plugin/custom-serializer)
	- [和 Thymeleaf 集成](/plugin/thymeleaf-extend)
	- [和 Freemarker 集成](/plugin/freemarker-extend)
	- [注解鉴权 SpEL 表达式](/plugin/spel-at)
	- [和 jwt 集成](/plugin/jwt-extend)
	- [和 Dubbo 集成](/plugin/dubbo-extend)
	- [和 gRPC 集成](/plugin/grpc-extend)
	- [API 接口参数签名](/plugin/api-sign)
	- [API Key 接口调用秘钥](/plugin/api-key)
	- [Sa-Token 插件开发指南](/fun/plugin-dev)
	- [自定义 SaTokenContext 指南](/fun/sa-token-context)

- **API手册**
	- [StpUtil-鉴权工具类](/api/stp-util)
	- [SaSession-会话对象](/api/sa-session)
	- [SaTokenDao-数据持久接口](/api/sa-token-dao)
	- [SaStrategy-全局策略](/api/sa-strategy)
	- [全局类、方法](/more/common-action) 


- **其它**
	- [更新日志](/more/update-log) 
	- [框架生态](/more/link) 
	- [框架博客](/more/blog) 
	- [推荐公众号](/more/tj-gzh) 
	- [加入讨论群](/more/join-group) 
	- [赞助 Sa-Token](/more/sa-token-donate)
	- [需求提交](/more/demand-commit) 
	- [问卷调查](/more/wenjuan) 

- **附录**
	- [常见问题排查](/more/common-questions)  
	- [框架名词解释](/more/noun-intro)  
	- [Sa-Token功能结构图](/fun/auth-flow)
	- [全局 Log 输出](/fun/log) 
	- [未登录场景值详解](/fun/not-login-scene)
	- [Token有效期详解](/fun/token-timeout)
	- [Session模型详解](/fun/session-model)
	- [数据读写三大作用域](/fun/three-scope)  
	- [TokenInfo参数详解](/fun/token-info)
	- [异常细分状态码](/fun/exception-code)
	- [数据结构](/fun/data-structure)
	- [自定义注解](/fun/custom-annotations)
	- [防火墙](/fun/firewall)
	- [参考：把权限放在缓存里](/fun/jur-cache)
	- [参考：把路由拦截鉴权动态化](/fun/dynamic-router-check)
	- [解决反向代理 uri 丢失的问题](/fun/curr-domain)
	- [解决跨域问题](/fun/cors-filter)
	- [技术选型：SSO 与 OAuth2 对比](/fun/sso-vs-oauth2)
	- [集成 MongoDB 参考一](/up/integ-spring-mongod-1)
	- [集成 MongoDB 参考二](/up/integ-spring-mongod-2)
	<!-- - [框架源码所有技术栈](/fun/tech-stack) -->
	- [从 Shiro、SpringSecurity、JWT 迁移](/fun/auth-framework-function-test)
	- [issue 提问模板](/fun/issue-template)
	- [为Sa-Token贡献代码](/fun/git-pr)
	- [Sa-Token开源大事记](/fun/timeline)
	<!-- - [参考资料](/fun/refer-info) -->
	- [团队成员](/fun/team)
	- [Sa-Token框架掌握度--在线考试](/fun/sa-token-test)
	


<br/><br/><br/><br/><br/><br/><br/>
<p style="text-align: center;">----- 到底线了 -----</p>