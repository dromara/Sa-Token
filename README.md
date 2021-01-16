<p align="center">
    <img alt="logo" src="https://gitee.com/sz6/sa-token/raw/master/sa-token-doc/doc/logo.png" width="150" height="150">
</p>
<h1 align="center" style="margin: 30px 0 30px; font-weight: bold;">sa-token v1.12.1</h1>
<h4 align="center">这可能是史上功能最全的Java权限认证框架！</h4>
<h4 align="center">
	<a href="https://gitee.com/sz6/sa-token/stargazers"><img src="https://gitee.com/sz6/sa-token/badge/star.svg"></a>
	<a href="https://github.com/click33/sa-token"><img src="https://img.shields.io/badge/sa--token-v1.12.1-2B9939"></a>
	<a href="https://github.com/click33/sa-token/stargazers"><img src="https://img.shields.io/github/stars/click33/sa-token"></a>
	<a href="https://github.com/click33/sa-token/watchers"><img src="https://img.shields.io/github/watchers/click33/sa-token"></a>
	<a href="https://github.com/click33/sa-token/network/members"><img src="https://img.shields.io/github/forks/click33/sa-token"></a>
	<a href="https://github.com/click33/sa-token/issues"><img src="https://img.shields.io/github/issues/click33/sa-token.svg"></a>
	<a href="https://github.com/click33/sa-token/blob/master/LICENSE"><img src="https://img.shields.io/github/license/click33/sa-token.svg"></a>
</h4>

---


## 在线资料

