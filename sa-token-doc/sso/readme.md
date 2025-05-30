# Sa-Token-SSO 单点登录模块 

凡是稍微上点规模的系统，统一认证中心都是绕不过去的槛。而单点登录——便是我们搭建统一认证中心的关键。

--- 

### 什么是单点登录？解决什么问题？

举个场景，假设我们的系统被切割为N个部分：商城、论坛、直播、社交…… 如果用户每访问一个模块都要登录一次，那么用户将会疯掉，
为了优化用户体验，我们急需一套机制将这N个系统的认证授权互通共享，让用户在一个系统登录之后，便可以畅通无阻的访问其它所有系统。 

单点登录——就是为了解决这个问题而生！

简而言之，单点登录可以做到： **`在多个互相信任的系统中，用户只需登录一次，就可以访问所有系统。`**


### 架构选型
Sa-Token-SSO 由简入难划分为三种模式，解决不同架构下的 SSO 接入问题：

| 系统架构					| 采用模式	| 简介					|  文档链接	|
| :--------					| :--------	| :--------				| :--------	|
| 前端同域 + 后端同 Redis		| 模式一		| 共享 Cookie 同步会话	| [文档](/sso/sso-type1)、[示例](https://gitee.com/dromara/sa-token/blob/master/sa-token-demo/sa-token-demo-sso/sa-token-demo-sso1-client)	|
| 前端不同域 + 后端同 Redis	| 模式二		| URL重定向传播会话 		| [文档](/sso/sso-type2)、[示例](https://gitee.com/dromara/sa-token/blob/master/sa-token-demo/sa-token-demo-sso/sa-token-demo-sso2-client)	|
| 前端不同域 + 后端不同 Redis	| 模式三		| Http请求获取会话		| [文档](/sso/sso-type3)、[示例](https://gitee.com/dromara/sa-token/blob/master/sa-token-demo/sa-token-demo-sso/sa-token-demo-sso3-client)	|


1. 前端同域：就是指多个系统可以部署在同一个主域名之下，比如：`c1.domain.com`、`c2.domain.com`、`c3.domain.com`。
2. 后端同Redis：就是指多个系统可以连接同一个Redis。PS：这里并不需要把所有项目的数据都放在同一个Redis中，Sa-Token提供了 **`[权限缓存与业务缓存分离]`** 的解决方案，详情戳： <a href="#/plugin/alone-redis" target="_blank">Alone独立Redis插件</a>。
3. 如果既无法做到前端同域，也无法做到后端同Redis，那么只能走模式三，Http请求获取会话（Sa-Token对SSO提供了完整的封装，你只需要按照示例从文档上复制几段代码便可以轻松集成）。

![sa-token-jss](https://oss.dev33.cn/sa-token/doc/home/sa-token-sso--white.png)


### Sa-Token-SSO 特性
1. API 简单易用，文档介绍详细，且提供直接可用的集成示例。
2. 支持三种模式，不论是否跨域、是否共享Redis、是否前后端分离，都可以完美解决。
3. 安全性高：内置域名校验、Ticket校验、秘钥校验等，杜绝`Ticket劫持`、`Token窃取`等常见攻击手段（文档讲述攻击原理和防御手段）。
4. 不丢参数：笔者曾试验多个单点登录框架，均有参数丢失的情况，比如重定向之前是：`http://a.com?id=1&name=2`，登录成功之后就变成了：`http://a.com?id=1`，Sa-Token-SSO内有专门的算法保证了参数不丢失，登录成功之后原路返回页面。
5. 无缝集成：由于Sa-Token本身就是一个权限认证框架，因此你可以只用一个框架同时解决`权限认证` + `单点登录`问题，让你不再到处搜索：xxx单点登录与xxx权限认证如何整合……
6. 高可定制：Sa-Token-SSO模块对代码架构侵入性极低，结合Sa-Token本身的路由拦截特性，你可以非常轻松的定制化开发。


### 学习注意点
1. sa-token-sso 虽然是个单独的插件，但其本质仍是对 Sa-Token 本身各个功能的组合使用，所以先熟练掌握 Sa-Token 可有效降低 SSO 章节的学习压力。
2. 相比单体系统的会话管理，SSO 登录与注销的整体链路较长，出现 bug 时调试步骤也更复杂，因此建议先通过 demo 打通各个技术细节，再正式集成到项目中。
3. 文档对 跨Redis、跨域、前后端分离 等常见场景提供直接可用的示例，但真实项目往往是多种特殊场景交叉组合存在，每个项目各不相同。
所以文档无法依次列出所有技术点交叉组合的 demo 示例，文档会更注重解释清楚每一种特殊场景的特殊点所在，以及其解决原理，
所以推荐大家细心阅读相关段落，以便在真实项目中可以做到灵活组合、举一反三。

