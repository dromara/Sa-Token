<p align="center">
	<img alt="logo" src="https://gitee.com/dromara/sa-token/raw/master/sa-token-doc/doc/logo.png" width="150" height="150">
</p>
<h1 align="center" style="margin: 30px 0 30px; font-weight: bold;">Sa-Token v1.27.0</h1>
<h4 align="center">一个轻量级 Java 权限认证框架，让鉴权变得简单、优雅！</h4>
<p align="center">
	<a href="https://gitee.com/dromara/sa-token/stargazers"><img src="https://gitee.com/dromara/sa-token/badge/star.svg"></a>
	<a href="https://gitee.com/dromara/sa-token/members"><img src="https://gitee.com/dromara/sa-token/badge/fork.svg"></a>
	<a href="https://github.com/dromara/sa-token/stargazers"><img src="https://img.shields.io/github/stars/dromara/sa-token?style=flat-square&logo=GitHub"></a>
	<a href="https://github.com/dromara/sa-token/network/members"><img src="https://img.shields.io/github/forks/dromara/sa-token?style=flat-square&logo=GitHub"></a>
	<a href="https://github.com/dromara/sa-token/watchers"><img src="https://img.shields.io/github/watchers/dromara/sa-token?style=flat-square&logo=GitHub"></a>
	<a href="https://github.com/dromara/sa-token/issues"><img src="https://img.shields.io/github/issues/dromara/sa-token.svg?style=flat-square&logo=GitHub"></a>
	<a href="https://github.com/dromara/sa-token/blob/master/LICENSE"><img src="https://img.shields.io/github/license/dromara/sa-token.svg?style=flat-square"></a>
</p>

---

