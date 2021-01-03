<p align="center">
    <img alt="logo" src="https://gitee.com/sz6/sa-token/raw/master/sa-token-doc/doc/logo.png" width="150" height="150" style="margin-bottom: 10px;">
</p>
<h1 align="center" style="margin: 30px 0 30px; font-weight: bold;">sa-token v1.8.0</h1>
<h4 align="center">一个JavaWeb轻量级权限认证框架，功能全面，上手简单</h4>
<h4 align="center">
	<a href="https://gitee.com/sz6/sa-token/stargazers"><img src="https://gitee.com/sz6/sa-token/badge/star.svg"></a>
	<a href="https://github.com/click33/sa-token"><img src="https://img.shields.io/badge/sa--token-v1.8.0-2B9939"></a>
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

- [开源不易，求鼓励，点个star吧](https://github.com/click33/sa-token)
 

## sa-token是什么？
sa-token是一个JavaWeb轻量级权限认证框架，其API调用非常简单，有多简单呢？以登录验证为例，你只需要：

``` java
// 在登录时写入当前会话的账号id 
StpUtil.setLoginId(10001);	

// 然后在任意需要校验登录处调用以下API  --- 如果当前会话未登录，这句代码会抛出 `NotLoginException`异常
StpUtil.checkLogin();	
```


没有复杂的封装！不要任何的配置！只需这两行简单的调用，即可轻松完成系统登录鉴权！


## 框架设计思想
与其它权限认证框架相比，`sa-token`尽力保证两点：
- 上手简单：能自动化的配置全部自动化，不让你费脑子
- 功能强大：能涵盖的功能全部涵盖，不让你用个框架还要自己给框架打各种补丁


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
```
sa-token的API众多，请恕此处无法为您逐一展示，更多示例请戳官方在线文档




## 涵盖功能
- **登录验证** —— 轻松登录鉴权，并提供五种细分场景值
- **权限验证** —— 拦截违规调用，不同角色不同授权
- **Session会话** —— 专业的数据缓存中心
- **踢人下线** —— 将违规用户立刻清退下线
- **模拟他人账号** —— 实时操作任意用户状态数据
- **持久层扩展** —— 可集成redis、MongoDB等专业缓存中间件
- **多账号认证体系** —— 比如一个商城项目的user表和admin表分开鉴权
- **无Cookie模式** —— APP、小程序等前后台分离场景 
- **注解式鉴权** —— 优雅的将鉴权与业务代码分离
- **花式token生成** —— 内置六种token风格，还可自定义token生成策略
- **自动续签** —— 提供两种token过期策略，灵活搭配使用，还可自动续签
- **组件自动注入** —— 零配置与Spring等框架集成
- **更多功能正在集成中...** —— 如有您有好想法或者建议，欢迎加群交流


## 贡献代码
sa-token欢迎大家贡献代码，为框架添砖加瓦
1. 在github上fork一份到自己的仓库
2. clone自己的仓库到本地电脑
3. 在本地电脑修改、commit、push
4. 提交pr（点击：New Pull Request）
5. 等待合并


## 建议贡献的地方
- 修复源码现有bug，或增加新的实用功能
- 完善在线文档，或者修复现有错误之处
- 更多demo示例：比如SSM版搭建步骤 
- 您可以参考项目issues与需求墙进行贡献
- 如果更新实用功能，可在文档友情链接处留下自己的推广链接


## 友情链接
[**[ okhttps ]** 一个轻量级http通信框架，支持 WebSocket 以及 Stomp 协议](https://gitee.com/ejlchina-zhxu/okhttps)


## 交流群
QQ交流群：[1002350610 点击加入](https://jq.qq.com/?_wv=1027&k=45H977HM) ，欢迎你的加入


![扫码加群](https://color-test.oss-cn-qingdao.aliyuncs.com/sa-token/qq-group.png ':size=150')


