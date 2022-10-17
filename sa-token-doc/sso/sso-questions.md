# Sa-Token-SSO整合-常见问题总结

--- 

### 问：在模式一与模式二中，Client端 必须通过 Alone-Redis 插件来访问Redis吗？
答：不必须，只是推荐，权限缓存与业务缓存分离后会减少 `SSO-Redis` 的访问压力，且可以避免多个 `Client端` 的缓存读写冲突


### 问：将旧有系统改造为单点登录时，应该注意哪些？
答：建议不要把其中一个系统改造为SSO服务端，而是新起一个项目作为Server端，所有旧有项目全部作为Client端与此对接 


### 问：SSO模式二，第一个域名登录成功之后其他两个不会自动登录？
答：系统1登录成功之后，系统二与系统三需要点击登录按钮，才会登录成功 

> 第一个系统，需要：点击 [登录] 按钮 -> 跳转到登录页 -> 输账号密码 -> 登录成功 <br>
> 第二个系统，需要：点击 [登录] 按钮 -> 登录成功 <br>
> 第三个系统，需要：点击 [登录] 按钮 -> 登录成功 （免去重复跳转登录页输入账号密码的步骤）


### 追问：那我是否可以设计成不需要点登录按钮的，只要访问页面，它就能登录成功 
可以：加个过滤器检测到未登录 自动跳转就行了，详细可以参照章节：[[何时引导用户去登录]](/sso/sso-custom-login) 给出的建议进行设计


### 问：我参照文档的SSO模式二搭建，一直提示：Ticket无效，请问怎么回事？
根据群友的反馈，出现此异常概率最大的原因是因为 `Client` 与 `Server` 没有连接同一个Redis，SSO模式二中两者必须连接同一个 Redis 才可以登录成功，
如果您排查之后不是此原因，可以加入QQ群或者在issues反馈一下


### 模式一或者模式二报错：Could not write JSON: No serializer found for class com.pj.sso.SysUser and no properties discovered to create BeanSerializer 

一般是因为在 sso-server 端往 session 上写入了某个实体类（比如 User），而在 sso-client 端没有这个实体类，导致反序列化失败。

解决方案：在 sso-client 也新建上这个类，而且包名需要与 sso-server 端的一致（直接从 sso-server 把实体类复制过来就好了）


### 问：如果 sso-client 端我没有集成 sa-token-sso，如何对接？
需要手动调用 http 请求来对接 sso-server 开放的接口，参考示例：[sa-token-demo-sso3-client-nosdk](https://gitee.com/dromara/sa-token/tree/master/sa-token-demo/sa-token-demo-sso3-client-nosdk)


### 问：如果 sso-client 端不是 java语言，可以对接吗？
可以，只不过有点麻烦，基本思路和上个问题一致，需要手动调用 http 请求来对接 sso-server 开放的接口，参考：
[SSO-Server 认证中心开放接口](/sso/sso-apidoc)


<br/>

<details>
<summary>还有其它问题？</summary>
	
可以加群反馈一下，比较典型的问题我们解决之后都会提交到此页面方便大家快速排查
</details>