## 前言：
- [在线文档：http://sa-token.dev33.cn/](http://sa-token.dev33.cn/)

- 我们将会尽力讲解每个功能的设计原因、应用场景，用心阅读文档，你学习到的将不止是 `Sa-Token` 框架本身，更是绝大多数场景下权限设计的最佳实践。

- 注：学习测试请拉取 master 分支，dev 为正在开发的分支，有很多特性并不稳定。

- 开源不易，点个 star 鼓励一下吧！

## Sa-Token 介绍

**Sa-Token** 是一个轻量级 Java 权限认证框架，主要解决：**`登录认证`**、**`权限认证`**、**`Session会话`**、**`单点登录`**、**`OAuth2.0`**、**`微服务网关鉴权`** 
等一系列权限相关问题。

Sa-Token 的 API 设计非常简单，有多简单呢？以登录认证为例，你只需要：

``` java
// 在登录时写入当前会话的账号id
StpUtil.login(10001);

// 然后在需要校验登录处调用以下方法：
// 如果当前会话未登录，这句代码会抛出 `NotLoginException` 异常
StpUtil.checkLogin();
```

至此，我们已经借助 Sa-Token 完成登录认证！

此时的你小脑袋可能飘满了问号，就这么简单？自定义 Realm 呢？全局过滤器呢？我不用写各种配置文件吗？

没错，在 Sa-Token 中，登录认证就是如此简单，不需要任何的复杂前置工作，只需这一行简单的API调用，就可以完成会话登录认证！

当你受够 Shiro、SpringSecurity 等框架的三拜九叩之后，你就会明白，相对于这些传统老牌框架，Sa-Token 的 API 设计是多么的简单、优雅！

权限认证示例（只有具备 `user:add` 权限的会话才可以进入请求）
``` java
@SaCheckPermission("user:add")
@RequestMapping("/user/insert")
public String insert(SysUser user) {
	// ... 
	return "用户增加";
}
```

将某个账号踢下线（待到对方再次访问系统时会抛出`NotLoginException`异常）
``` java
// 将账号id为 10001 的会话踢下线 
StpUtil.kickout(10001);
```

在 Sa-Token 中，绝大多数功能都可以 **一行代码** 完成：
``` java
StpUtil.login(10001);                     // 标记当前会话登录的账号id
StpUtil.getLoginId();                     // 获取当前会话登录的账号id
StpUtil.isLogin();                        // 获取当前会话是否已经登录, 返回true或false
StpUtil.logout();                         // 当前会话注销登录
StpUtil.kickout(10001);                   // 将账号为10001的会话踢下线
StpUtil.hasRole("super-admin");           // 查询当前账号是否含有指定角色标识, 返回true或false
StpUtil.hasPermission("user:add");        // 查询当前账号是否含有指定权限, 返回true或false
StpUtil.getSession();                     // 获取当前账号id的Session
StpUtil.getSessionByLoginId(10001);       // 获取账号id为10001的Session
StpUtil.getTokenValueByLoginId(10001);    // 获取账号id为10001的token令牌值
StpUtil.login(10001, "PC");               // 指定设备标识登录，常用于“同端互斥登录”
StpUtil.kickout(10001, "PC");             // 指定账号指定设备标识踢下线 (不同端不受影响)
StpUtil.openSafe(120);                    // 在当前会话开启二级认证，有效期为120秒 
StpUtil.checkSafe();                      // 校验当前会话是否处于二级认证有效期内，校验失败会抛出异常 
StpUtil.switchTo(10044);                  // 将当前会话身份临时切换为其它账号 
```

即使不运行测试，相信您也能意会到绝大多数 API 的用法。



## Sa-Token 功能一览

- **登录认证** —— 单端登录、多端登录、同端互斥登录、七天内免登录
- **权限认证** —— 权限认证、角色认证、会话二级认证
- **Session会话** —— 全端共享Session、单端独享Session、自定义Session 
- **踢人下线** —— 根据账号id踢人下线、根据Token值踢人下线
- **账号封禁** —— 指定天数封禁、永久封禁、设定解封时间 
- **持久层扩展** —— 可集成Redis、Memcached等专业缓存中间件，重启数据不丢失
- **分布式会话** —— 提供jwt集成、共享数据中心两种分布式会话方案
- **微服务网关鉴权** —— 适配Gateway、ShenYu、Zuul等常见网关的路由拦截认证
- **单点登录** —— 内置三种单点登录模式：无论是否跨域、是否共享Redis，都可以搞定
- **OAuth2.0认证** —— 基于RFC-6749标准编写，OAuth2.0标准流程的授权认证，支持openid模式 
- **二级认证** —— 在已登录的基础上再次认证，保证安全性 
- **Basic认证** —— 一行代码接入 Http Basic 认证 
- **独立Redis** —— 将权限缓存与业务缓存分离 
- **临时Token验证** —— 解决短时间的Token授权问题
- **模拟他人账号** —— 实时操作任意用户状态数据
- **临时身份切换** —— 将会话身份临时切换为其它账号
- **前后台分离** —— APP、小程序等不支持Cookie的终端
- **同端互斥登录** —— 像QQ一样手机电脑同时在线，但是两个手机上互斥登录
- **多账号认证体系** —— 比如一个商城项目的user表和admin表分开鉴权
- **花式token生成** —— 内置六种Token风格，还可：自定义Token生成策略、自定义Token前缀
- **注解式鉴权** —— 优雅的将鉴权与业务代码分离
- **路由拦截式鉴权** —— 根据路由拦截鉴权，可适配restful模式
- **自动续签** —— 提供两种Token过期策略，灵活搭配使用，还可自动续签
- **会话治理** —— 提供方便灵活的会话查询接口
- **记住我模式** —— 适配[记住我]模式，重启浏览器免验证
- **密码加密** —— 提供密码加密模块，可快速MD5、SHA1、SHA256、AES、RSA加密 
- **全局侦听器** —— 在用户登陆、注销、被踢下线等关键性操作时进行一些AOP操作
- **开箱即用** —— 提供SpringMVC、WebFlux等常见web框架starter集成包，真正的开箱即用
- **更多功能正在集成中...** —— 如有您有好想法或者建议，欢迎加群交流


## Sa-Token-SSO 单点登录
对于单点登录，网上教程大多以CAS模式为主，其实对于不同的系统架构，实现单点登录的步骤也大为不同，Sa-Token由简入难将其划分为三种模式：

| 系统架构						| 采用模式	| 简介						|  文档链接	|
| :--------						| :--------	| :--------					| :--------	|
| 前端同域 + 后端同 Redis		| 模式一		| 共享Cookie同步会话			| [文档](http://sa-token.dev33.cn/doc/index.html#/sso/sso-type1)、[示例](https://gitee.com/dromara/sa-token/blob/dev/sa-token-demo/sa-token-demo-sso1)	|
| 前端不同域 + 后端同 Redis		| 模式二		| URL重定向传播会话 			| [文档](http://sa-token.dev33.cn/doc/index.html#/sso/sso-type2)、[示例](https://gitee.com/dromara/sa-token/blob/dev/sa-token-demo/sa-token-demo-sso2-server)	|
| 前端不同域 + 后端 不同Redis	| 模式三		| Http请求获取会话			| [文档](http://sa-token.dev33.cn/doc/index.html#/sso/sso-type3)、[示例](https://gitee.com/dromara/sa-token/blob/dev/sa-token-demo/sa-token-demo-sso3-server)	|


1. 前端同域：就是指多个系统可以部署在同一个主域名之下，比如：`c1.domain.com`、`c2.domain.com`、`c3.domain.com`
2. 后端同Redis：就是指多个系统可以连接同一个Redis，其它的缓存数据中心亦可。PS：这里并不需要把所有项目的数据都放在同一个Redis中，Sa-Token提供了 **`[权限缓存与业务缓存分离]`** 的解决方案，详情戳：[Alone独立Redis插件](http://sa-token.dev33.cn/doc/index.html#/plugin/alone-redis)
3. 如果既无法做到前端同域，也无法做到后端同Redis，那么只能走模式三，Http请求获取会话（Sa-Token对SSO提供了完整的封装，你只需要按照示例从文档上复制几段代码便可以轻松集成）
4. 技术选型一定要根据系统架构对症下药，切不可胡乱选择 


## Sa-Token-SSO 特性
1. API简单易用，文档介绍详细，且提供直接可用的集成示例
2. 支持三种模式，不论是否跨域、是否共享Redis、是否前后端分离，都可以完美解决
3. 安全性高：内置域名校验、Ticket校验、秘钥校验等，杜绝`Ticket劫持`、`Token窃取`等常见攻击手段（文档讲述攻击原理和防御手段）
4. 不丢参数：笔者曾试验多个单点登录框架，均有参数丢失的情况，比如重定向之前是：`http://a.com?id=1&name=2`，登录成功之后就变成了：`http://a.com?id=1`，Sa-Token-SSO内有专门的算法保证了参数不丢失，登录成功之后原路返回页面
5. 无缝集成：由于Sa-Token本身就是一个权限认证框架，因此你可以只用一个框架同时解决`权限认证` + `单点登录`问题，让你不再到处搜索：xxx单点登录与xxx权限认证如何整合……
6. 高可定制：Sa-Token-SSO模块对代码架构侵入性极低，结合Sa-Token本身的路由拦截特性，你可以非常轻松的定制化开发 


## Sa-Token-OAuth2.0 授权登录
Sa-OAuth2 模块基于 [RFC-6749 标准](https://tools.ietf.org/html/rfc6749) 编写，通过Sa-OAuth2你可以非常轻松的实现系统的OAuth2.0授权认证 

1. 授权码（Authorization Code）：OAuth2.0标准授权步骤，Server端向Client端下放Code码，Client端再用Code码换取授权Token
2. 隐藏式（Implicit）：无法使用授权码模式时的备用选择，Server端使用URL重定向方式直接将Token下放到Client端页面
3. 密码式（Password）：Client直接拿着用户的账号密码换取授权Token
4. 客户端凭证（Client Credentials）：Server端针对Client级别的Token，代表应用自身的资源授权

详细参考文档：[http://sa-token.dev33.cn/doc/index.html#/oauth2/readme](http://sa-token.dev33.cn/doc/index.html#/oauth2/readme)



## Sa-Token 功能结构图
![sa-token-js](https://color-test.oss-cn-qingdao.aliyuncs.com/sa-token/x/sa-token-js3.png 's-w')

![sa-token-rz](https://color-test.oss-cn-qingdao.aliyuncs.com/sa-token/x/sa-token-rz2.png 's-w')


## Star 趋势
[![giteye-chart](https://chart.giteye.net/gitee/dromara/sa-token/77YQZ6UK.png 'Gitee')](https://giteye.net/chart/77YQZ6UK)

[![github-chart](https://starchart.cc/dromara/sa-token.svg 'GitHub')](https://starchart.cc/dromara/sa-token)


## 使用Sa-Token的开源项目 
- **[ sa-plus ]**：[一个基于 SpringBoot 架构的快速开发框架，内置代码生成器](https://gitee.com/click33/sa-plus)

- **[ jthink ]**： [一个基于 SpringBoot + Sa-Token + Thymeleaf 的博客系统](https://gitee.com/wtsoftware/jthink)

- **[ dcy-fast ]**：[ 一个基于 SpringBoot + Sa-Token + Mybatis-Plus 的后台管理系统，前端vue-element-admin，并且内置代码生成器](https://gitee.com/dcy421/dcy-fast)

- **[ helio-starters ]**：[ 基于JDK15 + Spring Boot 2.4 + Sa-Token + Mybatis-Plus的单体Boot版脚手架和微服务Cloud版脚手架，带有配套后台管理前端模板及代码生成器](https://gitee.com/uncarbon97/helio-starters)

- **[ sa-token-plugin ]**：[Sa-Token第三方插件实现，基于Sa-Token-Core，提供一些与官方不同实现机制的的插件集合，作为Sa-Token开源生态的补充](https://gitee.com/bootx/sa-token-plugin)

- **[ easy-admin ]**：[一个基于SpringBoot2 + Sa-Token + Mybatis-Plus + Snakerflow + Layui 的后台管理系统，灵活多变可前后端分离，也可单体，内置代码生成器、权限管理、工作流引擎等](https://gitee.com/lakernote/easy-admin)

- **[ RuoYi-Vue-Plus ]**：[基于 RuoYi-Vue 集成 SaToken + Lombok + Mybatis-Plus + Undertow + knife4j + Hutool + Feign 重写所有原生业务 定期与 RuoYi-Vue 同步](https://gitee.com/JavaLionLi/RuoYi-Vue-Plus/tree/satoken/)


如果您的项目使用了Sa-Token，欢迎提交pr

## 友情链接
- **[ OkHttps ]**：[ 一个轻量级http通信框架，API设计无比优雅，支持 WebSocket 以及 Stomp 协议](https://gitee.com/ejlchina-zhxu/okhttps)

- **[ 小诺快速开发平台 ]**：[ 基于SpringBoot2 + AntDesignVue全新快速开发平台，同时拥有三个版本](https://xiaonuo.vip/index#pricing)

- **[ Jpom ]**：[ 简而轻的低侵入式在线构建、自动部署、日常运维、项目监控软件](https://gitee.com/dromara/Jpom)

- **[ TLog ]**：[ 一个轻量级的分布式日志标记追踪神器](https://gitee.com/dromara/TLog)



## 交流群
QQ交流群：1群：1002350610 (已满) 、
2群：614714762 [点击加入](https://jq.qq.com/?_wv=1027&k=b759RZrL)

微信交流群：

<!-- ![微信群](https://dev33-test.oss-cn-beijing.aliyuncs.com/sa-token/doc/km/sa-token-hm1.jpg ':size=230') -->

![微信群](https://dev33-test.oss-cn-beijing.aliyuncs.com/sa-token/i-wx-qr.png ':size=230')

(扫码添加微信，备注：sa-token，邀您加入群聊)

<br>
