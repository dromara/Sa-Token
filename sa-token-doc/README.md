<p align="center">
	<img alt="logo" src="https://sa-token.cc/logo.png" width="150" height="150">
</p>
<h1 align="center" style="margin: 30px 0 30px; font-weight: bold;">Sa-Token v1.40.0</h1>
<h5 align="center">一个轻量级 Java 权限认证框架，让鉴权变得简单、优雅！</h5>
<p align="center" class="badge-box">
	<a href="https://gitee.com/dromara/sa-token/stargazers"><img src="https://gitee.com/dromara/sa-token/badge/star.svg?theme=gvp"></a>
	<a href="https://gitee.com/dromara/sa-token/members"><img src="https://gitee.com/dromara/sa-token/badge/fork.svg?theme=gvp"></a>
	<a href="https://gitcode.com/dromara/sa-token/stargazers"><img src="https://gitcode.com/dromara/Sa-Token/star/badge.svg"></a>
	<a href="https://github.com/dromara/sa-token/stargazers"><img src="https://img.shields.io/github/stars/dromara/sa-token?style=flat-square&logo=GitHub"></a>
	<a href="https://github.com/dromara/sa-token/network/members"><img src="https://img.shields.io/github/forks/dromara/sa-token?style=flat-square&logo=GitHub"></a>
	<a href="https://github.com/dromara/sa-token/watchers"><img src="https://img.shields.io/github/watchers/dromara/sa-token?style=flat-square&logo=GitHub"></a>
	<!-- <a href="https://github.com/dromara/sa-token/issues"><img src="https://img.shields.io/github/issues/dromara/sa-token.svg?style=flat-square&logo=GitHub"></a> -->
	<a href="https://github.com/dromara/sa-token/blob/master/LICENSE"><img src="https://img.shields.io/github/license/dromara/sa-token.svg?style=flat-square"></a>
</p>

---

