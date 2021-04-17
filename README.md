<p align="center">
	<img alt="logo" src="https://gitee.com/dromara/sa-token/raw/master/sa-token-doc/doc/logo.png" width="150" height="150">
</p>
<h1 align="center" style="margin: 30px 0 30px; font-weight: bold;">sa-token v1.16.0</h1>
<h4 align="center">这可能是史上功能最全的 Java 权限认证框架！</h4>
<h4 align="center">
	<a href="https://gitee.com/dromara/sa-token/stargazers"><img src="https://gitee.com/dromara/sa-token/badge/star.svg"></a>
	<a href="https://gitee.com/dromara/sa-token/members"><img src="https://gitee.com/dromara/sa-token/badge/fork.svg"></a>
	<a href="https://github.com/dromara/sa-token/stargazers"><img src="https://img.shields.io/github/stars/dromara/sa-token?style=flat-square&logo=GitHub"></a>
	<a href="https://github.com/dromara/sa-token/network/members"><img src="https://img.shields.io/github/forks/dromara/sa-token?style=flat-square&logo=GitHub"></a>
	<a href="https://github.com/dromara/sa-token/watchers"><img src="https://img.shields.io/github/watchers/dromara/sa-token?style=flat-square&logo=GitHub"></a>
	<a href="https://github.com/dromara/sa-token/issues"><img src="https://img.shields.io/github/issues/dromara/sa-token.svg?style=flat-square&logo=GitHub"></a>
	<a href="https://github.com/dromara/sa-token/blob/master/LICENSE"><img src="https://img.shields.io/github/license/dromara/sa-token.svg?style=flat-square"></a>
</h4>

---


## 在线资料