- [官网首页：http://sa-token.dev33.cn/](http://sa-token.dev33.cn/)

- [在线文档：http://sa-token.dev33.cn/doc/index.html](http://sa-token.dev33.cn/doc/index.html)

- [需求提交：我们深知一个优秀的项目需要海纳百川，点我在线提交需求](http://sa-app.dev33.cn/wall.html?name=sa-token)

- [开源不易，求鼓励，点个star吧](###)
 

## Sa-Token是什么？
sa-token是一个轻量级Java权限认证框架，主要解决: 登录认证、权限认证、Session会话 等一系列权限相关问题

在架构设计上，`sa-token`拒绝引入复杂的概念，以实际业务需求为第一目标进行定向突破，例如踢人下线、自动续签、同端互斥登录等常见业务在框架内**均可以一行代码调用实现**，简单粗暴，拒绝复杂！

对于传统Session会话模型的N多难题，例如难以分布式、水平扩展性差，难以兼容前后台分离环境，多会话管理混乱等，
`sa-token`独创了以账号为主的`User-Session`模式，同时又兼容传统以token为主的`Token-Session`模式，两者彼此独立，互不干扰，
让你在进行会话管理时如鱼得水，在`sa-toekn`的强力加持下，权限问题将不再成为业务逻辑的瓶颈！

总的来说，与其它权限认证框架相比，`sa-token`具有以下优势：
1. 上手简单：可零配置启动框架，能自动化的均已自动化，不让你费脑子
2. 功能强大：能集成的功能全部集成，不让你用个框架还要自己给框架打各种补丁
3. API简单易用：同样的一个功能，可能在别的框架中需要上百行代码，但是在sa-token中统统一行代码调个方法即可解决
4. 组件易于扩展：框架中几乎所有组件都提供了对应的扩展接口，90%以上的逻辑都是可以被按需重写的

有了sa-token，你所有的权限认证问题，都不再是问题！


## 代码示例

sa-token的API调用非常简单，有多简单呢？以登录验证为例，你只需要：

``` java
// 在登录时写入当前会话的账号id 
StpUtil.setLoginId(10001);	

// 然后在任意需要校验登录处调用以下API  --- 如果当前会话未登录，这句代码会抛出 `NotLoginException`异常
StpUtil.checkLogin();	
```

权限认证示例 (只有具有`user:add`权限的会话才可以进入请求)
``` java
@SaCheckPermission("user:add")        
@RequestMapping("/user/insert")
public String insert(SysUser user) {
	return "用户增加";
}
```

将某个账号踢下线 (待到对方再次访问系统时会抛出`NotLoginException`异常)
``` java
// 使账号id为10001的会话注销登录
StpUtil.logoutByLoginId(10001); 
```

如果上面的示例能够证明`sa-token`的简单，那么以下API则可以证明`sa-token`的强大
``` java
StpUtil.setLoginId(10001);          // 标记当前会话登录的账号id
StpUtil.getLoginId();               // 获取当前会话登录的账号id
StpUtil.isLogin();                  // 获取当前会话是否已经登录, 返回true或false
StpUtil.logout();                   // 当前会话注销登录
StpUtil.logoutByLoginId(10001);     // 让账号为10001的会话注销登录（踢人下线）
StpUtil.hasRole("super-admin");     // 查询当前账号是否含有指定角色标识, 返回true或false
StpUtil.hasPermission("user:add");  // 查询当前账号是否含有指定权限, 返回true或false
StpUtil.getSession();               // 获取当前账号id的Session 
StpUtil.getSessionByLoginId(10001); // 获取账号id为10001的Session
StpUtil.getTokenValueByLoginId(10001);  // 获取账号id为10001的token令牌值
StpUtil.setLoginId(10001, "PC");        // 指定设备标识登录
StpUtil.logoutByLoginId(10001, "PC");   // 指定设备标识进行强制注销 (不同端不受影响)
StpUtil.switchTo(10044);                // 将当前会话身份临时切换为其它账号 
```
sa-token的API众多，请恕此处无法为您逐一展示，更多示例请戳官方在线文档


## 涵盖功能
- **登录验证** —— 轻松登录鉴权，并提供五种细分场景值
- **权限验证** —— 适配RBAC模型，不同角色不同授权
- **Session会话** —— 专业的数据缓存中心
- **踢人下线** —— 将违规用户立刻清退下线
- **持久层扩展** —— 可集成redis、MongoDB等专业缓存中间件
- **多账号认证体系** —— 比如一个商城项目的user表和admin表分开鉴权
- **无Cookie模式** —— APP、小程序等前后台分离场景 
- **注解式鉴权** —— 优雅的将鉴权与业务代码分离
- **路由拦截式鉴权** —— 设定全局路由拦截，并排除指定路由
- **模拟他人账号** —— 实时操作任意用户状态数据
- **临时身份切换** —— 将会话身份临时切换为其它账号
- **花式token生成** —— 内置六种token风格，还可自定义token生成策略
- **自动续签** —— 提供两种token过期策略，灵活搭配使用，还可自动续签
- **同端互斥登录** —— 像QQ一样手机电脑同时在线，但是两个手机上互斥登录
- **组件自动注入** —— 零配置与Spring等框架集成
- **会话治理** —— 提供方便灵活的会话查询接口
- **更多功能正在集成中...** —— 如有您有好想法或者建议，欢迎加群交流


## 知乎专栏
- [初识sa-token，一行代码搞定登录授权！](https://zhuanlan.zhihu.com/p/344106099)
- [一个登录功能也能玩出这么多花样？sa-token带你轻松搞定多地登录、单地登录、同端互斥登录](https://zhuanlan.zhihu.com/p/344511415)
- 更多文章连载中...欢迎投稿



## 参与贡献
众人拾柴火焰高，sa-token秉承着开放的思想，欢迎大家贡献代码，为框架添砖加瓦

1. 在gitee或者github上fork一份代码到自己的仓库
2. clone自己的仓库到本地电脑
3. 在本地电脑修改、commit、push
4. 提交pr（点击：New Pull Request）
5. 等待合并

作者寄语：参与贡献不光只有提交代码一个选择，点一个star、提一个issues都是对开源项目的促进，
如果框架帮助到了你，欢迎你把框架推荐给你的朋友、同事使用，为sa-token的推广做一份贡献


## 建议贡献的地方
- 修复源码现有bug，或优化代码架构，或增加新的实用功能
- 完善在线文档，或者修复现有描述错误之处
- 更多的第三方框架集成方案，更多的demo示例：比如SSM版搭建步骤 
- 您可以参考项目issues与需求墙进行贡献


## 友情链接
[**[ okhttps ]** 一个轻量级http通信框架，支持 WebSocket 以及 Stomp 协议](https://gitee.com/ejlchina-zhxu/okhttps)


## 交流群
QQ交流群：[1002350610 点击加入](https://jq.qq.com/?_wv=1027&k=45H977HM) ，欢迎你的加入


![扫码加群](https://color-test.oss-cn-qingdao.aliyuncs.com/sa-token/qq-group.png ':size=150')


