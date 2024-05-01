# SSO整合-配置域名校验

--- 

### 1、Ticket劫持攻击
在前面章节的 SSO-Server 示例中，配置项 `sa-token.sso-server.allow-url=*` 意为配置所有允许的Client端授权地址，不在此配置项中的URL将无法单点登录成功

为了方便测试，上述代码将其配置为`*`，但是，<font color="#FF0000" >在生产环境中，此配置项绝对不能配置为 * </font>，否则会有被 Ticket 劫持的风险 

假设攻击者根据模仿我们的授权地址，巧妙的构造一个URL

> [http://sa-sso-server.com:9000/sso/auth?redirect=https://www.baidu.com/](http://sa-sso-server.com:9000/sso/auth?redirect=https://www.baidu.com/)

当不知情的小红被诱导访问了这个URL时，它将被重定向至百度首页

![sso-ticket-jc](https://oss.dev33.cn/sa-token/doc/sso/sso-ticket-jc.png 's-w-sh')

可以看到，代表着用户身份的 Ticket 码也显现到了URL之中，借此漏洞，攻击者完全可以构建一个URL将小红的 Ticket 码自动提交到攻击者自己的服务器，伪造小红身份登录网站

### 2、防范方法

造成此漏洞的直接原因就是SSO-Server认证中心没有对 `redirect地址` 进行任何的限制，防范的方法也很简单，就是对`redirect参数`进行校验，如果其不在指定的URL列表中时，拒绝下放ticket 

我们将其配置为一个具体的URL：
<!---------------------------- tabs:start ---------------------------->
<!------------- tab:yaml 风格  ------------->
``` yaml
sa-token: 
	sso-server: 
        # 配置允许单点登录的 url 
        allow-url: http://sa-sso-client1.com:9001/sso/login
```
<!------------- tab:properties 风格  ------------->
``` properties
# 配置允许单点登录的 url 
sa-token.sso.allow-url=http://sa-sso-client1.com:9001/sso/login
```
<!---------------------------- tabs:end ---------------------------->

再次访问上述链接：

![sso-feifa-rf](https://oss.dev33.cn/sa-token/doc/sso/sso-feifa-rf.png 's-w-sh')

域名没有通过校验，拒绝授权！

### 3、配置安全性参考表

| 配置方式		| 举例										| 安全性								|  建议									|
| :--------		| :--------									| :--------							| :--------								|
| 配置为*		| `*`										| <font color="#F00" >低</font>		| **<font color="#F00" >禁止在生产环境下使用</font>**	|
| 配置到域名	| `http://sa-sso-client1.com/*`					| <font color="#F70" >中</font>		| <font color="#F70" >不建议在生产环境下使用</font>	|
| 配置到详细地址| `http://sa-sso-client1.com:9001/sso/login`	| <font color="#080" >高</font>		| <font color="#080" >可以在生产环境下使用</font>	|


### 4、疑问：为什么不直接回传 Token，而是先回传 Ticket，再用 Ticket 去查询对应的账号id？
Token 作为长时间有效的会话凭证，在任何时候都不应该直接暴露在 URL 之中（虽然 Token 直接的暴露本身不会造成安全漏洞，但会为很多漏洞提供可乘之机）

为了不让系统安全处于亚健康状态，Sa-Token-SSO 选择先回传 Ticket，再由 Ticket 获取账号id，且 Ticket 一次性用完即废，提高安全性。