## 前言：️️
为了保证新同学不迷路，请允许我唠叨一下：无论您从何处看到本篇文章，最新开发文档永远在：[https://sa-token.cc](https://sa-token.cc)，
建议收藏在浏览器书签，如果您已经身处本网站下，则请忽略此条说明。

本文档将会尽力讲解每个功能的设计原因、应用场景，用心阅读文档，你学习到的将不止是 `Sa-Token` 框架本身，更是绝大多数场景下权限设计的最佳实践。


## Sa-Token 介绍

**Sa-Token** 是一个轻量级 Java 权限认证框架，主要解决：**登录认证**、**权限认证**、**单点登录**、**OAuth2.0**、**分布式Session会话**、**微服务网关鉴权**
等一系列权限相关问题。

Sa-Token 旨在以简单、优雅的方式完成系统的权限认证部分，以登录认证为例，你只需要：

``` java
// 会话登录，参数填登录人的账号id 
StpUtil.login(10001);
```

无需实现任何接口，无需创建任何配置文件，只需要这一句静态代码的调用，便可以完成会话登录认证。

如果一个接口需要登录后才能访问，我们只需调用以下代码：

``` java
// 校验当前客户端是否已经登录，如果未登录则抛出 `NotLoginException` 异常
StpUtil.checkLogin();
```

在 Sa-Token 中，大多数功能都可以一行代码解决：

踢人下线：

``` java
// 将账号id为 10077 的会话踢下线 
StpUtil.kickout(10077);
```

权限认证：

``` java
// 注解鉴权：只有具备 `user:add` 权限的会话才可以进入方法
@SaCheckPermission("user:add")
public String insert(SysUser user) {
    // ... 
    return "用户增加";
}
```

路由拦截鉴权：

``` java
// 根据路由划分模块，不同模块不同鉴权 
registry.addInterceptor(new SaInterceptor(handler -> {
	SaRouter.match("/user/**", r -> StpUtil.checkPermission("user"));
	SaRouter.match("/admin/**", r -> StpUtil.checkPermission("admin"));
	SaRouter.match("/goods/**", r -> StpUtil.checkPermission("goods"));
	SaRouter.match("/orders/**", r -> StpUtil.checkPermission("orders"));
	SaRouter.match("/notice/**", r -> StpUtil.checkPermission("notice"));
	// 更多模块... 
})).addPathPatterns("/**");
```

当你受够 Shiro、SpringSecurity 等框架的三拜九叩之后，你就会明白，相对于这些传统老牌框架，Sa-Token 的 API 设计是多么的简单、优雅！


## Sa-Token 功能一览

Sa-Token 目前主要五大功能模块：登录认证、权限认证、单点登录、OAuth2.0、微服务鉴权。

- **登录认证** —— 单端登录、多端登录、同端互斥登录、七天内免登录。
- **权限认证** —— 权限认证、角色认证、会话二级认证。
- **踢人下线** —— 根据账号id踢人下线、根据Token值踢人下线。
- **注解式鉴权** —— 优雅的将鉴权与业务代码分离。
- **路由拦截式鉴权** —— 根据路由拦截鉴权，可适配 restful 模式。
- **Session会话** —— 全端共享Session,单端独享Session,自定义Session,方便的存取值。
- **持久层扩展** —— 可集成 Redis，重启数据不丢失。
- **前后台分离** —— APP、小程序等不支持 Cookie 的终端也可以轻松鉴权。
- **Token风格定制** —— 内置六种 Token 风格，还可：自定义 Token 生成策略。
- **记住我模式** —— 适配 [记住我] 模式，重启浏览器免验证。
- **二级认证** —— 在已登录的基础上再次认证，保证安全性。 
- **模拟他人账号** —— 实时操作任意用户状态数据。
- **临时身份切换** —— 将会话身份临时切换为其它账号。
- **同端互斥登录** —— 像QQ一样手机电脑同时在线，但是两个手机上互斥登录。
- **账号封禁** —— 登录封禁、按照业务分类封禁、按照处罚阶梯封禁。
- **密码加密** —— 提供基础加密算法，可快速 MD5、SHA1、SHA256、AES 加密。
- **会话查询** —— 提供方便灵活的会话查询接口。
- **Http Basic认证** —— 一行代码接入 Http Basic、Digest 认证。
- **全局侦听器** —— 在用户登陆、注销、被踢下线等关键性操作时进行一些AOP操作。
- **全局过滤器** —— 方便的处理跨域，全局设置安全响应头等操作。
- **多账号体系认证** —— 一个系统多套账号分开鉴权（比如商城的 User 表和 Admin 表）
- **单点登录** —— 内置三种单点登录模式：同域、跨域、同Redis、跨Redis、前后端分离等架构都可以搞定。
- **单点注销** —— 任意子系统内发起注销，即可全端下线。
- **OAuth2.0认证** —— 轻松搭建 OAuth2.0 服务，支持openid模式 。
- **分布式会话** —— 提供共享数据中心分布式会话方案。
- **微服务网关鉴权** —— 适配Gateway、ShenYu、Zuul等常见网关的路由拦截认证。
- **RPC调用鉴权** —— 网关转发鉴权，RPC调用鉴权，让服务调用不再裸奔
- **临时Token认证** —— 解决短时间的 Token 授权问题。
- **独立Redis** —— 将权限缓存与业务缓存分离。
- **Quick快速登录认证** —— 为项目零代码注入一个登录页面。
- **标签方言** —— 提供 Thymeleaf 标签方言集成包，提供 beetl 集成示例。
- **jwt集成** —— 提供三种模式的 jwt 集成方案，提供 token 扩展参数能力。
- **RPC调用状态传递** —— 提供 dubbo、grpc 等集成包，在RPC调用时登录状态不丢失。
- **参数签名** —— 提供跨系统API调用签名校验模块，防参数篡改，防请求重放。
- **自动续签** —— 提供两种Token过期策略，灵活搭配使用，还可自动续签。
- **开箱即用** —— 提供SpringMVC、WebFlux、Solon 等常见框架集成包，开箱即用。
- **最新技术栈** —— 适配最新技术栈：支持 SpringBoot 3.x，jdk 17。

功能结构图：

![sa-token-js](https://color-test.oss-cn-qingdao.aliyuncs.com/sa-token/x/sa-token-js4.png 's-w')


## 开源仓库 Star 趋势

<p class="un-dec-a-pre"></p>

[![github-chart](https://starchart.cc/dromara/sa-token.svg 'GitHub')](https://starchart.cc/dromara/sa-token)

如果 Sa-Token 帮助到了您，希望您可以为其点上一个 `star`：
[码云](https://gitee.com/dromara/sa-token)、
[GitHub](https://github.com/dromara/sa-token)


## 使用Sa-Token的开源项目 
参考：[Sa-Token 生态](/more/link)



## 交流群
加入 Sa-Token 框架 QQ、微信讨论群：[点击加入](/more/join-group.md)