- [官网首页：http://sa-token.dev33.cn/](http://sa-token.dev33.cn/)

- [在线文档：http://sa-token.dev33.cn/doc/index.html](http://sa-token.dev33.cn/doc/index.html)

- [需求提交：我们深知一个优秀的项目需要海纳百川，点我在线提交需求](http://sa-app.dev33.cn/wall.html?name=sa-token)

- [开源不易，求鼓励，点个star吧 ！](###)


## Sa-Token是什么？
sa-token是一个轻量级Java权限认证框架，主要解决：登录认证、权限认证、Session会话、单点登录、OAuth2.0 等一系列权限相关问题

框架针对踢人下线、自动续签、前后台分离、分布式会话……等常见业务进行N多适配，通过sa-token，你可以以一种极简的方式实现系统的权限认证部分

与其它权限认证框架相比，`sa-token` 具有以下优势：
1. **简单** ：可零配置启动框架，真正的开箱即用，低成本上手
2. **强大** ：目前已集成几十项权限相关特性，涵盖了大部分业务场景的解决方案
3. **易用** ：如丝般顺滑的API调用，大量高级特性统统只需一行代码即可实现
4. **高扩展** ：几乎所有组件都提供了扩展接口，90%以上的逻辑都可以按需重写

有了sa-token，你所有的权限认证问题，都不再是问题！

## Sa-Token 能做什么？

![sa-token-js](https://oss.dev33.cn/sa-token/doc/sa-token-js.png)

- **登录验证** —— 轻松登录鉴权，并提供五种细分场景值
- **权限验证** —— 适配RBAC权限模型，不同角色不同授权
- **Session会话** —— 专业的数据缓存中心
- **踢人下线** —— 将违规用户立刻清退下线
- **账号封禁** —— 封禁指定账号，使其无法登陆，还可指定解封时间
- **持久层扩展** —— 可集成Redis、Memcached等专业缓存中间件，重启数据不丢失
- **分布式会话** —— 提供jwt集成和共享数据中心两种分布式会话方案
- **微服务网关鉴权** —— 适配Gateway、Soul、Zuul等常见网关组件的请求拦截认证
- **单点登录** —— 一处登录，处处通行
- **模拟他人账号** —— 实时操作任意用户状态数据
- **临时身份切换** —— 将会话身份临时切换为其它账号
- **无Cookie模式** —— APP、小程序等前后台分离场景
- **同端互斥登录** —— 像QQ一样手机电脑同时在线，但是两个手机上互斥登录
- **多账号认证体系** —— 比如一个商城项目的user表和admin表分开鉴权
- **花式token生成** —— 内置六种token风格，还可自定义token生成策略
- **注解式鉴权** —— 优雅的将鉴权与业务代码分离
- **路由拦截式鉴权** —— 根据路由拦截鉴权，可适配restful模式
- **自动续签** —— 提供两种token过期策略，灵活搭配使用，还可自动续签
- **会话治理** —— 提供方便灵活的会话查询接口
- **记住我模式** —— 适配[记住我]模式，重启浏览器免验证
- **密码加密** —— 提供密码加密模块，可快速MD5、SHA1、SHA256、AES、RSA加密 
- **开箱即用** —— 提供SpringMVC、WebFlux等常见web框架starter集成包，真正的开箱即用
- **更多功能正在集成中...** —— 如有您有好想法或者建议，欢迎加群交流


## 代码示例

sa-token的API调用非常简单，有多简单呢？以登录验证为例，你只需要：

``` java
// 在登录时写入当前会话的账号id
StpUtil.setLoginId(10001);

// 然后在任意需要校验登录处调用以下API
// 如果当前会话未登录，这句代码会抛出 `NotLoginException`异常
StpUtil.checkLogin();
```
至此，我们已经借助sa-token框架完成登录授权！

此时的你小脑袋可能飘满了问号，就这么简单？自定义Realm呢？全局过滤器呢？我不用写各种配置文件吗？

事实上在此我可以负责的告诉你，在sa-token中，登录授权就是如此的简单，不需要什么全局过滤器，不需要各种乱七八糟的配置！只需要这一行简单的API调用，即可完成会话的登录授权！

当你受够Shiro、Security等框架的三拜九叩之后，你就会明白，相对于这些传统老牌框架，sa-token的API设计是多么的清爽！

权限认证示例 (只有具有`user:add`权限的会话才可以进入请求)
``` java
@SaCheckPermission("user:add")
@RequestMapping("/user/insert")
public String insert(SysUser user) {
	// ... 
	return "用户增加";
}
```

将某个账号踢下线 (待到对方再次访问系统时会抛出`NotLoginException`异常)
``` java
// 使账号id为10001的会话注销登录
StpUtil.logoutByLoginId(10001);
```

除了以上的示例，sa-token还可以一行代码完成以下功能：
``` java
StpUtil.setLoginId(10001);                // 标记当前会话登录的账号id
StpUtil.getLoginId();                     // 获取当前会话登录的账号id
StpUtil.isLogin();                        // 获取当前会话是否已经登录, 返回true或false
StpUtil.logout();                         // 当前会话注销登录
StpUtil.logoutByLoginId(10001);           // 让账号为10001的会话注销登录（踢人下线）
StpUtil.hasRole("super-admin");           // 查询当前账号是否含有指定角色标识, 返回true或false
StpUtil.hasPermission("user:add");        // 查询当前账号是否含有指定权限, 返回true或false
StpUtil.getSession();                     // 获取当前账号id的Session
StpUtil.getSessionByLoginId(10001);       // 获取账号id为10001的Session
StpUtil.getTokenValueByLoginId(10001);    // 获取账号id为10001的token令牌值
StpUtil.setLoginId(10001, "PC");          // 指定设备标识登录
StpUtil.logoutByLoginId(10001, "PC");     // 指定设备标识进行强制注销 (不同端不受影响)
StpUtil.switchTo(10044);                  // 将当前会话身份临时切换为其它账号
```
sa-token API 众多，请恕此处无法为您逐一展示，更多示例请戳官方在线文档


## Star 趋势
[![giteye-chart](https://chart.giteye.net/gitee/dromara/sa-token/77YQZ6UK.png 'Gitee')](https://giteye.net/chart/77YQZ6UK)

[![github-chart](https://starchart.cc/dromara/sa-token.svg 'GitHub')](https://starchart.cc/dromara/sa-token)


## 参与贡献
众人拾柴火焰高，万丈高楼众人起！
sa-token秉承着开放的思想，欢迎大家为框架添砖加瓦：

1. 核心代码：该部分需要开发者了解整个框架的架构，遵循已有代码规范进行bug修复或提交新功能
2. 文档部分：需要以清晰明了的语句书写文档，力求简单易读，授人以鱼同时更授人以渔
3. 社区建设：如果框架帮助到了您，希望您可以加入qq群参与交流，对不熟悉框架的新人进行排难解惑
4. 框架推广：一个优秀的开源项目不能仅靠闭门造车，它还需要一定的推广方案让更多的人一起参与到项目中
5. 其它部分：您可以参考项目issues与需求墙进行贡献

作者寄语：参与贡献不光只有提交代码，点一个star、提一个issues都是对开源项目的促进，
如果sa-token帮助到了你，欢迎你把框架推荐给朋友、同事使用，为sa-token的推广做一份贡献


## 使用sa-token的开源项目
[**[ sa-plus]** 一个基于springboot架构的快速开发框架，内置代码生成器](https://gitee.com/click33/sa-plus)

[**[ jthink]** 一个基于springboot+sa-token+thymeleaf的博客系统](https://gitee.com/wtsoftware/jthink)


如果您的项目使用了sa-token，欢迎提交pr


## 友情链接
[**[ okhttps ]** 一个轻量级http通信框架，API设计无比优雅，支持 WebSocket 以及 Stomp 协议](https://gitee.com/ejlchina-zhxu/okhttps)

[**[ 小诺快速开发平台 ]** 基于SpringBoot2 + AntDesignVue全新快速开发平台，同时拥有三个版本](https://xiaonuo.vip/index#pricing)


## 交流群
QQ交流群：1002350610 [点击加入](https://jq.qq.com/?_wv=1027&k=45H977HM)

微信交流群：

![微信群](https://dev33-test.oss-cn-beijing.aliyuncs.com/sa-token/i-wx-qr.png ':size=230')

(扫码添加微信，备注：sa-token，邀您加入群聊)

<br>
