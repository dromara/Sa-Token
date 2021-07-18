# Sa-Token-OAuth2.0 模块 

--- 

### 什么是OAuth2.0？解决什么问题？

简单来讲，OAuth2.0的应用场景可以理解为单点登录的升级版，单点登录解决了多个系统间会话的共享，OAuth2.0在此基础上增加了应用之间的权限控制
（SO：有些系统采用OAuth2.0模式实现了单点登录，但这总给人一种“杀鸡焉用宰牛刀”的感觉）

有关OAuth2.0的设计思想网上教程较多，此处不再重复赘述，详细可参考博客：
[OAuth2.0 简单解释](https://www.ruanyifeng.com/blog/2019/04/oauth_design.html)
<!-- 、[OAuth2.0 的四种方式](http://www.ruanyifeng.com/blog/2019/04/oauth-grant-types.html) -->

Sa-OAuth2 模块基于 [RFC-6749 标准](https://tools.ietf.org/html/rfc6749) 编写，通过Sa-OAuth2你可以非常轻松的实现系统的OAuth2.0授权认证 


### OAuth2.0 四种模式 

基于不同的使用场景，OAuth2.0设计了四种模式：

1. 授权码（Authorization Code）：OAuth2.0标准授权步骤，Server端向Client端下放Code码，Client端再用Code码换取授权Token
2. 隐藏式（Implicit）：无法使用授权码模式时的备用选择，Server端使用URL重定向方式直接将Token下放到Client端页面
3. 密码式（Password）：Client直接拿着用户的账号密码换取授权Token
4. 客户端凭证（Client Credentials）：Server端针对Client级别的Token，代表应用自身的资源授权

![https://oss.dev33.cn/sa-token/doc/oauth2/sa-oauth2-setup.png](https://oss.dev33.cn/sa-token/doc/oauth2/sa-oauth2-setup.png)

接下来我们将通过简单示例演示如何在Sa-OAuth2中完成这四种模式的对接: [搭建OAuth2-Server](/oauth2/oauth2-server)



