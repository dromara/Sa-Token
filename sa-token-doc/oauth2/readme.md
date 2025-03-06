# Sa-Token-OAuth2.0 模块 

--- 

### 什么是 OAuth2.0 ？解决什么问题？

OAuth2.0 与 SSO 相比，增加了对应用授权范围的控制，减弱了应用之间数据同步的能力。

有关 OAuth2.0 的设计思想网上教程较多，此处不再重复赘述，详细可参考博客：
[OAuth2.0 简单解释](https://www.ruanyifeng.com/blog/2019/04/oauth_design.html)
<!-- 、[OAuth2.0 的四种方式](http://www.ruanyifeng.com/blog/2019/04/oauth-grant-types.html) -->

如果你还不知道你的项目应该选择 SSO 还是 OAuth2.0，可以参考这篇：[技术选型：[ 单点登录 ] VS [ OAuth2.0 ]](/fun/sso-vs-oauth2)



### OAuth2.0 四种模式 

基于不同的使用场景，OAuth2.0设计了四种模式：

1. 授权码（Authorization Code）：OAuth2.0 标准授权步骤，Server 端向 Client 端下放 `Code` 码，Client 端再用 `Code` 码换取授权 `Access-Token`。
2. 隐藏式（Implicit）：无法使用授权码模式时的备用选择，Server 端使用 URL 重定向方式直接将 `Access-Token` 下放到 Client 端页面。
3. 密码式（Password）：Client 端直接拿着用户的账号密码换取授权 `Access-Token`。
4. 客户端凭证（Client Credentials）：Server 端针对 Client 级别的 Token，代表应用自身的资源授权。

![https://oss.dev33.cn/sa-token/doc/oauth2/sa-oauth2-setup.png](https://oss.dev33.cn/sa-token/doc/oauth2/sa-oauth2-setup.png)

接下来我们将通过简单示例演示如何在 Sa-Token-OAuth2 中完成这四种模式的对接: [搭建OAuth2-Server](/oauth2/oauth2-server)


### OAuth2.0 第三方开放平台完整开发流程参考

1. oauth2-server 平台端 
	1. 搭建 oauth2-server 数据后台管理端（后台人员对底层数据增删改查维护的地方）。
	2. 搭建 oauth2-server 数据前台申请端（给第三方公司提供一个申请注册 client 的地方）。
	3. 搭建 oauth2-server 授权端 以及其接口文档（让第三方公司拿到 access_token）。
	4. 搭建 oauth2-server 资源端 以及其接口文档（让第三方公司通过 access_token 拿到对应的资源数据）。
	5. 以上四端可以为一个项目，也可以为四个独立的项目。

2. oauth2-client 第三方公司端
	1. 第三方公司登录 oauth-server 数据前台申请端，申请注册应用，拿到 `clientId`、`clientSecret` 等数据。
	2. 根据自己的业务选择对应的 scope 申请签约，等待平台端审核通过。
	3. 在自己系统通过 `clientId`、`clientSecret` 等参数对接 oauth2-server 授权端，拿到 `access_token`。
	4. 通过 `access_token` 调用 oauth2-server 资源端接口，拿到对应资源数据。

3. 用户端操作
	1. 打开第三方公司开发的网站或APP等程序。
	2. 一般有个“通过xx第三方登录”的按钮，点它。
	3. 跳转到了 oauth2-server 端的网站，在此网站用 oauth2-server 的账号开始登录。
	4. 登录完成，继续跳转到授权页，点击确认授权。
	5. 授权完成，oauth2-server 端生成一个 code 码，重定向回 oauth2-client 的网站，把 code 参数挂到对应的 url 上。
	6. oauth2-client 从 url 中读取 code 参数，提交到 oauth2-client 的后端。
	7. oauth2-client 后端拿着 `code`、`clientId`、`clientSecret` 等信息调用 oauth2-server 授权端 的接口，得到 `access_token`。
	8. 继续拿着 `access_token` 调用 oauth2-server 资源端获取此用户对应的数据。
	9. 一般最终目的拿到一个 openid 值，oauth2-client 根据 openid 进行登录。生成自己的会话 token ，返回到数据到前端。
	10. 前端拿到自己 oauth2-client 生成的会话 token ，完成登录。开始进行业务操作。


